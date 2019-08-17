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
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MssqlDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlCreateProcedureStatement
import com.intellij.sql.psi.SqlParameterDefinition
import com.intellij.sql.psi.SqlParameterList
import com.intellij.sql.psi.SqlType
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.isReadonly

class MsReadonlyParameterInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsMessages.message("inspection.ddl.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return ReadonlyParameterVisitor(dialect, manager, problems, onTheFly)
    }

    private class ReadonlyParameterVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateProcedureStatement(createProcedureStatement: SqlCreateProcedureStatement?) {
            val parameterList = PsiTreeUtil.getChildOfType(createProcedureStatement, SqlParameterList::class.java)
                    ?: return
            val parameterDefinitions = parameterList.children
                    .filter {
                        it is SqlParameterDefinition
                                && it.sqlType.category == SqlType.Category.TABLE
                                && !it.isReadonly()
                    }.map { it as SqlParameterDefinition }.toList()

            for (parameterDefinition in parameterDefinitions) {
                val problemDescription = MsMessages.getMessage("inspection.ddl.readonly.missing.problem", parameterDefinition.name)
                val parameterPointer = SmartPointerManager.createPointer(parameterDefinition)
                val problem = myManager.createProblemDescriptor(
                        parameterDefinition,
                        problemDescription,
                        true,
                        ProblemHighlightType.ERROR,
                        onTheFly,
                        AddReadonlyQuickFix(parameterPointer)
                )
                addDescriptor(problem)
            }

            super.visitSqlCreateProcedureStatement(createProcedureStatement)
        }
    }

    private class AddReadonlyQuickFix(private val parameterPointer: SmartPsiElementPointer<SqlParameterDefinition>) :
            LocalQuickFixOnPsiElement(parameterPointer.element, parameterPointer.element) {
        override fun getFamilyName(): String = MsMessages.getMessage("inspection.ddl.readonly.missing.fix.family")
        override fun getText(): String = MsMessages.getMessage("inspection.ddl.readonly.missing.fix.text", parameterPointer.element!!.name)

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val readonlyElement =
                    SqlPsiElementFactory.createLeafFromText(startElement.project, MssqlDialect.INSTANCE, "READONLY")
            val parameterDefinition = parameterPointer.element!!
            parameterDefinition.add(readonlyElement)
        }
    }
}