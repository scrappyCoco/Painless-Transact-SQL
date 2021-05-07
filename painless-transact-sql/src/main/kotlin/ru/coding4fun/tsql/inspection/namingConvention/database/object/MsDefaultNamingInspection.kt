package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.*
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsDefaultNamingInspection : MsNamingInspectionBase(
    "name.convention.default.constraint.default.pattern",
    "name.convention.default.constraint.default.description",
    "db",
    "schema",
    "table",
    "column"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return DefaultNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class DefaultNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlColumnDefinition(columnDefinition: SqlColumnDefinition?) {
            if (columnDefinition == null) return
            for (constraint in columnDefinition.constraints) visitSqlConstraintDefinitionImpl(constraint)
        }

        override fun visitSqlAlterTableInstruction(alterInstruction: SqlAlterInstruction?) {
            if (alterInstruction == null) return
            val constrains = alterInstruction.children.filterIsInstance<SqlConstraintDefinition>().toList()
            for (constraint in constrains) visitSqlConstraintDefinitionImpl(constraint)
        }

        fun visitSqlConstraintDefinitionImpl(constraintDefinition: SqlConstraintDefinition?) {
            if (constraintDefinition?.name?.isEmpty() ?: true) return
            if (constraintDefinition!!.table == null) return
            if (constraintDefinition.constraintType != SqlConstraintDefinition.Type.DEFAULT) return

            val dbName: String = DasUtil.getCatalog(constraintDefinition.table)
            val schemaName: String = DasUtil.getSchema(constraintDefinition.table)
            val tableName: String = constraintDefinition.table!!.name
            val columnName: String = constraintDefinition.columnsRef.resolveObjects().firstOrNull()?.name
                ?: constraintDefinition.children
                    .filterIsInstance<SqlReferenceExpression>()
                    .firstOrNull { it.elementType == SqlElementTypes.SQL_COLUMN_SHORT_REFERENCE }
                    ?.name
                ?: return

            val names = listOf(dbName, schemaName, tableName, columnName, constraintDefinition.name)

            validateNameAndReport(names, constraintDefinition, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    "table" -> tableName
                    "column" -> tableName
                    else -> null
                }
            }
        }
    }
}