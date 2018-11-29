package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.*;
import java.util.function.Function;

import org.antlr.v4.runtime.*;

import jp.ac.osaka_u.ist.sel.ccgrep.antlr.c.*;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.cpp14.*;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.java9.*;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.python3.*;

public enum Language
{
    C(
        new String[]{"c"},
        new String[]{".c", ".h"},
        CLexer::new,
        CParser::new,
        new SpecialSet(CLexer.CCG_SPECIAL_ID, CLexer.CCG_SPECIAL_SEQ, CLexer.CCG_SPECIAL_EXPR, CLexer.CCG_SPECIAL_BLOCK),
        new CommentSet("//", "/*", "*/"),
        new BracketPair[]{
            new BracketPair(CLexer.LeftParen, CLexer.RightParen),
            new BracketPair(CLexer.LeftBracket, CLexer.RightBracket),
            new BracketPair(CLexer.LeftBrace, CLexer.RightBrace)
        },
        new BlindSet[]{
            new BlindSet(BlindLevel.NONE, CLexer.Char, CLexer.Double, CLexer.Float, CLexer.Int, CLexer.Long, CLexer.Short, CLexer.Void, CLexer.Bool, CLexer.Identifier),
            new BlindSet(BlindLevel.FULL, CLexer.Constant, CLexer.DigitSequence, CLexer.StringLiteral)/*,
            new BlindSet(BlindLevel.FULL, CLexer.Less, CLexer.LessEqual, CLexer.Greater, CLexer.GreaterEqual, CLexer.LeftShift, CLexer.RightShift, CLexer.Plus, CLexer.PlusPlus, CLexer.Minus, CLexer.MinusMinus, CLexer.Star, CLexer.Div, CLexer.Mod, CLexer.And, CLexer.AndAnd, CLexer.OrOr, CLexer.Caret, CLexer.Not, CLexer.Tilde),
            new BlindSet(BlindLevel.FULL, CLexer.Assign, CLexer.StarAssign, CLexer.DivAssign, CLexer.ModAssign, CLexer.PlusAssign, CLexer.MinusAssign, CLexer.LeftShiftAssign, CLexer.RightShiftAssign, CLexer.AndAssign, CLexer.XorAssign, CLexer.OrAssign, CLexer.Equal, CLexer.NotEqual)*/
        }
    ),
    CPP14(
        new String[]{"cpp", "cpp14", "c++", "c++14"},
        new String[]{".cpp", ".cc", ".c++", ".cxx", ".c", ".h", ".hpp"},
        CPP14Lexer::new,
        CPP14Parser::new,
        new SpecialSet(CPP14Lexer.CCG_SPECIAL_ID, CPP14Lexer.CCG_SPECIAL_SEQ, CPP14Lexer.CCG_SPECIAL_EXPR, CPP14Lexer.CCG_SPECIAL_BLOCK),
        new CommentSet("//", "/*", "*/"),
        new BracketPair[]{
            new BracketPair(CPP14Lexer.LeftParen, CPP14Lexer.RightParen),
            new BracketPair(CPP14Lexer.LeftBracket, CPP14Lexer.RightBracket),
            new BracketPair(CPP14Lexer.LeftBrace, CPP14Lexer.RightBrace)
        },
        new BlindSet[]{
            new BlindSet(BlindLevel.NONE, CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char, CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double, CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long, CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar, CPP14Lexer.Identifier),
            new BlindSet(BlindLevel.FULL, CPP14Lexer.False, CPP14Lexer.Nullptr, CPP14Lexer.True, CPP14Lexer.Integerliteral, CPP14Lexer.Decimalliteral, CPP14Lexer.Octalliteral, CPP14Lexer.Hexadecimalliteral, CPP14Lexer.Binaryliteral, CPP14Lexer.Characterliteral, CPP14Lexer.Floatingliteral, CPP14Lexer.Stringliteral, CPP14Lexer.Userdefinedintegerliteral, CPP14Lexer.Userdefinedfloatingliteral, CPP14Lexer.Userdefinedstringliteral, CPP14Lexer.Userdefinedcharacterliteral)/*,
            new BlindSet(BlindLevel.FULL, CPP14Lexer.Plus, CPP14Lexer.Minus, CPP14Lexer.Star, CPP14Lexer.Div, CPP14Lexer.Mod, CPP14Lexer.Caret, CPP14Lexer.And, CPP14Lexer.Or, CPP14Lexer.Tilde, CPP14Lexer.Not, CPP14Lexer.Less, CPP14Lexer.Greater, CPP14Lexer.LeftShift, CPP14Lexer.Equal, CPP14Lexer.NotEqual, CPP14Lexer.LessEqual, CPP14Lexer.GreaterEqual, CPP14Lexer.AndAnd, CPP14Lexer.OrOr, CPP14Lexer.PlusPlus, CPP14Lexer.MinusMinus),
            new BlindSet(BlindLevel.FULL, CPP14Lexer.Assign, CPP14Lexer.PlusAssign, CPP14Lexer.MinusAssign, CPP14Lexer.StarAssign, CPP14Lexer.DivAssign, CPP14Lexer.ModAssign, CPP14Lexer.XorAssign, CPP14Lexer.AndAssign, CPP14Lexer.OrAssign, CPP14Lexer.LeftShiftAssign)*/
        }
    ),
    JAVA9(
        new String[]{"java", "java9"},
        new String[]{".java"},
        Java9Lexer::new,
        Java9Parser::new,
        new SpecialSet(Java9Lexer.CCG_SPECIAL_ID, Java9Lexer.CCG_SPECIAL_SEQ, Java9Lexer.CCG_SPECIAL_EXPR, Java9Lexer.CCG_SPECIAL_BLOCK),
        new CommentSet("//", "/*", "*/"),
        new BracketPair[]{
            new BracketPair(Java9Lexer.LPAREN, Java9Lexer.RPAREN),
            new BracketPair(Java9Lexer.LBRACE, Java9Lexer.RBRACE),
            new BracketPair(Java9Lexer.LBRACK, Java9Lexer.RBRACK)
        },
        new BlindSet[]{
            new BlindSet(BlindLevel.NONE, Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR, Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT, Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.SUPER, Java9Lexer.THIS, Java9Lexer.VOID, Java9Lexer.Identifier),
            new BlindSet(BlindLevel.FULL, Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral, Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral)/*,
            new BlindSet(BlindLevel.FULL, Java9Lexer.GT, Java9Lexer.LT, Java9Lexer.TILDE, Java9Lexer.EQUAL, Java9Lexer.LE, Java9Lexer.GE, Java9Lexer.NOTEQUAL, Java9Lexer.AND, Java9Lexer.OR, Java9Lexer.INC, Java9Lexer.DEC, Java9Lexer.ADD, Java9Lexer.SUB, Java9Lexer.MUL, Java9Lexer.DIV, Java9Lexer.BITAND, Java9Lexer.BITOR, Java9Lexer.CARET, Java9Lexer.MOD),
            new BlindSet(BlindLevel.FULL, Java9Lexer.ASSIGN, Java9Lexer.ADD_ASSIGN, Java9Lexer.SUB_ASSIGN, Java9Lexer.MUL_ASSIGN, Java9Lexer.DIV_ASSIGN, Java9Lexer.AND_ASSIGN, Java9Lexer.OR_ASSIGN, Java9Lexer.XOR_ASSIGN, Java9Lexer.MOD_ASSIGN, Java9Lexer.LSHIFT_ASSIGN, Java9Lexer.RSHIFT_ASSIGN, Java9Lexer.URSHIFT_ASSIGN)*/
        }
    ),
    PYTHON3(
        new String[]{"python", "python3"},
        new String[]{".py"},
        Python3Lexer::new,
        Python3Parser::new,
        new SpecialSet(Python3Lexer.CCG_SPECIAL_ID, Python3Lexer.CCG_SPECIAL_SEQ, Python3Lexer.CCG_SPECIAL_EXPR, Python3Lexer.CCG_SPECIAL_BLOCK),
        new CommentSet("#", "\"\"\"", "\"\"\""),
        new BracketPair[]{
            new BracketPair(Python3Lexer.OPEN_PAREN, Python3Lexer.CLOSE_PAREN),
            new BracketPair(Python3Lexer.OPEN_BRACK, Python3Lexer.CLOSE_BRACK),
            new BracketPair(Python3Lexer.OPEN_BRACE, Python3Lexer.CLOSE_BRACE),
        },
        new BlindSet[]{
            new BlindSet(BlindLevel.NONE, Python3Lexer.NAME),
            new BlindSet(BlindLevel.FULL, Python3Lexer.STRING, Python3Lexer.NUMBER, Python3Lexer.INTEGER, Python3Lexer.NONE, Python3Lexer.TRUE, Python3Lexer.FALSE, Python3Lexer.STRING_LITERAL, Python3Lexer.BYTES_LITERAL, Python3Lexer.DECIMAL_INTEGER, Python3Lexer.OCT_INTEGER, Python3Lexer.HEX_INTEGER, Python3Lexer.BIN_INTEGER, Python3Lexer.FLOAT_NUMBER, Python3Lexer.IMAG_NUMBER)/*,
            new BlindSet(BlindLevel.FULL, Python3Lexer.IN, Python3Lexer.OR, Python3Lexer.AND, Python3Lexer.NOT, Python3Lexer.IS, Python3Lexer.STAR, Python3Lexer.POWER, Python3Lexer.OR_OP, Python3Lexer.XOR, Python3Lexer.AND_OP, Python3Lexer.LEFT_SHIFT, Python3Lexer.RIGHT_SHIFT, Python3Lexer.ADD, Python3Lexer.MINUS, Python3Lexer.DIV, Python3Lexer.MOD, Python3Lexer.IDIV, Python3Lexer.NOT_OP, Python3Lexer.LESS_THAN, Python3Lexer.GREATER_THAN, Python3Lexer.EQUALS, Python3Lexer.GT_EQ, Python3Lexer.LT_EQ, Python3Lexer.NOT_EQ_1, Python3Lexer.NOT_EQ_2, Python3Lexer.AT),
            new BlindSet(BlindLevel.FULL, Python3Lexer.ASSIGN, Python3Lexer.ADD_ASSIGN, Python3Lexer.SUB_ASSIGN, Python3Lexer.MULT_ASSIGN, Python3Lexer.AT_ASSIGN, Python3Lexer.DIV_ASSIGN, Python3Lexer.MOD_ASSIGN, Python3Lexer.AND_ASSIGN, Python3Lexer.OR_ASSIGN, Python3Lexer.XOR_ASSIGN, Python3Lexer.LEFT_SHIFT_ASSIGN, Python3Lexer.RIGHT_SHIFT_ASSIGN, Python3Lexer.POWER_ASSIGN, Python3Lexer.IDIV_ASSIGN)*/
        }
    );

    private final String[] names;
    private final String[] extensions;
    private final Function<CharStream, Lexer> lexerCreater;
    private final Function<TokenStream, Parser> parserCreater;

    private final SpecialSet specialSet;
    private final CommentSet commentSet;
    private final BracketPair[] bracketPairs;
    private final BlindSet[] blindSets;

    Language(
        String[] names, String[] extensions,
        Function<CharStream, Lexer> lexerCreater,
        Function<TokenStream, Parser> parserCreater,
        SpecialSet specialSet,
        CommentSet commentSet,
        BracketPair[] bracketPairs,
        BlindSet[] blindSets)
    {
        Arrays.sort(names);
        Arrays.sort(extensions);
        this.names = names;
        this.extensions = extensions;
        this.lexerCreater = lexerCreater;
        this.parserCreater = parserCreater;

        this.specialSet = specialSet;
        this.commentSet = commentSet;
        this.bracketPairs = bracketPairs;
        this.blindSets = blindSets;
    }

    public static Language findByName(String languageName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> Arrays.stream(l.names).anyMatch(languageName::equalsIgnoreCase))
            .findFirst()
            .orElse(null);
    }

    public static Language findByFileNameWithExtension(String fileName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> Arrays.stream(l.extensions).anyMatch(fileName::endsWith))
            .findFirst()
            .orElse(null);
    }

    public Lexer createLexer(CharStream stream)
    {
        return lexerCreater.apply(stream);
    }

    public Parser createParser(TokenStream stream)
    {
        return parserCreater.apply(stream);
    }

    public boolean checkTokenEquality(
        GrepToken needleToken, GrepToken haystackToken, BlindLevel blindLevel, Map<String, GrepToken> blindConstraint)
    {
        if(needleToken.getType() == specialId())
        {
            return needleToken.equalsAsSpecialTo(haystackToken);
        }
        return Arrays.stream(blindSets)
            .filter(set -> set.contains(needleToken.getType())
                        && set.contains(haystackToken.getType()))
            .map(set -> set.minLevel)
            .map(minLevel -> (blindLevel.value != 0 && blindLevel.value >= minLevel.value)
                            ? blindLevel: minLevel)
            .findFirst()
            .orElse(BlindLevel.NONE)
            .checkTokenEquality(needleToken, haystackToken, blindConstraint);
    }

    public boolean matchesExtension(String filename)
    {
        return Arrays.stream(extensions)
            .anyMatch(filename::endsWith);
    }

    public String lineCommented(String str)
    {
        return commentSet.line + str;
    }

    public String blockCommented(String str)
    {
        return commentSet.blockBegin + str + commentSet.blockEnd;
    }

    public String blockCommentBegin()
    {
        return commentSet.blockBegin;
    }

    public String blockCommentEnd()
    {
        return commentSet.blockEnd;
    }


    boolean isOpenBracket(int type)
    {
        return Arrays.stream(bracketPairs)
            .mapToInt(p -> p.open)
            .anyMatch(i -> i == type);
    }

    boolean isCloseBracket(int type)
    {
        return Arrays.stream(bracketPairs)
            .mapToInt(p -> p.close)
            .anyMatch(i -> i == type);
    }

    boolean isBracketPair(int open, int close)
    {
        return Arrays.stream(bracketPairs)
            .anyMatch(p -> p.open == open && p.close == close);
    }


    public int specialId()
    {
        return specialSet.id;
    }

    public int specialSeq()
    {
        return specialSet.seq;
    }

    public int specialExpr()
    {
        return specialSet.expr;
    }

    public int specialBlock()
    {
        return specialSet.block;
    }

    private static final class SpecialSet
    {
        final int id;
        final int seq;
        final int expr;
        final int block;
        SpecialSet(int id, int seq, int expr, int block)
        {
            this.id = id;
            this.seq = seq;
            this.expr = expr;
            this.block = block;
        }
    }

    private static final class CommentSet
    {
        final String line;
        final String blockBegin;
        final String blockEnd;
        CommentSet(String line, String blockBegin, String blockEnd)
        {
            this.line = line;
            this.blockBegin = blockBegin;
            this.blockEnd = blockEnd;
        }
    }

    private static final class BracketPair
    {
        final int open;
        final int close;
        BracketPair(int open, int close)
        {
            this.open = open;
            this.close = close;
        }
    }

    private static final class BlindSet
    {
        final BlindLevel minLevel;
        final int[] blindTypes;
        BlindSet(BlindLevel minLevel, int... blindTypes)
        {
            this.minLevel = minLevel;
            Arrays.sort(blindTypes);
            this.blindTypes = blindTypes;
        }

        boolean contains(int type)
        {
            return Arrays.binarySearch(blindTypes, type) >= 0;
        }
    }
}
