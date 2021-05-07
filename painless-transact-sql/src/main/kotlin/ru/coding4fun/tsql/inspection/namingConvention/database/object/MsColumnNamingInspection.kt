package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsTokens
import com.intellij.sql.psi.SqlColumnDefinition
import com.intellij.sql.psi.SqlCreateIndexStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase
import ru.coding4fun.tsql.psi.findLeaf

class MsColumnNamingInspection : MsNamingInspectionBase(
    "name.convention.column.default.pattern",
    "name.convention.column.default.description",
    "db",
    "schema",
    "table",
    "sparse"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return ColumnNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class ColumnNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlColumnDefinition(columnDefinition: SqlColumnDefinition?) {
            if (columnDefinition?.name?.isEmpty() ?: true) return
            if (columnDefinition!!.table == null) return

            val dbName: String = DasUtil.getCatalog(columnDefinition.table)
            val schemaName: String = DasUtil.getSchema(columnDefinition.table)
            val tableName: String = columnDefinition.table!!.name
            val sparse = columnDefinition.findLeaf(MsTokens.MSSQL_SPARSE)?.elementType.toString().toLowerCase()

            val names = listOf(dbName, schemaName, tableName, columnDefinition.name, sparse)

            validateNameAndReport(names, columnDefinition, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    "table" -> tableName
                    else -> null
                }
            }
        }
    }
}