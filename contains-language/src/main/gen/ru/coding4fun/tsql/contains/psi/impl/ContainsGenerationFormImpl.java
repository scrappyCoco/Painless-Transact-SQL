// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.tsql.contains.psi.ContainsGenerationForm;
import ru.coding4fun.tsql.contains.psi.ContainsVisitor;

public class ContainsGenerationFormImpl extends ASTWrapperPsiElement implements ContainsGenerationForm {

  public ContainsGenerationFormImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ContainsVisitor visitor) {
    visitor.visitGenerationForm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ContainsVisitor) accept((ContainsVisitor)visitor);
    else super.accept(visitor);
  }

}
