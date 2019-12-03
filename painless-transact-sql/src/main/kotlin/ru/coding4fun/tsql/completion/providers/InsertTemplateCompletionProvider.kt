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

package ru.coding4fun.tsql.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DdlBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlInsertStatement
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.psi.SqlTableColumnsList
import com.intellij.util.ProcessingContext
import ru.coding4fun.tsql.psi.getPrevNotEmptyLeaf

class InsertTemplateCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        // Strange that it presented there, but in another way is not working.
        if (parameters.position.containingFile.language != MsDialect.INSTANCE) return

        val prevNotEmptyLeaf = parameters.position.getPrevNotEmptyLeaf() ?: return
        if (!PREV_TYPES.contains(prevNotEmptyLeaf.elementType)) return
        val insertDml = PsiTreeUtil.getParentOfType(parameters.position, SqlInsertStatement::class.java) ?: return
        val ddlBuilder = DdlBuilder()
                .applyCodeStyle(parameters.editor.project, com.intellij.sql.dialects.mssql.MsDialect.INSTANCE)
                .keyword("SELECT").space()

        // Explicit column list.
        val tableColumnList = PsiTreeUtil.findChildOfType(insertDml, SqlTableColumnsList::class.java)

        val columnsList: List<SqlReferenceExpression> = if (tableColumnList != null) {
            // With explicit column list.
            tableColumnList.columnsReferenceList.referenceList
        } else {
            // All columns of table.
            insertDml.dmlInstruction?.targetColumnReferences ?: return
        }
        val colNames = getColNames(columnsList)
        if (colNames.size == 0) return

        for ((colNum, colName) in colNames.withIndex()) {
            if (colNum > 0) ddlBuilder.plain(",").newLine().tab()
            ddlBuilder.columnRef(colName).plain(" = ").keyword("NULL")
        }

        val generatedSql = ddlBuilder.cast(StringBuilder::class.java).toString()
        result.addElement(LookupElementBuilder.create(generatedSql))
    }

    private fun getColNames(columnReferences: List<SqlReferenceExpression>): ArrayList<String> {
        val colNames = arrayListOf<String>()
        for (ref in columnReferences) {
            val dasColumn = ref.multiResolve(false).mapNotNull { it.element as? DasColumn }.firstOrNull() ?: continue
            if (!DasUtil.isAuto(dasColumn)) colNames.add(dasColumn.name)
        }
        return colNames
    }

    companion object {
        val PREV_TYPES = listOf(
                SqlElementTypes.SQL_RIGHT_PAREN,
                SqlElementTypes.SQL_UNION,
                SqlElementTypes.SQL_EXCEPT,
                SqlElementTypes.SQL_INTERSECT,
                SqlElementTypes.SQL_ALL
        )
    }
}