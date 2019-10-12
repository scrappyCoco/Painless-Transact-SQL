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