package ru.coding4fun.tsql.intention.function.string

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.intention.IntentionFunUtil

class MsLeftToSubstringIntention : BaseElementAtCaretIntentionAction() {
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return IntentionFunUtil.isAvailable(element, "LEFT", arrayListOf(2))
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val funCallExpression = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java) ?: return
        val params = funCallExpression.parameterList!!.expressionList
        val scriptBuilder = StringBuilder().append("SUBSTRING(", params[0].text, ", 1, ", params[1].text, ")")
        val substringCallExpression = SqlPsiElementFactory.createExpressionFromText(scriptBuilder.toString(), MsDialect.INSTANCE, project, null)!!
        funCallExpression.replace(substringCallExpression)
    }

    override fun getText(): String = MsIntentionMessages.message("left.to.substring.name")
    override fun getFamilyName(): String = MsIntentionMessages.message("left.to.substring.name")

}