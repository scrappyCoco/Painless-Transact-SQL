// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsIntegerLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

import static ru.coding4fun.tsql.contains.psi.ContainsTypes.INTEGER;

public class ContainsIntegerLiteralImpl extends ContainsLiteralImpl implements ContainsIntegerLiteral {

  public ContainsIntegerLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitIntegerLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getInteger() {
    return findNotNullChildByType(INTEGER);
  }

}
