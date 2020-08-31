package ru.coding4fun.tsql.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.sql.psi.SqlCreateStatement
import ru.coding4fun.tsql.usages.MsUsageUsageManager

class ShowUsedReferencesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MsUsageUsageManager.searchForUsedReferences(e.getData(CommonDataKeys.PSI_FILE)!!)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = e.getData(CommonDataKeys.PSI_FILE) != null
    }
}