# Manual for Adding Language to ccgrep

*updated on 2019/09/17, written on 2019/09/17*

1. Build Lexer

   1. prepare ANTLR grammar file (*YourLangLexer.g4*). (write by yourself or download from [github](https://github.com/antlr/grammars-v4))

   2. copy *YourLangLexer.g4* to *YourLangQueryLexer.g4*, and rename `gramar YourLang;` in it to `gramar YourLangQuery;`.

   3. add package declaration for java under `grammar YourLang;` in each grammar file.

      ```java
      @header { package jp.ac.osaka_u.ist.sel.ccgrep.antlr; }
      ```

   4. escape `$` in *YourLangQueryLexer.g4* like (e.g. `'$'` -> `'\\$'`), if any.

   5. add following rules to the **bottom** of the query grammar. (specify literal rules of your language to CCG_SPECIAL_ID)

      ```java
      CCG_SPECIAL_ID
         : '$' Identifier
          // modify followings to literal rules of your language
         | '$' IntegerLiteral
         | '$' FloatingPointLiteral
         | '$' BooleanLiteral
         | '$' CharacterLiteral
         | '$' StringLiteral
         | '$' NullLiteral;
      CCG_SPECIAL_SEQ : '$$';
      CCG_SPECIAL_ANYSEQ : '$#';
      CCG_SPECIAL_LPAR  : '$(';
      CCG_SPECIAL_RPAR  : '$)';
      CCG_SPECIAL_ORLNG : '$|';
      CCG_SPECIAL_ORFST : '$/';
      CCG_SPECIAL_MORE0 : '$*';
      CCG_SPECIAL_MORE1 : '$+';
      CCG_SPECIAL_EITH  : '$?';
      CCG_SPECIAL_LAP  : '$=';
      CCG_SPECIAL_LAN  : '$!';
      CCG_SPECIAL_ANY   : '$.';
      ```

   6. build grammars.

      ```shell
      $ antlr4 YourLangLexer.g4
      $ antlr4 YourLangQueryLexer.g4
      ```

   7. move all the generated java files (and other files if needed) to `CCGrep/src/main/java/jp/ac/osaka_u/ist/sel/ccgrep/antlr`.

2. Configure ccgrep

   1. add your language to `Language` class in `CCGrep/src/main/java/jp/ac/osaka_u/ist/sel/ccgrep/model/Language.java`.

      ```java
      public class Language
      {
         C(...),
         CPP14(...),
         JAVA9(...),
         PYTHON3(...),
         YOUR_LANG(...);
      }
      ```

   2. write config for your language like following Java example. (the rule variables are declared in *YourLangLexer.java*)

      ```java
      JAVA9(
         // name list to be specified to -l option
         Arrays.asList("java", "java9"),
         // file extension list to be searched
         Arrays.asList("java"),
         // if you use YourLangQueryLexer.g4, specify YourLangQueryLexer::new
         Java9QueryLexer::new,
         // if you use YourLangLexer.g4, specify YourLangLexer::new
         Java9Lexer::new,
         // if you use YourLangQueryLexer.g4, specify YourLangQueryLexer.class
         new SpecialSet(Java9QueryLexer.class),
         // begin of line comment, begin of block comment, end of block comment
         new CommentSet("//", "/*", "*/"),
         // bracket pairs of the language
         Arrays.<BracketPair>asList(
            new BracketPair(Java9Lexer.LPAREN, Java9Lexer.RPAREN),
            new BracketPair(Java9Lexer.LBRACE, Java9Lexer.RBRACE),
            new BracketPair(Java9Lexer.LBRACK, Java9Lexer.RBRACK)
         ),
         Arrays.<BlindSet>asList(
            // specify identifier rules
            new BlindSet(
               null, // do not modify
               // identifdier rules
               Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR,
               Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT,
               Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.SUPER,
               Java9Lexer.THIS, Java9Lexer.VOID, Java9Lexer.Identifier
            ),
            // specify literal rules
            new BlindSet(
               BlindLevel.FULL, // do not modify
               // literal rules
               Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral,
               Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral
            )
         ),
         // you can modify the tokenized list before using it
         /*** If you have escaped $ in the grammar, you must replace them here. (c.f. JAVA9 example) ***/
         tokens -> Language.filterJavaDollar(tokens)
      )
      ```

   3. build ccgrep.

      ```shell
      $ mvn package
      ```

3. Use.

   ```shell
   $ ccgrep 'query' -r src/ -l yourlang
   ```
