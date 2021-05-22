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

public class NamingRuleCompareExprImpl extends NamingRuleExprImpl implements NamingRuleCompareExpr {

  public NamingRuleCompareExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull NamingRuleVisitor visitor) {
    visitor.visitCompareExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NamingRuleVisitor) accept((NamingRuleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public NamingRuleCompareOperator getCompareOperator() {
    return findNotNullChildByClass(NamingRuleCompareOperator.class);
  }

  @Override
  @NotNull
  public List<NamingRuleExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, NamingRuleExpr.class);
  }

}
