// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsBoolLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsMatchOrder;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsMatchOrderImpl extends ASTWrapperPsiElement implements ContainsMatchOrder {

  public ContainsMatchOrderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitMatchOrder(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ContainsBoolLiteral getBoolLiteral() {
    return findNotNullChildByClass(ContainsBoolLiteral.class);
  }

}
