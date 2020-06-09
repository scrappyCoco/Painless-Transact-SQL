// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsTermLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsTermLiteralImpl extends ContainsLiteralImpl implements ContainsTermLiteral {

  public ContainsTermLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitTermLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor) visitor);
    else super.accept(visitor);
  }

}
