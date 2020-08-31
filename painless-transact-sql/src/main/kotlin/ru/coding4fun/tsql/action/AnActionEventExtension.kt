package ru.coding4fun.tsql.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

inline fun <reified T : PsiElement> AnActionEvent.getElementAtCaret(): T? {
    val caret = this.getData(CommonDataKeys.CARET) ?: return null
    val file = this.getData(CommonDataKeys.PSI_FILE) ?: return null
    val selectedElement = file.findElementAt(caret.offset)
    if (selectedElement is T) return selectedElement
    return PsiTreeUtil.getParentOfType(selectedElement, T::class.java)
}