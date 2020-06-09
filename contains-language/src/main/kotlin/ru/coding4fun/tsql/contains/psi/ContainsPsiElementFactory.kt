package ru.coding4fun.tsql.contains.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import ru.coding4fun.tsql.contains.ContainsLanguage

object ContainsPsiElementFactory {
    fun createItem(project: Project, text: String): ContainsItem {
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("dummy.simple", ContainsLanguage, text)
        return psiFile.firstChild as ContainsItem
    }
}