package ru.coding4fun.tsql.usages

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Factory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.slicer.SliceAnalysisParams
import com.intellij.sql.psi.*
import com.intellij.sql.slicer.SqlSliceUsage
import com.intellij.usages.UsageSearcher
import com.intellij.usages.UsageViewManager
import com.intellij.usages.UsageViewPresentation
import com.intellij.util.castSafelyTo
import ru.coding4fun.tsql.psi.firstNotEmpty
import ru.coding4fun.tsql.psi.isTempOrVariable

object MsOutgoingUsageManager {
    fun generate(sourceFile: PsiFile) {
        val usageViewPresentation = UsageViewPresentation()
        val createStatement = sourceFile.children.firstNotEmpty().castSafelyTo<SqlCreateStatement>()
        val title = "Outgoing dependencies for " + (createStatement?.name ?: "Unknown")
        usageViewPresentation.usagesString = title
        usageViewPresentation.tabName = title
        usageViewPresentation.tabText = title

        val usageViewManager = UsageViewManager.getInstance(sourceFile.project)
        val usageTarget = PsiElement2UsageTargetAdapter(sourceFile)
        val nameElement = createStatement?.nameElement

        val references = PsiTreeUtil
                .findChildrenOfAnyType(sourceFile, SqlReferenceExpression::class.java)
                .mapNotNull { getTopMostReference(it, nameElement) }

        val usageSearcher = Factory {
            UsageSearcher { processor ->
                ApplicationManager.getApplication().runReadAction {
                    for (reference in references) {
                        val usage = SqlSliceUsage(reference, SliceAnalysisParams().also { it.dataFlowToThis = false })
                        processor.process(usage)
                    }
                }
            }
        }


        usageViewManager.searchAndShowUsages(
                arrayOf(usageTarget),
                usageSearcher,
                true,
                true,
                usageViewPresentation,
                null)
    }

    private fun getTopMostReference(reference: SqlReferenceExpression, nameElement: SqlNameElement?): SqlReferenceExpression? {
        var element: PsiElement? = reference.parent
        while (element != null) {
            if (element == nameElement) return null
            if (element !is SqlIdentifier) break
            if (element is SqlReference) {
                val resolvedElement = element.resolve()
                if (resolvedElement is SqlCreateTableStatement && resolvedElement.nameElement?.isTempOrVariable() == true) return null
            } else break
            element = element.parent
        }

        if (element is SqlReferenceExpression) {
            val resolvedElement = element.resolve()
            if (resolvedElement is DasObject) {
                if (resolvedElement.kind == ObjectKind.SCHEMA) return null
                if (resolvedElement.kind == ObjectKind.DATABASE) return null
            }
            return element
        }
        return null
    }
}