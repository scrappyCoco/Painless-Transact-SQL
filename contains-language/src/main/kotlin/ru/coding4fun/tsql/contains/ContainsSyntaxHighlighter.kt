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

package ru.coding4fun.tsql.contains

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import ru.coding4fun.tsql.contains.psi.ContainsTypes

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

    private val keywordTypes = setOf(
            ContainsTypes.OR,
            ContainsTypes.AND,
            ContainsTypes.NOT,
            ContainsTypes.AND_NOT,
            ContainsTypes.NEAR,
            ContainsTypes.FORMS_OF,
            ContainsTypes.INFLECTIONAL,
            ContainsTypes.THESAURUS,
            ContainsTypes.TRUE,
            ContainsTypes.FALSE,
            ContainsTypes.WEIGHT,
            ContainsTypes.MAX,
            ContainsTypes.IS_ABOUT
    )

    private val operationSignTypes = setOf(
            ContainsTypes.COMMA,
            ContainsTypes.LPAREN,
            ContainsTypes.RPAREN,
            ContainsTypes.ASTERISK,
            ContainsTypes.OR_OP,
            ContainsTypes.AND_OP,
            ContainsTypes.AMP_NOT_OP,
            ContainsTypes.TILDA,
            ContainsTypes.QUOTE
    )

    private val valueTypes = setOf(
            ContainsTypes.STRING, ContainsTypes.WORD, ContainsTypes.DECIMAL, ContainsTypes.INTEGER
    )


    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (keywordTypes.contains(tokenType)) return keywordKeys
        if (operationSignTypes.contains(tokenType)) return operatorKeys
        if (valueTypes.contains(tokenType)) return valueKeys
        return emptyKeys
    }

    override fun getHighlightingLexer(): Lexer = ContainsLexerAdapter()
}