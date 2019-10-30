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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.rename.RenameUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlElement
import com.intellij.sql.psi.SqlRenamePsiElementProcessor
import com.intellij.sql.psi.SqlStatement
import com.intellij.sql.psi.impl.SqlPsiElementFactory

abstract class TempVarTableBaseIntention : BaseElementAtCaretIntentionAction() {
    final override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        var isAvailable = false
        isAvailableImpl(element) { availableRange ->
            val currentOffset = editor?.caretModel?.offset ?: return@isAvailableImpl
            isAvailable = availableRange.contains(currentOffset)
        }
        return isAvailable

    }

    abstract fun isAvailableImpl(element: PsiElement, consumer: ((availableRange: TextRange) -> Unit))

    abstract fun invokeImpl(project: Project, element: PsiElement, consumer: ((newTableName: String, newSql: String, toRenameElement: SqlElement) -> Unit))

    final override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        invokeImpl(project, element) { newTableName, newSql, toRenameElement ->
            val usages = RenameUtil.findUsages(toRenameElement, newTableName, LocalSearchScope(element.containingFile), true, false, emptyMap())
            SqlRenamePsiElementProcessor().renameElement(toRenameElement, newTableName, usages, null)
            val newStatement = SqlPsiElementFactory.createStatementFromText(newSql, MsDialect.INSTANCE, project, null)!!
            val tableStmt = if (toRenameElement is SqlStatement) toRenameElement else PsiTreeUtil.getParentOfType(toRenameElement, SqlStatement::class.java)!!
            tableStmt.replace(newStatement)
        }
    }
}