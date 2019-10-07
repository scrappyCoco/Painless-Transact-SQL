package ru.coding4fun.tsql.intention

import com.intellij.psi.PsiElement
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlReferenceExpression
import ru.coding4fun.tsql.psi.getParams
import ru.coding4fun.tsql.psi.getTextOwner

object IntentionFunUtil {
    fun isAvailable(element: PsiElement, funName: String, argsCount: List<Int>): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        if (!funName.equals(element.text, true)) return false
        val topMostElement = element.getTextOwner()
        val refExpr = topMostElement as? SqlReferenceExpression ?: return false
        val funCallExpr = refExpr.parent as? SqlFunctionCallExpression ?: return false
        val paramsSize = funCallExpr.getParams().size
        if (!argsCount.contains(paramsSize)) return false
        return true
    }
}