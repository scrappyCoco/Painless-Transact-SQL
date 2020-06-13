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

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.PsiParser
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import ru.coding4fun.tsql.contains.psi.ContainsTypes
import javax.swing.Icon


object ContainsLanguage : Language("Contains") {
    override fun isCaseSensitive(): Boolean = false
}

object ContainsFileType : LanguageFileType(ContainsLanguage) {
    override fun getIcon(): Icon? = AllIcons.Actions.Replace
    override fun getName(): String = "Contains File"
    override fun getDefaultExtension(): String = "contains"
    override fun getDescription(): String = "Contains search for T-SQL"
}

class ContainsTokenType(debugName: String) : IElementType(debugName, ContainsLanguage) {
    override fun toString(): String = "ContainsTokenType." + super.toString()
}

class ContainsElementType(debugName: String) : IElementType(debugName, ContainsLanguage)

class ContainsLexerAdapter : FlexAdapter(_ContainsLexer(null))

class ContainsFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ContainsLanguage) {
    override fun getFileType(): FileType = ContainsFileType
    override fun toString(): String = "Contains File"
}

class ContainsParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = ContainsLexerAdapter()
    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createParser(project: Project?): PsiParser = ContainsParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = ContainsFile(viewProvider)
    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): SpaceRequirements = SpaceRequirements.MAY
    override fun createElement(node: ASTNode?): PsiElement = ContainsTypes.Factory.createElement(node)

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val FILE = IFileElementType(ContainsLanguage)
    }
}