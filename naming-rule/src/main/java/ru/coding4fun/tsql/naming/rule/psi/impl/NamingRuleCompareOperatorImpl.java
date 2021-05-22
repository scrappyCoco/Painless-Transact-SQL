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

public class NamingRuleCompareOperatorImpl extends ASTWrapperPsiElement implements NamingRuleCompareOperator {

  public NamingRuleCompareOperatorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NamingRuleVisitor visitor) {
    visitor.visitCompareOperator(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NamingRuleVisitor) accept((NamingRuleVisitor)visitor);
    else super.accept(visitor);
  }

}
