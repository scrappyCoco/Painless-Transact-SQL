package ru.coding4fun.tsql.usages

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.usages.PsiElementUsageGroupBase
import com.intellij.usages.Usage
import com.intellij.usages.UsageGroup
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.FileStructureGroupRuleProvider
import com.intellij.usages.rules.UsageGroupingRule
import ru.coding4fun.tsql.psi.findChildrenOfType
import ru.coding4fun.tsql.psi.firstNotEmpty

class MsDbTreeGroupRuleProvider : FileStructureGroupRuleProvider {
    override fun getUsageGroupingRule(project: Project): UsageGroupingRule? = object : UsageGroupingRule {
        override fun getParentGroupsFor(usage: Usage, targets: Array<out UsageTarget>): MutableList<UsageGroup> {
            val sqlSliceUsage = usage as? MsBdTreeSliceUsage ?: return arrayListOf()
            val target = targets.filterIsInstance<PsiElement2UsageTargetAdapter>().firstOrNull()
                    ?.element?.children?.firstNotEmpty() ?: return arrayListOf()

            val parents = arrayListOf<UsageGroup>()
            var element: Any? = sqlSliceUsage.element
            while (element != null) {
                if (element is SqlReferenceExpression) {
                    val resolvedElement = element.resolve()

                    if (resolvedElement != null) element = resolvedElement
                    else { // Not introspected object.
                        val dbRefs = element.findChildrenOfType<SqlReferenceExpression>()

                        val dbElement = dbRefs.asSequence()
                                .map { it.resolve() }
                                .filterIsInstance<DbElement>()
                                .firstOrNull { it.kind == ObjectKind.DATABASE }

                        if (dbElement != null) element = dbElement
                    }
                }

                if (element !is NavigatablePsiElement) return mutableListOf()

                if (element == target) return mutableListOf()
                parents.add(PsiElementUsageGroupBase(element))

                if (element is DasObject) element = (element as DasObject).dasParent as? NavigationItem
                else break
            }
            return parents.reversed().toMutableList()
        }
    }
}