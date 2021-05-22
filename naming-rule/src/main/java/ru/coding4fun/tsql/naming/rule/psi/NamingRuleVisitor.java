// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class NamingRuleVisitor extends PsiElementVisitor {

  public void visitAndExpr(@NotNull NamingRuleAndExpr o) {
    visitExpr(o);
  }

  public void visitArgList(@NotNull NamingRuleArgList o) {
    visitPsiElement(o);
  }

  public void visitCallExpr(@NotNull NamingRuleCallExpr o) {
    visitExpr(o);
  }

  public void visitCompareExpr(@NotNull NamingRuleCompareExpr o) {
    visitExpr(o);
  }

  public void visitCompareOperator(@NotNull NamingRuleCompareOperator o) {
    visitPsiElement(o);
  }

  public void visitConditionalExpr(@NotNull NamingRuleConditionalExpr o) {
    visitExpr(o);
  }

  public void visitExpr(@NotNull NamingRuleExpr o) {
    visitPsiElement(o);
  }

  public void visitIdentifier(@NotNull NamingRuleIdentifier o) {
    visitPsiElement(o);
  }

  public void visitIntegerLiteralExpr(@NotNull NamingRuleIntegerLiteralExpr o) {
    visitExpr(o);
  }

  public void visitLiteralExpr(@NotNull NamingRuleLiteralExpr o) {
    visitExpr(o);
  }

  public void visitNotExpr(@NotNull NamingRuleNotExpr o) {
    visitExpr(o);
  }

  public void visitOrExpr(@NotNull NamingRuleOrExpr o) {
    visitExpr(o);
  }

  public void visitParenExpr(@NotNull NamingRuleParenExpr o) {
    visitExpr(o);
  }

  public void visitPlusExpr(@NotNull NamingRulePlusExpr o) {
    visitExpr(o);
  }

  public void visitStringLiteralExpr(@NotNull NamingRuleStringLiteralExpr o) {
    visitExpr(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
