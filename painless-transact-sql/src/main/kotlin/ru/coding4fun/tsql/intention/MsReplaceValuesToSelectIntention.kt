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
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages

class MsReplaceValuesToSelectIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.values.to.select.name")
    override fun getText(): String = MsIntentionMessages.message("replace.values.to.select.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        return element.elementType == SqlElementTypes.SQL_VALUES
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val valuesExpression = PsiTreeUtil.getParentOfType(element, SqlValuesExpression::class.java) ?: return
        val scriptBuilder = StringBuilder()
        val columnNames = getColumnNames(valuesExpression)
        for ((rowNumber, rowExpression) in valuesExpression.expressions.withIndex()) {
            if (rowNumber > 0) scriptBuilder.append("\nUNION ALL\n")
            val parenthesizedExpression = rowExpression as SqlParenthesizedExpression
            if (!parenthesizedExpression.expressionList.any()) continue
            if (parenthesizedExpression.expressionList.size != columnNames.size) continue
            scriptBuilder.append("SELECT ")

            for ((cellNumber, cellExpression) in parenthesizedExpression.expressionList.withIndex()) {
                val columnName = columnNames[cellNumber]
                if (cellNumber > 0) scriptBuilder.append(",\n")
                scriptBuilder.append(columnName, " = ", cellExpression.text)
            }
        }
        val selectStatement = SqlPsiElementFactory.createStatementFromText(scriptBuilder.toString(), MsDialect.INSTANCE, project, null)
                ?: return
        valuesExpression.replace(selectStatement)
    }

    private fun getColumnNames(valuesExpression: SqlValuesExpression): List<String> {
        // SELECT * FROM (VALUES(1,2)) AS Data(A,B);
        if (valuesExpression.parent is SqlParenthesizedExpression) {
            val asExpression = valuesExpression.parent.parent as? SqlAsExpression
            val columnAliasList = asExpression?.columnAliasList ?: return emptyList()
            return columnAliasList.map { it.name }.toList()
        }

        // INSERT INTO T (A,B,C) VALUES(1,2,3);
        val insertInstruction = valuesExpression.parent as? SqlInsertDmlInstruction
        if (insertInstruction != null) {
            return insertInstruction.columnsList?.columnsReferenceList?.referenceList?.map { it.name }?.toList()
                    ?: emptyList()
        }

        return emptyList()
    }
}