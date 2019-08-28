package ru.coding4fun.tsql.inspection

import com.intellij.codeInspection.*
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlStatement
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.getNextNotEmptyLeaf
import javax.swing.JComponent

class MsSemicolonAtTheEndInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    @Suppress("MemberVisibilityCanBePrivate")
    var preferSemicolonAtTheEnd = true

    override fun createOptionsPanel(): JComponent? {
        val panel = MultipleCheckboxOptionsPanel(this)
        panel.addCheckbox(MsMessages.message("inspection.code.style.semicolon.at.the.end.option"), "preferSemicolonAtTheEnd")
        return panel
    }

    override fun createAnnotationVisitor(
            dialectEx: SqlLanguageDialectEx,
            inspectionManager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return SemicolonAtTheEndVisitor(inspectionManager, dialectEx, problems, onTheFly, preferSemicolonAtTheEnd)
    }

    private class SemicolonAtTheEndVisitor(manager: InspectionManager,
                                           dialect: SqlLanguageDialectEx,
                                           problems: MutableList<ProblemDescriptor>,
                                           private val onTheFly: Boolean,
                                           private val preferSemicolonAtTheEnd: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlStatement(sqlStatement: SqlStatement?) {
            if (sqlStatement == null) return
            val lastLeaf = PsiTreeUtil.getDeepestVisibleLast(sqlStatement)
            if (lastLeaf == null) {
                super.visitSqlStatement(sqlStatement)
                return
            }

            val semicolonLeaf = lastLeaf.getNextNotEmptyLeaf()
            val isEndedWithSemicolon = semicolonLeaf?.type == SqlElementTypes.SQL_SEMICOLON
            if (isEndedWithSemicolon.xor(preferSemicolonAtTheEnd)) {
                val problemMessage = if (preferSemicolonAtTheEnd)
                    MsMessages.message("inspection.code.style.semicolon.at.the.end.fix.add")
                else MsMessages.message("inspection.code.style.semicolon.at.the.end.fix.remove")

                val highlightType = if (preferSemicolonAtTheEnd)
                    ProblemHighlightType.INFORMATION else ProblemHighlightType.LIKE_UNUSED_SYMBOL

                val problemElement = if (preferSemicolonAtTheEnd)
                    sqlStatement else semicolonLeaf!!

                val problem = myManager.createProblemDescriptor(
                        problemElement,
                        problemMessage,
                        true,
                        highlightType,
                        onTheFly,
                        SemicolonAtTheEndQuickFix(problemElement, preferSemicolonAtTheEnd)
                )
                addDescriptor(problem)
            }
            super.visitSqlStatement(sqlStatement)
        }
    }
}

private class SemicolonAtTheEndQuickFix(
        problemElement: PsiElement,
        private val preferSemicolonAtTheEnd: Boolean
) : LocalQuickFixOnPsiElement(problemElement) {
    override fun getFamilyName(): String = if (preferSemicolonAtTheEnd)
        MsMessages.message("inspection.code.style.semicolon.at.the.end.fix.add")
    else MsMessages.message("inspection.code.style.semicolon.at.the.end.fix.remove")

    override fun getText(): String = familyName

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        if (preferSemicolonAtTheEnd) {
            val semicolonLeaf = SqlPsiElementFactory.createLeafFromText(project, MssqlDialect.INSTANCE, ";")
            startElement.parent.addAfter(semicolonLeaf, startElement)
        } else {
            startElement.delete() // semicolonLeaf
        }
    }
}