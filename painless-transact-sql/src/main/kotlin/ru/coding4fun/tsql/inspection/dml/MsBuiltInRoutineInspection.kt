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

package ru.coding4fun.tsql.inspection.dml

import com.intellij.codeInspection.CleanupLocalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.database.model.DasTypedObject
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import ru.coding4fun.tsql.MsInspectionMessages

class MsBuiltInRoutineInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return BuiltInFunVisitor(dialect, manager, problems, onTheFly)
    }

    private class BuiltInFunVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlFunctionCallExpression(funCallExpression: SqlFunctionCallExpression?) {
            if (funCallExpression == null) return
            if (!"STRING_SPLIT".equals(funCallExpression.nameElement?.text, true)) return
            if (funCallExpression.parameterList?.expressionList?.size ?: 0 != 2) return
            val separatorExpression = funCallExpression.parameterList!!.expressionList.last()

            fun addProblem() {
                val problemDescription = MsInspectionMessages.message("dml.built.in.routine.string.split.problem")
                val problem = myManager.createProblemDescriptor(
                        separatorExpression,
                        separatorExpression,
                        problemDescription,
                        ProblemHighlightType.WARNING,
                        onTheFly
                )
                addDescriptor(problem)
            }

            if (separatorExpression is SqlStringLiteralExpression) {
                if (separatorExpression.value?.length != 1) addProblem()
            } else if (separatorExpression is SqlReferenceExpression) {
                val delimiterVarDefinition = separatorExpression.reference.resolve()
                if (delimiterVarDefinition is DasTypedObject && delimiterVarDefinition.dataType.size != 1) addProblem()
            }

            super.visitSqlFunctionCallExpression(funCallExpression)
        }
    }
}