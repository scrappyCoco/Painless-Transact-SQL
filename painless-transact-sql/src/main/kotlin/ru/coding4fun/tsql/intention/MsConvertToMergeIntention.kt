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
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages

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
        val selectStatement = PsiTreeUtil.getParentOfType(element, SqlSelectStatement::class.java) ?: return
        val mergeProcessor = MsMergeProcessor()
        PsiTreeUtil.processElements(mergeProcessor, selectStatement)

        val targetAliasText = mergeProcessor.targetAlias!!.text
        val sourceAliasText = mergeProcessor.sourceAlias!!.expression!!.text
        val joinText = mergeProcessor.joinClause!!.text
        val joinColumns = getJoinColumns(mergeProcessor.joinClause!!)
        val targetColumns = getColumns(mergeProcessor.targetAlias!!, joinColumns)
        val sourceColumns = getColumns(mergeProcessor.sourceAlias!!, null).toSet()
        val sourceColumnNames = sourceColumns.map { it.col.name.toLowerCase() }.toSet()
        val sourceColumnsText = targetColumns.joinToString(
                separator = ",\n           ",
                transform = { if (sourceColumnNames.contains(it.col.name.toLowerCase())) "${it.col.name} = ${it.col.name}" else "${it.col.name} = NULL" })
        val targetColumnsText = targetColumns.joinToString(separator = ", ") { it.col.name }
        val updateColumnsText = targetColumns.filter { !it.inJoinClause }.map { it.col.name }
                .joinToString(separator = ",\n    ", transform = { "$it = Source.$it" })


        val scriptBuilder = StringBuilder()
                .append("MERGE ", targetAliasText, "\n")
                .append("USING (\n")
                .append("    SELECT ", sourceColumnsText, "\n")
                .append("    FROM ", sourceAliasText, "\n")
                .append(") AS Source\n")
                .append(joinText, "\n")
                .append("WHEN NOT MATCHED BY TARGET THEN\n")
                .append("    INSERT (", targetColumnsText, ")\n")
                .append("    VALUES (", targetColumnsText, ")\n")
                .append("WHEN NOT MATCHED BY SOURCE THEN DELETE\n")
                .append("WHEN MATCHED THEN UPDATE SET\n")
                .append("    ", updateColumnsText, ";\n")

        val mergeStatement = SqlPsiElementFactory.createStatementFromText(scriptBuilder.toString(), MsDialect.INSTANCE, project, null)!!
        selectStatement.replace(mergeStatement)
    }

    private class MsMergeProcessor : PsiElementProcessor<PsiElement> {
        var sourceAlias: SqlAsExpression? = null
        var targetAlias: SqlAsExpression? = null
        var joinClause: SqlJoinConditionClause? = null

        override fun execute(element: PsiElement): Boolean {
            if (element is SqlAsExpression) {
                val aliasName = (element.nameElement as SqlIdentifier).name
                if ("SOURCE".equals(aliasName, true)) sourceAlias = element
                else if ("TARGET".equals(aliasName, true)) targetAlias = element
            } else if (element is SqlJoinConditionClause) {
                joinClause = element
            }
            return !isMatch()
        }

        fun isMatch(): Boolean = sourceAlias != null && targetAlias != null && joinClause != null
    }

    private fun getColumns(asExpression: SqlAsExpression, joinColumns: Set<PsiElement>?): List<MyColumn> {
        val referenceExpression = asExpression.expression as? SqlReferenceExpression ?: return emptyList()
        val tableType = referenceExpression.sqlType as? SqlTableType ?: return emptyList()
        val names = ArrayList<MyColumn>(tableType.columnCount)
        for (colNumber in 0 until tableType.columnCount) {
            val colElement = tableType.getColumnElement(colNumber) as SqlColumnDefinition
            names.add(MyColumn(colElement, joinColumns?.contains(colElement) ?: false))
        }
        return names
    }

    private class MyColumn(val col: SqlColumnDefinition, val inJoinClause: Boolean)

    private fun getJoinColumns(joinCondition: SqlJoinConditionClause): Set<PsiElement> {
        val filter = PsiElementFilter { fElement -> (fElement as? SqlReferenceExpression).elementType == SqlElementTypes.SQL_COLUMN_REFERENCE }
        val columns = ArrayList<PsiElement>()
        PsiTreeUtil.processElements(PsiElementProcessor.CollectFilteredElements(filter, columns), joinCondition)
        return columns.mapNotNull { (it as SqlReferenceExpression).resolve() }.toSet()
    }
}