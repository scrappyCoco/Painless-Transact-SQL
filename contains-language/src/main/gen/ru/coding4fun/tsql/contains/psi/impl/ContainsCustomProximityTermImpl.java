// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.*;

import java.util.List;

public class ContainsCustomProximityTermImpl extends ContainsTermExpressionImpl implements ContainsCustomProximityTerm {

  public ContainsCustomProximityTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitCustomProximityTerm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ContainsMatchOrder getMatchOrder() {
    return findChildByClass(ContainsMatchOrder.class);
  }

  @Override
  @Nullable
  public ContainsMaximumDistance getMaximumDistance() {
    return findChildByClass(ContainsMaximumDistance.class);
  }

  @Override
  @NotNull
  public List<ContainsSimpleTerm> getSimpleTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ContainsSimpleTerm.class);
  }

}
