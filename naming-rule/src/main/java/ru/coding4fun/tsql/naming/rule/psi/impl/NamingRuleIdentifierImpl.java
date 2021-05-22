// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ru.coding4fun.tsql.naming.rule.psi.*;

public class NamingRuleIdentifierImpl extends ASTWrapperPsiElement implements NamingRuleIdentifier {

  public NamingRuleIdentifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NamingRuleVisitor visitor) {
    visitor.visitIdentifier(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NamingRuleVisitor) accept((NamingRuleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(ID);
  }

}
