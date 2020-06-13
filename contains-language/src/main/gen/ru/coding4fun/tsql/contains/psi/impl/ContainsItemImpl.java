// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsElement;
import ru.coding4fun.tsql.contains.psi.ContainsItem;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsItemImpl extends ASTWrapperPsiElement implements ContainsItem {

  public ContainsItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ContainsElement getElement() {
    return findNotNullChildByClass(ContainsElement.class);
  }

}
