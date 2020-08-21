/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.tsql.action.folding

import com.intellij.database.dialects.mssql.model.MsDatabase
import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import com.intellij.database.view.DatabaseStructure
import com.intellij.database.view.DatabaseView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.ui.tree.TreePathUtil
import com.intellij.util.ui.tree.TreeUtil
import java.util.concurrent.CountDownLatch
import javax.swing.tree.TreePath

abstract class FoldingBaseAction(
        private val getPathToTop: (selectedPath: TreePath) -> TreePath?,
        private val getTopText: (parentObject: Any?) -> String,
        private val hideIfObjectNull: Boolean,
        private val isHideMode: Boolean,
        private val actionText: String
) : AnAction() {
    private lateinit var targetKind: ObjectKind
    private var topPath: TreePath? = null

    override fun actionPerformed(event: AnActionEvent) = object : Task.Modal(event.project, "Folding tree", true) {
        override fun run(indicator: ProgressIndicator) {
            val lath = CountDownLatch(1)
            val dbTree = event.getData(DatabaseView.DATABASE_VIEW_KEY)!!.tree
            val myVisitor = ObjectKindTreeVisitor(topPath, targetKind, indicator::isCanceled) { selectedPath ->
                isHideMode && dbTree.isCollapsed(selectedPath) && !dbTree.model.isLeaf(selectedPath.lastPathComponent)
            }
            TreeUtil.promiseVisit(dbTree, myVisitor).onSuccess {
                for (path in myVisitor.toHandleSet) {
                    if (path.pathCount == 1) continue
                    if (indicator.isCanceled) break
                    if (isHideMode) dbTree.collapsePath(path) else dbTree.expandPath(path)
                }
            }.then { lath.countDown() }
            lath.await()
        }
    }.queue()

    override fun update(event: AnActionEvent) {
        val dbTree = event.getData(DatabaseView.DATABASE_VIEW_KEY)?.tree
        if (dbTree == null) {
            event.presentation.isVisible = false
            return
        }
        val selectionPath = dbTree.selectionPath!!
        topPath = getPathToTop.invoke(selectionPath)
        val topComponent = topPath?.lastPathComponent
        val isItSelfSelected = selectionPath == topPath
        if (topPath == null && hideIfObjectNull || isItSelfSelected) {
            event.presentation.isVisible = false
        } else {
            targetKind = when (val selectedComponent = selectionPath.lastPathComponent) {
                is DatabaseStructure.FamilyGroup -> selectedComponent.childrenKind
                is DasObject -> selectedComponent.kind
                else -> ObjectKind.DATABASE
            }
            val topText = getTopText(topComponent)
            val targetText = targetKind.name().toLowerCase()
            event.presentation.text = "$actionText all ${targetText}s $topText"
            event.presentation.isVisible = true
        }

    }

    companion object {
        private fun getTreePath(treePath: TreePath, lastIndex: Int): TreePath? {
            return if (lastIndex >= 0) TreePathUtil.convertCollectionToTreePath(treePath.path.take(lastIndex + 1)) else null
        }

        @Suppress("UNUSED_PARAMETER")
        fun getEverywhereObject(selectionPath: TreePath) = null

        fun getDataSourceObject(selectionPath: TreePath): TreePath? {
            val indexOfDbSource = selectionPath.path.indexOfFirst { it is DbDataSource }
            return getTreePath(selectionPath, indexOfDbSource)
        }

        fun getDbObject(selectionPath: TreePath): TreePath? {
            val indexOfDb = selectionPath.path.indexOfFirst { it is MsDatabase }
            return getTreePath(selectionPath, indexOfDb)
        }

        fun getGroupObject(selectionPath: TreePath): TreePath? {
            val indexOfDbGroup = selectionPath.path.indexOfLast { it.javaClass == DatabaseStructure.DbGroup::class.java }
            return getTreePath(selectionPath, indexOfDbGroup)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getEverywhereText(parentObject: Any?) = "everywhere"

        fun getGroupText(parentObject: Any?): String {
            return "in " + (parentObject as DatabaseStructure.DbGroup).qualifiedName!!
        }

        fun getDasText(parentObject: Any?): String {
            return "in " + (parentObject as DasObject).name
        }
    }
}

abstract class ExpandBaseAction(
        getParentObject: ((treePath: TreePath) -> TreePath?),
        getParentText: ((parentObject: Any?) -> String),
        hideIfObjectNull: Boolean
) : FoldingBaseAction(getParentObject, getParentText, hideIfObjectNull, false, "Show")

abstract class CollapseBaseAction(
        getParentObject: ((treePath: TreePath) -> TreePath?),
        getParentText: ((parentObject: Any?) -> String),
        hideIfObjectNull: Boolean
) : FoldingBaseAction(getParentObject, getParentText, hideIfObjectNull, true, "Hide")

class ExpandEverywhereAction : ExpandBaseAction(Companion::getEverywhereObject, Companion::getEverywhereText, false)
class ExpandGroupAction : ExpandBaseAction(Companion::getGroupObject, Companion::getGroupText, true)
class ExpandDataSourceAction : ExpandBaseAction(Companion::getDataSourceObject, Companion::getDasText, true)
class ExpandDbAction : ExpandBaseAction(Companion::getDbObject, Companion::getDasText, true)

class CollapseEverywhereAction : CollapseBaseAction(Companion::getEverywhereObject, Companion::getEverywhereText, false)
class CollapseGroupAction : CollapseBaseAction(Companion::getGroupObject, Companion::getGroupText, true)
class CollapseDataSourceAction : CollapseBaseAction(Companion::getDataSourceObject, Companion::getDasText, true)
class CollapseDbAction : CollapseBaseAction(Companion::getDbObject, Companion::getDasText, true)