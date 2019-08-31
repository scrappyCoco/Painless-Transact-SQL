package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.type
import ru.coding4fun.tsql.MsIntentionMessages

class MsReverseIifIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("reverse.iif.name")
    override fun getText(): String = MsIntentionMessages.message("reverse.iif.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MssqlDialect.INSTANCE) return false
        if (element.type != SqlElementTypes.SQL_IDENT) return false
        if (!"IIF".equals(element.text, true)) return false
        return true
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val functionCallExpression = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java)
                ?: return
        val argExpressions = functionCallExpression.parameterList?.expressionList ?: return
        if (argExpressions.size != 3) return

        val binaryExpression = argExpressions[0] as? SqlBinaryExpression ?: return
        FlipUtil.reverse(project, binaryExpression)

        val secondArg = argExpressions[1]
        val thirdArg = argExpressions[2]
        val copyOfSecondArg = secondArg.copy()

        secondArg.replace(thirdArg)
        thirdArg.replace(copyOfSecondArg)
    }
}