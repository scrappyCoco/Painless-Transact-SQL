// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes.*;
import ru.coding4fun.tsql.naming.rule.psi.*;

public class NamingRulePlusExprImpl extends NamingRuleExprImpl implements NamingRulePlusExpr {

  public NamingRulePlusExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull NamingRuleVisitor visitor) {
    visitor.visitPlusExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NamingRuleVisitor) accept((NamingRuleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<NamingRuleExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NamingRuleExpr.class);
  }

}
