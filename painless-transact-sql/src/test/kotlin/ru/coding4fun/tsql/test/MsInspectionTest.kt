package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.inspection.codeStyle.MsRedundantQualifierInspection
import ru.coding4fun.tsql.inspection.function.string.MsImplicitlyVarcharLengthInspection
import ru.coding4fun.tsql.inspection.function.string.MsSubstringInspection
import ru.coding4fun.tsql.inspection.function.string.MsTrimInspection

class MsInspectionTest: MsFixtureTestCast() {
    fun testReplaceSubstringToLeft() {
        this.testInspections("codeInsight/inspection/substring", MsSubstringInspection())
    }

    fun testLtrimRtrim() {
        this.testInspections("codeInsight/inspection/trim", MsTrimInspection())
    }

    fun testRedundantQualifier() {
        this.testInspections("codeInsight/inspection/redundantQualifier", MsRedundantQualifierInspection())
    }

    fun testVarcharLength() {
        this.testInspections("codeInsight/inspection/varcharLength", MsImplicitlyVarcharLengthInspection())
    }
}