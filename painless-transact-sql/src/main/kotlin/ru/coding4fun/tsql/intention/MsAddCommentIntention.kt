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
import com.intellij.database.model.DasNamespace
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.util.TextRange
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
        val sqlDefinition = PsiTreeUtil.getParentOfType(element, SqlDefinition::class.java) ?: return false
        val objNameRef = PsiTreeUtil.getChildOfType(sqlDefinition, SqlReferenceExpression::class.java) ?: return false
        return TextRange(sqlDefinition.textRange.startOffset, objNameRef.textRange.endOffset).contains(editor!!.caretModel.offset)
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
        }

        if (popupValues == null) {
            sqlDefinition.accept(SqlDefinitionVisitor(false, false))
        } else {
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
                    .append("  @level1type = N'TABLE', @level1name = N'", objectName, "'\n")
                    .also {
                        if (columnName != null)
                            it.append(",\n").append("  @level2type = N'COLUMN', @level2name = N'", columnName, "'\n")
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

        private fun getObjectName(createStatement: SqlCreateStatement): String {
            return createStatement.name
        }

        private fun getSchemaName(createStatement: SqlCreateStatement): String {
            val defaultNamespace = "dbo"
            val nameElement = createStatement.nameElement ?: return defaultNamespace

            return nameElement.children
                    .filterIsInstance<SqlReferenceExpression>()
                    .mapNotNull { it.resolve() as? DasNamespace }
                    .firstOrNull()?.name ?: defaultNamespace
        }

        override fun visitSqlCreateTableStatement(tableDefinition: SqlCreateTableStatement?) {
            if (tableDefinition == null) return
            createScriptInDocument(tableDefinition, null)
        }

        override fun visitSqlCreateStatement(createStatement: SqlCreateStatement?) {
            if (createStatement == null) return
            val schemaName = getSchemaName(createStatement)
            val objectName = getObjectName(createStatement)
            val sqlCmdToAddComment = getCommand(schemaName, objectName, null)
            val commentStmt = SqlPsiElementFactory.createStatementFromText(
                    sqlCmdToAddComment,
                    MsDialect.INSTANCE,
                    createStatement.project,
                    null
            ) ?: return
            createStatement.parent.add(commentStmt)
        }
    }
}