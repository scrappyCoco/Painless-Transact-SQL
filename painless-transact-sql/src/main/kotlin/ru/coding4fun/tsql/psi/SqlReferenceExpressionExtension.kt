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

import com.intellij.database.model.DasColumn
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.*

fun SqlReferenceExpression.getAlias(): SqlIdentifier? {
    val asExpression = this.parent as? SqlAsExpression ?: return null
    return asExpression.nameElement
}

fun SqlReferenceExpression.getDmlHighlightRangeElements(): Pair<PsiElement, PsiElement>? {

    val intoElement = this.getPrevNotEmptyLeaf() as? LeafPsiElement
    if (intoElement != null && intoElement.elementType == SqlElementTypes.SQL_INTO) {
        val insertElement = intoElement.getPrevNotEmptyLeaf() as? LeafPsiElement
        if (insertElement != null && insertElement.elementType == SqlElementTypes.SQL_INSERT) {
            // [INSERT INTO @a] ...
            return Pair(insertElement, this)
        }
        // ... OUTPUT ... [INTO @a] ...
        return Pair(intoElement, this)
    }

    val dmlStatementElement = PsiTreeUtil.getParentOfType(this, SqlDmlStatement::class.java)
    if (dmlStatementElement != null) {
        val dmlOperationElement = dmlStatementElement.children.first { it is LeafPsiElement }
        val dmlInstruction = PsiTreeUtil.findChildOfType(dmlStatementElement, SqlDmlInstruction::class.java)
                ?: return null
        val dmlTargetElement = dmlInstruction.children.firstNotEmpty()

        // [DELETE @a] ... | [DELETE A] ... | [MERGE @a] ... | [UPDATE @a] ...
        return Pair(dmlOperationElement, dmlTargetElement)
    }

    return null
}

private val tv = arrayOf('#', '@')

fun SqlReferenceExpression.isTempOrVariable(): Boolean = tv.contains(this.text[0])

fun SqlReferenceExpression.resolveColumn(): DasColumn? {
    return this.multiResolve(false)
            .mapNotNull { ((it as? SqlResolveResult)?.element ?: it) as? DasColumn }
            .firstOrNull()
}