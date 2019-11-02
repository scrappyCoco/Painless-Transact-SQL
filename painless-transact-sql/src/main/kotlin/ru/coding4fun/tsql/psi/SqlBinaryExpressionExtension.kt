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

import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlLiteralExpression
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.SqlType


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