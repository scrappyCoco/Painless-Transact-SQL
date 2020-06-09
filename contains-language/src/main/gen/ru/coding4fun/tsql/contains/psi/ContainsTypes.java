// This is a generated file. Not intended for manual editing.
package ru.coding4fun.tsql.contains.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ru.coding4fun.tsql.contains.ContainsElementType;
import ru.coding4fun.tsql.contains.ContainsTokenType;
import ru.coding4fun.tsql.contains.psi.impl.*;

public interface ContainsTypes {

  IElementType BOOL_LITERAL = new ContainsElementType("BOOL_LITERAL");
  IElementType BOOL_OPERATOR = new ContainsElementType("BOOL_OPERATOR");
  IElementType CUSTOM_PROXIMITY_TERM = new ContainsElementType("CUSTOM_PROXIMITY_TERM");
  IElementType DECIMAL_LITERAL = new ContainsElementType("DECIMAL_LITERAL");
  IElementType GENERATION_FORM = new ContainsElementType("GENERATION_FORM");
  IElementType GENERATION_TERM = new ContainsElementType("GENERATION_TERM");
  IElementType GENERIC_PROXIMITY_TERM = new ContainsElementType("GENERIC_PROXIMITY_TERM");
  IElementType INTEGER_LITERAL = new ContainsElementType("INTEGER_LITERAL");
  IElementType ITEM = new ContainsElementType("ITEM");
  IElementType MATCH_ORDER = new ContainsElementType("MATCH_ORDER");
  IElementType MAXIMUM_DISTANCE = new ContainsElementType("MAXIMUM_DISTANCE");
  IElementType SIMPLE_TERM = new ContainsElementType("SIMPLE_TERM");
  IElementType WEIGHTED_TERM = new ContainsElementType("WEIGHTED_TERM");
  IElementType WEIGHT_OPTION = new ContainsElementType("WEIGHT_OPTION");
  IElementType WEIGHT_TERM = new ContainsElementType("WEIGHT_TERM");

  IElementType AMP_NOT_OP = new ContainsTokenType("&!");
  IElementType AND = new ContainsTokenType("AND");
  IElementType AND_NOT = new ContainsTokenType("AND_NOT");
  IElementType AND_NOT_OP = new ContainsTokenType("AND_NOT_OP");
  IElementType AND_OP = new ContainsTokenType("&");
  IElementType ASTERISK = new ContainsTokenType("*");
  IElementType COMMA = new ContainsTokenType(",");
  IElementType DECIMAL = new ContainsTokenType("decimal");
  IElementType FALSE = new ContainsTokenType("FALSE");
  IElementType FORMS_OF = new ContainsTokenType("FORMSOF");
  IElementType INFLECTIONAL = new ContainsTokenType("INFLECTIONAL");
  IElementType INTEGER = new ContainsTokenType("integer");
  IElementType IS_ABOUT = new ContainsTokenType("ISABOUT");
  IElementType LPAREN = new ContainsTokenType("(");
  IElementType MAX = new ContainsTokenType("MAX");
  IElementType NEAR = new ContainsTokenType("NEAR");
  IElementType NOT = new ContainsTokenType("NOT");
  IElementType OR = new ContainsTokenType("OR");
  IElementType OR_OP = new ContainsTokenType("|");
  IElementType QUOTE = new ContainsTokenType("\"");
  IElementType RPAREN = new ContainsTokenType(")");
  IElementType STRING = new ContainsTokenType("string");
  IElementType THESAURUS = new ContainsTokenType("THESAURUS");
  IElementType TILDA = new ContainsTokenType("~");
  IElementType TRUE = new ContainsTokenType("TRUE");
  IElementType WEIGHT = new ContainsTokenType("WEIGHT");
  IElementType WORD = new ContainsTokenType("word");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BOOL_LITERAL) {
        return new ContainsBoolLiteralImpl(node);
      }
      else if (type == BOOL_OPERATOR) {
        return new ContainsBoolOperatorImpl(node);
      }
      else if (type == CUSTOM_PROXIMITY_TERM) {
        return new ContainsCustomProximityTermImpl(node);
      }
      else if (type == DECIMAL_LITERAL) {
        return new ContainsDecimalLiteralImpl(node);
      }
      else if (type == GENERATION_FORM) {
        return new ContainsGenerationFormImpl(node);
      }
      else if (type == GENERATION_TERM) {
        return new ContainsGenerationTermImpl(node);
      }
      else if (type == GENERIC_PROXIMITY_TERM) {
        return new ContainsGenericProximityTermImpl(node);
      }
      else if (type == INTEGER_LITERAL) {
        return new ContainsIntegerLiteralImpl(node);
      }
      else if (type == ITEM) {
        return new ContainsItemImpl(node);
      }
      else if (type == MATCH_ORDER) {
        return new ContainsMatchOrderImpl(node);
      }
      else if (type == MAXIMUM_DISTANCE) {
        return new ContainsMaximumDistanceImpl(node);
      }
      else if (type == SIMPLE_TERM) {
        return new ContainsSimpleTermImpl(node);
      }
      else if (type == WEIGHTED_TERM) {
        return new ContainsWeightedTermImpl(node);
      }
      else if (type == WEIGHT_OPTION) {
        return new ContainsWeightOptionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
