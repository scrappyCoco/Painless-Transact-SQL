// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsElement;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;
import ru.coding4fun.tsql.contains.psi.ContainsWeightedTerm;

import java.util.List;

public class ContainsWeightedTermImpl extends ContainsTermExpressionImpl implements ContainsWeightedTerm {

  public ContainsWeightedTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitWeightedTerm(this);
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

}
