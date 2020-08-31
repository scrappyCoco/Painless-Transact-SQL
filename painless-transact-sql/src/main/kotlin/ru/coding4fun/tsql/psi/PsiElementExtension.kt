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

import com.intellij.database.model.ObjectKind
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.children
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlCreateStatement
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlInfoElementType
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.util.castSafelyTo

fun PsiElement.getTextOwner(): PsiElement {
    var current = this
    while (current.parent.textRange == current.textRange) current = current.parent
    return current
}

fun PsiElement.deleteAllExcept(exceptElement: PsiElement) {
    for (child in this.children) {
        if (child != exceptElement && child.elementType != SqlElementTypes.WHITE_SPACE) {
            child.delete()
        }
    }
}

fun Array<PsiElement>.firstNotEmpty(): PsiElement {
    return this.first { !SqlElementTypes.WS_OR_COMMENTS.contains(it.elementType) }
}

fun PsiElement.getPrevNotEmptyLeaf(): PsiElement? {
    var currentElement: PsiElement? = PsiTreeUtil.prevVisibleLeaf(this)
    while (currentElement != null && currentElement.isEmpty()) {
        currentElement = PsiTreeUtil.prevVisibleLeaf(currentElement)
    }
    return currentElement
}

fun PsiElement.findLeaf(elementType: IElementType): LeafPsiElement? {
    val endOffset = this.textRange.endOffset
    var curElement = PsiTreeUtil.getDeepestFirst(this)
    while (true) {
        if (curElement.elementType == elementType) return curElement.castSafelyTo<LeafPsiElement>()
        curElement = curElement.getNextNotEmptyLeaf() ?: break
        if (curElement.textRange.endOffset > endOffset) break
    }
    return null
}

fun PsiElement.getNextNotEmptyLeaf(): PsiElement? {
    var currentElement: PsiElement? = PsiTreeUtil.nextVisibleLeaf(this)
    while (currentElement != null && currentElement.isEmpty()) {
        currentElement = PsiTreeUtil.nextVisibleLeaf(currentElement)
    }
    return currentElement
}

fun PsiElement.isEmpty(): Boolean {
    return SqlElementTypes.WS_OR_COMMENTS.contains(this.elementType)
}

fun PsiElement.getNextNotEmptySibling(): PsiElement? {
    var currentElement: PsiElement? = this.nextSibling
    while (currentElement != null && currentElement.isEmpty()) {
        currentElement = currentElement.nextSibling
    }
    return currentElement
}

fun PsiElement.getPrevNotEmptySibling(): PsiElement? {
    var currentElement: PsiElement? = this.prevSibling
    while (currentElement != null && currentElement.isEmpty()) {
        currentElement = currentElement.prevSibling
    }
    return currentElement
}

fun PsiFile.isSqlConsole(): Boolean {
    val folderName = this.parent?.name ?: return false
    return guidRegex.matches(folderName)
}

val guidRegex = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

fun PsiElement.getChildOfElementType(type: IElementType): PsiElement? {
    for (child in this.children) {
        if (child is SqlInfoElementType<*>) continue
        if (child.elementType == type) return child
    }
    return null
}

fun getObjectReference(objectPath: String, createStatement: SqlCreateStatement): SqlReferenceExpression {
    val sql = when (createStatement.kind) {
        ObjectKind.ROUTINE -> "EXEC $objectPath"
        else -> "SELECT * FROM $objectPath"
    }
    val expression = SqlPsiElementFactory.createStatementFromText(sql, MsDialect.INSTANCE, createStatement.project, null)!!
    return PsiTreeUtil.findChildrenOfType(expression, SqlReferenceExpression::class.java).maxByOrNull { it.textRange.endOffset }!!
}

fun PsiElement.getLeafChildrenByAst(textRange: TextRange): ArrayList<LeafPsiElement> {
    val children = arrayListOf<LeafPsiElement>()
    for (child in this.node.children()) {
        if (child.textRange.startOffset < textRange.startOffset) continue
        if (child.textRange.startOffset >= textRange.endOffset) break
        val leafPsiElement = child.psi as? LeafPsiElement ?: continue
        if (leafPsiElement.isEmpty()) continue
        children.add(leafPsiElement)
    }
    return children
}

inline fun <reified T : PsiElement> PsiElement?.hasParentOfType(): Boolean {
    return this != null && PsiTreeUtil.getParentOfType(this, T::class.java) != null
}

inline fun <reified T : PsiElement> PsiElement?.getParentOfType(): T? {
    return if (this == null) null else PsiTreeUtil.getParentOfType(this, T::class.java)
}

inline fun <reified T : PsiElement> PsiElement?.getChildrenOfType(): List<T> {
    return if (this == null) emptyList()
    else PsiTreeUtil.getChildrenOfType(this, T::class.java)?.filterNotNull()?.toList() ?: emptyList()
}

inline fun <reified T : PsiElement> PsiElement?.findChildrenOfType(): List<T> {
    return if (this == null) emptyList()
    else PsiTreeUtil.findChildrenOfType(this, T::class.java).filterNotNull().toList()
}