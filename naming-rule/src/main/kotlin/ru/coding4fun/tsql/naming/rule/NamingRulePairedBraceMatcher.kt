package ru.coding4fun.tsql.naming.rule

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes

class NamingRulePairedBraceMatcher: PairedBraceMatcher {
    val braces = arrayOf(BracePair(NamingRuleTypes.LBRACE, NamingRuleTypes.RBRACE, false))
    override fun getPairs(): Array<BracePair> = braces

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset
}