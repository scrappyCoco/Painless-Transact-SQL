package ru.coding4fun.tsql.usages

import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.usages.PsiElementUsageGroupBase

class MsRefUsageGroup<T>(element: T, val refElement: T?) : PsiElementUsageGroupBase<T>(element)
        where T : PsiElement, T : NavigationItem {
    override fun canNavigate(): Boolean {
        if (refElement != null) return refElement.canNavigate()
        return super.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        if (refElement != null) return refElement.canNavigate()
        return super.canNavigateToSource()
    }

    override fun navigate(focus: Boolean) {
        if (refElement != null) refElement.navigate(focus) else super.navigate(focus)
    }
}