// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsDecimalLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

import static ru.coding4fun.tsql.contains.psi.ContainsTypes.DECIMAL;

public class ContainsDecimalLiteralImpl extends ContainsLiteralImpl implements ContainsDecimalLiteral {

  public ContainsDecimalLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitDecimalLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getDecimal() {
    return findNotNullChildByType(DECIMAL);
  }

}
