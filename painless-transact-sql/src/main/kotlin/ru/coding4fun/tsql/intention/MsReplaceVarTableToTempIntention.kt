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

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlTableElementListImpl
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.psi.findLeaf

class MsReplaceVarTableToTempIntention : TempVarTableBaseIntention() {
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.var.table.to.temp.name")
    override fun getText(): String = MsIntentionMessages.message("replace.var.table.to.temp.name")

    override fun isAvailableImpl(element: PsiElement, consumer: ((availableRange: TextRange) -> Unit)) {
        val declareStmt = PsiTreeUtil.getParentOfType(element, SqlDeclareStatement::class.java) ?: return
        val tableLeaf = declareStmt.findLeaf(SqlElementTypes.SQL_TABLE) ?: return
        val availableRange = TextRange(declareStmt.textRange.startOffset, tableLeaf.textRange.endOffset)
        consumer(availableRange)
    }

    override fun invokeImpl(project: Project, element: PsiElement, consumer: ((newTableName: String, newSql: String, toRenameElement: SqlElement) -> Unit)) {
        val declareStmt = PsiTreeUtil.getParentOfType(element, SqlDeclareStatement::class.java)!!
        val toRenameElement = PsiTreeUtil.findChildOfType(declareStmt, SqlReferenceExpression::class.java)?.resolve() as? SqlVariableDefinition
                ?: return
        val newTableName = toRenameElement.name.replace("@", "#")
        val tableDefText = PsiTreeUtil.findChildOfType(toRenameElement, SqlTableElementListImpl::class.java)?.text
                ?: return
        val newSql = "CREATE TABLE $newTableName $tableDefText"
        consumer(newTableName, newSql, toRenameElement)
    }
}