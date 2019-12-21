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
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.psi.MssqlVariableDefinitionImpl
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlColumnDefinition
import com.intellij.sql.psi.SqlElementTypes
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.nullIf
import ru.coding4fun.tsql.psi.getNextNotEmptyLeaf
import ru.coding4fun.tsql.psi.getPrevNotEmptyLeaf

class MsUnusedColumnInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return MissingColumnVisitor(dialect, manager, problems, onTheFly)
    }

    private class MissingColumnVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlColumnDefinition(columnDefinition: SqlColumnDefinition?) {
            if (columnDefinition == null) return
            if (getTableName(columnDefinition) == null) return
            val hasAnyRef = ReferencesSearch.search(columnDefinition).anyMatch {
                it.element != columnDefinition
            }
            if (hasAnyRef) return
            val problemDescription = MsInspectionMessages.message("unused.column.problem", columnDefinition.name)
            val problem = myManager.createProblemDescriptor(
                    columnDefinition,
                    columnDefinition,
                    problemDescription,
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    onTheFly,
                    RemoveColumnDefinitionQuickFix(SmartPointerManager.createPointer(columnDefinition))
            )
            addDescriptor(problem)
        }

        private fun getTableName(columnDefinition: SqlColumnDefinition): String? {
            var tableName = columnDefinition.tableName
            if (tableName == "") {
                // Getting a name from table variable.
                val tableVarDef = PsiTreeUtil.getParentOfType(columnDefinition, MssqlVariableDefinitionImpl::class.java)
                        ?: return null
                tableName = tableVarDef.name
            }
            val firstTableChar = tableName?.firstOrNull() ?: return null
            if (firstTableChar != '#' && firstTableChar != '@') return null
            return tableName
        }

        private class RemoveColumnDefinitionQuickFix(val columnDefinition: SmartPsiElementPointer<SqlColumnDefinition>) :
                LocalQuickFixOnPsiElement(columnDefinition.element, columnDefinition.element) {
            override fun getFamilyName(): String = MsInspectionMessages.message("unused.column.fix.family")

            override fun getText(): String {
                val columnName = columnDefinition.element?.name ?: return familyName
                return MsInspectionMessages.message("unused.column.fix.text", columnName)
            }

            override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
                val commaLeaf = startElement.getPrevNotEmptyLeaf().nullIf { it.elementType != SqlElementTypes.SQL_COMMA }
                        ?: startElement.getNextNotEmptyLeaf().nullIf { it.elementType != SqlElementTypes.SQL_COMMA }
                commaLeaf?.delete()
                startElement.delete()
            }
        }
    }

}