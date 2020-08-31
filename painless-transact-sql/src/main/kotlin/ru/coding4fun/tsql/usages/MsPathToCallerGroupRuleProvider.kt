package ru.coding4fun.tsql.usages

import com.intellij.openapi.project.Project
import com.intellij.psi.NavigatablePsiElement
import com.intellij.usages.Usage
import com.intellij.usages.UsageGroup
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.FileStructureGroupRuleProvider
import com.intellij.usages.rules.UsageGroupingRule

class MsPathToCallerGroupRuleProvider : FileStructureGroupRuleProvider {
    override fun getUsageGroupingRule(project: Project): UsageGroupingRule? =
            object : UsageGroupingRule {
                override fun getParentGroupsFor(usage: Usage, targets: Array<out UsageTarget>): MutableList<UsageGroup> {
                    val sqlSliceUsage = usage as? MsPathToCallerSliceUsage ?: return arrayListOf()

                    return sqlSliceUsage.elementsToRoot
                            .map { MsRefUsageGroup(it.target as NavigatablePsiElement, it.occurrence as? NavigatablePsiElement) }
                            .toMutableList()
                }
            }
}