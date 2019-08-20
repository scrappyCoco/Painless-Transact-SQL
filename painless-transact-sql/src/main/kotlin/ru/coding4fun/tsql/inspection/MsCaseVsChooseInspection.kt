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

import com.intellij.codeInspection.*
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.firstNotEmpty
import ru.coding4fun.tsql.psi.isSimple
import ru.coding4fun.tsql.psi.split
import java.util.*
import javax.swing.JComponent

class MsCaseVsChooseInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    @Suppress("MemberVisibilityCanBePrivate")
    var preferCaseOverChoose = false

    override fun createOptionsPanel(): JComponent? {
        val panel = MultipleCheckboxOptionsPanel(this)
        panel.addCheckbox(MsMessages.message("inspection.code.style.case.vs.choose.option"), "preferCaseOverChoose")
        return panel
    }

    override fun getGroupDisplayName(): String = MsMessages.message("inspection.code.style.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return if (preferCaseOverChoose) {
            ChooseToCaseVisitor(manager, dialect, problems, onTheFly)
        } else {
            CaseToChooseVisitor(manager, dialect, problems, onTheFly)
        }
    }

    private class CaseToChooseVisitor(manager: InspectionManager,
                                      dialect: SqlLanguageDialectEx,
                                      problems: MutableList<ProblemDescriptor>,
                                      private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCaseExpression(caseExpression: SqlCaseExpression?) {
            if (caseExpression == null) return

            val isSimpleCase = caseExpression.isSimple() ?: return
            val sequenceValues: Pair<SqlReferenceExpression, List<String>> = if (isSimpleCase) {
                getSimpleSequenceValues(caseExpression)
            } else {
                getSearchSequenceValues(caseExpression)
            } ?: return


            val problemMessage = MsMessages.message("inspection.code.style.case.vs.choose.problem.case.to.choose")
            val caseKeyword = caseExpression.children.firstNotEmpty()
            val problem = myManager.createProblemDescriptor(
                    caseKeyword,
                    problemMessage,
                    true,
                    ProblemHighlightType.WEAK_WARNING,
                    onTheFly,
                    ReplaceCaseToChooseQuickFix(caseKeyword, sequenceValues.first.text, sequenceValues.second)
            )
            addDescriptor(problem)

            super.visitSqlCaseExpression(caseExpression)
        }

        private fun getSearchSequenceValues(caseExpression: SqlCaseExpression): Pair<SqlReferenceExpression, List<String>>? {
            val result = ArrayList<Triple<SqlReferenceExpression, Int, String>>()
            val reference = caseExpression.children.firstOrNull { it is SqlReferenceExpression }
                    as? SqlReferenceExpression ?: return null

            for (clause in caseExpression.branches) {
                val sqlWhenThenClause = clause as? SqlWhenThenClause ?: return null
                val literalExpression = sqlWhenThenClause.whenClause?.expression as? SqlLiteralExpression
                        ?: return null
                if (literalExpression.type != SqlElementTypes.SQL_NUMERIC_LITERAL) return null
                val position = Integer.parseInt(literalExpression.text)
                val thenText = sqlWhenThenClause.thenClause?.body?.firstOrNull()?.text ?: return null
                result.add(Triple(reference, position, thenText))
            }

            return checkSequence(result)
        }

        private fun getSimpleSequenceValues(caseExpression: SqlCaseExpression): Pair<SqlReferenceExpression, List<String>>? {
            val result = ArrayList<Triple<SqlReferenceExpression, Int, String>>()

            for (clause in caseExpression.branches) {
                val sqlWhenThenClause = clause as? SqlWhenThenClause ?: return null
                val sqlBinaryExpression =
                        sqlWhenThenClause.whenClause?.expression as? SqlBinaryExpression ?: return null

                val referenceAndIntValue = sqlBinaryExpression.split() ?: return null
                result.add(Triple(referenceAndIntValue.first,
                        referenceAndIntValue.second,
                        clause.thenClause?.body?.firstOrNull()?.text ?: return null
                ))
            }

            return checkSequence(result)
        }

        private fun checkSequence(cases: ArrayList<Triple<SqlReferenceExpression, Int, String>>): Pair<SqlReferenceExpression, List<String>>? {
            cases.sortBy { s -> s.second }
            var hasSequence = true
            var reference: SqlReferenceExpression? = null
            for (index in 0 until cases.size) {
                val case = cases[index]
                if (index == 0) reference = case.first
                hasSequence = case.second == index + 1 && reference!!.resolve() == case.first.resolve()
                if (!hasSequence) break
            }

            return if (hasSequence && reference != null) Pair(reference, cases.map { it.third }) else null
        }
    }

    private class ChooseToCaseVisitor(manager: InspectionManager,
                                      dialect: SqlLanguageDialectEx,
                                      problems: MutableList<ProblemDescriptor>,
                                      private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlFunctionCallExpression(functionCallExpression: SqlFunctionCallExpression?) {
            if (functionCallExpression == null) return
            if (!"CHOOSE".equals(functionCallExpression.nameElement?.name, true)) return

            val problemMessage = MsMessages.message("inspection.code.style.case.vs.choose.problem.choose.to.case")
            val caseKeyword = functionCallExpression.children.firstNotEmpty()
            val problem = myManager.createProblemDescriptor(
                    caseKeyword,
                    problemMessage,
                    true,
                    ProblemHighlightType.WEAK_WARNING,
                    onTheFly
            )
            addDescriptor(problem)

            super.visitSqlFunctionCallExpression(functionCallExpression)
        }
    }

    private class ReplaceCaseToChooseQuickFix(caseKeyword: PsiElement,
                                              private val referenceText: String,
                                              private val values: List<String>
    ) : LocalQuickFixOnPsiElement(caseKeyword, caseKeyword) {
        override fun getFamilyName(): String = MsMessages.message("inspection.code.style.case.vs.choose.fix.case.to.choose")
        override fun getText(): String = MsMessages.message("inspection.code.style.case.vs.choose.fix.case.to.choose")

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val sqlCaseExpression = PsiTreeUtil.getParentOfType(startElement, SqlCaseExpression::class.java)!!
            val scriptBuilder = StringBuilder().append("CHOOSE(", referenceText)
            for (value in values) {
                scriptBuilder.append(",\n  ", value)
            }
            scriptBuilder.append(")")
            val chooseExpression = SqlPsiElementFactory.createExpressionFromText(scriptBuilder.toString(), MssqlDialect.INSTANCE, project, null)!!
            sqlCaseExpression.replace(chooseExpression)
        }
    }
}