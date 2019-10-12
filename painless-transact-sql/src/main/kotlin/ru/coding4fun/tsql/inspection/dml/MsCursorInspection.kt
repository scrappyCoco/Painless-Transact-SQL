package ru.coding4fun.tsql.inspection.dml

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlClause
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlQueryExpression
import com.intellij.sql.psi.SqlVariableDefinition
import com.intellij.sql.psi.impl.SqlCursorDefinitionImpl
import ru.coding4fun.tsql.MsInspectionMessages
import ru.coding4fun.tsql.psi.getChildOfElementType
import ru.coding4fun.tsql.psi.getLeafChildrenByAst
import java.util.*

class MsCursorInspection : SqlInspectionBase() {
    private companion object {
        val scrollSet = TreeSet<String>(String.CASE_INSENSITIVE_ORDER).also {
            it.addAll(arrayOf("LOCAL", "GLOBAL"))
        }
        val updateSet = TreeSet<String>(String.CASE_INSENSITIVE_ORDER).also {
            it.addAll(arrayOf("STATIC", "KEYSET", "DYNAMIC", "FAST_FORWARD"))
        }
        val isolationSet = TreeSet<String>(String.CASE_INSENSITIVE_ORDER).also {
            it.addAll(arrayOf("READ_ONLY", "SCROLL_LOCKS", "OPTIMISTIC"))
        }
        val updateDisabledOptionList = arrayListOf("READ_ONLY", "FAST_FORWARD")
        val readOnlyList = arrayListOf("FORWARD_ONLY", "READ_ONLY")
    }

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor {
        return CursorVisitor(dialect, manager, problems, onTheFly)
    }

    private inner class CursorVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlVariableDefinition(varDef: SqlVariableDefinition?) {
            val cursorDef = varDef as? SqlCursorDefinitionImpl ?: return
            val queryExprEnd = cursorDef.children.asSequence()
                    .filterIsInstance<SqlQueryExpression>()
                    .firstOrNull()
                    ?.textRange?.endOffset ?: return

            val forUpdate = PsiTreeUtil.getChildOfType(varDef, SqlClause::class.java)
                    .let { it?.getChildOfElementType(SqlElementTypes.SQL_UPDATE) != null }

            val options = cursorDef.getLeafChildrenByAst(TextRange(0, queryExprEnd)).associateBy { it.text }
            val toCheckSets = listOf(scrollSet, updateSet, isolationSet)

            // Sets checks.
            for (toCheckSet in toCheckSets) {
                val intersects = toCheckSet.mapNotNull { options.get(it) }
                if (intersects.size <= 1) continue
                for (intersect in intersects) {
                    val setString = toCheckSet.joinToString()
                    val problemDescription = MsInspectionMessages.getMessage("cursor.definition.check.problem.set", setString)
                    val problem = myManager.createProblemDescriptor(
                            intersect,
                            problemDescription,
                            true,
                            ProblemHighlightType.WARNING,
                            onTheFly,
                            RemoveQuickFix(SmartPointerManager.createPointer(intersect))
                    )
                    addDescriptor(problem)
                }
            }

            // FOR UPDATE check.
            if (forUpdate) {
                val incompatibleOptions = updateDisabledOptionList.mapNotNull { options[it] }
                for (incompatibleOption in incompatibleOptions) {
                    val problemDescription = MsInspectionMessages.getMessage("cursor.definition.check.problem.update.incompatible", incompatibleOption.text)
                    val problem = myManager.createProblemDescriptor(
                            incompatibleOption,
                            problemDescription,
                            true,
                            ProblemHighlightType.WARNING,
                            onTheFly,
                            RemoveQuickFix(SmartPointerManager.createPointer(incompatibleOption))
                    )
                    addDescriptor(problem)
                }
            }

            // FAST_FORWARD.
            val fastForwardElement = options["FAST_FORWARD"]
            if (fastForwardElement != null) {
                val sameElements = readOnlyList.mapNotNull { options[it] }
                for (sameElement in sameElements) {
                    val problemDescription = MsInspectionMessages.getMessage("cursor.definition.check.problem.fast.forward", sameElement.text)
                    val problem = myManager.createProblemDescriptor(
                            sameElement,
                            problemDescription,
                            true,
                            ProblemHighlightType.WARNING,
                            onTheFly,
                            RemoveQuickFix(SmartPointerManager.createPointer(sameElement))
                    )
                    addDescriptor(problem)
                }
            }

            super.visitSqlVariableDefinition(varDef)
        }

        private inner class RemoveQuickFix(private val leafPointer: SmartPsiElementPointer<LeafPsiElement>) :
                LocalQuickFixOnPsiElement(leafPointer.element, leafPointer.element) {
            override fun getFamilyName(): String = MsInspectionMessages.getMessage("cursor.definition.check.fix.family")
            override fun getText(): String = MsInspectionMessages.getMessage("cursor.definition.check.fix.text", leafPointer.element!!.text)

            override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
                startElement.delete()
            }
        }
    }
}