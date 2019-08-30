package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type
import ru.coding4fun.tsql.MsIntentionMessages

class MsFlipBinaryExpressionIntention : BaseElementAtCaretIntentionAction() {
    private val operators = hashMapOf(
            SqlElementTypes.SQL_OP_GT to SqlElementTypes.SQL_OP_LT,
            SqlElementTypes.SQL_OP_LT to SqlElementTypes.SQL_OP_GT,
            SqlElementTypes.SQL_OP_NEQ to SqlElementTypes.SQL_OP_NEQ,
            SqlElementTypes.SQL_OP_LE to SqlElementTypes.SQL_OP_GE,
            SqlElementTypes.SQL_OP_GE to SqlElementTypes.SQL_OP_LE,
            SqlElementTypes.SQL_OP_PLUS to SqlElementTypes.SQL_OP_PLUS,
            SqlElementTypes.SQL_OP_MINUS to SqlElementTypes.SQL_OP_MINUS,
            SqlElementTypes.SQL_OP_DIV to SqlElementTypes.SQL_OP_DIV,
            SqlElementTypes.SQL_OP_MUL to SqlElementTypes.SQL_OP_MUL,
            SqlElementTypes.SQL_OP_EQ to SqlElementTypes.SQL_OP_EQ,
            SqlElementTypes.SQL_AND to SqlElementTypes.SQL_AND,
            SqlElementTypes.SQL_OR to SqlElementTypes.SQL_OR
    )

    override fun getText(): String = MsIntentionMessages.message("intention.flip.binary.expression.name")
    override fun getFamilyName(): String = MsIntentionMessages.message("intention.flip.binary.expression.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return operators.contains((element as? LeafPsiElement)?.type)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val binaryExpression = PsiTreeUtil.getParentOfType(element, SqlBinaryExpression::class.java) ?: return
        val newOperatorType = operators[binaryExpression.opSignElement.type!!]!!
        val swappedSql = "IF ${binaryExpression.rOperand!!.text} $newOperatorType ${binaryExpression.lOperand.text} SELECT 1"
        val newExpression = SqlPsiElementFactory.createStatementFromText(swappedSql, MssqlDialect.INSTANCE, project, null)
        val newBinaryExpression = PsiTreeUtil.findChildOfType(newExpression, SqlBinaryExpression::class.java)!!
        binaryExpression.replace(newBinaryExpression)
    }
}