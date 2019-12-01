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
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlExpression
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsIntentionMessages

class MsReplaceEqualToExistsIntersectIntention : BaseElementAtCaretIntentionAction() {
    private var myText = familyName
    override fun getFamilyName(): String = MsIntentionMessages.message("replace.equal.to.exists.intersect.name")
    override fun getText(): String = myText

    private fun getExistIntersectExpression(binaryExpression: SqlBinaryExpression): SqlExpression {
        val sql = "SELECT 1 WHERE EXISTS(SELECT ${binaryExpression.lOperand.text} INTERSECT SELECT ${binaryExpression.rOperand!!.text})"
        val stmt = SqlPsiElementFactory.createStatementFromText(sql, MsDialect.INSTANCE, binaryExpression.project, null)!!
        return PsiTreeUtil.findChildOfType(stmt, SqlFunctionCallExpression::class.java)!!
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        if (element.elementType != SqlElementTypes.SQL_OP_EQ) return false
        val sqlBinaryExpression = element.parent as? SqlBinaryExpression ?: return false
        if (sqlBinaryExpression.rOperand == null) return false
        val existIntersectExpr = getExistIntersectExpression(sqlBinaryExpression)
        myText = MsIntentionMessages.message("replace.equal.to.exists.intersect.text", existIntersectExpr.text)
        return true
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val sqlBinExpr = element.parent as SqlBinaryExpression
        val existInsertExpr = getExistIntersectExpression(sqlBinExpr)
        sqlBinExpr.replace(existInsertExpr)
    }
}