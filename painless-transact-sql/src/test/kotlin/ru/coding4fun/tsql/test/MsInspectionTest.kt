package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.inspection.function.string.MsSubstringInspection

class MsInspectionTest: MsFixtureTestCast() {
    fun testReplaceSubstringToLeft() {
        this.testInspections("codeInsight/inspection/substring", MsSubstringInspection())
    }
}