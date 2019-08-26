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

package ru.coding4fun.tsql.dataSource

import com.intellij.database.dialects.mssql.MssqlDialect
import com.intellij.database.model.DasObject
import com.intellij.sql.psi.SqlCreateStatement
import com.intellij.sql.psi.SqlReferenceExpression
import ru.coding4fun.tsql.psi.getObjectReference


object PathPartManager {
    private val pathRegex = Regex("^/(?<root>[^/]+)/(?<dbSource>[^/]+)/(?<dbFolder>[^/]+)/(?<db>[^/]+)/(?<schemaFolder>[^/]+)/(?<schema>[^/]+)/(?<tableFolder>[^/]+)/(?<table>[^/]+)([.]sql|/.+)$")

    fun getReferenceFromFilePath(createStatement: SqlCreateStatement): SqlReferenceExpression? {
        val match = pathRegex.matchEntire(createStatement.containingFile.virtualFile.path) ?: return null
        val db = match.groups["db"]!!.value
        val schema = match.groups["schema"]!!.value
        val table = match.groups["table"]!!.value

        return getObjectReference("[$db].[$schema].[$table]", createStatement.project)
    }

    fun areSame(dasObject: DasObject, sqlReferenceExpression: SqlReferenceExpression): Boolean {
        val statementTextSb = StringBuilder()
        var currentDasObject: DasObject? = dasObject
        var iteration = 0
        while (currentDasObject != null && ++iteration <= 3) {
            if (currentDasObject.name.isBlank()) break
            var statementName = MssqlDialect.INSTANCE.quoteIdentifier(currentDasObject.name, true, false)
            if (statementTextSb.isNotEmpty()) {
                statementName += "."
            }
            statementTextSb.insert(0, statementName)
            currentDasObject = currentDasObject.dasParent ?: return false
        }

        return sqlReferenceExpression.text.endsWith(statementTextSb, true)
    }
}




