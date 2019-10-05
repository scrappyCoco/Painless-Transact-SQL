package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.intention.*
import ru.coding4fun.tsql.intention.function.MsCastToConvertIntention
import ru.coding4fun.tsql.intention.function.MsConvertToCastIntention
import ru.coding4fun.tsql.intention.function.MsReverseIifIntention
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

    fun testCastToConvert() =
            testIntention("codeInsight/intention/castToConvert", MsCastToConvertIntention())

    fun testConvertToCast() =
            testIntention("codeInsight/intention/convertToCast", MsConvertToCastIntention())
}