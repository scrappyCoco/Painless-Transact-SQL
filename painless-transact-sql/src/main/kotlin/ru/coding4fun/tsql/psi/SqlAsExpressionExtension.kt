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

import com.intellij.database.model.DasObject
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlAsExpression
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory

fun SqlAsExpression.convertColumnAsToEqual() = convertColumn(this, false)
fun SqlAsExpression.convertColumnEqualToAs() = convertColumn(this, true)

private fun convertColumn(asExpression: SqlAsExpression, toAs: Boolean) {
    val nameElement = asExpression.nameElement!!
    val expressionElement = asExpression.expression!!
    asExpression.deleteAllExcept(expressionElement)
    val columnName = MsDialect.INSTANCE.quoteIdentifier(nameElement.project, nameElement.name)
    val sql = if (toAs) "SELECT 1 AS $columnName" else "SELECT $columnName = 1"
    val queryExpression = SqlPsiElementFactory.createQueryExpressionFromText(sql, MsDialect.INSTANCE, asExpression.project)!!
    val asExpressionTemplate = PsiTreeUtil.findChildOfType(queryExpression, SqlAsExpression::class.java)!!
    val first = if (toAs) asExpressionTemplate.firstChild.nextSibling else asExpressionTemplate.firstChild
    val last = if (toAs) asExpressionTemplate.lastChild else asExpressionTemplate.lastChild.prevSibling
    if (toAs) {
        asExpression.addRangeAfter(first, last, expressionElement)
    } else {
        asExpression.addRangeBefore(first, last, expressionElement)
    }
}

fun SqlAsExpression.getTarget(): DasObject? {
    val refExpr = this.expression as? SqlReferenceExpression
    return refExpr?.resolve() as DasObject
}