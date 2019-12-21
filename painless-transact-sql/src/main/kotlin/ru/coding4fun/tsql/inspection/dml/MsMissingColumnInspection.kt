/*
 * Copyright [2020] Coding4fun
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

package ru.coding4fun.tsql.inspection.dml

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.database.model.DasColumn
import com.intellij.database.util.DasUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.resolveColumn

class MsMissingColumnInspection : SqlInspectionBase() {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return MissingColumnVisitor(dialect, manager, problems, onTheFly)
    }

    private class MissingColumnVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlMergeStatement(mergeStatement: SqlMergeStatement?) {
            if (mergeStatement == null) return
            val insertDmlInstruction = PsiTreeUtil.findChildOfType(mergeStatement, SqlInsertDmlInstruction::class.java) ?: return
            val referenceList = PsiTreeUtil.findChildOfType(mergeStatement, SqlReferenceList::class.java) ?: return
            visitSqlTableColumnListImpl(referenceList, insertDmlInstruction.targetType, referenceList)
            super.visitSqlMergeStatement(mergeStatement)
        }

        override fun visitSqlTableColumnList(tableColumnsList: SqlTableColumnsList?) {
            if (tableColumnsList?.columnsReferenceList == null) return
            val targetType: SqlTableType = tableColumnsList.tableReference.sqlType as? SqlTableType ?: return
            visitSqlTableColumnListImpl(tableColumnsList, targetType, tableColumnsList.columnsReferenceList)
            super.visitSqlTableColumnList(tableColumnsList)
        }

        fun visitSqlTableColumnListImpl(highlightElement: SqlElement, targetType: SqlTableType, referenceList: SqlReferenceList){
            val requiredColumns = hashSetOf<DasColumn>()

            for (columnNumber in 0 until targetType.columnCount) {
                val column = targetType.getColumnElement(columnNumber) as? DasColumn ?: continue

                val hasDefault = column.default != null
                val isRequired = column.isNotNull && !hasDefault && !DasUtil.isAutoGenerated(column)
                if (isRequired) requiredColumns.add(column)
            }

            for (columnRef in referenceList.referenceList) {
                val dasColumn = columnRef.resolveColumn() ?: break
                requiredColumns.remove(dasColumn)
            }

            if (!requiredColumns.any()) return

            val columnNames = requiredColumns.joinToString { it.name }
            val problemDescription = MsInspectionMessages.message("missing.column.problem", columnNames)
            val problem = myManager.createProblemDescriptor(
                    highlightElement,
                    highlightElement,
                    problemDescription,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    onTheFly,
                    null
            )
            addDescriptor(problem)
        }
    }
}