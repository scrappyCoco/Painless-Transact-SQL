package ru.coding4fun.tsql.inspection.namingConvention.database

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.psi.SqlCreateCatalogStatement
import ru.coding4fun.tsql.inspection.namingConvention.MsNamingInspectionBase

class MsDatabaseNamingInspection : MsNamingInspectionBase(
    "name.convention.db.default.pattern",
    "name.convention.db.default.description"
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
        override fun visitSqlCreateCatalogStatement(createDbStatement: SqlCreateCatalogStatement) {
            if (createDbStatement.nameIdentifier?.text?.isEmpty() ?: true) return
            validateNameAndReport(listOf(createDbStatement.name), createDbStatement, manager, problems, onTheFly)
        }
    }
}