package ru.coding4fun.tsql.usages

import com.intellij.database.model.DasObject
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.NavigatablePsiElement
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.slicer.SqlSliceUsage
import com.intellij.usages.PsiElementUsageGroupBase
import com.intellij.usages.Usage
import com.intellij.usages.UsageGroup
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.FileStructureGroupRuleProvider
import com.intellij.usages.rules.UsageGroupingRule
import ru.coding4fun.tsql.psi.firstNotEmpty

class MsTreeGroupRuleProvider : FileStructureGroupRuleProvider {
    override fun getUsageGroupingRule(project: Project): UsageGroupingRule? = object : UsageGroupingRule {
        override fun getParentGroupsFor(usage: Usage, targets: Array<out UsageTarget>): MutableList<UsageGroup> {
            val sqlSliceUsage = usage as? SqlSliceUsage ?: return arrayListOf()
            val target = (targets[0] as PsiElement2UsageTargetAdapter).element.children.firstNotEmpty()

            val parents = arrayListOf<UsageGroup>()
            var element: NavigationItem? = sqlSliceUsage.element.navigationElement as NavigationItem
            while (element != null) {
                if (element is SqlReferenceExpression) element = element.resolve() as? NavigationItem
                if (element == null || element == target) return mutableListOf()
                parents.add(PsiElementUsageGroupBase(element as NavigatablePsiElement))
                if (element is DasObject) element = element.dasParent as? NavigationItem
                else break
            }
            return parents.reversed().toMutableList()
        }
    }
}