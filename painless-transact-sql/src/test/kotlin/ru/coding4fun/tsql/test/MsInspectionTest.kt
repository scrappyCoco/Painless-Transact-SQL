/*
 * Copyright [2020] Coding4fun
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

import ru.coding4fun.tsql.inspection.dml.MsTypeCompatibilityInspection
import ru.coding4fun.tsql.inspection.codeStyle.MsRedundantQualifierInspection
import ru.coding4fun.tsql.inspection.dml.MsDmlColumnListInspection
import ru.coding4fun.tsql.inspection.function.string.MsImplicitlyVarcharLengthInspection
import ru.coding4fun.tsql.inspection.function.string.MsStringToReplicateInspection
import ru.coding4fun.tsql.inspection.function.string.MsSubstringInspection
import ru.coding4fun.tsql.inspection.function.string.MsTrimInspection

class MsInspectionTest: MsFixtureTestCase() {
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

    fun testColumnList() {
        this.testInspections("codeInsight/inspection/columnList", MsDmlColumnListInspection())
    }

    fun testStringToReplicate() {
        this.testInspections("codeInsight/inspection/stringToReplicate", MsStringToReplicateInspection())
    }

    fun testTypeCompare() {
        this.testInspections("codeInsight/inspection/typeCompare", MsTypeCompatibilityInspection())
    }
}