package ru.coding4fun.tsql.inspection.dml

import com.intellij.codeInspection.*
import com.intellij.database.dialects.mssql.model.MsTableOrView
import com.intellij.database.model.DasColumn
import com.intellij.database.psi.DbElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlInsertStatement
import com.intellij.sql.psi.SqlOrderByClause
import ru.coding4fun.tsql.MsInspectionMessages

class MsOrderByClIxInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("inspection.dml.group")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return OrderByClIxVisitor(dialect, manager, problems, onTheFly)
    }

    private class OrderByClIxVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlInsertStatement(insertStatement: SqlInsertStatement?) {
            if (insertStatement == null) return
            val dmlInstruction = insertStatement.dmlInstruction ?: return
            val targetResolve = dmlInstruction.targetExpression.reference?.resolve()
            val targetDbElement = targetResolve as? DbElement ?: return
            val targetMsTable = targetDbElement.delegate as? MsTableOrView ?: return
            val clIx = targetMsTable.indices.toList().firstOrNull { it.isClustering } ?: return
            val insertColumns = dmlInstruction.columnsList?.columnsReferenceList?.referenceList ?: return

            // Collecting intersected columns of clustered index and INSERT INTO T(...).
            val intersectedColumns = hashSetOf<String>()
            for (insertColumn in insertColumns) {
                val targetColumn = insertColumn.multiResolve(false).map { it.element }
                        .filterIsInstance<DasColumn>().firstOrNull() ?: continue

                if (clIx.colNames.any { it.equals(targetColumn.name, true) }) {
                    intersectedColumns.add(targetColumn.name)
                }
            }

            // All columns of clustered index was presented in INSERT INTO T(...).
            if (intersectedColumns.size == clIx.colNames.size) {
                val sqlOrderByClause = dmlInstruction.queryExpression?.children?.filterIsInstance<SqlOrderByClause>()?.firstOrNull()
                        ?: return

                val problemDescription = MsInspectionMessages.message("dml.ms.order.by.cl.ix.problem")
                val problem = myManager.createProblemDescriptor(
                        sqlOrderByClause,
                        sqlOrderByClause,
                        problemDescription,
                        ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                        onTheFly,
                        RemoveRedundantOrderByQuickFix(sqlOrderByClause)
                )

                addDescriptor(problem)
            }

            super.visitSqlInsertStatement(insertStatement)
        }

        private class RemoveRedundantOrderByQuickFix(
                orderByClause: SqlOrderByClause
        ) : LocalQuickFixOnPsiElement(orderByClause, orderByClause) {
            override fun getFamilyName(): String = MsInspectionMessages.message("dml.ms.order.by.cl.ix.fix")
            override fun getText(): String = MsInspectionMessages.message("dml.ms.order.by.cl.ix.fix")

            override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
                startElement.delete()
            }
        }
    }
}