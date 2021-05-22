package ru.coding4fun.tsql.naming.rule

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes

class NamingRuleSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter = NamingRuleSyntaxHighlighter()
}

class NamingRuleSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = NamingRuleLexerAdapter()

    val SEPARATOR: TextAttributesKey = createTextAttributesKey("NAMING_RULE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val KEY: TextAttributesKey = createTextAttributesKey("NAMING_RULE_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val VALUE: TextAttributesKey = createTextAttributesKey("NAMING_RULE_VALUE", DefaultLanguageHighlighterColors.STRING)
    val COMMENT: TextAttributesKey = createTextAttributesKey("NAMING_RULE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("NAMING_RULE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

    private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
    private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
    private val KEY_KEYS = arrayOf(KEY)
    private val VALUE_KEYS = arrayOf(VALUE)
    private val COMMENT_KEYS = arrayOf(COMMENT)
    private val EMPTY_KEYS = arrayOf<TextAttributesKey>()

    private val keywordTypes = setOf(NamingRuleTypes.OP_ADD,
            NamingRuleTypes.OP_OR,
            NamingRuleTypes.OP_AND,
            NamingRuleTypes.OP_EQ_EQ,
            NamingRuleTypes.OP_NOT_EQ,
            NamingRuleTypes.OP_GT,
            NamingRuleTypes.OP_GTE,
            NamingRuleTypes.OP_LT,
            NamingRuleTypes.OP_LTE,
            NamingRuleTypes.IF,
            NamingRuleTypes.ELSE,
            NamingRuleTypes.LBRACE,
            NamingRuleTypes.RBRACE,
            NamingRuleTypes.NOT,
    )

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (keywordTypes.contains(tokenType)) return KEY_KEYS
        if (tokenType == NamingRuleTypes.LINE_COMMENT) return COMMENT_KEYS
        if (tokenType == NamingRuleTypes.LBRACE) return SEPARATOR_KEYS
        if (tokenType == NamingRuleTypes.RBRACE) return SEPARATOR_KEYS
        if (tokenType == NamingRuleTypes.STRING) return VALUE_KEYS
        if (tokenType == NamingRuleTypes.INTEGER) return VALUE_KEYS
        return EMPTY_KEYS
    }
}