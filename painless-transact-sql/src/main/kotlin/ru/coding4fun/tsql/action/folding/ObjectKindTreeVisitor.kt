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

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.ObjectKind.*
import com.intellij.database.psi.DbDataSource
import com.intellij.ui.tree.TreePathUtil
import com.intellij.ui.tree.TreeVisitor
import com.intellij.ui.tree.TreeVisitor.Action
import java.util.*
import javax.swing.tree.TreePath
import kotlin.collections.HashSet

class ObjectKindTreeVisitor(
        private val topPath: TreePath?,
        private val targetKind: ObjectKind,
        private val isCanceled: (() -> Boolean),
        private val isCollapsed: ((treePath: TreePath) -> Boolean)
) : TreeVisitor {
    val toHandleSet = HashSet<TreePath>()
    override fun visit(path: TreePath): Action {
        if (isCanceled()) return Action.INTERRUPT
        val samePath = topPath != null && TreePathUtil.findCommonAncestor(path, topPath).pathCount == path.pathCount.coerceAtMost(topPath.pathCount)
        val searchEverywhere = topPath == null
        val containsInTop = searchEverywhere || samePath
        if (!containsInTop) return Action.SKIP_CHILDREN

        val dasObject = path.lastPathComponent as? DasObject ?: return Action.CONTINUE
        if (dasObject is DbDataSource && !dasObject.dbms.isMicrosoft) return Action.SKIP_CHILDREN
        if (dasObject.kind == SCHEMA && sysSchemas.contains(dasObject.name)) return Action.SKIP_CHILDREN
        if (dasObject.kind == targetKind && !isCollapsed(path.parentPath)) {
            toHandleSet.add(path.parentPath)
            return Action.SKIP_CHILDREN
        }

        return if (isAchievable(dasObject.kind, targetKind)) Action.CONTINUE else Action.SKIP_CHILDREN
    }

    companion object {
        private val ascendingTypes: Map<ObjectKind, HashSet<ObjectKind>>

        private val sysSchemas: Set<String> = TreeSet<String>(String.CASE_INSENSITIVE_ORDER).also {
            it.add("sys")
            it.add("INFORMATION_SCHEMA")
        }

        init {
            val kindHierarchy = arrayOf(
                    ROOT to listOf(DATABASE),
                    DATABASE to listOf(SCHEMA),
                    SCHEMA to listOf(TABLE, VIEW, ROUTINE, OBJECT_TYPE, TABLE_TYPE, SYNONYM),
                    TABLE to listOf(COLUMN, INDEX, KEY, FOREIGN_KEY, CHECK, DEFAULT, TRIGGER),
                    VIEW to listOf(COLUMN, INDEX, KEY, FOREIGN_KEY, CHECK, TRIGGER),
                    TABLE_TYPE to listOf(COLUMN, INDEX, KEY, CHECK, DEFAULT)
            )

            ascendingTypes = kindHierarchy.hierarchyToAchievableMap()
        }

        private fun <ObjectKind> Array<Pair<ObjectKind, List<ObjectKind>>>.hierarchyToAchievableMap(): Map<ObjectKind, HashSet<ObjectKind>> {
            val achievableList = ArrayList<HashSet<ObjectKind>>(this.size)
            for (description in this) {
                val currentKind = description.first
                val childrenKinds = description.second
                achievableList.add(childrenKinds.toHashSet())
                for (achievableSet in achievableList) {
                    if (achievableSet.contains(currentKind)) achievableSet.addAll(childrenKinds)
                }
            }

            return this.mapIndexed { index, description -> description.first to achievableList[index] }.toMap()
        }

        private fun isAchievable(topKind: ObjectKind, descendantKind: ObjectKind): Boolean {
            if (topKind == descendantKind) return true
            return ascendingTypes[topKind]?.contains(descendantKind) ?: false
        }
    }
}
