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
        new SpecialSet(CLexer.CCG_SPECIAL_ID, CLexer.CCG_SPECIAL_EXPR, CLexer.CCG_SPECIAL_BLOCK),
        new CommentSet("//", "/*", "*/"),
        new BlindSet[]{
            new BlindSet(CLexer.Char, CLexer.Double, CLexer.Float, CLexer.Int, CLexer.Long, CLexer.Short, CLexer.Void, CLexer.Bool, CLexer.Identifier),
            new BlindSet(CLexer.Constant, CLexer.StringLiteral)
        }
    ),
    CPP14(
        new String[]{"cpp", "cpp14", "c++", "c++14"},
        new String[]{".cpp", ".cc", ".c++", ".cxx", ".c", ".h", ".hpp"},
        CPP14Lexer::new,
        CPP14Parser::new,
        new SpecialSet(-1, -1, -1),
        new CommentSet("//", "/*", "*/"),
        new BlindSet[]{
            new BlindSet(CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char, CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double, CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long, CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar, CPP14Lexer.Identifier),
            new BlindSet(CPP14Lexer.False, CPP14Lexer.Nullptr, CPP14Lexer.True, CPP14Lexer.Integerliteral, CPP14Lexer.Decimalliteral, CPP14Lexer.Octalliteral, CPP14Lexer.Hexadecimalliteral, CPP14Lexer.Binaryliteral, CPP14Lexer.Characterliteral, CPP14Lexer.Floatingliteral, CPP14Lexer.Stringliteral, CPP14Lexer.Userdefinedintegerliteral, CPP14Lexer.Userdefinedfloatingliteral, CPP14Lexer.Userdefinedstringliteral, CPP14Lexer.Userdefinedcharacterliteral)
        }
    ),
    JAVA9(
        new String[]{"java", "java9"},
        new String[]{".java"},
        Java9Lexer::new,
        Java9Parser::new,
        new SpecialSet(Java9Lexer.CCG_SPECIAL_ID, Java9Lexer.CCG_SPECIAL_EXPR, Java9Lexer.CCG_SPECIAL_BLOCK),
        new CommentSet("//", "/*", "*/"),
        new BlindSet[]{
            new BlindSet(Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR, Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT, Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.VOID, Java9Lexer.Identifier),
            new BlindSet(Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral, Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral)
        }
    ),
    PYTHON3(
        new String[]{"python", "python3"},
        new String[]{".py"},
        Python3Lexer::new,
        Python3Parser::new,
        new SpecialSet(-1, -1, -1),
        new CommentSet("#", "\"\"\"", "\"\"\""),
        new BlindSet[]{
            new BlindSet(Python3Lexer.NAME),
            new BlindSet(Python3Lexer.STRING, Python3Lexer.NUMBER, Python3Lexer.INTEGER, Python3Lexer.NONE, Python3Lexer.TRUE, Python3Lexer.FALSE, Python3Lexer.STRING_LITERAL, Python3Lexer.BYTES_LITERAL, Python3Lexer.DECIMAL_INTEGER, Python3Lexer.OCT_INTEGER, Python3Lexer.HEX_INTEGER, Python3Lexer.BIN_INTEGER, Python3Lexer.FLOAT_NUMBER, Python3Lexer.IMAG_NUMBER)
        }
    );

    private final String[] names;
    private final String[] extensions;
    private final Function<CharStream, Lexer> lexerCreater;
    private final Function<TokenStream, Parser> parserCreater;

    private final SpecialSet specialSet;
    private final CommentSet commentSet;
    private final BlindSet[] blindSets;

    Language(
        String[] names, String[] extensions,
        Function<CharStream, Lexer> lexerCreater,
        Function<TokenStream, Parser> parserCreater,
        SpecialSet specialSet,
        CommentSet commentSet,
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
        this.blindSets = blindSets;
    }

    public static Language findByName(String languageName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> Arrays.stream(l.names).anyMatch(languageName::equalsIgnoreCase))
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

    public boolean checkTokenEquality(
        GrepToken needleToken, GrepToken haystackToken, BlindLevel blindLevel, Map<String, GrepToken> blindConstraint)
    {
        if(needleToken.getType() == specialId())
        {
            return needleToken.equalsAsSpecialTo(haystackToken);
        }
        final BlindLevel bl = Arrays.stream(blindSets)
            .filter(set -> set.contains(needleToken.getType())
                        && set.contains(haystackToken.getType()))
            .map(set -> set.minLevel)
            .map(minLevel -> (blindLevel.value >= minLevel.value)? blindLevel: minLevel)
            .findFirst()
            .orElse(null);
        return bl == null
            ? needleToken.equals(haystackToken)
            : bl.checkTokenEquality(needleToken, haystackToken, blindConstraint);
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


    public int specialId()
    {
        return specialSet.id;
    }

    public int specialExpr()
    {
        return specialSet.expr;
    }

    public int specialBlock()
    {
        return specialSet.block;
    }

    private static class SpecialSet
    {
        final int id;
        final int expr;
        final int block;
        SpecialSet(int id, int expr, int block)
        {
            this.id = id;
            this.expr = expr;
            this.block = block;
        }
    }

    private static class CommentSet
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

    private static class BlindSet
    {
        final BlindLevel minLevel;
        final int[] blindTypes;
        BlindSet(BlindLevel minLevel, int... blindTypes)
        {
            this.minLevel = minLevel;
            Arrays.sort(blindTypes);
            this.blindTypes = blindTypes;
        }

        BlindSet(int... blindTypes)
        {
            this(BlindLevel.NONE, blindTypes);
        }

        boolean contains(int type)
        {
            return Arrays.binarySearch(blindTypes, type) >= 0;
        }
    }
}
