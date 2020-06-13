// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.ContainsBoolOperator;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsBoolOperatorImpl extends ContainsElementImpl implements ContainsBoolOperator {

  public ContainsBoolOperatorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitBoolOperator(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IElementType getOperatorElementType() {
    return ContainsPsiImplUtil.getOperatorElementType(this);
  }

}
