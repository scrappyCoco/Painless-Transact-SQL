package ru.coding4fun.tsql.contains.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import ru.coding4fun.tsql.contains.psi.ContainsItem
import ru.coding4fun.tsql.contains.psi.ContainsPsiElementFactory
import ru.coding4fun.tsql.contains.psi.ContainsSimpleTerm
import ru.coding4fun.tsql.contains.psi.ContainsTypes

abstract class ContainsReplaceTermBaseIntention : BaseElementAtCaretIntentionAction() {
    private var intentionLabel: String? = null
    private var newText: String? = null
    override fun getFamilyName(): String = "Replace CONTAINS"
    override fun getText(): String = intentionLabel!!

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.elementType != ContainsTypes.WORD) return false
        val simpleTerm = PsiTreeUtil.getParentOfType(element, ContainsSimpleTerm::class.java) ?: return false
        if (simpleTerm.parent !is ContainsItem) return false
        val currentText = simpleTerm.text
        newText = getNewText(currentText)
        intentionLabel = "Replace ${simpleTerm.text} with $newText"
        return true
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val newText: String = getNewText(element.text)
        val newItem = ContainsPsiElementFactory.createItem(project, newText)
        val currentItem = PsiTreeUtil.getParentOfType(element, ContainsItem::class.java)
        currentItem!!.replace(newItem)
    }

    protected abstract fun getNewText(simpleTermText: String): String
}