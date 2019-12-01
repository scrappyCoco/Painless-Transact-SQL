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
import com.intellij.codeInspection.ui.SingleIntegerFieldOptionsPanel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlStringLiteralExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsInspectionMessages
import javax.swing.JComponent

class MsStringToReplicateInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    @JvmField
    var minCharsCount = 5
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createOptionsPanel(): JComponent? {
        return SingleIntegerFieldOptionsPanel("Minimum chars count", this, "minCharsCount", 4)
    }

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return StringToReplicateVisitor(manager, dialect, problems, onTheFly, minCharsCount)
    }

    private class StringToReplicateVisitor(manager: InspectionManager,
                                           dialect: SqlLanguageDialectEx,
                                           problems: MutableList<ProblemDescriptor>,
                                           private val onTheFly: Boolean,
                                           private val minCharsCount: Int
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlStringLiteralExpression(stringLiteralExpression: SqlStringLiteralExpression?) {
            if (stringLiteralExpression == null) return
            val valueText = stringLiteralExpression.value ?: return
            if (valueText.length < minCharsCount) return
            if (!repeatRegex.matches(valueText)) return

            val replicateText = getReplicate(stringLiteralExpression) ?: return
            val description = MsInspectionMessages.message("string.to.replicate.description", valueText, replicateText)
            val problem = myManager.createProblemDescriptor(
                    stringLiteralExpression,
                    description,
                    true,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    onTheFly,
                    ReplaceQuickFix(SmartPointerManager.createPointer(stringLiteralExpression))
            )
            addDescriptor(problem)

            super.visitSqlStringLiteralExpression(stringLiteralExpression)
        }
    }

    private class ReplaceQuickFix(private val stringPointer: SmartPsiElementPointer<SqlStringLiteralExpression>): LocalQuickFixOnPsiElement(stringPointer.element, stringPointer.element) {
        override fun getFamilyName(): String = MsInspectionMessages.message("string.to.replicate.fix.family")
        override fun getText(): String {
            val replicate = getReplicate(stringPointer.element) ?: return familyName
            val valueText = stringPointer.element?.value ?: return familyName
            return MsInspectionMessages.message("string.to.replicate.fix.text", valueText, replicate)
        }

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val replicate = getReplicate(stringPointer.element)
            val selectStmt = SqlPsiElementFactory.createStatementFromText("SELECT $replicate", MsDialect.INSTANCE, project, null)!!
            val replicateExpr = PsiTreeUtil.findChildOfType(selectStmt, SqlFunctionCallExpression::class.java)!!
            startElement.replace(replicateExpr)
        }
    }

    companion object {
        private val repeatRegex = Regex("^(.)\\1+$")

        private fun getReplicate(stringLiteralExpression: SqlStringLiteralExpression?): String? {
            val stringLiteral = stringLiteralExpression?.value ?: return null
            val repeatedChar = stringLiteral[0].toString()
            val len = stringLiteral.length.toString()
            return "REPLICATE('$repeatedChar', $len)"
        }
    }
}