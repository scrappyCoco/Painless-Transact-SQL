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

package ru.coding4fun.tsql.intention.function

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.intentions.SqlBaseElementAtCaretIntentionAction
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.intention.IntentionFunUtil
import ru.coding4fun.tsql.psi.getParams

class MsIsNullToCaseIntention : SqlBaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.isnull.to.case.name")
    override fun getText(): String = MsIntentionMessages.message("replace.isnull.to.case.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return IntentionFunUtil.isAvailable(element, "ISNULL", arrayListOf(2))
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val funCallExpr = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java)!!
        val params = funCallExpr.getParams()
        val checkExpr = params[0]
        val replaceExpr = params[1]
        val script = "CASE WHEN ${checkExpr.text} IS NULL THEN ${replaceExpr.text} ELSE ${checkExpr.text} END"
        val convertExpr = SqlPsiElementFactory.createExpressionFromText(script, MsDialect.INSTANCE, project, null)!!
        funCallExpr.replace(convertExpr)
    }
}