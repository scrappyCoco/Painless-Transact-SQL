package ru.coding4fun.tsql.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.SqlFunctionCallExpression
import java.util.*

/**
 * Checks the sequence of functions. If the sequence is right, the [execute] method will return true.
 * @param expectedFunctions Map of {function name -> arguments count}.
 * @sample {LTRIM -> 1}, {SUBSTRING -> 3}.
 */
class FunCallSequenceProcessor(private val expectedFunctions: Map<String, Int>) : PsiElementProcessor<PsiElement> {
    var allFound: Boolean = false
    var lastCallExpr: SqlFunctionCallExpression? = null

    override fun execute(callExpr: PsiElement): Boolean {
        // Check for already processed elements.
        val parentCall = PsiTreeUtil.getParentOfType(callExpr, SqlFunctionCallExpression::class.java)
        if (parentCall?.nameElement?.name != null && expectedFunctions.contains(parentCall.nameElement?.name)) return false

        val waitingFunctions = expectedFunctions.map { it.key }
                .toCollection(TreeSet(String.CASE_INSENSITIVE_ORDER))

        var currentCallExpression = callExpr as SqlFunctionCallExpression
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