package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.SqlCreateSchemaStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsSchemaNamingInspection: MsNamingInspectionBase(
    "name.convention.schema.default.pattern",
    "name.convention.schema.default.description",
    "db"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return SchemaNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class SchemaNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateSchemaStatement(createSchemaStatement: SqlCreateSchemaStatement?) {
            if (createSchemaStatement == null) return
            val dbName: String = DasUtil.getCatalog(createSchemaStatement)
            val names = listOf(dbName, createSchemaStatement.name)
            validateNameAndReport(names, createSchemaStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    else -> null
                }
            }
        }
    }
}