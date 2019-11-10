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

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.sql.dialects.SqlDialectMappings
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.impl.EmptyModuleFixtureBuilderImpl
import com.intellij.testFramework.fixtures.impl.ModuleFixtureImpl
import java.io.File

abstract class MsFixtureTestCast : CodeInsightFixtureTestCase<EmptyModuleFixtureBuilderImpl<ModuleFixtureImpl>>() {
    override fun setUp() {
        super.setUp()
        SqlDialectMappings.getInstance(myFixture.project).setMapping(null, MsDialect.INSTANCE)
        myFixture.testDataPath = "/Users/artemkorsunov/IdeaProjects/Painless-Transact-SQL/painless-transact-sql/testData/"
    }

    private val src = "src"
    private val target = "target"

    protected fun testInspections(inspectionPath: String, inspection: LocalInspectionTool) {
        myFixture.testInspection(inspectionPath, LocalInspectionToolWrapper(inspection))

        val srcDir = File(myFixture.testDataPath, "$inspectionPath/$src")

        for (file in srcDir.listFiles()) {
            this.myFixture.configureByFile(inspectionPath + "/$src/" + file.name)
            this.myFixture.enableInspections(inspection)

            val allQuickFixes = this.myFixture.getAllQuickFixes()
            if (file.name.startsWith("ignore")) {
                val hasIntention = allQuickFixes.any()
                UsefulTestCase.assertFalse(hasIntention)
            } else {
                for (quickFix in allQuickFixes) {
                    WriteCommandAction.runWriteCommandAction(myFixture.project) {
                        quickFix.invoke(myFixture.project, editor, myFixture.file)
                    }
                }

                this.myFixture.checkResultByFile(inspectionPath + "/$target/" + file.name, true)
            }
        }
    }

    protected fun testIntention(intentionPath: String, intention: IntentionAction) {
        val srcDir = File(myFixture.testDataPath, "$intentionPath/$src")

        for (file in srcDir.listFiles()) {
            this.myFixture.configureByFile(intentionPath + "/$src/" + file.name)

            if (file.name.startsWith("ignore")) {
                val hasIntention = myFixture.filterAvailableIntentions(intention.text).any()
                UsefulTestCase.assertFalse(hasIntention)
            } else {
                val foundIntention = myFixture.findSingleIntention(intention.text)
                WriteCommandAction.runWriteCommandAction(myFixture.project) {
                    foundIntention.invoke(myFixture.project, editor, myFixture.file)
                    this.myFixture.checkResultByFile(intentionPath + "/$target/" + file.name, true)
                }
            }
        }
    }
}