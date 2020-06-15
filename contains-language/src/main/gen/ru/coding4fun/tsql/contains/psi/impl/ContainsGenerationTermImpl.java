// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsGenerationForm;
import ru.coding4fun.tsql.contains.psi.ContainsGenerationTerm;
import ru.coding4fun.tsql.contains.psi.ContainsLiteral;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

import java.util.List;

public class ContainsGenerationTermImpl extends ContainsTermExpressionImpl implements ContainsGenerationTerm {

  public ContainsGenerationTermImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitGenerationTerm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ContainsGenerationForm getGenerationForm() {
    return findNotNullChildByClass(ContainsGenerationForm.class);
  }

  @Override
  @NotNull
  public List<ContainsLiteral> getLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ContainsLiteral.class);
  }

}
