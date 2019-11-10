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

import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.psi.SqlElement
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.SqlType

fun SqlElement.findFirstTableReference(): SqlReferenceExpression? {
    val filter = PsiElementFilter { element ->
        SqlType.Category.TABLE.`is`((element as? SqlReferenceExpression)?.sqlType) &&
                PsiTreeUtil.getDeepestFirst(element).elementType != SqlElementTypes.SQL_ASTERISK
    }
    val findTableRefProc = PsiElementProcessor.FindFilteredElement<SqlReferenceExpression>(filter)
    PsiTreeUtil.processElements(findTableRefProc, this)
    return findTableRefProc.foundElement
}