// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsExpression;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsExpressionImpl extends ContainsElementImpl implements ContainsExpression {

  public ContainsExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

}
