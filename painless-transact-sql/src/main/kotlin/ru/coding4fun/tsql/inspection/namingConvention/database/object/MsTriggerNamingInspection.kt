package ru.coding4fun.tsql.inspection.namingConvention.database.`object`

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.database.util.DasUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.SqlCreateTriggerStatement
import com.intellij.sql.psi.SqlElementTypes
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsTriggerNamingInspection : MsNamingInspectionBase(
    "name.convention.trigger.default.pattern",
    "name.convention.trigger.default.description",
    "db",
    "schema",
    "table",
    "action", // INSERT, UPDATE, DELETE
    "time" // AFTER, INSTEAD OF, FOR
) {
    override fun createAnnotationVisitor(
        dialect: SqlLanguageDialectEx,
        manager: InspectionManager,
        problems: MutableList<ProblemDescriptor>,
        onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return TriggerNameVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class TriggerNameVisitor(
        dialect: SqlLanguageDialectEx,
        private val manager: InspectionManager,
        private val problems: MutableList<ProblemDescriptor>,
        private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlCreateTriggerStatement(createTriggerStatement: SqlCreateTriggerStatement?) {
            if (createTriggerStatement == null) return
            if (createTriggerStatement.table == null) return

            val dbName: String = DasUtil.getCatalog(createTriggerStatement)
            val schemaName: String = DasUtil.getSchema(createTriggerStatement)
            val tableName: String = createTriggerStatement.table!!.name

            val time = createTriggerStatement.children
                .filter { it.elementType == SqlElementTypes.SQL_TRIGGER_TIME_CLAUSE }
                .joinToString(separator = " ") { it.text }

            val action =
                createTriggerStatement.children
                    .filter { it.elementType == SqlElementTypes.SQL_TRIGGER_EVENT_CLAUSE }
                    .joinToString(separator = " ") { it.text }

            val names = listOf(dbName, schemaName, tableName, action, time, createTriggerStatement.name)

            validateNameAndReport(names, createTriggerStatement, manager, problems, onTheFly) { contextVariable ->
                when (contextVariable) {
                    "db" -> dbName
                    "schema" -> schemaName
                    "table" -> tableName
                    "action" -> action
                    "time" -> time
                    else -> null
                }
            }
        }
    }
}