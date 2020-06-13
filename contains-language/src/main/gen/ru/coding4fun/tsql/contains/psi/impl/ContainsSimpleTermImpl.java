// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.ContainsSimpleTerm;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

import static ru.coding4fun.tsql.contains.psi.ContainsTypes.STRING;
import static ru.coding4fun.tsql.contains.psi.ContainsTypes.WORD;

public class ContainsSimpleTermImpl extends ContainsTermLiteralImpl implements ContainsSimpleTerm {

  public ContainsSimpleTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitSimpleTerm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

  @Override
  @Nullable
  public PsiElement getWord() {
    return findChildByType(WORD);
  }

}
