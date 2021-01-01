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

package ru.coding4fun.tsql.action

import com.intellij.database.model.DasTypedObject
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlReferenceExpression
import com.jetbrains.rd.util.getOrCreate
import ru.coding4fun.tsql.psi.getLeafSqlFiles
import ru.coding4fun.tsql.psi.getTopParent

class Test : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)!!
        val topParent = psiFile.getTopParent()
        val leafSqlFiles = topParent.getLeafSqlFiles()
        val sb = StringBuilder()

        for (sqlFile in leafSqlFiles) {
            val binExpressions = PsiTreeUtil.findChildrenOfType(sqlFile, SqlBinaryExpression::class.java)
            val references = hashMapOf<Pair<String, String>, ArrayList<String>>()

            for (binExpression in binExpressions) {
                val leftColumn =
                    (binExpression?.lOperand as? SqlReferenceExpression)?.resolve() as? DasTypedObject ?: continue

                val rightColumn =
                    (binExpression.rOperand as? SqlReferenceExpression)?.resolve() as? DasTypedObject ?: continue

                val leftTableName = leftColumn.dasParent!!.name
                val rightTableName = rightColumn.dasParent!!.name
                val sourceAndTargetPair = Pair(leftTableName, rightTableName)

                // DasTypedObject
                val columnList = references.getOrCreate(sourceAndTargetPair) { arrayListOf() }
                columnList.add("${leftColumn.name}:${rightColumn.name}")
            }

            for (referenceEntry in references.entries) {
                sb
                    .append(referenceEntry.key.first)
                    .append("->")
                    .append(referenceEntry.key.second)

                sb.append("[")
                for ((index, column) in referenceEntry.value.withIndex()) {
                    if (index > 0) sb.append(",")
                    sb.append(column)
                }
                sb.appendLine("]")
            }
        }

        val csv = sb.toString()
    }
}
