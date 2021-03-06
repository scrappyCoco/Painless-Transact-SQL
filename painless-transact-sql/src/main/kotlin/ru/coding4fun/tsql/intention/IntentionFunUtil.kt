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