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

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.database.model.ArgumentDirection
import com.intellij.database.model.DasRoutine
import com.intellij.database.model.DasTypedObject
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlNamedParameterValueExpression
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.PainlessResourceUtil
import ru.coding4fun.tsql.psi.getParams
import kotlin.math.min

class MsTypeCompareInspection : SqlInspectionBase() {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("type.compatibility.name")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return TypeCompareVisitor(manager, dialect, problems, onTheFly)
    }

    private class TypeCompareVisitor(manager: InspectionManager,
                                     dialect: SqlLanguageDialectEx,
                                     problems: MutableList<ProblemDescriptor>,
                                     private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlBinaryExpression(binaryExpression: SqlBinaryExpression?) {
            var mustStop = true
            while (true) {
                if (binaryExpression == null || binaryExpression.rOperand == null) break
                if (notAvailableOperators.contains(binaryExpression.opSignElement.elementType)) break
                mustStop = false
                break
            }
            if (mustStop) {
                super.visitSqlBinaryExpression(binaryExpression)
                return
            }

            val lTypeName: String? = getTypeName(binaryExpression!!.lOperand)
            val rTypeName: String? = getTypeName(binaryExpression.rOperand!!)
            if (lTypeName == null || rTypeName == null) {
                super.visitSqlBinaryExpression(binaryExpression)
                return
            }

            val matchResult = if (hasRightOrder(lTypeName, rTypeName))
                match(lTypeName, rTypeName) else match(rTypeName, lTypeName)
            addProblem(matchResult, binaryExpression.opSignElement)

            super.visitSqlBinaryExpression(binaryExpression)
        }

        override fun visitSqlVariableDefinition(varDef: SqlVariableDefinition?) {
            if (varDef?.initializer == null) {
                super.visitSqlVariableDefinition(varDef)
                return
            }

            val initializerTypeName = getTypeName(varDef.initializer!!)
            val defTypeName = getTypeName(varDef)
            val matchResult = match(initializerTypeName, defTypeName)
            addProblem(matchResult, varDef)
        }

        override fun visitSqlSetAssignment(setAssignment: SqlSetAssignment?) {
            if (setAssignment?.rValue == null) {
                super.visitSqlSetAssignment(setAssignment)
                return
            }

            val lTypeName: String? = getTypeName(setAssignment.lValue)
            val rTypeName: String? = getTypeName(setAssignment.rValue!!)
            val matchResult = match(rTypeName, lTypeName)
            addProblem(matchResult, setAssignment)

            super.visitSqlSetAssignment(setAssignment)
        }

        override fun visitSqlFunctionCallExpression(funCallExpr: SqlFunctionCallExpression?) {
            val dasRoutine = (funCallExpr?.callableExpression as? SqlReferenceExpression)?.resolve() as? DasRoutine
            if (dasRoutine == null) {
                super.visitSqlFunctionCallExpression(funCallExpr)
                return
            }

            val defArgs = dasRoutine.arguments.filter { paramTypes.contains(it.argumentDirection) }.toList()
            val actualArgs = funCallExpr.getParams()
            val commonParamsCount = min(actualArgs.count(), defArgs.count())

            // Named arguments mode is handled by BinaryExpression.
            // There handled only positioned argument.
            if (dasRoutine.routineKind == DasRoutine.Kind.FUNCTION ||
                    dasRoutine.routineKind == DasRoutine.Kind.PROCEDURE && !isNamedArgMode(actualArgs)) {
                for (p in 0 until commonParamsCount) {
                    val targetTypeName = getTypeName(defArgs[p])
                    val currentArg = actualArgs[p]
                    val sourceTypeName = getTypeName(currentArg)
                    val matchResult = match(sourceTypeName, targetTypeName)
                    addProblem(matchResult, currentArg)
                }
            }

            super.visitSqlFunctionCallExpression(funCallExpr)
        }

        override fun visitSqlUnionExpression(unionExpr: SqlUnionExpression?) {
            val queryExpressions = unionExpr?.operands?.filterIsInstance<SqlQueryExpression>() ?: emptyList()
            if (!queryExpressions.any()) {
                super.visitSqlUnionExpression(unionExpr)
                return
            }

            val rows = arrayListOf<List<Pair<String?, PsiElement>>>()
            val maxTypeNames = arrayListOf<String?>()

            // Collect max column type names.
            for (queryExpression in queryExpressions) {
                val columnsOfCurrentRow = arrayListOf<Pair<String?, PsiElement>>()

                queryExpression.selectClause.expressions.forEachIndexed { column, expression ->
                    val typeName = getTypeName(expression)
                    columnsOfCurrentRow.add(typeName to expression)
                    var maxTypeName: String? = null
                    if (maxTypeNames.size == column) maxTypeNames.add(null) else maxTypeName = maxTypeNames[column]
                    if (typeName != null && maxTypeName != null && !hasRightOrder(typeName, maxTypeName)
                            || maxTypeName == null && typeName != null
                    ) {
                        maxTypeName = typeName
                        maxTypeNames[column] = maxTypeName
                    }
                }
                rows.add(columnsOfCurrentRow)
            }

            for (columns in rows) {
                columns.forEachIndexed { columnNumber, column ->
                    val targetTypeName = maxTypeNames[columnNumber]
                    val actualTypeName = column.first
                    val highlightElement = column.second
                    if (targetTypeName != null && actualTypeName != null) {
                        val match = match(actualTypeName, targetTypeName)
                        addProblem(match, highlightElement)
                    }
                }
            }

            super.visitSqlUnionExpression(unionExpr)
        }

        override fun visitSqlInsertStatement(insertStatement: SqlInsertStatement?) {
            var targetColumnList: MutableList<SqlReferenceExpression>? = null
            var valuesList: List<SqlExpression>? = null
            while (true) {
                if (insertStatement == null) break
                val insertDmlInstruction = PsiTreeUtil.getChildOfType(insertStatement, SqlInsertDmlInstruction::class.java)
                        ?: break

                targetColumnList = insertDmlInstruction.columnsList?.columnsReferenceList?.referenceList
                        ?: break

                valuesList = (insertDmlInstruction.valuesExpression?.expressions?.firstOrNull() as? SqlParenthesizedExpression)?.expressionList
                        ?: insertDmlInstruction.queryExpression?.selectClause?.expressions?.toList()
                                ?: break

                break
            }

            if (targetColumnList != null && valuesList != null && targetColumnList.size == valuesList.size) {
                for ((index, targetColumn) in targetColumnList.withIndex()) {
                    val value = valuesList[index]
                    val sourceType = getTypeName(value)
                    val targetType = getTypeName(targetColumn)
                    val matchResult = match(targetType, sourceType)
                    addProblem(matchResult, value)
                }
            }


            super.visitSqlInsertStatement(insertStatement)
        }

        private fun isNamedArgMode(args: List<PsiElement>): Boolean {
            return args.filterIsInstance<SqlNamedParameterValueExpression>().size == args.size
        }

        private fun match(lType: String?, rType: String?): MatchResult {
            if (lType == null || rType == null) return MatchResult(ConvertType.Unknown)
            val convertPair = ConvertPair(lType, rType)
            val convertType = convertMap[convertPair] ?: ConvertType.Unknown

            // int is incompatible with date
            val message: String? = when (convertType) {
                ConvertType.Explicit -> "Explicit type convert required from ${lType.toUpperCase()} to ${rType.toUpperCase()}"
                ConvertType.Implicit -> "An implicit convert from ${lType.toUpperCase()} to ${rType.toUpperCase()}"
                ConvertType.Restrict -> "${lType.toUpperCase()} is incompatible with ${rType.toUpperCase()}"
                else -> null
            }
            return MatchResult(convertType, convertPair, message)
        }

        private fun addProblem(matchResult: MatchResult, highlightElement: PsiElement) {
            if (matchResult.message != null) {
                val highlight = when (matchResult.convertType) {
                    ConvertType.Restrict -> ProblemHighlightType.ERROR
                    ConvertType.Explicit -> ProblemHighlightType.WARNING
                    else -> ProblemHighlightType.INFORMATION
                }

                val problem = myManager.createProblemDescriptor(
                        highlightElement,
                        matchResult.message,
                        true,
                        highlight,
                        onTheFly,
                        null
                )
                addDescriptor(problem)
            }
        }

        private fun getTypeName(element: Any?): String? {
            if (element == null) return null
            if (element is SqlReferenceExpression) {
                val resolves = element.multiResolve(false)
                val typedObject = resolves.mapNotNull { it.element as? DasTypedObject }.firstOrNull()
                if (typedObject != null) return resolveTypeName(typedObject)

                // DECLARE @a INT = 1; SELECT A = @a;
                val asExpr = resolves.mapNotNull { it.element as? SqlAsExpression }.firstOrNull()
                if (asExpr != null) return getTypeName(asExpr.expression)
            }
            if (element is SqlAsExpression) return getTypeName(element.expression)
            if (element is DasTypedObject) return resolveTypeName(element)
            if (element is SqlStringLiteralExpression) return "varchar"
            if (element is SqlLiteralExpression) {
                if (element.text.equals("NULL", true)) return null
                if (element.text.startsWith("0x")) return "varbinary"
                if (element.text.contains("E", true)) return "float"
                if (element.text.contains("$")) return "money"
                if (element.text.contains(".")) return "decimal"
                return "int"
            }
            if (element is SqlFunctionCallExpression) {
                val dasRoutine = (element.callableExpression as? SqlReferenceExpression)?.resolve() as? DasRoutine
                        ?: return null
                return getTypeName(dasRoutine.returnArgument)
            }
            return null
        }

        private fun resolveTypeName(typedObject: DasTypedObject?) = typedObject?.dataType?.typeName?.toLowerCase()

        private fun hasRightOrder(lType: String, rType: String): Boolean {
            // https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-type-precedence-transact-sql?view=sql-server-ver15
            val lPriority = priorities[lType] ?: priorities.size // size mean user defined type
            val rPriority = priorities[rType] ?: priorities.size
            return lPriority <= rPriority
        }

        private class MatchResult(val convertType: ConvertType, val convertPair: ConvertPair? = null, val message: String? = null)
    }

    private data class ConvertPair(val fromType: String, val toType: String)

    private enum class ConvertType {
        Explicit,
        Implicit,
        Restrict,
        Same,
        Unknown
    }

    companion object {
        private val convertMap: Map<ConvertPair, ConvertType>
        private val priorities: Map<String, Int>
        private val notAvailableOperators = arrayOf(SqlElementTypes.SQL_AND, SqlElementTypes.SQL_OR)
        private val paramTypes = arrayOf(ArgumentDirection.IN, ArgumentDirection.OUT, ArgumentDirection.INOUT)

        init {
            convertMap = hashMapOf()
            priorities = hashMapOf()

            val separator = ";"
            val rowOffset = 2

            val mapDescription = PainlessResourceUtil.readContent("painless/convert.txt")
            val stringReader = mapDescription.reader()
            val lines = stringReader.readLines()
            val typeNames = lines[1].split(separator)

            for (row in rowOffset until lines.size) {
                val fromType = typeNames[row - rowOffset]
                val line = lines[row]
                val values = line.split(separator)
                for ((toIndex, toTypeName) in typeNames.withIndex()) {
                    convertMap[ConvertPair(fromType, toTypeName)] = when (values[toIndex]) {
                        "e" -> ConvertType.Explicit
                        "i" -> ConvertType.Implicit
                        "r" -> ConvertType.Restrict
                        "s" -> ConvertType.Same
                        else -> ConvertType.Unknown
                    }
                }
            }

            typeNames.forEachIndexed { index, typeName -> priorities[typeName] = index }
        }
    }
}