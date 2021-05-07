package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsTokens
import com.intellij.sql.psi.SqlCreateStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase
import ru.coding4fun.tsql.psi.getFirstLeaves

class MsPartitionSchemaNamingInspection: MsNamingInspectionBase(
    "name.convention.partition.schema.default.pattern",
    "name.convention.partition.schema.default.description",
    "db"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return PartitionSchemaNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class PartitionSchemaNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateStatement(createStatement: SqlCreateStatement?) {
            if (createStatement?.name?.isEmpty() ?: true) return
            val firstLeaves = createStatement!!.getFirstLeaves(3)
            if (firstLeaves[1].elementType != MsTokens.MSSQL_PARTITION) return
            if (firstLeaves[2].elementType != MsTokens.MSSQL_SCHEME) return
            val dbName: String = DasUtil.getCatalog(createStatement)
            val names = listOf(dbName, createStatement.name)

            validateNameAndReport(names, createStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    else -> null
                }
            }
        }
    }
}