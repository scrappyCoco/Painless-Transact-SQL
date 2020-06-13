package ru.coding4fun.tsql.contains.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import ru.coding4fun.tsql.contains.ContainsFile
import ru.coding4fun.tsql.contains.psi.ContainsElement
import ru.coding4fun.tsql.contains.psi.ContainsVisitor

abstract class ContainsInspectionBase : LocalInspectionTool() {
    override fun getID(): String = InspectionProfileEntry.getShortName(super.getID())
    override fun getShortName(): String = StringUtil.getShortName(this.javaClass.name)

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is ContainsFile) return null
        val result: MutableList<ProblemDescriptor> = arrayListOf()
        val visitor = createRecursiveVisitor(createAnnotationVisitor(manager, result, isOnTheFly))
        file.accept(visitor)
        return result.toTypedArray()
    }

    protected abstract fun createAnnotationVisitor(manager: InspectionManager, result: MutableList<ProblemDescriptor>, onTheFly: Boolean): ContainsAnnotationVisitor

    private fun createRecursiveVisitor(visitor: ContainsAnnotationVisitor): PsiRecursiveElementWalkingVisitor {
        return object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is ContainsElement) {
                    visitor.myCheckSubtree = false
                    element.accept(visitor)
                } else {
                    visitor.myCheckSubtree = true
                }

                if (visitor.myCheckSubtree) super.visitElement(element)
            }

            override fun elementFinished(element: PsiElement?) = visitor.visitElement(element!!)
        }
    }

    open class ContainsAnnotationVisitor(
            private val manager: InspectionManager,
            private val results: MutableList<ProblemDescriptor>
    ) : ContainsVisitor() {
        internal var myCheckSubtree = false

        override fun visitElement(o: ContainsElement) {
            this.myCheckSubtree = true
            super.visitElement(o)
        }

        protected fun addDescription(problemDescription: ProblemDescriptor) {
            results.add(problemDescription)
        }
    }
}