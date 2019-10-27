package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.database.model.DasNamespace
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages

class MsAddCommentIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = text
    override fun getText(): String = MsIntentionMessages.message("add.comment.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        PsiTreeUtil.getParentOfType(element, SqlDefinition::class.java) ?: return false
        return true
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val sqlDefinition = PsiTreeUtil.getParentOfType(element, SqlDefinition::class.java) ?: return
        val forTable = "For table"
        val forTableAndAllColumns = "For table and all columns"
        val forAllColumns = "For all columns"
        val forSelectedColumn = "For selected column"

        val popupValues = when (sqlDefinition) {
            is SqlColumnDefinition -> listOf(forAllColumns, forSelectedColumn)
            is SqlTableDefinition -> listOf(forTable, forTableAndAllColumns)
            else -> null
        } ?: return

        JBPopupFactory.getInstance()
                .createListPopup(object : BaseListPopupStep<String>("Create description", popupValues) {
                    override fun onChosen(selectedValue: String, finalChoice: Boolean): PopupStep<String>? {
                        WriteCommandAction.runWriteCommandAction(project) {
                            val createForAllColumns = arrayOf(forAllColumns, forTableAndAllColumns).contains(selectedValue)
                            val createForTable = arrayOf(forTable, forTableAndAllColumns).contains(selectedValue)
                            sqlDefinition.accept(SqlDefinitionVisitor(createForAllColumns, createForTable))
                        }
                        @Suppress("UNCHECKED_CAST")
                        return FINAL_CHOICE as PopupStep<String>?
                    }
                }).showInBestPositionFor(editor!!)
    }


    private inner class SqlDefinitionVisitor(
            private val createForAllColumns: Boolean,
            private val createForTable: Boolean
    ) : SqlVisitor() {
        fun getCommand(schemaName: String, objectName: String, columnName: String?): String {
            return StringBuilder()
                    .append("EXEC sys.sp_addextendedproperty\n")
                    .append("  @name = N'MS_Description', @value = N'...',\n")
                    .append("  @level0type = N'SCHEMA', @level0name = N'", schemaName, "',\n")
                    .append("  @level0type = N'TABLE', @level0name = N'", objectName, "'\n")
                    .also {
                        if (columnName != null)
                            it.append(",\n").append("  @level1type = N'COLUMN', @level1name = N'", columnName, "'\n")
                    }.append("\n").toString()
        }

        override fun visitSqlColumnDefinition(columnDefinition: SqlColumnDefinition?) {
            if (columnDefinition == null) return
            val tableDefinition =
                    PsiTreeUtil.getParentOfType(columnDefinition, SqlCreateTableStatement::class.java) ?: return
            createScriptInDocument(tableDefinition, columnDefinition.name)
            super.visitSqlColumnDefinition(columnDefinition)
        }

        private fun createScriptInDocument(tableDefinition: SqlCreateTableStatement, selectedColumnName: String?) {
            val schemaName = getSchemaName(tableDefinition)
            val tableName = getObjectName(tableDefinition)

            val columnNames = when {
                createForAllColumns -> PsiTreeUtil.findChildrenOfType(tableDefinition, SqlColumnDefinition::class.java)
                        .map { it.name }.toList()
                selectedColumnName != null -> listOf(selectedColumnName)
                else -> emptyList()
            }

            if (createForTable) {
                val sqlCmdToAddComment = getCommand(schemaName, tableName, null)
                val tableCommentStatement = SqlPsiElementFactory.createStatementFromText(
                        sqlCmdToAddComment,
                        MsDialect.INSTANCE,
                        tableDefinition.project,
                        null
                ) ?: return

                tableDefinition.parent.add(tableCommentStatement)
            }

            for (columnName in columnNames) {
                val sqlCmdToAddComment = getCommand(schemaName, tableName, columnName)
                val columnCommentStatement = SqlPsiElementFactory.createStatementFromText(
                        sqlCmdToAddComment,
                        MsDialect.INSTANCE,
                        tableDefinition.project,
                        null
                ) ?: continue

                tableDefinition.parent.add(columnCommentStatement)
            }
        }

        private fun getObjectName(tableDefinition: SqlCreateTableStatement): String {
            return tableDefinition.name
        }

        private fun getSchemaName(tableDefinition: SqlCreateTableStatement): String {
            val defaultNamespace = "dbo"
            val nameElement = tableDefinition.nameElement ?: return "dbo"

            return nameElement.children
                    .filterIsInstance<SqlReferenceExpression>()
                    .mapNotNull { it.resolve() as? DasNamespace }
                    .firstOrNull()?.name ?: defaultNamespace
        }

        override fun visitSqlCreateTableStatement(tableDefinition: SqlCreateTableStatement?) {
            if (tableDefinition == null) return
            createScriptInDocument(tableDefinition, null)
        }
    }
}