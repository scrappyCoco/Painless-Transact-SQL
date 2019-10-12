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

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlBinaryExpression
import ru.coding4fun.tsql.MsIntentionMessages

class MsFlipBinaryExpressionIntention : BaseElementAtCaretIntentionAction() {
    override fun getText(): String = MsIntentionMessages.message("flip.binary.expression.name")
    override fun getFamilyName(): String = MsIntentionMessages.message("flip.binary.expression.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        val binaryExpr = element.parent as? SqlBinaryExpression ?: return false
        return FlipUtil.contains(binaryExpr.opSign)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val binaryExpression = PsiTreeUtil.getParentOfType(element, SqlBinaryExpression::class.java) ?: return
        FlipUtil.flip(project, binaryExpression)
    }
}