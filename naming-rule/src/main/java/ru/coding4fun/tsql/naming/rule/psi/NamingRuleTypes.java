// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ru.coding4fun.tsql.naming.rule.NamingRuleElementType;
import ru.coding4fun.tsql.naming.rule.NamingRuleTokenType;
import ru.coding4fun.tsql.naming.rule.psi.impl.*;

public interface NamingRuleTypes {

  IElementType AND_EXPR = new NamingRuleElementType("AND_EXPR");
  IElementType ARG_LIST = new NamingRuleElementType("ARG_LIST");
  IElementType CALL_EXPR = new NamingRuleElementType("CALL_EXPR");
  IElementType COMPARE_EXPR = new NamingRuleElementType("COMPARE_EXPR");
  IElementType COMPARE_OPERATOR = new NamingRuleElementType("COMPARE_OPERATOR");
  IElementType CONDITIONAL_EXPR = new NamingRuleElementType("CONDITIONAL_EXPR");
  IElementType EXPR = new NamingRuleElementType("EXPR");
  IElementType IDENTIFIER = new NamingRuleElementType("IDENTIFIER");
  IElementType INTEGER_LITERAL_EXPR = new NamingRuleElementType("INTEGER_LITERAL_EXPR");
  IElementType LITERAL_EXPR = new NamingRuleElementType("LITERAL_EXPR");
  IElementType NOT_EXPR = new NamingRuleElementType("NOT_EXPR");
  IElementType OR_EXPR = new NamingRuleElementType("OR_EXPR");
  IElementType PAREN_EXPR = new NamingRuleElementType("PAREN_EXPR");
  IElementType PLUS_EXPR = new NamingRuleElementType("PLUS_EXPR");
  IElementType STRING_LITERAL_EXPR = new NamingRuleElementType("STRING_LITERAL_EXPR");

  IElementType ELSE = new NamingRuleTokenType("else");
  IElementType ID = new NamingRuleTokenType("id");
  IElementType IF = new NamingRuleTokenType("if");
  IElementType INTEGER = new NamingRuleTokenType("integer");
  IElementType LBRACE = new NamingRuleTokenType("(");
  IElementType LINE_COMMENT = new NamingRuleTokenType("line_comment");
  IElementType NOT = new NamingRuleTokenType("!");
  IElementType OP_ADD = new NamingRuleTokenType("+");
  IElementType OP_AND = new NamingRuleTokenType("&&");
  IElementType OP_EQ_EQ = new NamingRuleTokenType("==");
  IElementType OP_GT = new NamingRuleTokenType(">");
  IElementType OP_GTE = new NamingRuleTokenType(">=");
  IElementType OP_LT = new NamingRuleTokenType("<");
  IElementType OP_LTE = new NamingRuleTokenType("<=");
  IElementType OP_NOT_EQ = new NamingRuleTokenType("!=");
  IElementType OP_OR = new NamingRuleTokenType("||");
  IElementType RBRACE = new NamingRuleTokenType(")");
  IElementType STRING = new NamingRuleTokenType("string");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == AND_EXPR) {
        return new NamingRuleAndExprImpl(node);
      }
      else if (type == ARG_LIST) {
        return new NamingRuleArgListImpl(node);
      }
      else if (type == CALL_EXPR) {
        return new NamingRuleCallExprImpl(node);
      }
      else if (type == COMPARE_EXPR) {
        return new NamingRuleCompareExprImpl(node);
      }
      else if (type == COMPARE_OPERATOR) {
        return new NamingRuleCompareOperatorImpl(node);
      }
      else if (type == CONDITIONAL_EXPR) {
        return new NamingRuleConditionalExprImpl(node);
      }
      else if (type == IDENTIFIER) {
        return new NamingRuleIdentifierImpl(node);
      }
      else if (type == INTEGER_LITERAL_EXPR) {
        return new NamingRuleIntegerLiteralExprImpl(node);
      }
      else if (type == NOT_EXPR) {
        return new NamingRuleNotExprImpl(node);
      }
      else if (type == OR_EXPR) {
        return new NamingRuleOrExprImpl(node);
      }
      else if (type == PAREN_EXPR) {
        return new NamingRuleParenExprImpl(node);
      }
      else if (type == PLUS_EXPR) {
        return new NamingRulePlusExprImpl(node);
      }
      else if (type == STRING_LITERAL_EXPR) {
        return new NamingRuleStringLiteralExprImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
