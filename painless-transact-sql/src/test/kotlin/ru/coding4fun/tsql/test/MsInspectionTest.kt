package ru.coding4fun.tsql.test

import ru.coding4fun.tsql.inspection.dml.MsBuiltInRoutineInspection

class MsInspectionTest: MsFixtureTestCast() {
    fun testBuiltInRoutine() = testInspections("codeInsight/inspection/builtInRoutine", MsBuiltInRoutineInspection())
}