package ru.coding4fun.tsql.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import ru.coding4fun.tsql.usages.MsOutgoingUsageManager

class FindOutgoingUsagesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val selectedFile = e.getData(CommonDataKeys.PSI_FILE)!!
        MsOutgoingUsageManager.generate(selectedFile)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = true
    }
}