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

package ru.coding4fun.tsql.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.SyntaxTraverser
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.sql.psi.SqlCompositeElement
import com.intellij.sql.psi.impl.SqlPsiElementFactory

object PainlessPsiElementFactory {
    fun createIsNotElement(project: Project): SqlCompositeElement {
        val stmt = SqlPsiElementFactory.createStatementFromText("SELECT 1 IS NOT NULL", MsDialect.INSTANCE, project, null)
        return SyntaxTraverser.psiTraverser(stmt)
                .filter(SqlCompositeElement::class.java)
                .filter { it.text == "IS NOT" }
                .first()!!
    }
}