/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

import static ru.coding4fun.tsql.lang.ContainsParserUtil.*;
import static ru.coding4fun.tsql.lang.psi.ContainsTypes.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ContainsParser implements PsiParser, LightPsiParser {

    static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
        return contains_search_condition(b, l + 1);
    }

    /* ********************************************************** */
    // { AND NOT } | AMP_NOT
    public static boolean and_not_op(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "and_not_op")) return false;
        if (!nextTokenIs(b, "<and not op>", AMP_NOT, AND)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, AND_NOT_OP, "<and not op>");
        r = and_not_op_0(b, l + 1);
        if (!r) r = consumeToken(b, AMP_NOT);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // AND NOT
    private static boolean and_not_op_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "and_not_op_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, AND, NOT);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // AND | AND2
    public static boolean and_op(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "and_op")) return false;
        if (!nextTokenIs(b, "<and op>", AND, AND2)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, AND_OP, "<and op>");
        r = consumeToken(b, AND);
        if (!r) r = consumeToken(b, AND2);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // { generic_proximity_term | weighted_term | simple_term | generation_term | custom_proximity_term }
    //   { { and_op | and_not_op | or_op } contains_search_condition }*
    static boolean contains_search_condition(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "contains_search_condition")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = contains_search_condition_0(b, l + 1);
        r = r && contains_search_condition_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // generic_proximity_term | weighted_term | simple_term | generation_term | custom_proximity_term
    private static boolean contains_search_condition_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "contains_search_condition_0")) return false;
        boolean r;
        r = generic_proximity_term(b, l + 1);
        if (!r) r = weighted_term(b, l + 1);
        if (!r) r = simple_term(b, l + 1);
        if (!r) r = generation_term(b, l + 1);
        if (!r) r = custom_proximity_term(b, l + 1);
        return r;
    }

    // { { and_op | and_not_op | or_op } contains_search_condition }*
    private static boolean contains_search_condition_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "contains_search_condition_1")) return false;
        while (true) {
            int c = current_position_(b);
            if (!contains_search_condition_1_0(b, l + 1)) break;
            if (!empty_element_parsed_guard_(b, "contains_search_condition_1", c)) break;
        }
        return true;
    }

    // { and_op | and_not_op | or_op } contains_search_condition
    private static boolean contains_search_condition_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "contains_search_condition_1_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = contains_search_condition_1_0_0(b, l + 1);
        r = r && contains_search_condition(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // and_op | and_not_op | or_op
    private static boolean contains_search_condition_1_0_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "contains_search_condition_1_0_0")) return false;
        boolean r;
        r = and_op(b, l + 1);
        if (!r) r = and_not_op(b, l + 1);
        if (!r) r = or_op(b, l + 1);
        return r;
    }

    /* ********************************************************** */
    // NEAR LPAREN {
    //     simple_term { COMMA simple_term }* | { LPAREN simple_term { COMMA simple_term }* RPAREN }
    //      [COMMA maximum_distance [COMMA match_order ] ]
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

    // simple_term { COMMA simple_term }* | { LPAREN simple_term { COMMA simple_term }* RPAREN }
    //      [COMMA maximum_distance [COMMA match_order ] ]
    private static boolean custom_proximity_term_2(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = custom_proximity_term_2_0(b, l + 1);
        if (!r) r = custom_proximity_term_2_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // simple_term { COMMA simple_term }*
    private static boolean custom_proximity_term_2_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = simple_term(b, l + 1);
        r = r && custom_proximity_term_2_0_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // { COMMA simple_term }*
    private static boolean custom_proximity_term_2_0_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_0_1")) return false;
        while (true) {
            int c = current_position_(b);
            if (!custom_proximity_term_2_0_1_0(b, l + 1)) break;
            if (!empty_element_parsed_guard_(b, "custom_proximity_term_2_0_1", c)) break;
        }
        return true;
    }

    // COMMA simple_term
    private static boolean custom_proximity_term_2_0_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_0_1_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, COMMA);
        r = r && simple_term(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // { LPAREN simple_term { COMMA simple_term }* RPAREN }
    //      [COMMA maximum_distance [COMMA match_order ] ]
    private static boolean custom_proximity_term_2_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_1")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = custom_proximity_term_2_1_0(b, l + 1);
        r = r && custom_proximity_term_2_1_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // LPAREN simple_term { COMMA simple_term }* RPAREN
    private static boolean custom_proximity_term_2_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_1_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, LPAREN);
        r = r && simple_term(b, l + 1);
        r = r && custom_proximity_term_2_1_0_2(b, l + 1);
        r = r && consumeToken(b, RPAREN);
        exit_section_(b, m, null, r);
        return r;
    }

    // { COMMA simple_term }*
    private static boolean custom_proximity_term_2_1_0_2(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_1_0_2")) return false;
        while (true) {
            int c = current_position_(b);
            if (!custom_proximity_term_2_1_0_2_0(b, l + 1)) break;
            if (!empty_element_parsed_guard_(b, "custom_proximity_term_2_1_0_2", c)) break;
        }
        return true;
    }

    // COMMA simple_term
    private static boolean custom_proximity_term_2_1_0_2_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "custom_proximity_term_2_1_0_2_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, COMMA);
        r = r && simple_term(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // [COMMA maximum_distance [COMMA match_order ] ]
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
    // FORMSOF LPAREN { INFLECTIONAL | THESAURUS } COMMA simple_term+ RPAREN
    public static boolean generation_term(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generation_term")) return false;
        if (!nextTokenIs(b, FORMSOF)) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, FORMSOF, LPAREN);
        r = r && generation_term_2(b, l + 1);
        r = r && consumeToken(b, COMMA);
        r = r && generation_term_4(b, l + 1);
        r = r && consumeToken(b, RPAREN);
        exit_section_(b, m, GENERATION_TERM, r);
        return r;
    }

    // INFLECTIONAL | THESAURUS
    private static boolean generation_term_2(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generation_term_2")) return false;
        boolean r;
        r = consumeToken(b, INFLECTIONAL);
        if (!r) r = consumeToken(b, THESAURUS);
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
    // simple_term { { NEAR | TILDA } { simple_term }+ }
    public static boolean generic_proximity_term(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generic_proximity_term")) return false;
        if (!nextTokenIs(b, "<generic proximity term>", C_WORD, STRING)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, GENERIC_PROXIMITY_TERM, "<generic proximity term>");
        r = simple_term(b, l + 1);
        r = r && generic_proximity_term_1(b, l + 1);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // { NEAR | TILDA } { simple_term }+
    private static boolean generic_proximity_term_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generic_proximity_term_1")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = generic_proximity_term_1_0(b, l + 1);
        r = r && generic_proximity_term_1_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // NEAR | TILDA
    private static boolean generic_proximity_term_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generic_proximity_term_1_0")) return false;
        boolean r;
        r = consumeToken(b, NEAR);
        if (!r) r = consumeToken(b, TILDA);
        return r;
    }

    // { simple_term }+
    private static boolean generic_proximity_term_1_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generic_proximity_term_1_1")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = generic_proximity_term_1_1_0(b, l + 1);
        while (r) {
            int c = current_position_(b);
            if (!generic_proximity_term_1_1_0(b, l + 1)) break;
            if (!empty_element_parsed_guard_(b, "generic_proximity_term_1_1", c)) break;
        }
        exit_section_(b, m, null, r);
        return r;
    }

    // { simple_term }
    private static boolean generic_proximity_term_1_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "generic_proximity_term_1_1_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = simple_term(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // TRUE | FALSE
    public static boolean match_order(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "match_order")) return false;
        if (!nextTokenIs(b, "<match order>", FALSE, TRUE)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, MATCH_ORDER, "<match order>");
        r = consumeToken(b, TRUE);
        if (!r) r = consumeToken(b, FALSE);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // integer | MAX
    public static boolean maximum_distance(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "maximum_distance")) return false;
        if (!nextTokenIs(b, "<maximum distance>", INTEGER, MAX)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, MAXIMUM_DISTANCE, "<maximum distance>");
        r = consumeToken(b, INTEGER);
        if (!r) r = consumeToken(b, MAX);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // OR | OR2
    public static boolean or_op(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "or_op")) return false;
        if (!nextTokenIs(b, "<or op>", OR, OR2)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, OR_OP, "<or op>");
        r = consumeToken(b, OR);
        if (!r) r = consumeToken(b, OR2);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // C_WORD | string
    public static boolean simple_term(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "simple_term")) return false;
        if (!nextTokenIs(b, "<simple term>", C_WORD, STRING)) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, SIMPLE_TERM, "<simple term>");
        r = consumeToken(b, C_WORD);
        if (!r) r = consumeToken(b, STRING);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // { simple_term | generation_term } { WEIGHT LPAREN decimal RPAREN }
    public static boolean weight_option(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weight_option")) return false;
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, WEIGHT_OPTION, "<weight option>");
        r = weight_option_0(b, l + 1);
        r = r && weight_option_1(b, l + 1);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // simple_term | generation_term
    private static boolean weight_option_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weight_option_0")) return false;
        boolean r;
        r = simple_term(b, l + 1);
        if (!r) r = generation_term(b, l + 1);
        return r;
    }

    // WEIGHT LPAREN decimal RPAREN
    private static boolean weight_option_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weight_option_1")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, WEIGHT, LPAREN, DECIMAL, RPAREN);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // ISABOUT LPAREN { weight_option { COMMA weight_option }* } RPAREN
    public static boolean weighted_term(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weighted_term")) return false;
        if (!nextTokenIs(b, ISABOUT)) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, ISABOUT, LPAREN);
        r = r && weighted_term_2(b, l + 1);
        r = r && consumeToken(b, RPAREN);
        exit_section_(b, m, WEIGHTED_TERM, r);
        return r;
    }

    // weight_option { COMMA weight_option }*
    private static boolean weighted_term_2(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weighted_term_2")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = weight_option(b, l + 1);
        r = r && weighted_term_2_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // { COMMA weight_option }*
    private static boolean weighted_term_2_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weighted_term_2_1")) return false;
        while (true) {
            int c = current_position_(b);
            if (!weighted_term_2_1_0(b, l + 1)) break;
            if (!empty_element_parsed_guard_(b, "weighted_term_2_1", c)) break;
        }
        return true;
    }

    // COMMA weight_option
    private static boolean weighted_term_2_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "weighted_term_2_1_0")) return false;
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, COMMA);
        r = r && weight_option(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    public ASTNode parse(IElementType t, PsiBuilder b) {
        parseLight(t, b);
        return b.getTreeBuilt();
    }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
        b = adapt_builder_(t, b, this, null);
        Marker m = enter_section_(b, 0, _COLLAPSE_, null);
        r = parse_root_(t, b);
        exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
    }

    protected boolean parse_root_(IElementType t, PsiBuilder b) {
        return parse_root_(t, b, 0);
    }

}
