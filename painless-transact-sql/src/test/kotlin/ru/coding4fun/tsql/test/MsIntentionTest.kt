package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.intention.MsFlipBinaryExpressionIntention
import ru.coding4fun.tsql.intention.MsReplaceValuesToSelectIntention
import ru.coding4fun.tsql.intention.MsReverseIifIntention
import ru.coding4fun.tsql.intention.function.string.MsLeftToSubstringIntention

class MsIntentionTest: MsFixtureTestCast() {
    fun testFlipBinaryExpression() =
            testIntention("codeInsight/intention/flipBinaryExpression", MsFlipBinaryExpressionIntention())

    fun testReverseIif() =
            testIntention("codeInsight/intention/reverseIif", MsReverseIifIntention())

    fun testReplaceValuesToSelect() =
            testIntention("codeInsight/intention/replaceValuesToSelect", MsReplaceValuesToSelectIntention())

    fun testReplaceLeftToSubstring() =
            testIntention("codeInsight/intention/string", MsLeftToSubstringIntention())
}