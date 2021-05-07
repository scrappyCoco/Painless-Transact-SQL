package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsTokens
import com.intellij.sql.psi.SqlCreateIndexStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase
import ru.coding4fun.tsql.psi.findLeaf

class MsColumnStoreIndexNamingInspection: MsNamingInspectionBase(
    "name.convention.column.store.index.default.pattern",
    "name.convention.column.store.index.default.description",
    "db",
    "schema",
    "table",
    "clustered"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return ColumnStoreIndexNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class ColumnStoreIndexNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateIndexStatement(createIndexStatement: SqlCreateIndexStatement?) {
            if (createIndexStatement?.name?.isEmpty() ?: true) return
            if (createIndexStatement!!.table == null) return
            if (createIndexStatement.findLeaf(MsTokens.MSSQL_COLUMNSTORE) == null) return

            val dbName: String = DasUtil.getCatalog(createIndexStatement.table)
            val schemaName: String = DasUtil.getSchema(createIndexStatement.table)
            val tableName: String = createIndexStatement.table!!.name
            val clustered = (createIndexStatement.findLeaf(MsTokens.MSSQL_CLUSTERED)?.elementType ?: MsTokens.MSSQL_NONCLUSTERED)!!.toString().toLowerCase()

            val names = listOf(dbName, schemaName, tableName, clustered, createIndexStatement.name)

            validateNameAndReport(names, createIndexStatement, manager, problems, onTheFly) { contextVariable ->
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