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

package ru.coding4fun.tsql.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import ru.coding4fun.tsql.lang.psi.ContainsTypes

class ContainsSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = ContainsSyntaxHighlighter()
}

class ContainsSyntaxHighlighter : SyntaxHighlighterBase() {
    //@formatter:off
    private val operatorKeys = arrayOf(createTextAttributesKey("CONTAINS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN))
    private val keywordKeys = arrayOf(createTextAttributesKey("CONTAINS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD))
    private val valueKeys = arrayOf(createTextAttributesKey("CONTAINS_VALUE", DefaultLanguageHighlighterColors.STRING))
    private val emptyKeys = emptyArray<TextAttributesKey>()
    //@formatter:on

    private val Keywords = setOf(
            ContainsTypes.FORMSOF,
            ContainsTypes.INFLECTIONAL,
            ContainsTypes.THESAURUS,
            ContainsTypes.NEAR,
            ContainsTypes.MAX,
            ContainsTypes.TRUE,
            ContainsTypes.FALSE,
            ContainsTypes.ISABOUT,
            ContainsTypes.WEIGHT,
            ContainsTypes.AND2,
            ContainsTypes.OR2,
            ContainsTypes.NOT
    )

    private val OperationSigns = setOf(
            ContainsTypes.COMMA,
            ContainsTypes.LPAREN,
            ContainsTypes.RPAREN,
            ContainsTypes.ASTERISK,
            ContainsTypes.OR,
            ContainsTypes.TILDA,
            ContainsTypes.AND,
            ContainsTypes.AMP_NOT,
            ContainsTypes.QUOTE
    )


    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (Keywords.contains(tokenType)) return keywordKeys
        if (OperationSigns.contains(tokenType)) return operatorKeys
        if (tokenType == ContainsTypes.STRING) return valueKeys
        return emptyKeys
    }

    override fun getHighlightingLexer(): Lexer = ContainsLexerAdapter()
}