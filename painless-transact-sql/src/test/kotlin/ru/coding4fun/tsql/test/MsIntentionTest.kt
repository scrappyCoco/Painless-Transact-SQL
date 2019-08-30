package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.intention.MsFlipBinaryExpressionIntention

class MsIntentionTest: MsFixtureTestCast() {
    fun testFlipBinaryExpression() =
            testIntention("codeInsight/intention/flipBinaryExpression", MsFlipBinaryExpressionIntention())
}