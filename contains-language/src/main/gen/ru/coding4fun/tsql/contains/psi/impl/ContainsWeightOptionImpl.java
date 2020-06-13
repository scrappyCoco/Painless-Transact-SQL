// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.tsql.contains.psi.ContainsElement;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;
import ru.coding4fun.tsql.contains.psi.ContainsWeightOption;

import java.util.List;

public class ContainsWeightOptionImpl extends ContainsExpressionImpl implements ContainsWeightOption {

  public ContainsWeightOptionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitWeightOption(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ContainsElement> getElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ContainsElement.class);
  }

  @Override
  @Nullable
  public Float getWeightValue() {
    return ContainsPsiImplUtil.getWeightValue(this);
  }

  @Override
  @Nullable
  public ContainsElement getWeightLiteral() {
    List<ContainsElement> p1 = getElementList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
