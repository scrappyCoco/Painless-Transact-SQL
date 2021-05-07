package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.SqlCreateTableStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsTableNamingInspection : MsNamingInspectionBase(
    "name.convention.table.default.pattern",
    "name.convention.table.default.description",
    "db",
    "schema"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return DatabaseNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class DatabaseNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateTableStatement(createTableStatement: SqlCreateTableStatement?) {
            if (createTableStatement?.name?.isEmpty() ?: true) return

            val dbName: String = DasUtil.getCatalog(createTableStatement)
            val schemaName: String = DasUtil.getSchema(createTableStatement)

            val names = listOf(dbName, schemaName, createTableStatement!!.name)
            validateNameAndReport(names, createTableStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    else -> null
                }
            }
        }
    }
}