package ru.coding4fun.tsql.inspection

import com.intellij.codeInspection.*
import com.intellij.database.dataSource.srcStorage.DbSrcFileSystem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlCreateStatement
import com.intellij.sql.psi.SqlCreateTriggerStatement
import com.intellij.sql.psi.SqlElementTypes
import com.intellij.sql.psi.SqlReferenceExpression
import com.intellij.sql.type
import ru.coding4fun.tsql.MsMessages
import ru.coding4fun.tsql.psi.getChildOfElementType
import ru.coding4fun.tsql.psi.isSqlConsole

class MsRenamedInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun createAnnotationVisitor(
            dialectEx: SqlLanguageDialectEx,
            inspectionManager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return RenamedVisitor(inspectionManager, dialectEx, problems, onTheFly)
    }

    private class RenamedVisitor(
            manager: InspectionManager,
            dialect: SqlLanguageDialectEx,
            problems: MutableList<ProblemDescriptor>,
            private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        private val targetTypes = arrayListOf(
                SqlElementTypes.SQL_CREATE_PROCEDURE_STATEMENT,
                SqlElementTypes.SQL_CREATE_FUNCTION_STATEMENT,
                SqlElementTypes.SQL_CREATE_TRIGGER_STATEMENT
        )

        override fun visitSqlCreateStatement(createStatement: SqlCreateStatement?) {
            if (createStatement == null) return
            if (!targetTypes.contains(createStatement.type)) return
            if (createStatement.containingFile.isSqlConsole()) return
            val actualName = getActualFileName(createStatement.containingFile)
            if (createStatement.name == actualName) return

            val problemMessage = MsMessages.message(
                    "inspection.ddl.renamed.problem",
                    createStatement.name,
                    actualName)

            val problem = myManager.createProblemDescriptor(
                    createStatement.nameElement ?: return,
                    problemMessage,
                    true,
                    ProblemHighlightType.WARNING,
                    onTheFly,
                    RenameRoutineQuickFix(SmartPointerManager.createPointer(createStatement), actualName)
            )
            addDescriptor(problem)

            val trigger = createStatement as? SqlCreateTriggerStatement ?: return
            val targetElement = trigger.getChildOfElementType(SqlElementTypes.SQL_ON_TARGET_CLAUSE) ?: return
            val targetReference = PsiTreeUtil.getChildOfType(targetElement, SqlReferenceExpression::class.java)
                    ?: return

            val sysCommentTableFile = targetReference.resolve()?.containingFile ?: return
            val actualFile = trigger.containingFile.parent?.parent
            //val sysCommentTableFile =
//            if (actualTableName != trigger.targetContextExpression!!.text) {
//                val problem2 = myManager.createProblemDescriptor(
//                        trigger.targetContextExpression ?: return,
//                        "Table name is $actualTableName",
//                        true,
//                        ProblemHighlightType.WARNING,
//                        onTheFly
//                )
//                addDescriptor(problem2)
//            }
        }

        private fun getActualFileName(file: PsiFile): String {
            val actualName = file.name
            val isOriginal = DbSrcFileSystem.isOriginalFilePath(actualName)
            return if (isOriginal) {
                actualName.replace(".orig.sql", "")
            } else {
                actualName.replace(".sql", "")
            }
        }
    }

    private class RenameRoutineQuickFix(
            private val createStatement: SmartPsiElementPointer<SqlCreateStatement>,
            private val actualName: String
    ) : LocalQuickFixOnPsiElement(createStatement.element!!) {
        override fun getFamilyName(): String = MsMessages.message("inspection.ddl.renamed.fix.family")

        override fun getText(): String = MsMessages.message(
                "inspection.ddl.renamed.fix.text",
                createStatement.element!!.name,
                actualName)

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {

        }
    }
}