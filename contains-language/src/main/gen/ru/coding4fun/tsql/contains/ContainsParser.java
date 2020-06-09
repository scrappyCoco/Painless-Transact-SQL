// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static ru.coding4fun.tsql.contains.psi.ContainsTypes.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ContainsParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
          create_token_set_(BOOL_LITERAL, BOOL_OPERATOR, CUSTOM_PROXIMITY_TERM, DECIMAL_LITERAL,
                  GENERATION_TERM, GENERIC_PROXIMITY_TERM, INTEGER_LITERAL, SIMPLE_TERM,
                  WEIGHTED_TERM, WEIGHT_OPTION, WEIGHT_TERM),
  };

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return contains_search_condition(b, l + 1);
  }

  /* ********************************************************** */
  // decimal
  public static boolean decimal_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decimal_literal")) return false;
    if (!nextTokenIs(b, DECIMAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DECIMAL);
    exit_section_(b, m, DECIMAL_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // AND_NOT_OP | AND_NOT
  static boolean and_not_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "and_not_operator")) return false;
    if (!nextTokenIs(b, "", AND_NOT, AND_NOT_OP)) return false;
    boolean r;
    r = consumeToken(b, AND_NOT_OP);
    if (!r) r = consumeToken(b, AND_NOT);
    return r;
  }

  /* ********************************************************** */
  // AND_OP | AND
  static boolean and_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "and_operator")) return false;
    if (!nextTokenIs(b, "", AND, AND_OP)) return false;
    boolean r;
    r = consumeToken(b, AND_OP);
    if (!r) r = consumeToken(b, AND);
    return r;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean bool_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bool_literal")) return false;
    if (!nextTokenIs(b, "<bool literal>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOL_LITERAL, "<bool literal>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // and_operator | and_not_operator | or_operator
  public static boolean bool_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bool_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOL_OPERATOR, "<bool operator>");
    r = and_operator(b, l + 1);
    if (!r) r = and_not_operator(b, l + 1);
    if (!r) r = or_operator(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // <<param>> { COMMA <<param>> }*
  static boolean comma_separated_list(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "comma_separated_list")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = _param.parse(b, l);
    r = r && comma_separated_list_1(b, l + 1, _param);
    exit_section_(b, m, null, r);
    return r;
  }

  // { COMMA <<param>> }*
  private static boolean comma_separated_list_1(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "comma_separated_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!comma_separated_list_1_0(b, l + 1, _param)) break;
      if (!empty_element_parsed_guard_(b, "comma_separated_list_1", c)) break;
    }
    return true;
  }

  // COMMA <<param>>
  private static boolean comma_separated_list_1_0(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "comma_separated_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && _param.parse(b, l);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // item { bool_operator item }*
  static boolean contains_search_condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contains_search_condition")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = item(b, l + 1);
    r = r && contains_search_condition_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // { bool_operator item }*
  private static boolean contains_search_condition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contains_search_condition_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!contains_search_condition_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "contains_search_condition_1", c)) break;
    }
    return true;
  }

  // bool_operator item
  private static boolean contains_search_condition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contains_search_condition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bool_operator(b, l + 1);
    r = r && item(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NEAR LPAREN {
  //     <<comma_separated_list simple_term>> | { LPAREN <<comma_separated_list simple_term>> RPAREN }
  //     [ COMMA maximum_distance [COMMA match_order ] ]
  //   } RPAREN
  public static boolean custom_proximity_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term")) return false;
    if (!nextTokenIs(b, NEAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NEAR, LPAREN);
    r = r && custom_proximity_term_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, CUSTOM_PROXIMITY_TERM, r);
    return r;
  }

  // <<comma_separated_list simple_term>> | { LPAREN <<comma_separated_list simple_term>> RPAREN }
  //     [ COMMA maximum_distance [COMMA match_order ] ]
  private static boolean custom_proximity_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comma_separated_list(b, l + 1, simple_term_parser_);
    if (!r) r = custom_proximity_term_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // { LPAREN <<comma_separated_list simple_term>> RPAREN }
  //     [ COMMA maximum_distance [COMMA match_order ] ]
  private static boolean custom_proximity_term_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = custom_proximity_term_2_1_0(b, l + 1);
    r = r && custom_proximity_term_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN <<comma_separated_list simple_term>> RPAREN
  private static boolean custom_proximity_term_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && comma_separated_list(b, l + 1, simple_term_parser_);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // [ COMMA maximum_distance [COMMA match_order ] ]
  private static boolean custom_proximity_term_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1_1")) return false;
    custom_proximity_term_2_1_1_0(b, l + 1);
    return true;
  }

  // COMMA maximum_distance [COMMA match_order ]
  private static boolean custom_proximity_term_2_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && maximum_distance(b, l + 1);
    r = r && custom_proximity_term_2_1_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [COMMA match_order ]
  private static boolean custom_proximity_term_2_1_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1_1_0_2")) return false;
    custom_proximity_term_2_1_1_0_2_0(b, l + 1);
    return true;
  }

  // COMMA match_order
  private static boolean custom_proximity_term_2_1_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "custom_proximity_term_2_1_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && match_order(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simple_term { { NEAR | TILDA } simple_term }*
  public static boolean generic_proximity_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_proximity_term")) return false;
    if (!nextTokenIs(b, "<generic proximity term>", STRING, WORD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, GENERIC_PROXIMITY_TERM, "<generic proximity term>");
    r = simple_term(b, l + 1);
    r = r && generic_proximity_term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // INFLECTIONAL | THESAURUS
  public static boolean generation_form(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generation_form")) return false;
    if (!nextTokenIs(b, "<generation form>", INFLECTIONAL, THESAURUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERATION_FORM, "<generation form>");
    r = consumeToken(b, INFLECTIONAL);
    if (!r) r = consumeToken(b, THESAURUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FORMS_OF LPAREN generation_form COMMA simple_term+ RPAREN
  public static boolean generation_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generation_term")) return false;
    if (!nextTokenIs(b, FORMS_OF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FORMS_OF, LPAREN);
    r = r && generation_form(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && generation_term_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, GENERATION_TERM, r);
    return r;
  }

  // simple_term+
  private static boolean generation_term_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generation_term_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = simple_term(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!simple_term(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "generation_term_4", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // integer
  public static boolean integer_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "integer_literal")) return false;
    if (!nextTokenIs(b, INTEGER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTEGER);
    exit_section_(b, m, INTEGER_LITERAL, r);
    return r;
  }

  // { { NEAR | TILDA } simple_term }*
  private static boolean generic_proximity_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_proximity_term_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!generic_proximity_term_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "generic_proximity_term_1", c)) break;
    }
    return true;
  }

  // { NEAR | TILDA } simple_term
  private static boolean generic_proximity_term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_proximity_term_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = generic_proximity_term_1_0_0(b, l + 1);
    r = r && simple_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NEAR | TILDA
  private static boolean generic_proximity_term_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_proximity_term_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, NEAR);
    if (!r) r = consumeToken(b, TILDA);
    return r;
  }

  /* ********************************************************** */
  // custom_proximity_term | weighted_term | generation_term | generic_proximity_term | simple_term
  public static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ITEM, "<item>");
    r = custom_proximity_term(b, l + 1);
    if (!r) r = weighted_term(b, l + 1);
    if (!r) r = generation_term(b, l + 1);
    if (!r) r = generic_proximity_term(b, l + 1);
    if (!r) r = simple_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // bool_literal
  public static boolean match_order(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "match_order")) return false;
    if (!nextTokenIs(b, "<match order>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MATCH_ORDER, "<match order>");
    r = bool_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // weight_term [ WEIGHT LPAREN decimal_literal RPAREN ]
  public static boolean weight_option(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "weight_option")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, WEIGHT_OPTION, "<weight option>");
    r = weight_term(b, l + 1);
    r = r && weight_option_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // integer_literal | MAX
  public static boolean maximum_distance(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maximum_distance")) return false;
    if (!nextTokenIs(b, "<maximum distance>", INTEGER, MAX)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAXIMUM_DISTANCE, "<maximum distance>");
    r = integer_literal(b, l + 1);
    if (!r) r = consumeToken(b, MAX);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OR_OP | OR
  static boolean or_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "or_operator")) return false;
    if (!nextTokenIs(b, "", OR, OR_OP)) return false;
    boolean r;
    r = consumeToken(b, OR_OP);
    if (!r) r = consumeToken(b, OR);
    return r;
  }

  /* ********************************************************** */
  // word | string
  public static boolean simple_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_term")) return false;
    if (!nextTokenIs(b, "<simple term>", STRING, WORD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_TERM, "<simple term>");
    r = consumeToken(b, WORD);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [ WEIGHT LPAREN decimal_literal RPAREN ]
  private static boolean weight_option_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "weight_option_1")) return false;
    weight_option_1_0(b, l + 1);
    return true;
  }

  // WEIGHT LPAREN decimal_literal RPAREN
  private static boolean weight_option_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "weight_option_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WEIGHT, LPAREN);
    r = r && decimal_literal(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  /* ********************************************************** */
  // simple_term | generation_term
  public static boolean weight_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "weight_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, WEIGHT_TERM, "<weight term>");
    r = simple_term(b, l + 1);
    if (!r) r = generation_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IS_ABOUT LPAREN { <<comma_separated_list weight_option>> } RPAREN
  public static boolean weighted_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "weighted_term")) return false;
    if (!nextTokenIs(b, IS_ABOUT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IS_ABOUT, LPAREN);
    r = r && weighted_term_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, WEIGHTED_TERM, r);
    return r;
  }

  // <<comma_separated_list weight_option>>
  private static boolean weighted_term_2(PsiBuilder b, int l) {
    return comma_separated_list(b, l + 1, weight_option_parser_);
  }

  static final Parser simple_term_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return simple_term(b, l + 1);
    }
  };
  static final Parser weight_option_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return weight_option(b, l + 1);
    }
  };
}
