/*
 * Copyright [2019] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.tsql.inspection.ddl

import com.intellij.codeInspection.*
import com.intellij.database.model.DasObject
import com.intellij.database.model.PsiTable
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlCreateStatement
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.SqlResolveResult
import com.intellij.sql.type
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.dataSource.PathPartManager
import ru.coding4fun.tsql.psi.getChildOfElementType
import ru.coding4fun.tsql.psi.isSqlConsole

class MsRenamedInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.ddl.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialectEx: SqlLanguageDialectEx,
            inspectionManager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return RenamedVisitor(inspectionManager, dialectEx, problems, onTheFly)
    }

    private class RenamedVisitor(
            manager: InspectionManager,
            dialect: SqlLanguageDialectEx,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val targetTypes = arrayListOf(
                SqlElementTypes.SQL_CREATE_PROCEDURE_STATEMENT,
                SqlElementTypes.SQL_CREATE_FUNCTION_STATEMENT,
                SqlElementTypes.SQL_CREATE_TRIGGER_STATEMENT
        )

        override fun visitSqlCreateStatement(createStatement: SqlCreateStatement?) {
            if (createStatement == null) return
            if (!targetTypes.contains(createStatement.type)) return
            if (createStatement.containingFile.isSqlConsole()) return
            // Reference to the procedure/function from the file path. If there the context of trigger, then it's the table/view.
            val referenceFromFilePath = PathPartManager.getReferenceFromFilePath(createStatement) ?: return

            val problemReference: SqlReferenceExpression
            val createStatementToCheck: DasObject
            if (createStatement.type != SqlElementTypes.SQL_CREATE_TRIGGER_STATEMENT) {
                createStatementToCheck = createStatement
                problemReference = PsiTreeUtil.findChildOfType(createStatement, SqlReferenceExpression::class.java)
                        ?: return
            } else {
                // Target table/view.
                val targetElement = createStatement
                        .getChildOfElementType(SqlElementTypes.SQL_ON_TARGET_CLAUSE) ?: return

                // Reference to target table/view.
                problemReference = PsiTreeUtil
                        .getChildOfType(targetElement, SqlReferenceExpression::class.java) ?: return

                // Try to resolver target table/view.
                createStatementToCheck = (problemReference.reference.multiResolve(false)
                        .asSequence().map { (it as SqlResolveResult).element }.filterIsInstance<PsiTable>().firstOrNull()
                        ?: return)
            }


            if (!PathPartManager.areSame(createStatementToCheck, referenceFromFilePath)) {
                val problemMessage = MsInspectionMessages.message(
                        "inspection.ddl.renamed.problem",
                        problemReference.text,
                        referenceFromFilePath.text)

                val problem = myManager.createProblemDescriptor(
                        problemReference,
                        problemMessage,
                        true,
                        ProblemHighlightType.WARNING,
                        onTheFly,
                        RenameRoutineQuickFix(
                                SmartPointerManager.createPointer(problemReference),
                                SmartPointerManager.createPointer(referenceFromFilePath)
                        )
                )
                addDescriptor(problem)
            }
        }
    }

    private class RenameRoutineQuickFix(
            private val problemReferencePoint: SmartPsiElementPointer<SqlReferenceExpression>,
            private val actualReferencePoint: SmartPsiElementPointer<SqlReferenceExpression>
    ) : LocalQuickFixOnPsiElement(problemReferencePoint.element!!) {
        override fun getFamilyName(): String = MsInspectionMessages.message("inspection.ddl.renamed.fix.family")

        override fun getText(): String = MsInspectionMessages.message(
                "inspection.ddl.renamed.fix.text",
                problemReferencePoint.element?.name ?: "problem reference",
                actualReferencePoint.element?.name ?: "actual reference")

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val replacedFullReference = problemReferencePoint.element!!.replace(actualReferencePoint.element!!)
            val schemaReference = PsiTreeUtil.getChildOfType(replacedFullReference, SqlReferenceExpression::class.java)
                    ?: return
            val dbReference = PsiTreeUtil.getChildOfType(schemaReference, SqlReferenceExpression::class.java) ?: return
            val dotSibling = PsiTreeUtil.findSiblingForward(dbReference, SqlElementTypes.SQL_PERIOD, null) ?: return
            dbReference.delete()
            dotSibling.delete()
        }
    }
}