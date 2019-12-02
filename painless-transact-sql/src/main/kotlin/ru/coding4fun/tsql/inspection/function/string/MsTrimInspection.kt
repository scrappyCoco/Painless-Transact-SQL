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

package ru.coding4fun.tsql.inspection.function.string

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.FunCallSequenceProcessor
import java.util.*

class MsTrimInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.function.string.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return TrimVisitor(manager, dialect, problems, onTheFly)
    }

    private class TrimVisitor(manager: InspectionManager,
                              dialect: SqlLanguageDialectEx,
                              problems: MutableList<ProblemDescriptor>,
                              private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val funSequences = TreeMap<String, Int>(String.CASE_INSENSITIVE_ORDER).also {
            it["LTRIM"] = 1
            it["RTRIM"] = 1
        }

        override fun visitSqlFunctionCallExpression(callExpr: SqlFunctionCallExpression?) {
            if (callExpr == null) return
            val callSequenceProcessor = FunCallSequenceProcessor(funSequences)
            PsiTreeUtil.processElements(callSequenceProcessor, callExpr)

            if (callSequenceProcessor.allFound) {
                val problemMessage = MsInspectionMessages.message("inspection.function.trim.problem")
                val problem = myManager.createProblemDescriptor(
                        callExpr.nameElement!!, callSequenceProcessor.lastCallExpr!!.nameElement!!,
                        problemMessage, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly,
                        SimplifyTrimQuickFix(callExpr, callSequenceProcessor.lastCallExpr!!))
                addDescriptor(problem)
            }

            super.visitSqlFunctionCallExpression(callExpr)
        }
    }


    private class SimplifyTrimQuickFix(
            firstCallExpression: SqlFunctionCallExpression,
            secondCallExpression: SqlFunctionCallExpression
    ) : LocalQuickFixOnPsiElement(firstCallExpression, secondCallExpression) {
        override fun getFamilyName(): String = MsInspectionMessages.message("inspection.function.trim.fix.family")
        override fun getText(): String = MsInspectionMessages.message("inspection.function.trim.fix.family")

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val callExpr = endElement as SqlFunctionCallExpression
            val paramText = callExpr.parameterList!!.expressionList[0].text
            val script = "TRIM($paramText)"
            val trimCallExpr = SqlPsiElementFactory.createExpressionFromText(script, MsDialect.INSTANCE, project, null)!!
            startElement.replace(trimCallExpr)
        }
    }
}