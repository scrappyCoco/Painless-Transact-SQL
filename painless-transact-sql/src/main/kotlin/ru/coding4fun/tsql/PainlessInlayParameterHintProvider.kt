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

package ru.coding4fun.tsql

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.codeInsight.hints.Option
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import ru.coding4fun.tsql.intention.DateStyle
import ru.coding4fun.tsql.psi.getParams

class PainlessInlayParameterHintProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement?): MutableList<InlayInfo> {
        if (!isCultureInfoArg(element)) return mutableListOf()
        val msDateFormat = DateStyle.STYLES[element!!.text] ?: return mutableListOf()
        return arrayListOf(InlayInfo(msDateFormat.format, element.textOffset))
    }

    private fun isCultureInfoArg(element: PsiElement?): Boolean {
        if (element?.containingFile?.language != MsDialect.INSTANCE) return false
        if (element !is LeafPsiElement) return false
        val funCallExpr = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java) ?: return false
        if (!"CONVERT".equals(funCallExpr.nameElement?.text, true)) return false
        val params = funCallExpr.getParams()
        if (params.size != 3) return false
        val cultureElement = params[2]
        if (cultureElement.textRange != element.textRange) return false
        return true
    }

    override fun getDefaultBlackList(): MutableSet<String> = mutableSetOf()

    override fun getHintInfo(element: PsiElement?): HintInfo? {
        if (element == null) return null
        if (isCultureInfoArg(element)) {
            return HintInfo.OptionInfo(MY_OPTIONS)
        }
        return null
    }

    companion object {
        private val MY_OPTIONS = Option("painless.bla.bla.bla", "Bla bla", true)
    }
}