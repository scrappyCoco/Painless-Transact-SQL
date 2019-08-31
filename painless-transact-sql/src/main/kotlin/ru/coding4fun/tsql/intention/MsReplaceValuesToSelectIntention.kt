package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type
import ru.coding4fun.tsql.MsIntentionMessages

class MsReplaceValuesToSelectIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.values.to.select.name")
    override fun getText(): String = MsIntentionMessages.message("replace.values.to.select.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MssqlDialect.INSTANCE) return false
        return element.type == SqlElementTypes.SQL_VALUES
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
        val selectStatement = SqlPsiElementFactory.createStatementFromText(scriptBuilder.toString(), MssqlDialect.INSTANCE, project, null)
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