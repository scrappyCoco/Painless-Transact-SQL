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

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.dialects.mssql.MssqlTypes
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type

fun PsiElement.deleteAllExcept(exceptElement: PsiElement) {
    for (child in this.children) {
        if (child != exceptElement && child.type != SqlElementTypes.WHITE_SPACE) {
            child.delete()
        }
    }
}

fun SqlAsExpression.convertColumnAsToEqual() = convertColumn(this, false)
fun SqlAsExpression.convertColumnEqualToAs() = convertColumn(this, true)

private fun convertColumn(asExpression: SqlAsExpression, toAs: Boolean) {
    val nameElement = asExpression.nameElement!!
    val expressionElement = asExpression.expression!!
    asExpression.deleteAllExcept(expressionElement)
    val columnName = MssqlDialect.INSTANCE.quoteIdentifier(nameElement.project, nameElement.name)
    val sql = if (toAs) "SELECT 1 AS $columnName" else "SELECT $columnName = 1"
    val queryExpression = SqlPsiElementFactory.createQueryExpressionFromText(sql, MssqlDialect.INSTANCE, asExpression.project)!!
    val asExpressionTemplate = PsiTreeUtil.findChildOfType(queryExpression, SqlAsExpression::class.java)!!
    val first = if (toAs) asExpressionTemplate.firstChild.nextSibling else asExpressionTemplate.firstChild
    val last = if (toAs) asExpressionTemplate.lastChild else asExpressionTemplate.lastChild.prevSibling
    if (toAs) {
        asExpression.addRangeAfter(first, last, expressionElement)
    } else {
        asExpression.addRangeBefore(first, last, expressionElement)
    }
}

fun SqlParameterDefinition.isReadonly(): Boolean {
    val lastChild = this.lastChild as? LeafPsiElement ?: return false
    return lastChild.elementType == MssqlTypes.MSSQL_READONLY
}

fun SqlReferenceExpression.getAlias(): SqlIdentifier? {
    val asExpression = this.parent as? SqlAsExpression ?: return null
    return asExpression.nameElement
}

fun Array<PsiElement>.firstNotEmpty(): PsiElement {
    return this.first { !SqlElementTypes.WS_OR_COMMENTS.contains(it.type) }
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

fun PsiElement.getPrevNotEmptyLeaf(): PsiElement? {
    var currentElement: PsiElement? = PsiTreeUtil.prevVisibleLeaf(this)
    while (currentElement != null && SqlElementTypes.WS_OR_COMMENTS.contains(currentElement.type)) {
        currentElement = PsiTreeUtil.prevVisibleLeaf(currentElement)
    }
    return currentElement
}

fun PsiElement.getNextNotEmptyLeaf(): PsiElement? {
    var currentElement: PsiElement? = PsiTreeUtil.nextVisibleLeaf(this)
    while (currentElement != null && SqlElementTypes.WS_OR_COMMENTS.contains(currentElement.type)) {
        currentElement = PsiTreeUtil.nextVisibleLeaf(currentElement)
    }
    return currentElement
}

fun PsiElement.getNextNotEmptySibling(): PsiElement? {
    var currentElement: PsiElement? = this.nextSibling
    while (currentElement != null && SqlElementTypes.WS_OR_COMMENTS.contains(currentElement.type)) {
        currentElement = currentElement.nextSibling
    }
    return currentElement
}

fun PsiElement.getPrevNotEmptySibling(): PsiElement? {
    var currentElement: PsiElement? = this.prevSibling
    while (currentElement != null && SqlElementTypes.WS_OR_COMMENTS.contains(currentElement.type)) {
        currentElement = currentElement.prevSibling
    }
    return currentElement
}

/*
 Is Simple CASE expression or Searched CASE expression?
 */
fun SqlCaseExpression.isSimple(): Boolean? {
    val caseKeyword = this.children.firstOrNull()!!
    val searchedExpressionCandidate = caseKeyword.getNextNotEmptySibling() ?: return null
    return searchedExpressionCandidate is SqlWhenThenClause
}

fun SqlBinaryExpression.split(): Pair<SqlReferenceExpression, Int>? {
    // ... @a = 1 ... || 1 = @a
    val variants = arrayOf(this.rOperand to this.lOperand, this.lOperand to this.rOperand)
    for (variant in variants) {
        val literalExpression = variant.first as? SqlLiteralExpression
        val hasLiteralInteger = literalExpression?.sqlType?.category == SqlType.Category.INTEGER
        val referenceExpression = variant.second as? SqlReferenceExpression
        val hasReference = referenceExpression != null
        if (hasLiteralInteger && hasReference) {
            val intValue = Integer.parseInt(literalExpression!!.text)
            return Pair(referenceExpression!!, intValue)
        }
    }
    return null
}