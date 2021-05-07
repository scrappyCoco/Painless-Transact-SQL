package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsTypes
import com.intellij.sql.psi.SqlCreateStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsSequenceNamingInspection: MsNamingInspectionBase(
    "name.convention.sequence.default.pattern",
    "name.convention.sequence.default.description",
    "db",
    "schema"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return SequenceNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class SequenceNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateStatement(createStatement: SqlCreateStatement?) {
            if (createStatement == null) return
            if (createStatement.elementType != MsTypes.MSSQL_CREATE_SEQUENCE_STATEMENT) return
            val dbName: String = DasUtil.getCatalog(createStatement)
            val schemaName: String = DasUtil.getSchema(createStatement)
            val names = listOf(dbName, schemaName, createStatement.name)
            validateNameAndReport(names, createStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    else -> null
                }
            }
        }
    }
}