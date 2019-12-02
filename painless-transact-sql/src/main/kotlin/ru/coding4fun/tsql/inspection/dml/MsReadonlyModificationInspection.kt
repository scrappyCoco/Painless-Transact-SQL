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
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlReturningClauseImpl
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.getAlias
import ru.coding4fun.tsql.psi.getDmlHighlightRangeElements
import ru.coding4fun.tsql.psi.isReadonly

class MsReadonlyModificationInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return ReadonlyModificationVisitor(dialect, manager, problems, onTheFly)
    }

    private class ReadonlyModificationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlReferenceExpression(referenceExpression: SqlReferenceExpression?) {
            if (referenceExpression == null) return
            if (SqlElementTypes.SQL_VARIABLE_REFERENCE != referenceExpression.referenceElementType) return

            // CREATE PROCEDURE P @a dbo.MyType READONLY
            val tableParameterDefinition = referenceExpression.resolve() as? SqlParameterDefinition ?: return
            if (!tableParameterDefinition.isReadonly()) return

            // OUTPUT ... INTO @a
            val outputElement = PsiTreeUtil.getParentOfType(referenceExpression, SqlReturningClauseImpl::class.java)
            if (outputElement != null) {
                addDmlProblem(referenceExpression)
                return
            }

            val dmlInstruction =
                    PsiTreeUtil.getParentOfType(referenceExpression, SqlDmlInstruction::class.java) ?: return

            val dmlTarget = dmlInstruction.children.firstOrNull { it !is LeafPsiElement } ?: return

            // DELETE @a | INSERT INTO @a | MERGE @a | UPDATE @a
            val isDirectWriteAction = dmlTarget.textRange.contains(referenceExpression.textRange)
            if (isDirectWriteAction) {
                addDmlProblem(referenceExpression)
                return
            }

            // DELETE A FROM @a AS A INNER JOIN B ...
            val alias = referenceExpression.getAlias() ?: return
            val targetIdentifier = PsiTreeUtil.findChildOfType(dmlTarget, SqlIdentifier::class.java) ?: return
            if (!targetIdentifier.name.equals(alias.name, true)) return

            addDmlProblem(referenceExpression)

            super.visitSqlReferenceExpression(referenceExpression)
        }

        private fun addDmlProblem(readonlyElement: SqlReferenceExpression) {
            val parameterDefinition = readonlyElement.resolve() as SqlParameterDefinition
            val problemDescription = MsInspectionMessages.message("inspection.dml.readonly.modification.problem", parameterDefinition.name)
            val highlightElements = readonlyElement.getDmlHighlightRangeElements() ?: return
            val problem = myManager.createProblemDescriptor(
                    highlightElements.first,
                    highlightElements.second,
                    problemDescription,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    onTheFly
            )

            addDescriptor(problem)
        }
    }
}