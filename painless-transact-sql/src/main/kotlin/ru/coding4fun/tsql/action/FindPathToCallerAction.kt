package ru.coding4fun.tsql.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.sql.psi.SqlCreateStatement
import ru.coding4fun.tsql.usages.MsUsageUsageManager

class FindPathToCallerAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MsUsageUsageManager.searchForPathToCaller(getCreateStmt(e) ?: return)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = getCreateStmt(e) != null
    }

    private fun getCreateStmt(e: AnActionEvent): SqlCreateStatement? = e.getElementAtCaret()
}