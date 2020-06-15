package ru.coding4fun.tsql.contains

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import ru.coding4fun.tsql.contains.psi.ContainsTypes

class ContainsBraceMatcher: PairedBraceMatcher {
    private val braces = arrayOf(BracePair(ContainsTypes.LPAREN, ContainsTypes.RPAREN, false))

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset
    override fun getPairs(): Array<BracePair> = braces
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = false
}