package ru.coding4fun.tsql.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ru.coding4fun.tsql.lang.psi.ContainsTypes.*;

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

OR2=[Oo][Rr]
AND2=[Aa][Nn][Dd]
NOT=[Nn][Oo][Tt]
NEAR=[Nn][Ee][Aa][Rr]
FORMSOF=[Ff][Oo][Rr][Mm][Ss][Oo][Ff]
INFLECTIONAL=[Ii][Nn][Ff][Ll][Ee][Cc][Tt][Ii][Oo][Nn][Aa][Ll]
THESAURUS=[Tt][Hh][Ee][Ss][Aa][Uu][Rr][Uu][Ss]
TRUE=[Tt][Rr][Uu][Ee]
FALSE=[Ff][Aa][Ll][Ss][Ee]
WEIGHT=[Ww][Ee][Ii][Gg][Hh][Tt]
MAX=[Mm][Aa][Xx]
ISABOUT=[Ii][Ss][Aa][Bb][Oo][Uu][Tt]
C_WORD=[\p{L}0-9]+
STRING=\"([^\"]+)\"
DECIMAL=[0-9]?[.][0-9]
INTEGER=[0-9]+
SPACE=[ \t\n\x0B\f\r]+

%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  ","                 { return COMMA; }
  "("                 { return LPAREN; }
  ")"                 { return RPAREN; }
  "*"                 { return ASTERISK; }
  "|"                 { return OR; }
  "~"                 { return TILDA; }
  "&"                 { return AND; }
  "&!"                { return AMP_NOT; }
  "\""                { return QUOTE; }

  {OR2}               { return OR2; }
  {AND2}              { return AND2; }
  {NOT}               { return NOT; }
  {NEAR}              { return NEAR; }
  {FORMSOF}           { return FORMSOF; }
  {INFLECTIONAL}      { return INFLECTIONAL; }
  {THESAURUS}         { return THESAURUS; }
  {TRUE}              { return TRUE; }
  {FALSE}             { return FALSE; }
  {WEIGHT}            { return WEIGHT; }
  {MAX}               { return MAX; }
  {ISABOUT}           { return ISABOUT; }
  {C_WORD}            { return C_WORD; }
  {STRING}            { return STRING; }
  {DECIMAL}           { return DECIMAL; }
  {INTEGER}           { return INTEGER; }
  {SPACE}             { return SPACE; }

}

[^] { return BAD_CHARACTER; }
