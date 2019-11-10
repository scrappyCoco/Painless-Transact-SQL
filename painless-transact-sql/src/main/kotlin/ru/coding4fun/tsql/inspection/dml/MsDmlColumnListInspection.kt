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

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.dialects.mssql.MsTypes
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.intentions.SqlExpandColumnListIntention
import com.intellij.sql.psi.*
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.findFirstTableReference
import ru.coding4fun.tsql.psi.findLeaf
import ru.coding4fun.tsql.psi.getNextNotEmptyLeaf
import ru.coding4fun.tsql.psi.getPrevNotEmptyLeaf

class MsDmlColumnListInspection : SqlInspectionBase() {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return ColumnListVisitor(dialect, manager, problems, onTheFly)
    }

    private class ColumnListVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlDmlStatement(dmlStatement: SqlDmlStatement?) {
            if (dmlStatement == null) return

            // ... INTO ... !()
            val tableColumnLists = PsiTreeUtil.findChildrenOfType(dmlStatement, SqlTableColumnsList::class.java)
            for (tableColumnList in tableColumnLists) {
                if (tableColumnList.columnsReferenceList?.referenceList?.size ?: 0 == 0) addProblem(tableColumnList)
            }

            // MERGE ... INSERT !() VALUES(...)
            val firstDmlElement = PsiTreeUtil.getDeepestVisibleFirst(dmlStatement)
            if (firstDmlElement.elementType == SqlElementTypes.SQL_MERGE) {
                val dmlInstruction = PsiTreeUtil.findChildOfType(dmlStatement, SqlInsertDmlInstruction::class.java)
                if (dmlInstruction != null && dmlInstruction.targetColumnReferences.size == 0) {
                    val insertKeyword = PsiTreeUtil.getDeepestVisibleFirst(dmlInstruction)
                    if (insertKeyword != null) addProblem(insertKeyword)
                }
            }

            // OUTPUT * INTO ...
            // INSERT INTO ... SELECT *
            val asteriskElement = dmlStatement.findLeaf(SqlElementTypes.SQL_ASTERISK)
            if (asteriskElement != null) {
                val prevLeaf = asteriskElement.getPrevNotEmptyLeaf()
                val nextLeaf = asteriskElement.getNextNotEmptyLeaf()
                if (MsTypes.MSSQL_OUTPUT == prevLeaf?.elementType ||
                        SqlElementTypes.SQL_SELECT == prevLeaf?.elementType) {
                    val parentExpr = PsiTreeUtil.getParentOfType(asteriskElement, SqlParenthesizedExpression::class.java)
                    // Is not sub-query.
                    if (parentExpr == null) addProblem(asteriskElement)
                }
            }
        }

        private fun addProblem(element: PsiElement) {
            val problemDescription = MsInspectionMessages.message("implicit.column.list.problem")
            val problem = myManager.createProblemDescriptor(
                    element,
                    element,
                    problemDescription,
                    ProblemHighlightType.WEAK_WARNING,
                    onTheFly,
                    ExpandColumnList(SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element))
            )
            addDescriptor(problem)
        }
    }

    private class ExpandColumnList(elementPointer: SmartPsiElementPointer<PsiElement>) :
            LocalQuickFixOnPsiElement(elementPointer.element, elementPointer.element) {
        override fun getFamilyName(): String = MsInspectionMessages.message("implicit.column.list.fix.family")
        override fun getText(): String = familyName

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {

            if (startElement is SqlTableColumnsList) {
                val refExpr = PsiTreeUtil.getChildOfType(startElement, SqlReferenceExpression::class.java)!!
                appendTargetColumnList(startElement, refExpr, project)
            } else if (startElement.elementType == SqlElementTypes.SQL_INSERT) { // In MERGE
                val dmlInstr = PsiTreeUtil.getParentOfType(startElement, SqlDmlInstruction::class.java)!!
                val targetExpr = dmlInstr.targetExpression
                appendTargetColumnList(startElement, targetExpr.findFirstTableReference()!!, project)
            } else {
                val refExpr = PsiTreeUtil.getParentOfType(startElement, SqlReferenceExpression::class.java)!!
                val columnListText = getColumnList(refExpr, project)!!
                if (startElement.elementType == SqlElementTypes.SQL_ASTERISK) {
                    val tempText = "SELECT $columnListText FROM t"
                    val tempExpr = SqlPsiElementFactory.createQueryExpressionFromText(tempText, MsDialect.INSTANCE, project)!!
                    val expressions: List<SqlExpression> = tempExpr.selectClause.expressions
                    refExpr.parent.addRangeBefore(expressions.first() as PsiElement, expressions.last() as PsiElement, refExpr)
                    refExpr.delete()
                }
            }

            //SqlExpandColumnListIntention().invoke(project, null, startElement)
        }

        private fun appendTargetColumnList(elementToInsert: PsiElement, targetTableReference: SqlReferenceExpression, project: Project) {
            val columnListText = getColumnList(targetTableReference, project) ?: return
            val tempText = "INSERT INTO t ($columnListText) VALUES(1)"
            val tempExpr = SqlPsiElementFactory.createStatementFromText(tempText, MsDialect.INSTANCE, project, null)!!
            val tableColList = PsiTreeUtil.findChildOfAnyType(tempExpr, SqlTableColumnsList::class.java) ?: return
            if (elementToInsert is LeafPsiElement) {
                elementToInsert.parent.addAfter(tableColList.columnsReferenceList, elementToInsert)
            } else {
                elementToInsert.add(tableColList.columnsReferenceList)
            }

        }

        private fun getColumnList(refExpr: SqlReferenceExpression, project: Project): String? {
            var myRefExpr = refExpr
            var sqlType = refExpr.sqlType
            if (sqlType !is SqlTableType) return null
            if (sqlType.columnCount == 0) {
                val outputClause = PsiTreeUtil.getParentOfType(refExpr, SqlReturningClause::class.java) ?: return null
                myRefExpr = outputClause.findFirstTableReference() ?: return null
                sqlType = myRefExpr.sqlType
                if (sqlType !is SqlTableType) return null
                if (sqlType.columnCount == 0) return null
            }
            // SqlReturningClause
            return SqlExpandColumnListIntention.getAllColumnsList(project, myRefExpr, sqlType, MsDialect.INSTANCE)
        }
    }
}