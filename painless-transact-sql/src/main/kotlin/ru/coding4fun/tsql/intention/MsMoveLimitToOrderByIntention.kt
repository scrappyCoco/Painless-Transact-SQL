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
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlLimitClause
import com.intellij.sql.psi.SqlOrderByClause
import com.intellij.sql.psi.SqlQueryExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages

class MsMoveLimitToOrderByIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("move.limit.to.order.by.name")
    override fun getText(): String = MsIntentionMessages.message("move.limit.to.order.by.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        PsiTreeUtil.getParentOfType(element, SqlLimitClause::class.java) ?: return false
        return true
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val limitClause = PsiTreeUtil.getParentOfType(element, SqlLimitClause::class.java)!!
        val queryExpr = PsiTreeUtil.getParentOfType(element, SqlQueryExpression::class.java)!!
        val orderByClause = PsiTreeUtil.getChildOfType(queryExpr, SqlOrderByClause::class.java)
        val newOrderBySql = (if (orderByClause == null) "ORDER BY 1" else orderByClause.text) +
                " OFFSET 0 ROWS FETCH NEXT " + limitClause.rowCountExpression!!.text + " ROWS ONLY"
        val newOrderByExpr = SqlPsiElementFactory.createExpressionFromText(newOrderBySql, MsDialect.INSTANCE, project, null)!!
        limitClause.delete()
        if (orderByClause != null) {
            orderByClause.replace(newOrderByExpr)
        } else {
            queryExpr.add(newOrderByExpr)
        }
    }
}