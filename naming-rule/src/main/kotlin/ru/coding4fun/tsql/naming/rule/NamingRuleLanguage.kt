package ru.coding4fun.tsql.naming.rule

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
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
import ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes
import ru.coding4fun.tsql.naming.rule.psi.grammar._NamingRuleLexer
import javax.swing.Icon

object NamingRuleLanguage: Language("Naming Rule") {
    override fun isCaseSensitive(): Boolean = true
}

object NamingRuleFileType : LanguageFileType(NamingRuleLanguage) {
    override fun getIcon(): Icon = AllIcons.Actions.Replace
    override fun getName(): String = "Naming Rule File"
    override fun getDefaultExtension(): String = "nmr"
    override fun getDescription(): String = "Naming rule"
}

class NamingRuleTokenType(debugName: String) : IElementType(debugName, NamingRuleLanguage) {
    override fun toString(): String = "NamingRuleTokenType." + super.toString()
}

class NamingRuleElementType(debugName: String) : IElementType(debugName, NamingRuleLanguage)

class NamingRuleLexerAdapter : FlexAdapter(_NamingRuleLexer(null))

class NamingRuleFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NamingRuleLanguage) {
    override fun getFileType(): FileType = NamingRuleFileType
    override fun toString(): String = "Naming Rule File"
}

class NamingRuleParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = NamingRuleLexerAdapter()
    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES
    override fun getCommentTokens(): TokenSet = TokenSet.create(NamingRuleTypes.LINE_COMMENT)
    override fun getStringLiteralElements(): TokenSet = TokenSet.create(NamingRuleTypes.STRING)
    override fun createParser(project: Project?): PsiParser = NamingRuleParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun createFile(viewProvider: FileViewProvider): PsiFile = NamingRuleFile(viewProvider)
    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements = ParserDefinition.SpaceRequirements.MAY
    override fun createElement(node: ASTNode?): PsiElement = NamingRuleTypes.Factory.createElement(node)

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val FILE = IFileElementType(NamingRuleLanguage)
    }
}