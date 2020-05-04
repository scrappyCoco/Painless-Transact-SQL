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
package ru.coding4fun.tsql.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ru.coding4fun.tsql.lang.ContainsElementType;
import ru.coding4fun.tsql.lang.ContainsTokenType;
import ru.coding4fun.tsql.lang.psi.impl.*;

public interface ContainsTypes {

  IElementType AND_NOT_OP = new ContainsElementType("AND_NOT_OP");
  IElementType AND_OP = new ContainsElementType("AND_OP");
  IElementType CUSTOM_PROXIMITY_TERM = new ContainsElementType("CUSTOM_PROXIMITY_TERM");
  IElementType GENERATION_TERM = new ContainsElementType("GENERATION_TERM");
  IElementType GENERIC_PROXIMITY_TERM = new ContainsElementType("GENERIC_PROXIMITY_TERM");
  IElementType MATCH_ORDER = new ContainsElementType("MATCH_ORDER");
  IElementType MAXIMUM_DISTANCE = new ContainsElementType("MAXIMUM_DISTANCE");
  IElementType OR_OP = new ContainsElementType("OR_OP");
  IElementType SIMPLE_TERM = new ContainsElementType("SIMPLE_TERM");
  IElementType WEIGHTED_TERM = new ContainsElementType("WEIGHTED_TERM");
  IElementType WEIGHT_OPTION = new ContainsElementType("WEIGHT_OPTION");

  IElementType AMP_NOT = new ContainsTokenType("&!");
  IElementType AND = new ContainsTokenType("&");
  IElementType AND2 = new ContainsTokenType("AND2");
  IElementType ASTERISK = new ContainsTokenType("*");
  IElementType COMMA = new ContainsTokenType(",");
  IElementType C_WORD = new ContainsTokenType("C_WORD");
  IElementType DECIMAL = new ContainsTokenType("decimal");
  IElementType FALSE = new ContainsTokenType("FALSE");
  IElementType FORMSOF = new ContainsTokenType("FORMSOF");
  IElementType INFLECTIONAL = new ContainsTokenType("INFLECTIONAL");
  IElementType INTEGER = new ContainsTokenType("integer");
  IElementType ISABOUT = new ContainsTokenType("ISABOUT");
  IElementType LPAREN = new ContainsTokenType("(");
  IElementType MAX = new ContainsTokenType("MAX");
  IElementType NEAR = new ContainsTokenType("NEAR");
  IElementType NOT = new ContainsTokenType("NOT");
  IElementType OR = new ContainsTokenType("|");
  IElementType OR2 = new ContainsTokenType("OR2");
  IElementType QUOTE = new ContainsTokenType("\"");
  IElementType RPAREN = new ContainsTokenType(")");
  IElementType STRING = new ContainsTokenType("string");
  IElementType THESAURUS = new ContainsTokenType("THESAURUS");
  IElementType TILDA = new ContainsTokenType("~");
  IElementType TRUE = new ContainsTokenType("TRUE");
  IElementType WEIGHT = new ContainsTokenType("WEIGHT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == AND_NOT_OP) {
        return new ContainsAndNotOpImpl(node);
      } else if (type == AND_OP) {
        return new ContainsAndOpImpl(node);
      } else if (type == CUSTOM_PROXIMITY_TERM) {
        return new ContainsCustomProximityTermImpl(node);
      } else if (type == GENERATION_TERM) {
        return new ContainsGenerationTermImpl(node);
      } else if (type == GENERIC_PROXIMITY_TERM) {
        return new ContainsGenericProximityTermImpl(node);
      } else if (type == MATCH_ORDER) {
        return new ContainsMatchOrderImpl(node);
      } else if (type == MAXIMUM_DISTANCE) {
        return new ContainsMaximumDistanceImpl(node);
      } else if (type == OR_OP) {
        return new ContainsOrOpImpl(node);
      } else if (type == SIMPLE_TERM) {
        return new ContainsSimpleTermImpl(node);
      } else if (type == WEIGHTED_TERM) {
        return new ContainsWeightedTermImpl(node);
      } else if (type == WEIGHT_OPTION) {
        return new ContainsWeightOptionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
