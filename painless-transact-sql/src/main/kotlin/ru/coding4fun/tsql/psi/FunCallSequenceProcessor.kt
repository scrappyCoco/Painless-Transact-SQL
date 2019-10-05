package ru.coding4fun.tsql.psi

import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.SqlFunctionCallExpression
import java.util.*

class FunCallSequenceProcessor(private val expectedFunctions: Map<String, Int>) : PsiElementProcessor<SqlFunctionCallExpression> {
    var allFound: Boolean = false
    var lastCallExpr: SqlFunctionCallExpression? = null

    override fun execute(callExpr: SqlFunctionCallExpression): Boolean {
        // Check for already processed elements.
        val parentCall = PsiTreeUtil.getParentOfType(callExpr, SqlFunctionCallExpression::class.java)
        if (parentCall != null && expectedFunctions.contains(parentCall.nameElement?.name)) return false

        val waitingFunctions = expectedFunctions.map { it.key }.toCollection(TreeSet(String.CASE_INSENSITIVE_ORDER))

        var currentCallExpression = callExpr
        while (true) {
            val currentFunName: String? = currentCallExpression.nameElement?.name ?: break
            val expectedFunParamCount = expectedFunctions[currentFunName] ?: break
            waitingFunctions.remove(currentFunName)
            val paramList = currentCallExpression.parameterList?.expressionList ?: break
            if (paramList.size != expectedFunParamCount) break
            lastCallExpr = currentCallExpression
            currentCallExpression = paramList[0] as? SqlFunctionCallExpression ?: break
        }
        allFound = waitingFunctions.size == 0

        return false
    }
}