package ru.coding4fun.tsql.gutter

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.dialects.mssql.MsReservedKeywords
import com.intellij.sql.psi.*
import ru.coding4fun.tsql.psi.findFirstTableReference
import ru.coding4fun.tsql.psi.isTempOrVariable
import javax.swing.Icon

class DmlLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        var tableReference: SqlReferenceExpression? = null

        when {
            // TRUNCATE TABLE T1.
            element is SqlTruncateTableStatement -> {
                tableReference = (element as SqlElement).findFirstTableReference() ?: return
            }
            // INSERT INTO ...
            // ... OUTPUT ... INTO ...
            element is SqlTableColumnsList -> {
                // Skip for CREATE INDEX IX ON MyTable (...)
                tableReference =
                    if (PsiTreeUtil.getParentOfType(element, SqlCreateIndexStatement::class.java) != null) null
                    else element.tableReference
            }
            // UPDATE/DELETE/MERGE
            element is SqlDmlStatement && element !is SqlInsertStatement -> {
                val instruction = PsiTreeUtil.getChildOfType(element, SqlDmlInstruction::class.java) ?: return
                tableReference = instruction.findFirstTableReference() ?: return
            }
            // BULK INSERT
            element.elementType == MsReservedKeywords.TSQL_BULK && element is SqlElement -> {
                tableReference = element.findFirstTableReference() ?: return
            }
        }

        if (tableReference == null || tableReference.isTempOrVariable()) return
        var tableOrAlias = tableReference.resolve() ?: return
        if (tableOrAlias.isTempOrVariable()) return

        // UPDATE a
        // SET ...
        // FROM T1 AS a
        if (tableOrAlias is SqlAsExpression) {
            tableReference = tableOrAlias.expression as? SqlReferenceExpression ?: return
            tableOrAlias = tableReference.resolve() ?: return
        }

        result.add(
            NavigationGutterIconBuilder.create(icon)
                .setTarget(tableOrAlias)
                .createLineMarkerInfo(tableReference)
        )
    }

    override fun getName(): String = "DML"
    override fun getIcon(): Icon = AllIcons.Gutter.WriteAccess
}