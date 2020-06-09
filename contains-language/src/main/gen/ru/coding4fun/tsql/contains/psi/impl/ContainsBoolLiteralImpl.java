// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsBoolLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsBoolLiteralImpl extends ContainsLiteralImpl implements ContainsBoolLiteral {

  public ContainsBoolLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitBoolLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public boolean isTrue() {
    return ContainsPsiImplUtil.isTrue(this);
  }

}
