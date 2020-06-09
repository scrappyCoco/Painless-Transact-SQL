// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class ContainsVisitor extends PsiElementVisitor {

  public void visitBoolLiteral(@NotNull ContainsBoolLiteral o) {
    visitLiteral(o);
  }

  public void visitBoolOperator(@NotNull ContainsBoolOperator o) {
    visitElement(o);
  }

  public void visitCustomProximityTerm(@NotNull ContainsCustomProximityTerm o) {
    visitTermExpression(o);
  }

  public void visitDecimalLiteral(@NotNull ContainsDecimalLiteral o) {
    visitLiteral(o);
  }

  public void visitElement(@NotNull ContainsElement o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull ContainsExpression o) {
    visitElement(o);
  }

  public void visitGenerationForm(@NotNull ContainsGenerationForm o) {
    visitPsiElement(o);
  }

  public void visitGenerationTerm(@NotNull ContainsGenerationTerm o) {
    visitTermExpression(o);
  }

  public void visitGenericProximityTerm(@NotNull ContainsGenericProximityTerm o) {
    visitTermExpression(o);
  }

  public void visitIntegerLiteral(@NotNull ContainsIntegerLiteral o) {
    visitLiteral(o);
  }

  public void visitItem(@NotNull ContainsItem o) {
    visitPsiElement(o);
  }

  public void visitLiteral(@NotNull ContainsLiteral o) {
    visitElement(o);
  }

  public void visitMatchOrder(@NotNull ContainsMatchOrder o) {
    visitPsiElement(o);
  }

  public void visitMaximumDistance(@NotNull ContainsMaximumDistance o) {
    visitPsiElement(o);
  }

  public void visitSimpleTerm(@NotNull ContainsSimpleTerm o) {
    visitTermLiteral(o);
  }

  public void visitTermExpression(@NotNull ContainsTermExpression o) {
    visitExpression(o);
  }

  public void visitTermLiteral(@NotNull ContainsTermLiteral o) {
    visitLiteral(o);
  }

  public void visitWeightOption(@NotNull ContainsWeightOption o) {
    visitExpression(o);
  }

  public void visitWeightTerm(@NotNull ContainsWeightTerm o) {
    visitTermExpression(o);
  }

  public void visitWeightedTerm(@NotNull ContainsWeightedTerm o) {
    visitTermExpression(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
