package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.antlr.v4.runtime.*;

import antlr.c.*;
import antlr.cpp14.*;
import antlr.java9.*;
import antlr.python3.*;

public enum Language
{
    C(
        new String[]{"c"},
        new String[]{".c", ".h"},
        CLexer::new,
        CParser::new,
        CLexer.CCG_SPECIAL_ID, CLexer.CCG_SPECIAL_EXPR, CLexer.CCG_SPECIAL_BLOCK,
        "//", "/*", "*/",
        new int[]{CLexer.Char, CLexer.Double, CLexer.Float, CLexer.Int, CLexer.Long, CLexer.Short, CLexer.Void, CLexer.Bool, CLexer.Identifier},
        new int[]{CLexer.Constant, CLexer.StringLiteral}
    ),
    CPP14(
        new String[]{"cpp", "cpp14", "c++", "c++14"},
        new String[]{".cpp", ".h", ".hpp"},
        CPP14Lexer::new,
        CPP14Parser::new,
        -1, -1, -1,
        "//", "/*", "*/",
        new int[]{CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char, CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double, CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long, CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar, CPP14Lexer.Identifier},
        new int[]{CPP14Lexer.False, CPP14Lexer.Nullptr, CPP14Lexer.True, CPP14Lexer.Integerliteral, CPP14Lexer.Decimalliteral, CPP14Lexer.Octalliteral, CPP14Lexer.Hexadecimalliteral, CPP14Lexer.Binaryliteral, CPP14Lexer.Characterliteral, CPP14Lexer.Floatingliteral, CPP14Lexer.Stringliteral, CPP14Lexer.Userdefinedintegerliteral, CPP14Lexer.Userdefinedfloatingliteral, CPP14Lexer.Userdefinedstringliteral, CPP14Lexer.Userdefinedcharacterliteral}
    ),
    JAVA9(
        new String[]{"java", "java9"},
        new String[]{".java"},
        Java9Lexer::new,
        Java9Parser::new,
        Java9Lexer.CCG_SPECIAL_ID, Java9Lexer.CCG_SPECIAL_EXPR, Java9Lexer.CCG_SPECIAL_BLOCK,
        "//", "/*", "*/",
        new int[]{Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR, Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT, Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.VOID, Java9Lexer.Identifier},
        new int[]{Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral, Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral}
    ),
    PYTHON3(
        new String[]{"python", "python3"},
        new String[]{".py"},
        Python3Lexer::new,
        Python3Parser::new,
        -1, -1, -1,
        "#", "\"\"\"", "\"\"\"",
        new int[]{Python3Lexer.NAME},
        new int[]{Python3Lexer.STRING, Python3Lexer.NUMBER, Python3Lexer.INTEGER, Python3Lexer.NONE, Python3Lexer.TRUE, Python3Lexer.FALSE, Python3Lexer.STRING_LITERAL, Python3Lexer.BYTES_LITERAL, Python3Lexer.DECIMAL_INTEGER, Python3Lexer.OCT_INTEGER, Python3Lexer.HEX_INTEGER, Python3Lexer.BIN_INTEGER, Python3Lexer.FLOAT_NUMBER, Python3Lexer.IMAG_NUMBER}
    );

    private final String[] names;
    private final String[] extensions;
    private final Function<CharStream, Lexer> lexerCreater;
    private final Function<TokenStream, Parser> parserCreater;

    private final int _specialId, _specialExpr, _specialBlock;

    private final int[] identifierBlindables;
    private final int[] literalBlindables;

    private String lineComment, blockCommentBegin, blockCommentEnd;

    Language(
        String[] names, String[] extensions,
        Function<CharStream, Lexer> lexerCreater,
        Function<TokenStream, Parser> parserCreater,
        int specialId, int specialExpr, int specialBlock,
        String lineComment, String blockCommentBegin, String blockCommentEnd,
        int[] identifierBlindables, int[] literalBlindables)
    {
        Arrays.sort(names);
        Arrays.sort(extensions);
        this.names = names;
        this.extensions = extensions;
        this.lexerCreater = lexerCreater;
        this.parserCreater = parserCreater;

        this._specialId = specialId;
        this._specialExpr = specialExpr;
        this._specialBlock = specialBlock;

        Arrays.sort(identifierBlindables);
        Arrays.sort(literalBlindables);
        this.identifierBlindables = identifierBlindables;
        this.literalBlindables = literalBlindables;

        this.lineComment = lineComment;
        this.blockCommentBegin = blockCommentBegin;
        this.blockCommentEnd = blockCommentEnd;
    }

    public static Language findByName(String languageName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> Arrays.binarySearch(l.names, languageName) >= 0)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Language `" + languageName + "` is not supported."));
    }

    public Lexer createLexer(CharStream stream)
    {
        return lexerCreater.apply(stream);
    }

    public Parser createParser(TokenStream stream)
    {
        return parserCreater.apply(stream);
    }

    public boolean isBlindableIdentifier(final int tokenType)
    {
        return Arrays.binarySearch(identifierBlindables, tokenType) >= 0;
    }

    public boolean isBlindableLiteral(final int tokenType)
    {
        return Arrays.binarySearch(literalBlindables, tokenType) >= 0;
    }

    public boolean matchesExtension(String filename)
    {
        return Arrays.stream(extensions)
            .anyMatch(filename::endsWith);
    }

    public String lineCommented(String str)
    {
        return lineComment + str;
    }

    public String blockCommented(String str)
    {
        return blockCommentBegin + str + blockCommentEnd;
    }


    public int specialId()
    {
        return _specialId;
    }

    public int specialExpr()
    {
        return _specialExpr;
    }

    public int specialBlock()
    {
        return _specialBlock;
    }
}
