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
import com.intellij.sql.psi.SqlCreateTableStatement
import com.intellij.sql.psi.SqlElement
import com.intellij.sql.psi.impl.SqlTableElementListImpl
import ru.coding4fun.tsql.MsIntentionMessages

class MsReplaceTempTableToVarIntention : TempVarTableBaseIntention() {
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.temp.table.to.var.name")
    override fun getText(): String = MsIntentionMessages.message("replace.temp.table.to.var.name")

    override fun isAvailableImpl(element: PsiElement, consumer: ((availableRange: TextRange) -> Unit)) {
        val createTableStmt = PsiTreeUtil.getParentOfType(element, SqlCreateTableStatement::class.java) ?: return
        val nameElement = createTableStmt.nameElement ?: return
        if (!nameElement.text.startsWith("#")) return
        val endOfAvailableOffset = nameElement.textRange?.endOffset ?: return
        val availableRange = TextRange(createTableStmt.textRange.startOffset, endOfAvailableOffset)
        consumer(availableRange)
    }

    override fun invokeImpl(project: Project, element: PsiElement, consumer: ((newTableName: String, newSql: String, toRenameElement: SqlElement) -> Unit)) {
        val createTableStmt = PsiTreeUtil.getParentOfType(element, SqlCreateTableStatement::class.java) ?: return
        val toRenameElement = createTableStmt.nameElement?.resolve() as? SqlElement ?: return
        val newTableName = toRenameElement.name!!.replace("#", "@")
        val tableDefText = PsiTreeUtil.findChildOfType(toRenameElement, SqlTableElementListImpl::class.java)?.text
                ?: return
        val newSql = "DECLARE $newTableName TABLE $tableDefText"
        consumer(newTableName, newSql, toRenameElement)
    }
}