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

package ru.coding4fun.tsql.inspection.codeStyle

import com.intellij.codeInspection.*
import com.intellij.database.model.ObjectKind
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.getNextNotEmptyLeaf

class MsRedundantQualifierInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.code.style.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return RedundantQualifierVisitor(dialect, manager, problems, onTheFly)
    }

    private class RedundantQualifierVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlSetAssignment(setAssignment: SqlSetAssignment?) {
            while (true) {
                if (setAssignment == null) break
                addProblem(setAssignment.lValue)
                break
            }
            super.visitSqlSetAssignment(setAssignment)
        }

        override fun visitSqlValuesExpression(valuesExpression: SqlValuesExpression?) {
            while (true) {
                if (valuesExpression == null) break
                PsiTreeUtil.getParentOfType(valuesExpression, SqlMergeStatement::class.java) ?: break
                val colRefs = PsiTreeUtil.findChildrenOfType(valuesExpression, SqlReferenceExpression::class.java)
                for (colRef in colRefs) addProblem(colRef)
                break
            }
            super.visitSqlValuesExpression(valuesExpression)
        }

        private fun addProblem(element: SqlElement) {
            val columnRef = element as? SqlReferenceExpression ?: return
            if (columnRef.referenceElementType.targetKind != ObjectKind.COLUMN) return
            val qualifier = PsiTreeUtil.getChildOfType(columnRef, SqlReferenceExpression::class.java) ?: return

            val problemMessage = MsInspectionMessages.message("redundant.qualifier.problem", qualifier.text)
            val problem = myManager.createProblemDescriptor(
                    qualifier,
                    problemMessage,
                    true,
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    onTheFly,
                    RemoveQualifierQuickFix(SmartPointerManager.createPointer(qualifier))
            )
            addDescriptor(problem)
        }

        private class RemoveQualifierQuickFix(private val refExpr: SmartPsiElementPointer<SqlReferenceExpression>
        ) : LocalQuickFixOnPsiElement(refExpr.element!!) {
            override fun getFamilyName(): String = MsInspectionMessages.message("redundant.qualifier.fix.family")
            override fun getText(): String {
                if (refExpr.element?.text == null) return familyName
                return MsInspectionMessages.message("redundant.qualifier.fix.text", refExpr.element!!.text)
            }

            override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
                refExpr.element?.getNextNotEmptyLeaf()?.delete()
                refExpr.element!!.delete()
            }
        }
    }
}