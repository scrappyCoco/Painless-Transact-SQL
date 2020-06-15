// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsStringLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

import static ru.coding4fun.tsql.contains.psi.ContainsTypes.STRING;

public class ContainsStringLiteralImpl extends ContainsLiteralImpl implements ContainsStringLiteral {

  public ContainsStringLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitStringLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    return findNotNullChildByType(STRING);
  }

}
