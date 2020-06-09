// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.ContainsIntegerLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsMaximumDistance;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsMaximumDistanceImpl extends ASTWrapperPsiElement implements ContainsMaximumDistance {

  public ContainsMaximumDistanceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitMaximumDistance(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ContainsIntegerLiteral getIntegerLiteral() {
    return findChildByClass(ContainsIntegerLiteral.class);
  }

}
