package ru.coding4fun.tsql.usages

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Factory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.sql.psi.*
import com.intellij.usages.UsageSearcher
import com.intellij.usages.UsageViewManager
import com.intellij.usages.UsageViewPresentation
import ru.coding4fun.tsql.psi.findChildrenOfType
import ru.coding4fun.tsql.psi.getParentOfType
import ru.coding4fun.tsql.psi.isAsteriskColumn
import ru.coding4fun.tsql.psi.isTempOrVariable

object MsUsageUsageManager {
    fun searchForPathToCaller(createStatement: SqlCreateStatement) {
        val usageSearcher = Factory {
            UsageSearcher { processor ->
                ApplicationManager.getApplication().runReadAction {
                    val resultPaths: MutableList<MutableList<MsUsageElement>> = arrayListOf()
                    val workingPaths: MutableList<MutableList<MsUsageElement>> = arrayListOf(arrayListOf(MsUsageElement(createStatement)))
                    val futurePaths: MutableList<MutableList<MsUsageElement>> = arrayListOf()

                    while (workingPaths.any()) {
                        for (workingPath in workingPaths) {
                            val headElement = workingPath.last()
                            val callerRefs = ReferencesSearch.search(headElement.target).findAll()

                            if (callerRefs.isEmpty()) {
                                resultPaths.add(workingPath)
                            } else {
                                for (callerRef in callerRefs) {
                                    val usedStmt = callerRef.element.getParentOfType<SqlCreateStatement>() ?: continue

                                    val usageElement = MsUsageElement(usedStmt, callerRef.element)

                                    if (workingPath.contains(usageElement)) {
                                        resultPaths.add(workingPath)
                                        continue
                                    }

                                    val copyOfPath = workingPath.toMutableList()
                                    copyOfPath.add(usageElement)
                                    futurePaths.add(copyOfPath)
                                }
                            }
                        }
                        workingPaths.clear()
                        workingPaths.addAll(futurePaths)
                        futurePaths.clear()
                    }

                    for (resultPath in resultPaths) {
                        val usage = MsPathToCallerSliceUsage(resultPath.last().createStatement, resultPath.reversed())
                        processor.process(usage)
                    }
                }
            }
        }

        val usageViewPresentation = UsageViewPresentation()
        val title = "Path to Caller " + createStatement.name
        usageViewPresentation.tabName = title
        usageViewPresentation.tabText = title
        usageViewPresentation.searchString = title

        val usageViewManager = UsageViewManager.getInstance(createStatement.project)
        val usageTarget = PsiElement2UsageTargetAdapter(createStatement, true)
        usageViewManager.searchAndShowUsages(
                arrayOf(usageTarget),
                usageSearcher,
                true,
                true,
                usageViewPresentation,
                null)
    }

    fun searchForUsedReferences(file: PsiFile) {
        val usageViewPresentation = UsageViewPresentation()
        val title = "Used References in " + file.name
        usageViewPresentation.tabName = title
        usageViewPresentation.tabText = title
        usageViewPresentation.searchString = title

        val usageViewManager = UsageViewManager.getInstance(file.project)
        val usageTarget = PsiElement2UsageTargetAdapter(file, true)

        val usageSearcher = Factory {
            UsageSearcher { processor ->
                ApplicationManager.getApplication().runReadAction {
                    val references = file.findChildrenOfType<SqlReferenceExpression>()
                            .mapNotNull { getTopMostReference(it) }

                    for (reference in references) {
                        // SELECT * FROM ...
                        //        ^
                        if (reference.isAsteriskColumn()) continue

                        val resolvedElement: PsiElement? = reference.resolve()

                        // Skip for objects, that is not introspected and has not reference to a database.
                        if (resolvedElement == null) {
                            val dbRefs = reference.originalElement.findChildrenOfType<SqlReferenceExpression>()

                            val hasDb = dbRefs.asSequence()
                                    .map { it.resolve() }
                                    .filterIsInstance<DbElement>()
                                    .firstOrNull { it.kind == ObjectKind.DATABASE } != null

                            if (!hasDb) continue
                        }

                        if (resolvedElement == reference) continue

                        // SELECT Id FROM (SELECT Id FROM MyTable) AS T
                        //        ^
                        if (resolvedElement is SqlReferenceExpression) continue

                        // CREATE PRORCEDURE dbo.MyProc @T dbo.MyTableType
                        //                              ^
                        if (resolvedElement is SqlParameterDefinition) continue

                        // SELECT t.c FROM (SELECT c = 1) AS t
                        //                         ^         ^
                        if (resolvedElement is SqlAsExpression) continue

                        // SELECT c FROM (VALUES(1)) AS t (c)
                        //        ^
                        if (resolvedElement is SqlColumnAliasDefinition) continue


                        // CREATE FUNCTION dbo.MyTableFun() RETURNS @t TABLE (Id INT) ...
                        // SELECT Id FROM dbo.MyTableFun()
                        //        ^
                        // It's look like a little bit strange.
                        if (resolvedElement is DasObject && resolvedElement.kind == ObjectKind.ARGUMENT) continue

                        val usage = MsBdTreeSliceUsage(reference)
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

    private fun getTopMostReference(reference: SqlReferenceExpression): SqlReferenceExpression? {
        // DECLARE @T TABLE (...)
        //         ^
        if (reference.isTempOrVariable()) return null

        val resolvedElement: PsiElement? = reference.resolve() ?: return reference
        val parentOfResolvedElement = resolvedElement!!.parent

        // CREATE TABLE #T ...
        //              ^
        if (parentOfResolvedElement is SqlCreateTableStatement && parentOfResolvedElement.nameElement?.isTempOrVariable() == true) return null

        if (resolvedElement is DasObject) {
            if (resolvedElement.kind == ObjectKind.SCHEMA) return null
            if (resolvedElement.kind == ObjectKind.DATABASE) return null
        }
        return reference
    }
}