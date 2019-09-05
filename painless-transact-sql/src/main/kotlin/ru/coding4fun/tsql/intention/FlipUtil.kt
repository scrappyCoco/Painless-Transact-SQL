package ru.coding4fun.tsql.intention

import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlBinaryExpression
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type

object FlipUtil {
    private val operators = hashMapOf(
            SqlElementTypes.SQL_OP_GT to SqlElementTypes.SQL_OP_LT,
            SqlElementTypes.SQL_OP_LT to SqlElementTypes.SQL_OP_GT,
            SqlElementTypes.SQL_OP_NEQ to SqlElementTypes.SQL_OP_NEQ,
            SqlElementTypes.SQL_OP_NEQ2 to SqlElementTypes.SQL_OP_NEQ2,
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

    fun contains(elementType: IElementType): Boolean {
        return operators.containsKey(elementType)
    }

    fun reverse(project: Project, binaryExpression: SqlBinaryExpression) {
        val opSignElement = binaryExpression.opSignElement
        val newOperatorType = when (opSignElement.type!!) {
            SqlElementTypes.SQL_OP_EQ -> SqlElementTypes.SQL_OP_NEQ
            SqlElementTypes.SQL_OP_NEQ -> SqlElementTypes.SQL_OP_EQ
            SqlElementTypes.SQL_OP_NEQ2 -> SqlElementTypes.SQL_OP_EQ
            else -> operators[opSignElement.type]!!
        }
        val newOperatorElement = SqlPsiElementFactory.createLeafFromText(project, MsDialect.INSTANCE, newOperatorType.toString())
        opSignElement.replace(newOperatorElement)
    }

    fun flip(project: Project, binaryExpression: SqlBinaryExpression) {
        val newOperatorType = operators[binaryExpression.opSignElement.type!!]!!
        val swappedSql = "IF ${binaryExpression.rOperand!!.text} $newOperatorType ${binaryExpression.lOperand.text} SELECT 1"
        val newExpression = SqlPsiElementFactory.createStatementFromText(swappedSql, MsDialect.INSTANCE, project, null)
        val newBinaryExpression = PsiTreeUtil.findChildOfType(newExpression, SqlBinaryExpression::class.java)!!
        binaryExpression.replace(newBinaryExpression)
    }
}