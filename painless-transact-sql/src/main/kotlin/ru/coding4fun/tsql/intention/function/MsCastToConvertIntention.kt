package ru.coding4fun.tsql.intention.function

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.intention.IntentionFunUtil
import ru.coding4fun.tsql.psi.getChildOfElementType
import ru.coding4fun.tsql.psi.getNextNotEmptySibling

class MsCastToConvertIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("cast.to.convert.name")
    override fun getText(): String = MsIntentionMessages.message("cast.to.convert.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return IntentionFunUtil.isAvailable(element, "CAST", arrayListOf(1))
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val funCallExpr = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java)!!
        val exprList = funCallExpr.parameterList!!
        val typeDef = exprList.firstChild
        val expr = exprList.getChildOfElementType(SqlElementTypes.SQL_AS)?.getNextNotEmptySibling() ?: return
        val script = "CONVERT(${typeDef.text}, ${expr.text})"
        val convertExpr = SqlPsiElementFactory.createExpressionFromText(script, MsDialect.INSTANCE, project, null)!!
        funCallExpr.replace(convertExpr)
    }
}