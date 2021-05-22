// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.naming.rule;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes.*;
import static ru.coding4fun.tsql.naming.rule.NamingRuleParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class NamingRuleParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(AND_EXPR, CALL_EXPR, COMPARE_EXPR, CONDITIONAL_EXPR,
      EXPR, INTEGER_LITERAL_EXPR, LITERAL_EXPR, NOT_EXPR,
      OR_EXPR, PAREN_EXPR, PLUS_EXPR, STRING_LITERAL_EXPR),
  };

  /* ********************************************************** */
  // '(' [ !')' expr?  (',' expr) * ] ')'
  public static boolean arg_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARG_LIST, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, arg_list_1(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // [ !')' expr?  (',' expr) * ]
  private static boolean arg_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1")) return false;
    arg_list_1_0(b, l + 1);
    return true;
  }

  // !')' expr?  (',' expr) *
  private static boolean arg_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = arg_list_1_0_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, arg_list_1_0_1(b, l + 1));
    r = p && arg_list_1_0_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // !')'
  private static boolean arg_list_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr?
  private static boolean arg_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_1")) return false;
    expr(b, l + 1, -1);
    return true;
  }

  // (',' expr) *
  private static boolean arg_list_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arg_list_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arg_list_1_0_2", c)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean arg_list_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ",");
    p = r; // pin = 1
    r = r && expr(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '==' | '!=' | '>' | '>=' | '<' | '<='
  public static boolean compare_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compare_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPARE_OPERATOR, "<compare operator>");
    r = consumeToken(b, OP_EQ_EQ);
    if (!r) r = consumeToken(b, OP_NOT_EQ);
    if (!r) r = consumeToken(b, OP_GT);
    if (!r) r = consumeToken(b, OP_GTE);
    if (!r) r = consumeToken(b, OP_LT);
    if (!r) r = consumeToken(b, OP_LTE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  static boolean element(PsiBuilder b, int l) {
    return expr(b, l + 1, -1);
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    exit_section_(b, m, IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // integer
  public static boolean integer_literal_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "integer_literal_expr")) return false;
    if (!nextTokenIs(b, INTEGER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTEGER);
    exit_section_(b, m, INTEGER_LITERAL_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // element *
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    while (true) {
      int c = current_position_(b);
      if (!element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean string_literal_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal_expr")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, STRING_LITERAL_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: ATOM(conditional_expr)
  // 1: ATOM(call_expr)
  // 2: BINARY(plus_expr)
  // 3: BINARY(or_expr)
  // 4: BINARY(and_expr)
  // 5: PREFIX(not_expr)
  // 6: BINARY(compare_expr)
  // 7: ATOM(paren_expr)
  // 8: ATOM(literal_expr)
  public static boolean expr(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr")) return false;
    addVariant(b, "<expr>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expr>");
    r = conditional_expr(b, l + 1);
    if (!r) r = call_expr(b, l + 1);
    if (!r) r = not_expr(b, l + 1);
    if (!r) r = paren_expr(b, l + 1);
    if (!r) r = literal_expr(b, l + 1);
    p = r;
    r = r && expr_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expr_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 2 && consumeTokenSmart(b, OP_ADD)) {
        r = expr(b, l, 2);
        exit_section_(b, l, m, PLUS_EXPR, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, OP_OR)) {
        r = expr(b, l, 3);
        exit_section_(b, l, m, OR_EXPR, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, OP_AND)) {
        r = expr(b, l, 4);
        exit_section_(b, l, m, AND_EXPR, r, true, null);
      }
      else if (g < 6 && compare_operator(b, l + 1)) {
        r = expr(b, l, 6);
        exit_section_(b, l, m, COMPARE_EXPR, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // 'if' '(' (expr) ')' expr 'else' expr
  public static boolean conditional_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conditional_expr")) return false;
    if (!nextTokenIsSmart(b, IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, IF, LBRACE);
    r = r && conditional_expr_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, ELSE);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, CONDITIONAL_EXPR, r);
    return r;
  }

  // (expr)
  private static boolean conditional_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conditional_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // identifier arg_list
  public static boolean call_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr")) return false;
    if (!nextTokenIsSmart(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && arg_list(b, l + 1);
    exit_section_(b, m, CALL_EXPR, r);
    return r;
  }

  public static boolean not_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_expr")) return false;
    if (!nextTokenIsSmart(b, NOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, NOT);
    p = r;
    r = p && expr(b, l, 5);
    exit_section_(b, l, m, NOT_EXPR, r, p, null);
    return r || p;
  }

  // '(' + expr + ')'
  public static boolean paren_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expr")) return false;
    if (!nextTokenIsSmart(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = paren_expr_0(b, l + 1);
    r = r && paren_expr_1(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, PAREN_EXPR, r);
    return r;
  }

  // '(' +
  private static boolean paren_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LBRACE);
    while (r) {
      int c = current_position_(b);
      if (!consumeTokenSmart(b, LBRACE)) break;
      if (!empty_element_parsed_guard_(b, "paren_expr_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // expr +
  private static boolean paren_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1, -1);
    while (r) {
      int c = current_position_(b);
      if (!expr(b, l + 1, -1)) break;
      if (!empty_element_parsed_guard_(b, "paren_expr_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // string_literal_expr | integer_literal_expr
  public static boolean literal_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expr")) return false;
    if (!nextTokenIsSmart(b, INTEGER, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, LITERAL_EXPR, "<literal expr>");
    r = string_literal_expr(b, l + 1);
    if (!r) r = integer_literal_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
