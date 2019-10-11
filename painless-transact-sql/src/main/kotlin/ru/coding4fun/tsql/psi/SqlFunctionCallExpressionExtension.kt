package ru.coding4fun.tsql.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory

fun SqlFunctionCallExpression.getParams(): List<PsiElement> {
    val params = arrayListOf<PsiElement>()
    var isFirstBeforeComma = true
    for (child in this.parameterList?.children ?: return params) {
        if (child.isEmpty()) continue
        if (isFirstBeforeComma) {
            params.add(child)
            isFirstBeforeComma = false
        }
        else if ((child as? LeafPsiElement)?.text == ",") isFirstBeforeComma = true
    }
    return params
}

fun SqlFunctionCallExpression.addParam(param: String, project: Project) {
    val funParams = this.getParams()
    val last = funParams.last()
    val leftPart = this.text.substring(0, last.textRange.endOffset - this.textOffset)
    val newSql = "$leftPart, $param)"
    val newExpr = SqlPsiElementFactory.createExpressionFromText(newSql, MsDialect.INSTANCE, project, null)!!
    this.replace(newExpr)
}