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
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlAsExpression
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.convertColumnAsToEqual
import ru.coding4fun.tsql.psi.convertColumnEqualToAs
import javax.swing.JComponent

class MsColumnAliasDefinitionInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    @Suppress("MemberVisibilityCanBePrivate")
    var preferEqualOverAs = true

    override fun getGroupDisplayName(): String = MsMessages.message("inspection.code.style.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createOptionsPanel(): JComponent? {
        val panel = MultipleCheckboxOptionsPanel(this)
        panel.addCheckbox(MsMessages.message("inspection.code.style.alias.as.equal.option"), "preferEqualOverAs")
        return panel
    }

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return ColumnAliasVisitor(dialect, manager, problems, onTheFly, preferEqualOverAs)
    }

    private class ColumnAliasVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean,
            private val preferEqualOverAs: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlAsExpression(asExpression: SqlAsExpression?) {
            if (asExpression == null) return
            if (SqlElementTypes.SQL_SELECT_CLAUSE != asExpression.context.type) {
                super.visitSqlAsExpression(asExpression)
                return
            }

            val searchForElement = if (preferEqualOverAs) SqlElementTypes.SQL_AS else SqlElementTypes.SQL_OP_EQ
            val aliasLeaf = asExpression.children
                    .firstOrNull { it is LeafPsiElement && it.elementType == searchForElement }
                    as? LeafPsiElement

            if (aliasLeaf == null) {
                super.visitSqlAsExpression(asExpression)
                return
            }

            val problemDescription = if (preferEqualOverAs)
                MsMessages.message("inspection.code.style.alias.as.equal.problem.as")
            else MsMessages.message("inspection.code.style.alias.as.equal.problem.equals")

            val asExpressionPointer = SmartPointerManager.createPointer(asExpression)
            val problem = myManager.createProblemDescriptor(
                    aliasLeaf,
                    problemDescription,
                    true,
                    ProblemHighlightType.WEAK_WARNING,
                    onTheFly,
                    ColumnAliasQuickFix(asExpressionPointer, preferEqualOverAs)
            )
            addDescriptor(problem)
            super.visitSqlAsExpression(asExpression)
        }
    }

    private class ColumnAliasQuickFix(
            private val asExpressionPointer: SmartPsiElementPointer<SqlAsExpression>,
            private val preferEqualOverAs: Boolean
    ) : LocalQuickFixOnPsiElement(asExpressionPointer.element, asExpressionPointer.element) {
        override fun getFamilyName(): String {
            return if (preferEqualOverAs) MsMessages.message("inspection.code.style.alias.as.equal.fix.family.as")
            else MsMessages.message("inspection.code.style.alias.as.equal.fix.family.equals")
        }

        override fun getText(): String {
            return if (preferEqualOverAs)
                MsMessages.message("inspection.code.style.alias.as.equal.fix.text.as", getAliasName())
            else MsMessages.message("inspection.code.style.alias.as.equal.fix.text.equals", getAliasName())
        }

        private fun getAliasName() = asExpressionPointer.element!!.nameElement!!.text

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            if (preferEqualOverAs) {
                asExpressionPointer.element!!.convertColumnAsToEqual()
            } else {
                asExpressionPointer.element!!.convertColumnEqualToAs()
            }
        }
    }
}