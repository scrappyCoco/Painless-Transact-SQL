package ru.coding4fun.tsql.contains;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ru.coding4fun.tsql.contains.psi.ContainsTypes.*;

%%

%{
  public _ContainsLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ContainsLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

OR=[Oo][Rr]
AND=[Aa][Nn][Dd]
NOT=[Nn][Oo][Tt]
AND_NOT={AND}{WHITE_SPACE}+{NOT}
NEAR=[Nn][Ee][Aa][Rr]
FORMS_OF=[Ff][Oo][Rr][Mm][Ss][Oo][Ff]
INFLECTIONAL=[Ii][Nn][Ff][Ll][Ee][Cc][Tt][Ii][Oo][Nn][Aa][Ll]
THESAURUS=[Tt][Hh][Ee][Ss][Aa][Uu][Rr][Uu][Ss]
TRUE=[Tt][Rr][Uu][Ee]
FALSE=[Ff][Aa][Ll][Ss][Ee]
WEIGHT=[Ww][Ee][Ii][Gg][Hh][Tt]
MAX=[Mm][Aa][Xx]
IS_ABOUT=[Ii][Ss][Aa][Bb][Oo][Uu][Tt]
WORD=[\p{L}0-9]+
STRING=\"([^\"]+)\"
DECIMAL=[0-9]?[.][0-9]
INTEGER=[0-9]+
SPACE=[ \t\n\x0B\f\r]+

%state YYINITIAL

%%

<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  ","                 { return COMMA; }
  "("                 { return LPAREN; }
  ")"                 { return RPAREN; }
  "*"                 { return ASTERISK; }
  "|"                 { return OR_OP; }
  "&"                 { return AND_OP; }
  "&!"                { return AMP_NOT_OP; }
  "~"                 { return TILDA; }
  "\""                { return QUOTE; }
  "AND_NOT_OP"        { return AND_NOT_OP; }

  {OR}                { return OR; }
  {AND}               { return AND; }
  {AND_NOT}           { return AND_NOT; }
  {NOT}               { return NOT; }
  {NEAR}              { return NEAR; }
  {FORMS_OF}          { return FORMS_OF; }
  {INFLECTIONAL}      { return INFLECTIONAL; }
  {THESAURUS}         { return THESAURUS; }
  {TRUE}              { return TRUE; }
  {FALSE}             { return FALSE; }
  {WEIGHT}            { return WEIGHT; }
  {MAX}               { return MAX; }
  {IS_ABOUT}          { return IS_ABOUT; }
  {WORD}              { return WORD; }
  {STRING}            { return STRING; }
  {DECIMAL}           { return DECIMAL; }
  {INTEGER}           { return INTEGER; }
  {SPACE}             { return SPACE; }
  [^]                 { return BAD_CHARACTER; }
}

[^]                   { return BAD_CHARACTER; }
