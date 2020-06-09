package ru.coding4fun.tsql.contains.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import ru.coding4fun.tsql.contains.psi.ContainsWeightOption

class ContainsWeightInspection : ContainsInspectionBase() {
    override fun createAnnotationVisitor(manager: InspectionManager, result: MutableList<ProblemDescriptor>, onTheFly: Boolean): ContainsAnnotationVisitor {
        return object : ContainsAnnotationVisitor(manager, result) {
            override fun visitWeightOption(weightOption: ContainsWeightOption) {
                visitWeightOptionImpl(weightOption)
                super.visitWeightOption(weightOption)
            }

            private fun visitWeightOptionImpl(weightOption: ContainsWeightOption) {
                val weightValue = weightOption.weightValue ?: return
                val weightLiteral = weightOption.weightLiteral!!
                if (weightValue < 0.0 || weightValue > 1.0) {
                    addDescription(manager.createProblemDescriptor(weightLiteral, "The weight must be between 0.0 and 1.0", onTheFly, emptyArray(), ProblemHighlightType.ERROR))
                }
            }
        }
    }
}