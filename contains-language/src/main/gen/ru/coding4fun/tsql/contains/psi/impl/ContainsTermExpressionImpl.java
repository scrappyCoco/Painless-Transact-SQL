// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsTermExpression;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsTermExpressionImpl extends ContainsExpressionImpl implements ContainsTermExpression {

  public ContainsTermExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitTermExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

}
