package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.SqlCreateProcedureStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsProcedureNamingInspection: MsNamingInspectionBase(
    "name.convention.procedure.default.pattern",
    "name.convention.procedure.default.description",
    "db",
    "schema"
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return ProcedureNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class ProcedureNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateProcedureStatement(createProcedureStatement: SqlCreateProcedureStatement?) {
            if (createProcedureStatement == null) return
            val dbName: String = DasUtil.getCatalog(createProcedureStatement)
            val schemaName: String = DasUtil.getSchema(createProcedureStatement)
            val names = listOf(dbName, schemaName, createProcedureStatement.name)

            validateNameAndReport(names, createProcedureStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    else -> null
                }
            }
        }
    }
}