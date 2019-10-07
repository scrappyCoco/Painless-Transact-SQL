package ru.coding4fun.tsql.intention.function

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlTypeElement
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.intention.IntentionFunUtil

class MsConvertToCastIntention: BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("convert.to.cast.name")
    override fun getText(): String = MsIntentionMessages.message("convert.to.cast.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return IntentionFunUtil.isAvailable(element, "CONVERT", arrayListOf(2, 3))
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val funCallExpr = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java)!!
        val typeDef = PsiTreeUtil.getChildOfType(funCallExpr.parameterList, SqlTypeElement::class.java) ?: return
        val expr = funCallExpr.parameterList?.children?.firstOrNull() ?: return
        val script = "CAST(${typeDef.text}, ${expr.text})"
        val convertExpr = SqlPsiElementFactory.createExpressionFromText(script, MsDialect.INSTANCE, project, null)!!
        funCallExpr.replace(convertExpr)
    }
}