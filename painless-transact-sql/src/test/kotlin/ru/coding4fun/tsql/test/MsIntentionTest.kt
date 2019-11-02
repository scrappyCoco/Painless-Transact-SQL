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

package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.intention.*
import ru.coding4fun.tsql.intention.function.MsCastToConvertIntention
import ru.coding4fun.tsql.intention.function.MsConvertToCastIntention
import ru.coding4fun.tsql.intention.function.MsReverseIifIntention
import ru.coding4fun.tsql.intention.function.string.MsLeftToSubstringIntention

class MsIntentionTest : MsFixtureTestCast() {
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

    fun testConvertToMerge() = testIntention("codeInsight/intention/convertToMerge", MsConvertToMergeIntention())

    fun testReplaceVarTableToTemp() = testIntention("codeInsight/intention/replaceVarTableToTemp", MsReplaceVarTableToTempIntention())

    fun testReplaceTempTableToVar() = testIntention("codeInsight/intention/replaceTempTableToVar", MsReplaceTempTableToVarIntention())

    fun testAddComment() = testIntention("codeInsight/intention/addComment", MsAddCommentIntention())
}