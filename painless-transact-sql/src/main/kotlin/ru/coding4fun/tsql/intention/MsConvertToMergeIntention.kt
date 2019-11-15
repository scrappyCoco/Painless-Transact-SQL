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
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasObject
import com.intellij.database.psi.DbColumn
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.util.castSafelyTo
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.psi.getTarget
import java.util.*
import kotlin.collections.ArrayList

class MsConvertToMergeIntention : BaseElementAtCaretIntentionAction() {
    override fun getText(): String = MsIntentionMessages.message("convert.to.merge.name")
    override fun getFamilyName(): String = MsIntentionMessages.message("convert.to.merge.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        val selectStatement = PsiTreeUtil.getParentOfType(element, SqlSelectStatement::class.java) ?: return false
        val mergeProcessor = MsMergeProcessor()
        PsiTreeUtil.processElements(mergeProcessor, selectStatement)
        return mergeProcessor.isMatch()
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        fun quote(colName: String): String = MsDialect.INSTANCE.quoteIdentifier(project, colName)

        val selectStatement = PsiTreeUtil.getParentOfType(element, SqlSelectStatement::class.java) ?: return
        val mergeProcessor = MsMergeProcessor()
        PsiTreeUtil.processElements(mergeProcessor, selectStatement)

        val target = mergeProcessor.targetAlias?.getTarget()!!
        val joinColumns = getJoinColumns(mergeProcessor.joinClause!!, target)
        val joinText = joinColumns.joinToString(" AND ") { col ->
            "Source.${quote(col.name)} = Target.${quote(col.name)}"
        }
        val sourceColumns = getColumns(mergeProcessor.sourceAlias!!)
                .associateByTo(TreeMap(String.CASE_INSENSITIVE_ORDER)) { it.name }

        val targetColumns = getColumns(mergeProcessor.targetAlias!!)
        val selectColText = targetColumns.joinToString(",\n", "           ") { col ->
            val sourceCol = sourceColumns[col.name]
            val targetCol = mergeProcessor.joinTargetToSourceColumns?.get(col as? PsiElement) as? DasColumn
            var targetColName = (sourceCol?.name ?: targetCol?.name)
            targetColName = if (targetColName != null) quote(targetColName) else "NULL"
            "${quote(col.name)} = $targetColName"
        }
        val insertColText = targetColumns.joinToString(separator = ", ") { quote(it.name) }
        val updateColText = targetColumns
                .filter { !joinColumns.contains(it) }
                .map { it.name }
                .joinToString(separator = ",\n    ", transform = { "${quote(it)} = Source.${quote(it)}" })


        val scriptBuilder = StringBuilder()
                .append("MERGE ", mergeProcessor.targetAlias!!.expression!!.text, " AS Target\n")
                .append("USING (\n")
                .append("    SELECT ", selectColText, "\n")
                .append("    FROM ", mergeProcessor.sourceAlias!!.text, "\n")
                .append(") AS Source\n")
                .append("ON ", joinText, "\n")
                .append("WHEN NOT MATCHED BY TARGET THEN\n")
                .append("    INSERT (", insertColText, ")\n")
                .append("    VALUES (", insertColText, ")\n")
                .append("WHEN NOT MATCHED BY SOURCE THEN DELETE\n")
                .append("WHEN MATCHED THEN UPDATE SET\n")
                .append("    ", updateColText, ";\n")

        val mergeStatement = SqlPsiElementFactory.createStatementFromText(scriptBuilder.toString(), MsDialect.INSTANCE, project, null)!!
        selectStatement.replace(mergeStatement)
    }

    private class MsMergeProcessor : PsiElementProcessor<PsiElement> {
        var sourceAlias: SqlAsExpression? = null
        var targetAlias: SqlAsExpression? = null
        var joinClause: SqlJoinConditionClause? = null
        var joinTargetToSourceColumns: Map<PsiElement, PsiElement>? = null

        override fun execute(element: PsiElement): Boolean {
            if (element is SqlAsExpression) {
                val aliasName = (element.nameElement as SqlIdentifier).name
                if ("SOURCE".equals(aliasName, true)) sourceAlias = element
                else if ("TARGET".equals(aliasName, true)) targetAlias = element
            } else if (element is SqlJoinConditionClause) {
                joinClause = element
                val targetObj = targetAlias?.getTarget() ?: return false
                joinTargetToSourceColumns = getTargetToSources(element, targetObj)
            }
            return !isMatch()
        }

        private fun getTargetToSources(joinClause: SqlJoinConditionClause, target: DasObject): Map<PsiElement, PsiElement> {
            val targetsToSource = hashMapOf<PsiElement, PsiElement>()
            val binaryExpressions = PsiTreeUtil.getChildrenOfType(joinClause, SqlBinaryExpression::class.java)
                    ?: return targetsToSource
            for (binExpr in binaryExpressions) {
                val cols = arrayOf(
                        (binExpr.lOperand to binExpr.rOperand),
                        (binExpr.rOperand to binExpr.lOperand)
                )
                for (colExpr in cols) {
                    val colRefExpr = colExpr.first as? SqlReferenceExpression ?: continue
                    val col = colRefExpr.resolve() ?: continue
                    val colDef = col as? SqlColumnDefinition
                    val colDb = col as? DbColumn
                    val table = colDef?.table ?: colDb?.table
                    if (table == target)
                        targetsToSource[col] = (colExpr.second!! as? SqlReferenceExpression)?.resolve() ?: continue
                }
            }
            return targetsToSource
        }

        fun isMatch(): Boolean = sourceAlias != null && targetAlias != null && joinClause != null
    }

    private fun getColumns(asExpression: SqlAsExpression): List<DasColumn> {
        val refExpr = asExpression.expression as? SqlReferenceExpression ?: return emptyList()
        val tableType = refExpr.sqlType as? SqlTableType ?: return emptyList()
        val names = ArrayList<DasColumn>(tableType.columnCount)
        for (colNumber in 0 until tableType.columnCount) {
            val column = tableType.getColumnElement(colNumber) as DasColumn
            names.add(column)
        }
        return names
    }

    private fun getJoinColumns(joinCondition: SqlJoinConditionClause, target: DasObject): Set<DasObject> {
        val filter = PsiElementFilter { fElement -> (fElement as? SqlReferenceExpression)?.elementType == SqlElementTypes.SQL_COLUMN_REFERENCE }
        val columns = ArrayList<PsiElement>()
        PsiTreeUtil.processElements(PsiElementProcessor.CollectFilteredElements(filter, columns), joinCondition)
        return columns.mapNotNull { filterColumnOfTarget(it, target) }.toSet()
    }

    private fun filterColumnOfTarget(element: PsiElement, target: DasObject): DasObject? {
        val resolve = (element as? SqlReferenceExpression)?.resolve() as? DasColumn ?: return null
        if (resolve.castSafelyTo<DasColumn>()?.table != target) return null
        return resolve
    }
}