package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.intention.MsFlipBinaryExpressionIntention
import ru.coding4fun.tsql.intention.MsReverseIifIntention

class MsIntentionTest: MsFixtureTestCast() {
    fun testFlipBinaryExpression() =
            testIntention("codeInsight/intention/flipBinaryExpression", MsFlipBinaryExpressionIntention())

    fun testReverseIif() =
            testIntention("codeInsight/intention/reverseIif", MsReverseIifIntention())
}