/*
 * Copyright [2019] Coding4fun
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

package ru.coding4fun.tsql.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.ui.ListCellRendererWithRightAlignedComponent
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.psi.addParam
import java.awt.Color

class MsAddDateStyleInConvertIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("add.date.style.in.convert.name")
    override fun getText(): String = MsIntentionMessages.message("add.date.style.in.convert.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.containingFile.language != MsDialect.INSTANCE) return false
        return IntentionFunUtil.isAvailable(element, "CONVERT", arrayListOf(2))
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val list = DateStyle.STYLES.values.toList()

        JBPopupFactory.getInstance()
                .createPopupChooserBuilder(list)
                .setRenderer(MsDateFormatRenderer())
                .setMovable(true)
                .setResizable(true)
                .setTitle("Choose date style")
                .setNamerForFiltering { it.title }
                .setItemChosenCallback { chosenFormat ->
                    while (true) {
                        val funCallExpr = PsiTreeUtil.getParentOfType(element, SqlFunctionCallExpression::class.java)
                                ?: break
                        WriteCommandAction.runWriteCommandAction(project) {
                            funCallExpr.addParam(chosenFormat.id, project)
                        }
                        break
                    }
                }
                .createPopup()
                .showInBestPositionFor(editor!!)
    }

    private class MsDateFormatRenderer : ListCellRendererWithRightAlignedComponent<DateStyle.MsDateFormat>() {
        override fun customize(value: DateStyle.MsDateFormat?) {
            setLeftText(value!!.title)
            setRightText(value.format + " | " + value.id)
            setRightForeground(Color.GRAY)
        }
    }
}