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

package ru.coding4fun.tsql.inspection

import com.intellij.codeInspection.CleanupLocalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlIdentifier
import com.intellij.sql.psi.SqlVariableDefinition
import ru.coding4fun.tsql.MsMessages

class MsVariableNameUniqueInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return VariableNameVisitor(manager, dialect, problems, onTheFly)
    }

    private class VariableNameVisitor(manager: InspectionManager,
                                      dialect: SqlLanguageDialectEx,
                                      problems: MutableList<ProblemDescriptor>,
                                      private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val variables = HashSet<String>()

        override fun visitSqlVariableDefinition(variableDefinition: SqlVariableDefinition?) {
            val sqlIdentifier = PsiTreeUtil.findChildOfType(variableDefinition, SqlIdentifier::class.java) ?: return
            val varName = sqlIdentifier.name.toLowerCase()
            if (!variables.contains(varName)) {
                variables.add(varName)
            } else {
                val problemMessage = MsMessages.message("inspection.dml.variable.name.unique.problem", varName)

                val problem = myManager.createProblemDescriptor(
                        sqlIdentifier,
                        problemMessage,
                        true,
                        ProblemHighlightType.WARNING,
                        onTheFly
                )
                addDescriptor(problem)
            }
        }
    }
}