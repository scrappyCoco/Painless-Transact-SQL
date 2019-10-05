package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.inspection.function.string.MsSubstringInspection
import ru.coding4fun.tsql.inspection.function.string.MsTrimInspection

class MsInspectionTest: MsFixtureTestCast() {
    fun testReplaceSubstringToLeft() {
        this.testInspections("codeInsight/inspection/substring", MsSubstringInspection())
    }

    fun testLtrimRtrim() {
        this.testInspections("codeInsight/inspection/trim", MsTrimInspection())
    }
}