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

import com.intellij.codeInsight.completion.CompletionType
import org.junit.Assert

class MsCompletionTest: MsFixtureTestCase() {
    fun testUnionAll() {
        myFixture.configureByFile("codeInsight/completion/basic-insert-template.sql")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings!!
        val resultRegex = Regex(
                """^SELECT\s+Id\s+=\s+NULL,\s+Name\s+=\s+NULL$""", RegexOption.MULTILINE)
        Assert.assertTrue(lookupElementStrings.any { resultRegex.matches(it) })
    }
}