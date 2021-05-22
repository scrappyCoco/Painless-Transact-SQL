// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface NamingRuleCompareExpr extends NamingRuleExpr {

  @NotNull
  NamingRuleCompareOperator getCompareOperator();

  @NotNull
  List<NamingRuleExpr> getExprList();

}
