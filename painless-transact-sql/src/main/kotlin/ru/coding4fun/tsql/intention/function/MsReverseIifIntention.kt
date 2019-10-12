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

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlFunctionCallExpression
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.intention.FlipUtil
import ru.coding4fun.tsql.intention.IntentionFunUtil

class MsReverseIifIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("reverse.iif.name")
    override fun getText(): String = MsIntentionMessages.message("reverse.iif.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return IntentionFunUtil.isAvailable(element, "IIF", arrayListOf(3))
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