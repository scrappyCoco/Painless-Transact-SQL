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
import com.intellij.sql.psi.SqlFunctionCallExpression
import com.intellij.ui.ListCellRendererWithRightAlignedComponent
import ru.coding4fun.tsql.MsIntentionMessages
import ru.coding4fun.tsql.psi.addParam
import java.awt.Color

class MsAddDateStyleInConvertIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = MsIntentionMessages.message("add.date.style.in.convert.name")
    override fun getText(): String = MsIntentionMessages.message("add.date.style.in.convert.name")

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean =
            IntentionFunUtil.isAvailable(element, "CONVERT", arrayListOf(2))

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val list = listOf(
                MsDateFormat("100", "Default", "mon dd yyyy hh:miAM"),
                MsDateFormat("101", "U.S.", "mm/dd/yyyy"),
                MsDateFormat("102", "ANSI", "yyyy.mm.dd"),
                MsDateFormat("103", "British/French", "dd/mm/yyyy"),
                MsDateFormat("104", "German", "dd.mm.yyyy"),
                MsDateFormat("105", "Italian", "dd-mm-yyyy"),
                MsDateFormat("106", "-", "dd mon yyyy"),
                MsDateFormat("107", "-", "Mon dd, yyyy"),
                MsDateFormat("108", "-", "hh:mi:ss"),
                MsDateFormat("109", "-", "mon dd yyyy hh:mi:ss:mmmAM"),
                MsDateFormat("110", "USA", "mm-dd-yyyy"),
                MsDateFormat("111", "JAPAN", "yyyy/mm/dd"),
                MsDateFormat("112", "ISO", "yyyymmdd"),
                MsDateFormat("113", "Europe default (with ms)", "dd mon yyyy hh:mi:ss:mmm (24h)"),
                MsDateFormat("114", "-", "hh:mi:ss:mmm (24h)"),
                MsDateFormat("120", "ODBC canonical", "yyyy-mm-dd hh:mi:ss (24h)"),
                MsDateFormat("121", "ODBC canonical (with ms)", "yyyy-mm-dd hh:mi:ss.mmm (24h)"),
                MsDateFormat("22", "U.S.", "mm/dd/yy hh:mi:ss AM"),
                MsDateFormat("23", "ISO8601", "yyyy-mm-dd"),
                MsDateFormat("126", "ISO8601", "yyyy-mm-ddThh:mi:ss.mmm"),
                MsDateFormat("127", "ISO8601 with time zone Z", "yyyy-mm-ddThh:mi:ss.mmmZ"),
                MsDateFormat("130", "Hijri", "dd mon yyyy hh:mi:ss:mmmAM"),
                MsDateFormat("131", "Hijri", "dd/mm/yyyy hh:mi:ss:mmmAM")
        )

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

    private class MsDateFormat(val id: String, val title: String, val format: String)

    private class MsDateFormatRenderer : ListCellRendererWithRightAlignedComponent<MsDateFormat>() {
        override fun customize(value: MsDateFormat?) {
            setLeftText(value!!.title)
            setRightText(value.id + " | " + value.format)
            setRightForeground(Color.GRAY)
        }
    }
}