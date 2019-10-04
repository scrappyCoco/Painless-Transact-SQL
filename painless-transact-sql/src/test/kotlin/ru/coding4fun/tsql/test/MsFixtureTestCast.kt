package ru.coding4fun.tsql.test

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.sql.dialects.SqlDialectMappings
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.impl.EmptyModuleFixtureBuilderImpl
import com.intellij.testFramework.fixtures.impl.ModuleFixtureImpl
import java.io.File

abstract class MsFixtureTestCast: CodeInsightFixtureTestCase<EmptyModuleFixtureBuilderImpl<ModuleFixtureImpl>>() {
    override fun setUp() {
        super.setUp()
        SqlDialectMappings.getInstance(myFixture.project).setMapping(null, MsDialect.INSTANCE)
        myFixture.testDataPath = "/Users/artemkorsunov/IdeaProjects/Painless-Transact-SQL/painless-transact-sql/testData"
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
            for (quickFix in allQuickFixes) {
                WriteCommandAction.runWriteCommandAction(myFixture.project) {
                    quickFix.invoke(myFixture.project, editor, myFixture.file)
                }
            }

            this.myFixture.checkResultByFile(inspectionPath + "/$target/" + file.name, true)
        }
    }

    protected fun testIntention(intentionPath: String, intention: IntentionAction) {
        val srcDir = File(myFixture.testDataPath, "$intentionPath/$src")

        for (file in srcDir.listFiles()) {
            this.myFixture.configureByFile(intentionPath + "/$src/" + file.name)

            val foundIntention = myFixture.findSingleIntention(intention.text)
            WriteCommandAction.runWriteCommandAction(myFixture.project) {
                foundIntention.invoke(myFixture.project, editor, myFixture.file)
            }
        }
    }
}