package ru.coding4fun.tsql.inspection.function.string

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.SqlLanguageDialectEx
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.inspections.SqlInspectionBase
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.sql.psi.SqlTypeElement
import com.intellij.sql.psi.impl.SqlPsiElementFactory
import ru.coding4fun.tsql.MsInspectionMessages

class MsImplicitlyVarcharLengthInspection : SqlInspectionBase(), CleanupLocalInspectionTool {
    override fun getGroupDisplayName(): String = MsInspectionMessages.message("implicitly.varchar.length.name")
    override fun isDialectIgnored(dialect: SqlLanguageDialectEx?): Boolean = !(dialect?.dbms?.isMicrosoft ?: false)

    override fun createAnnotationVisitor(
            dialect: SqlLanguageDialectEx,
            manager: InspectionManager,
            problems: MutableList<ProblemDescriptor>,
            onTheFly: Boolean
    ): SqlAnnotationVisitor? {
        return LengthVisitor(manager, dialect, problems, onTheFly)
    }

    private class LengthVisitor(manager: InspectionManager,
                                dialect: SqlLanguageDialectEx,
                                problems: MutableList<ProblemDescriptor>,
                                private val onTheFly: Boolean
    ) : SqlAnnotationVisitor(manager, dialect, problems) {
        override fun visitSqlTypeElement(typeElement: SqlTypeElement?) {
            if (typeElement == null) return
            val typeName = typeElement.dataType.typeName
            if (!listOf("NVARCHAR", "VARCHAR").any { it.equals(typeName, true) }) return
            val funCallExpr = PsiTreeUtil.getParentOfType(typeElement, SqlFunctionCallExpression::class.java) ?: return
            val funName = funCallExpr.nameElement?.text ?: return
            if (!listOf("CAST", "TRY_CAST", "CONVERT", "TRY_CONVERT").any { it.equals(funName, true) }) return
            if (typeElement.text.contains("MAX", true)) return
            if (typeElement.dataType.length > 0) return

            val problemMessage = MsInspectionMessages.message("implicitly.varchar.length.problem")
            val problem = myManager.createProblemDescriptor(
                    typeElement,
                    problemMessage,
                    true,
                    ProblemHighlightType.WARNING,
                    onTheFly,
                    SetImplicitlyLengthQuickFix(SmartPointerManager.createPointer(typeElement))
            )
            addDescriptor(problem)

            super.visitSqlTypeElement(typeElement)
        }
    }

    private class SetImplicitlyLengthQuickFix(private val typeElement: SmartPsiElementPointer<SqlTypeElement>) : LocalQuickFixOnPsiElement(typeElement.element, typeElement.element) {
        override fun getFamilyName(): String = MsInspectionMessages.getMessage("implicitly.varchar.length.fix.family")
        override fun getText(): String = MsInspectionMessages.getMessage("implicitly.varchar.length.fix.text", typeElement.element!!.dataType.typeName)

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val sql = "${typeElement.element!!.dataType.typeName}(30)"
            val sqlTypeExpr = SqlPsiElementFactory.createDataTypeFromText(sql, MsDialect.INSTANCE, startElement.context!!)!!
            startElement.replace(sqlTypeExpr)
        }
    }
}