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

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages

class MsRedundantDistinctInSetOperatorsInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(dialect: SqlLanguageDialectEx, manager: InspectionManager, result: MutableList<ProblemDescriptor>, onTheFly: Boolean): SqlAnnotationVisitor? {
        return RedundantDistinctVisitor(manager, dialect, result, onTheFly)
    }

    private class RedundantDistinctVisitor(
            manager: InspectionManager,
            dialect: SqlLanguageDialectEx,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val setElementTypes = arrayOf(
                SqlElementTypes.SQL_UNION,
                SqlElementTypes.SQL_EXCEPT,
                SqlElementTypes.SQL_INTERSECT
        )

        override fun visitSqlUnionExpression(unionExpression: SqlUnionExpression?) {
            if (unionExpression == null) return

            val setOperatorElements = unionExpression.children
                    .filterIsInstance<LeafPsiElement>()
                    .filter { setElementTypes.contains(it.type) || it.type == SqlElementTypes.SQL_ALL }

            for (index in 0 until setOperatorElements.size) {
                val setElement = setOperatorElements[index]
                if (!setElementTypes.contains(setElement.type)) continue
                val nextElement = setOperatorElements.elementAtOrNull(index + 1)
                // Skip for UNION ALL.
                if (nextElement.type == SqlElementTypes.SQL_ALL) continue
                val firstQuery = PsiTreeUtil.getPrevSiblingOfType(setElement, SqlQueryExpression::class.java)
                        ?: continue
                val nextQuery = PsiTreeUtil.getNextSiblingOfType(setElement, SqlQueryExpression::class.java) ?: continue
                val queries = arrayOf(firstQuery, nextQuery)

                for (query in queries) {
                    val selectClause = query.children.filterIsInstance<SqlSelectClause>().firstOrNull() ?: continue

                    val distinctElement = selectClause.options
                            .filterIsInstance<SqlSelectOption>()
                            .firstOrNull { SqlElementTypes.SQL_DISTINCT.toString().equals(it.optionName, true) }
                            ?: continue

                    val setOperator = setElement.text.toUpperCase()
                    val problemMessage = MsMessages.message("inspection.dml.redundant.distinct.keyword.in.set.operators.problem", setOperator)

                    val problem = myManager.createProblemDescriptor(
                            distinctElement,
                            problemMessage,
                            true,
                            ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                            onTheFly,
                            RemoveRedundantDistinctQuickFix(distinctElement, distinctElement, setOperator)
                    )
                    addDescriptor(problem)
                }
            }

            super.visitSqlUnionExpression(unionExpression)
        }
    }

    private class RemoveRedundantDistinctQuickFix(
            startElement: PsiElement,
            endElement: PsiElement,
            private val setOperator: String
    ) : LocalQuickFixOnPsiElement(startElement, endElement) {
        override fun getFamilyName(): String = MsMessages.message("inspection.dml.redundant.distinct.keyword.in.set.operators.fix.family")
        override fun getText(): String = MsMessages.message("inspection.dml.redundant.distinct.keyword.in.set.operators.fix.text", setOperator)

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            startElement.delete()
        }
    }
}