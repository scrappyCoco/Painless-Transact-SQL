package ru.coding4fun.tsql.naming.rule.psi.grammar;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ru.coding4fun.tsql.naming.rule.psi.NamingRuleTypes.*;

%%

%{
  public _NamingRuleLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _NamingRuleLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ \t\n\x0B\f\r]+
LINE_COMMENT="//".*
INTEGER=[0-9]+
ID=[:letter:][a-zA-Z_0-9]*
STRING=(\"[^\"]*\")

%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  "+"                 { return OP_ADD; }
  "||"                { return OP_OR; }
  "&&"                { return OP_AND; }
  "=="                { return OP_EQ_EQ; }
  "!="                { return OP_NOT_EQ; }
  ">"                 { return OP_GT; }
  ">="                { return OP_GTE; }
  "<"                 { return OP_LT; }
  "<="                { return OP_LTE; }
  "("                 { return LBRACE; }
  ")"                 { return RBRACE; }
  "!"                 { return NOT; }
  "if"                { return IF; }
  "else"              { return ELSE; }

  {SPACE}             { return SPACE; }
  {LINE_COMMENT}      { return LINE_COMMENT; }
  {INTEGER}           { return INTEGER; }
  {ID}                { return ID; }
  {STRING}            { return STRING; }

}

[^] { return BAD_CHARACTER; }
