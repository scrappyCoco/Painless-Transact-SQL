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

package ru.coding4fun.tsql.inspection

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlQueryExpression
import com.intellij.sql.psi.SqlSelectStatement
import com.intellij.sql.psi.SqlWithQueryExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.getPrevNotEmptyLeaf

class MsSemicolonCteInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)


    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return SemicolonCteVisitor(manager, dialect, problems, onTheFly)
    }

    private class SemicolonCteVisitor(manager: InspectionManager,
                                      dialect: SqlLanguageDialectEx,
                                      problems: MutableList<ProblemDescriptor>,
                                      private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val requiredPrevTypes = arrayListOf(
                SqlElementTypes.SQL_SEMICOLON, // In the middle of the batch.
                SqlElementTypes.SQL_BEGIN, // Any begin
                SqlElementTypes.SQL_AS, // Begin of the procedure definition
                SqlElementTypes.SQL_GO
        )

        override fun visitSqlQueryExpression(queryExpression: SqlQueryExpression?) {
            if (queryExpression == null) return
            val sqlWithQueryExpression = queryExpression as? SqlWithQueryExpression ?: return
            val flatPrevLeaf = sqlWithQueryExpression.getPrevNotEmptyLeaf() ?: return
            if (requiredPrevTypes.contains(flatPrevLeaf.type)) return

            val problemMessage = MsMessages.message("inspection.dml.semicolon.cte.problem")
            val withKeyword = PsiTreeUtil.findChildOfType(sqlWithQueryExpression, LeafPsiElement::class.java) ?: return
            val selectStatement = queryExpression.parent as? SqlSelectStatement ?: return
            val problem = myManager.createProblemDescriptor(
                    withKeyword,
                    problemMessage,
                    true,
                    ProblemHighlightType.WARNING,
                    onTheFly,
                    AddSemicolonQuickFix(selectStatement)
            )
            addDescriptor(problem)

            super.visitSqlQueryExpression(queryExpression)
        }
    }

    private class AddSemicolonQuickFix(selectStatement: SqlSelectStatement
    ) : LocalQuickFixOnPsiElement(selectStatement) {
        override fun getFamilyName(): String = MsMessages.message("inspection.dml.semicolon.cte.fix")
        override fun getText(): String = MsMessages.message("inspection.dml.semicolon.cte.fix")

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val selectStatement = startElement as SqlSelectStatement
            val semicolonElement = SqlPsiElementFactory.createLeafFromText(project, MssqlDialect.INSTANCE, ";")
            selectStatement.parent.addBefore(semicolonElement, selectStatement)
        }
    }
}