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

package ru.coding4fun.tsql.inspection.function.string

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlLiteralExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsInspectionMessages

class MsSubstringInspection:  SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.function.string.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return SubstringVisitor(manager, dialect, problems, onTheFly)
    }

    private class SubstringVisitor(manager: InspectionManager,
                                      dialect: SqlLanguageDialectEx,
                                      problems: MutableList<ProblemDescriptor>,
                                      private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlFunctionCallExpression(funExpression: SqlFunctionCallExpression?) {
            checkForLeft(funExpression)
            super.visitSqlFunctionCallExpression(funExpression)
        }

        private fun checkForLeft(funExpression: SqlFunctionCallExpression?) {
            if (!"SUBSTRING".equals(funExpression?.nameElement?.name, true)) return
            val parameters = funExpression?.parameterList?.expressionList
            if (parameters == null || parameters.size != 3) return
            val offsetParam = parameters[1] as? SqlLiteralExpression ?: return

            if (offsetParam.text == "1") {
                val problemMessage = MsInspectionMessages.message("inspection.function.substring.problem")
                val funName = funExpression.nameElement!!
                val problem = myManager.createProblemDescriptor(
                        funName,
                        problemMessage,
                        true,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        onTheFly,
                        ReplaceSubstringToLeftQuickFix(funExpression)
                )
                addDescriptor(problem)
            }
        }
    }

    private class ReplaceSubstringToLeftQuickFix(substringCallExpression: SqlFunctionCallExpression) : LocalQuickFixOnPsiElement(substringCallExpression, substringCallExpression) {
        override fun getFamilyName(): String = MsInspectionMessages.message("inspection.function.substring.fix.family")
        override fun getText(): String = MsInspectionMessages.message("inspection.function.substring.fix.family")

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val funExpression = startElement as SqlFunctionCallExpression
            val parameters = funExpression.parameterList!!.expressionList
            val scriptBuilder = StringBuilder()
                    .append("LEFT(", parameters[0].text, ", ", parameters[2].text, ")")

            val leftFunExpression = SqlPsiElementFactory.createExpressionFromText(scriptBuilder.toString(), MsDialect.INSTANCE, project, null)!!
            funExpression.replace(leftFunExpression)
        }
    }
}