package ru.coding4fun.tsql.usages

import com.intellij.psi.PsiElement
import com.intellij.slicer.SliceAnalysisParams
import com.intellij.slicer.SliceUsage
import com.intellij.sql.psi.SqlElement
import com.intellij.util.Processor

class MsBdTreeSliceUsage(element: SqlElement) : SliceUsage(element, SliceAnalysisParams()) {
    override fun processUsagesFlownFromThe(element: PsiElement?, uniqueProcessor: Processor<in SliceUsage>?) {
    }

    override fun processUsagesFlownDownTo(element: PsiElement?, uniqueProcessor: Processor<in SliceUsage>?) {
    }

    override fun copy(): SliceUsage = this
}