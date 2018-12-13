package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;

import jp.ac.osaka_u.ist.sel.ccgrep.antlr.c.CLexer;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.cpp14.CPP14Lexer;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.java9.Java9Lexer;
import jp.ac.osaka_u.ist.sel.ccgrep.antlr.python3.Python3Lexer;


public enum Language
{
    C(
        Arrays.asList("c"),
        Arrays.asList(".c", ".h"),
        CLexer::new,
        new SpecialSet(CLexer.CCG_SPECIAL_ID, CLexer.CCG_SPECIAL_SEQ, CLexer.CCG_SPECIAL_LPAR, CLexer.CCG_SPECIAL_RPAR, CLexer.CCG_SPECIAL_OR, CLexer.CCG_SPECIAL_MORE0),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(CLexer.LeftParen, CLexer.RightParen),
            new BracketPair(CLexer.LeftBracket, CLexer.RightBracket),
            new BracketPair(CLexer.LeftBrace, CLexer.RightBrace)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(BlindLevel.NONE, CLexer.Char, CLexer.Double, CLexer.Float, CLexer.Int, CLexer.Long, CLexer.Short, CLexer.Void, CLexer.Bool, CLexer.Identifier),
            new BlindSet(BlindLevel.FULL, CLexer.Constant, CLexer.DigitSequence, CLexer.StringLiteral)
        ),
        t -> preprocessorFilter(t, CLexer.Directive)
    ),
    CPP14(
        Arrays.asList("cpp", "cpp14", "c++", "c++14"),
        Arrays.asList(".cpp", ".cc", ".c++", ".cxx", ".c", ".h", ".hpp"),
        CPP14Lexer::new,
        new SpecialSet(CPP14Lexer.CCG_SPECIAL_ID, CPP14Lexer.CCG_SPECIAL_SEQ, CPP14Lexer.CCG_SPECIAL_LPAR, CPP14Lexer.CCG_SPECIAL_RPAR, CPP14Lexer.CCG_SPECIAL_OR, CPP14Lexer.CCG_SPECIAL_MORE0),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(CPP14Lexer.LeftParen, CPP14Lexer.RightParen),
            new BracketPair(CPP14Lexer.LeftBracket, CPP14Lexer.RightBracket),
            new BracketPair(CPP14Lexer.LeftBrace, CPP14Lexer.RightBrace)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(BlindLevel.NONE, CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char, CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double, CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long, CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar, CPP14Lexer.Identifier),
            new BlindSet(BlindLevel.FULL, CPP14Lexer.False, CPP14Lexer.Nullptr, CPP14Lexer.True, CPP14Lexer.Integerliteral, CPP14Lexer.Decimalliteral, CPP14Lexer.Octalliteral, CPP14Lexer.Hexadecimalliteral, CPP14Lexer.Binaryliteral, CPP14Lexer.Characterliteral, CPP14Lexer.Floatingliteral, CPP14Lexer.Stringliteral, CPP14Lexer.Userdefinedintegerliteral, CPP14Lexer.Userdefinedfloatingliteral, CPP14Lexer.Userdefinedstringliteral, CPP14Lexer.Userdefinedcharacterliteral)
        ),
        t -> preprocessorFilter(t, CPP14Lexer.Directive)
    ),
    JAVA9(
        Arrays.asList("java", "java9"),
        Arrays.asList(".java"),
        Java9Lexer::new,
        new SpecialSet(Java9Lexer.CCG_SPECIAL_ID, Java9Lexer.CCG_SPECIAL_SEQ, Java9Lexer.CCG_SPECIAL_LPAR, Java9Lexer.CCG_SPECIAL_RPAR, Java9Lexer.CCG_SPECIAL_OR, Java9Lexer.CCG_SPECIAL_MORE0),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(Java9Lexer.LPAREN, Java9Lexer.RPAREN),
            new BracketPair(Java9Lexer.LBRACE, Java9Lexer.RBRACE),
            new BracketPair(Java9Lexer.LBRACK, Java9Lexer.RBRACK)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(BlindLevel.NONE, Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR, Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT, Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.SUPER, Java9Lexer.THIS, Java9Lexer.VOID, Java9Lexer.Identifier),
            new BlindSet(BlindLevel.FULL, Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral, Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral)
        ),
        UnaryOperator.identity()
    ),
    PYTHON3(
        Arrays.asList("python", "python3"),
        Arrays.asList(".py"),
        Python3Lexer::new,
        new SpecialSet(Python3Lexer.CCG_SPECIAL_ID, Python3Lexer.CCG_SPECIAL_SEQ, Python3Lexer.CCG_SPECIAL_LPAR, Python3Lexer.CCG_SPECIAL_RPAR, Python3Lexer.CCG_SPECIAL_OR, Python3Lexer.CCG_SPECIAL_MORE0),
        new CommentSet("#", "\"\"\"", "\"\"\""),
        Arrays.<BracketPair>asList(
            new BracketPair(Python3Lexer.OPEN_PAREN, Python3Lexer.CLOSE_PAREN),
            new BracketPair(Python3Lexer.OPEN_BRACK, Python3Lexer.CLOSE_BRACK),
            new BracketPair(Python3Lexer.OPEN_BRACE, Python3Lexer.CLOSE_BRACE)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(BlindLevel.NONE, Python3Lexer.NAME),
            new BlindSet(BlindLevel.FULL, Python3Lexer.STRING, Python3Lexer.NUMBER, Python3Lexer.INTEGER, Python3Lexer.NONE, Python3Lexer.TRUE, Python3Lexer.FALSE, Python3Lexer.STRING_LITERAL, Python3Lexer.BYTES_LITERAL, Python3Lexer.DECIMAL_INTEGER, Python3Lexer.OCT_INTEGER, Python3Lexer.HEX_INTEGER, Python3Lexer.BIN_INTEGER, Python3Lexer.FLOAT_NUMBER, Python3Lexer.IMAG_NUMBER)
        ),
        UnaryOperator.identity()
    );

    private final List<String> names;
    private final List<String> extensions;
    private final Function<CharStream, Lexer> lexerCreater;

    private final SpecialSet specialSet;
    private final CommentSet commentSet;
    public final List<BracketPair> bracketPairs;
    private final List<BlindSet> blindSets;

    private final UnaryOperator<List<GrepToken>> tokenListFilter;

    Language(
        List<String> names, List<String> extensions,
        Function<CharStream, Lexer> lexerCreater,
        SpecialSet specialSet,
        CommentSet commentSet,
        List<BracketPair> bracketPairs,
        List<BlindSet> blindSets,
        UnaryOperator<List<GrepToken>> tokenListFilter)
    {
        this.names = names;
        this.extensions = extensions;
        this.lexerCreater = lexerCreater;

        this.specialSet = specialSet;
        this.commentSet = commentSet;
        this.bracketPairs = bracketPairs;
        this.blindSets = blindSets;

        this.tokenListFilter = tokenListFilter;
    }

    public static final Language getDefaultLanguage()
    {
        return Language.JAVA9;
    }

    public static final Optional<Language> findByName(String languageName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> l.names.stream().anyMatch(languageName::equalsIgnoreCase))
            .findFirst();
    }

    public static final Optional<Language> findByFileNameWithExtension(String fileName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> l.extensions.stream().anyMatch(fileName::endsWith))
            .findFirst();
    }

    public final Lexer createLexer(CharStream stream)
    {
        return lexerCreater.apply(stream);
    }

    public final BlindLevel findBlindLevel(GrepToken needleToken, GrepToken haystackToken, BlindLevel blindLevel)
    {
        return blindSets.stream()
            .filter(set -> set.contains(needleToken.getType())
                        && set.contains(haystackToken.getType()))
            .map(set -> set.minLevel)
            .map(minLevel -> blindLevel.value >= minLevel.value
                            ? blindLevel: minLevel)
            .findFirst()
            .orElse(BlindLevel.NONE);
    }

    public final boolean matchesExtension(String filename)
    {
        return extensions.stream()
            .anyMatch(filename::endsWith);
    }

    public final String lineCommented(String str)
    {
        return commentSet.line + str;
    }

    public final String blockCommented(String str)
    {
        return commentSet.blockBegin + str + commentSet.blockEnd;
    }

    public final String lineComment()
    {
        return commentSet.line;
    }

    public final String blockCommentBegin()
    {
        return commentSet.blockBegin;
    }

    public final String blockCommentEnd()
    {
        return commentSet.blockEnd;
    }


    public final boolean isOpenBracket(int type)
    {
        return bracketPairs.stream()
            .mapToInt(p -> p.open)
            .anyMatch(i -> i == type);
    }

    public final boolean isCloseBracket(int type)
    {
        return bracketPairs.stream()
            .mapToInt(p -> p.close)
            .anyMatch(i -> i == type);
    }

    public final boolean isBracketPair(int open, int close)
    {
        return bracketPairs.stream()
            .anyMatch(p -> p.open == open && p.close == close);
    }


    public final int specialId()
    {
        return specialSet.id;
    }

    public final int specialSeq()
    {
        return specialSet.seq;
    }

    public final int specialLpar()
    {
        return specialSet.lpar;
    }

    public final int specialRpar()
    {
        return specialSet.rpar;
    }

    public final int specialOr()
    {
        return specialSet.or;
    }

    public final int specialMore0()
    {
        return specialSet.more0;
    }

    private static final class SpecialSet
    {
        final int id;
        final int seq;
        final int lpar;
        final int rpar;
        final int or;
        final int more0;
        SpecialSet(int id, int seq, int lpar, int rpar, int or, int more0)
        {
            this.id = id;
            this.seq = seq;
            this.lpar = lpar;
            this.rpar = rpar;
            this.or = or;
            this.more0 = more0;
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

    public static final class BracketPair
    {
        public final int open;
        public final int close;
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

    public final List<GrepToken> filter(List<GrepToken> tokens)
    {
        return tokenListFilter.apply(tokens);
    }

    // From: jp.ac.osaka_u.ist.sel.clonedetector.analyze.CAnalyzer4.preProcessor
    // プリプロセッサ
    // マクロの除去
    private static final Pattern pElse = Pattern.compile("#[ \\t]*el(se|if)(.|[\\n\\r])*");
    private static final Pattern pEndif = Pattern.compile("#[ \\t]*endif(.|[\\n\\r])*");
    private static final List<GrepToken> preprocessorFilter(List<GrepToken> tokens, int directiveType)
    {
        final Iterator<GrepToken> it = tokens.iterator();
        while(it.hasNext())
        {
            GrepToken token = it.next();
            if(token.getType() == directiveType)
            {
                if (pElse.matcher(token.getText()).matches())
                {
                    it.remove();
                    token = it.next();
                    while (token.getType() != directiveType || !pEndif.matcher(token.getText()).matches())
                    {
                        it.remove();
                        token = it.next();
                    }
                }
                it.remove();
            }
        }
        return tokens;
    }
}
