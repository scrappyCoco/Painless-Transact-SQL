package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlBinaryExpression
import ru.coding4fun.tsql.MsIntentionMessages

class MsFlipBinaryExpressionIntention : BaseElementAtCaretIntentionAction() {
    override fun getText(): String = MsIntentionMessages.message("flip.binary.expression.name")
    override fun getFamilyName(): String = MsIntentionMessages.message("flip.binary.expression.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        val binaryExpr = element.parent as? SqlBinaryExpression ?: return false
        return FlipUtil.contains(binaryExpr.opSign)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val binaryExpression = PsiTreeUtil.getParentOfType(element, SqlBinaryExpression::class.java) ?: return
        FlipUtil.flip(project, binaryExpression)
    }
}