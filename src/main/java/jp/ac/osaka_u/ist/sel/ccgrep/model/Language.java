package jp.ac.osaka_u.ist.sel.ccgrep.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import jp.ac.osaka_u.ist.sel.ccgrep.antlr.*;


public enum Language
{
    C(
        Arrays.asList("c"),
        Arrays.asList("c", "h"),
        CLexer::new,
        CLexer::new,
        new SpecialSet(CLexer.class),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(CLexer.LeftParen, CLexer.RightParen),
            new BracketPair(CLexer.LeftBracket, CLexer.RightBracket),
            new BracketPair(CLexer.LeftBrace, CLexer.RightBrace)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(
                null,
                CLexer.Char, CLexer.Double, CLexer.Float,
                CLexer.Int, CLexer.Long, CLexer.Short,
                CLexer.Void, CLexer.Bool, CLexer.Identifier
            ),
            new BlindSet(
                BlindLevel.FULL,
                CLexer.Constant, CLexer.DigitSequence, CLexer.StringLiteral
            )
        ),
        t -> filterPreprocessor(t, CLexer.Directive)
    ),
    CPP14(
        Arrays.asList("cpp", "cpp14", "c++", "c++14"),
        Arrays.asList("cpp", "cc", "c++", "cxx", "c", "h", "hpp"),
        CPP14Lexer::new,
        CPP14Lexer::new,
        new SpecialSet(CPP14Lexer.class),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(CPP14Lexer.LeftParen, CPP14Lexer.RightParen),
            new BracketPair(CPP14Lexer.LeftBracket, CPP14Lexer.RightBracket),
            new BracketPair(CPP14Lexer.LeftBrace, CPP14Lexer.RightBrace)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(
                null,
                CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char,
                CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double,
                CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long,
                CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar,
                CPP14Lexer.Identifier
            ),
            new BlindSet(
                BlindLevel.FULL,
                CPP14Lexer.False, CPP14Lexer.Nullptr, CPP14Lexer.True,
                CPP14Lexer.Integerliteral, CPP14Lexer.Decimalliteral, CPP14Lexer.Octalliteral,
                CPP14Lexer.Hexadecimalliteral, CPP14Lexer.Binaryliteral, CPP14Lexer.Characterliteral,
                CPP14Lexer.Floatingliteral, CPP14Lexer.Stringliteral, CPP14Lexer.Userdefinedintegerliteral,
                CPP14Lexer.Userdefinedfloatingliteral, CPP14Lexer.Userdefinedstringliteral, CPP14Lexer.Userdefinedcharacterliteral
            )
        ),
        t -> filterPreprocessor(t, CPP14Lexer.Directive)
    ),
    JAVA9(
        Arrays.asList("java", "java9"),
        Arrays.asList("java"),
        Java9QueryLexer::new,
        Java9Lexer::new,
        new SpecialSet(Java9QueryLexer.class),
        new CommentSet("//", "/*", "*/"),
        Arrays.<BracketPair>asList(
            new BracketPair(Java9Lexer.LPAREN, Java9Lexer.RPAREN),
            new BracketPair(Java9Lexer.LBRACE, Java9Lexer.RBRACE),
            new BracketPair(Java9Lexer.LBRACK, Java9Lexer.RBRACK)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(
                null,
                Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR,
                Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT,
                Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.SUPER,
                Java9Lexer.THIS, Java9Lexer.VOID, Java9Lexer.Identifier,
                Java9Lexer.T__0, Java9Lexer.T__1, Java9Lexer.T__2,
                Java9Lexer.T__3, Java9Lexer.T__4, Java9Lexer.T__5,
                Java9Lexer.T__6, Java9Lexer.T__7, Java9Lexer.T__8,
                Java9Lexer.T__9
            ),
            new BlindSet(
                BlindLevel.FULL,
                Java9Lexer.IntegerLiteral, Java9Lexer.FloatingPointLiteral, Java9Lexer.BooleanLiteral,
                Java9Lexer.CharacterLiteral, Java9Lexer.StringLiteral, Java9Lexer.NullLiteral
            )
        ),
        Language::filterJavaDollar
    ),
    PYTHON3(
        Arrays.asList("python", "python3"),
        Arrays.asList("py"),
        Python3Lexer::new,
        Python3Lexer::new,
        new SpecialSet(Python3Lexer.class),
        new CommentSet("#", "\"\"\"", "\"\"\""),
        Arrays.<BracketPair>asList(
            new BracketPair(Python3Lexer.OPEN_PAREN, Python3Lexer.CLOSE_PAREN),
            new BracketPair(Python3Lexer.OPEN_BRACK, Python3Lexer.CLOSE_BRACK),
            new BracketPair(Python3Lexer.OPEN_BRACE, Python3Lexer.CLOSE_BRACE),
            new BracketPair(Python3Parser.INDENT, Python3Parser.DEDENT)
        ),
        Arrays.<BlindSet>asList(
            new BlindSet(null, Python3Lexer.NAME),
            new BlindSet(
                BlindLevel.FULL,
                Python3Lexer.STRING, Python3Lexer.NUMBER, Python3Lexer.INTEGER,
                Python3Lexer.NONE, Python3Lexer.TRUE, Python3Lexer.FALSE,
                Python3Lexer.STRING_LITERAL, Python3Lexer.BYTES_LITERAL, Python3Lexer.DECIMAL_INTEGER,
                Python3Lexer.OCT_INTEGER, Python3Lexer.HEX_INTEGER, Python3Lexer.BIN_INTEGER,
                Python3Lexer.FLOAT_NUMBER, Python3Lexer.IMAG_NUMBER
            )
        ),
        t -> filterPythonDedent(t, Python3Parser.NEWLINE, Python3Parser.INDENT, Python3Parser.DEDENT)
    );

    private final List<String> names;
    private final List<String> extensions;
    private final Function<CharStream, Lexer> queryLexerCreater;
    private final Function<CharStream, Lexer> lexerCreater;

    private final SpecialSet specialSet;
    private final CommentSet commentSet;
    public final List<BracketPair> bracketPairs;
    private final List<BlindSet> blindSets;

    private final UnaryOperator<List<GrepToken>> tokenListFilter;

    Language(
        List<String> names, List<String> extensions,
        Function<CharStream, Lexer> queryLexerCreater,
        Function<CharStream, Lexer> lexerCreater,
        SpecialSet specialSet,
        CommentSet commentSet,
        List<BracketPair> bracketPairs,
        List<BlindSet> blindSets,
        UnaryOperator<List<GrepToken>> tokenListFilter)
    {
        this.names = names;
        this.extensions = extensions;
        this.queryLexerCreater = queryLexerCreater;
        this.lexerCreater = lexerCreater;

        this.specialSet = specialSet;
        this.commentSet = commentSet;
        this.bracketPairs = bracketPairs;
        this.blindSets = blindSets;

        this.tokenListFilter = tokenListFilter;
        IntStream.range(0, 256)
            .forEach(this::findBlindSet);
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

    public final Lexer createQueryLexer(CharStream stream)
    {
        return queryLexerCreater.apply(stream);
    }

    public final Lexer createLexer(CharStream stream)
    {
        return lexerCreater.apply(stream);
    }

    private final HashMap<Integer, BlindSet> blindSetTable = new HashMap<>();
    final BlindSet findBlindSet(GrepToken token)
    {
        return findBlindSet(token.getType());
    }

    final synchronized BlindSet findBlindSet(int tokenType)
    {
        return blindSetTable.computeIfAbsent(
            tokenType,
            t -> blindSets.stream()
                .filter(set -> set.contains(tokenType))
                .findFirst()
                .orElse(BlindSet.OTHERWISE_SET)
        );
    }

    public final BlindLevel findBlindLevel(BlindSet needleSet, BlindSet haystackSet, BlindLevel blindLevel)
    {
        return needleSet == haystackSet
            ? needleSet.findBlindLevel(blindLevel)
            : BlindLevel.ONLY_TOKEN_TYPE;
    }

    public final List<String> getExtensions()
    {
        return extensions;
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


    public final boolean isSpecialId(GrepToken token)
    {
        return token.getType() == specialSet.id;
    }

    public final boolean isSpecialSeq(GrepToken token)
    {
        return token.getType() == specialSet.seq;
    }

    public final boolean isSpecialAnySeq(GrepToken token)
    {
        return token.getType() == specialSet.anyseq;
    }

    public final boolean isSpecialLpar(GrepToken token)
    {
        return token.getType() == specialSet.lpar;
    }

    public final boolean isSpecialRpar(GrepToken token)
    {
        return token.getType() == specialSet.rpar;
    }

    public final boolean isSpecialOrFst(GrepToken token)
    {
        return token.getType() == specialSet.orfst;
    }

    public final boolean isSpecialOrLng(GrepToken token)
    {
        return token.getType() == specialSet.orlng;
    }

    public final boolean isSpecialMore0(GrepToken token)
    {
        return token.getType() == specialSet.more0;
    }

    public final boolean isSpecialMore1(GrepToken token)
    {
        return token.getType() == specialSet.more1;
    }

    public final boolean isSpecialEith(GrepToken token)
    {
        return token.getType() == specialSet.eith;
    }

    public final boolean isSpecialLookaheadPos(GrepToken token)
    {
        return token.getType() == specialSet.lookaheadpos;
    }

    public final boolean isSpecialLookaheadNeg(GrepToken token)
    {
        return token.getType() == specialSet.lookaheadneg;
    }

    public final boolean isSpecialAny(GrepToken token)
    {
        return token.getType() == specialSet.any;
    }

    public final boolean isSomeSpecial(GrepToken token)
    {
        return isSpecialId(token)
            || isSpecialSeq(token)
            || isSpecialAnySeq(token)
            || isSpecialLpar(token)
            || isSpecialRpar(token)
            || isSpecialOrFst(token)
            || isSpecialOrLng(token)
            || isSpecialMore0(token)
            || isSpecialMore1(token)
            || isSpecialEith(token)
            || isSpecialLookaheadPos(token)
            || isSpecialLookaheadNeg(token)
            || isSpecialAny(token);
    }

    private static final class SpecialSet
    {
        final int id;
        final int seq;
        final int anyseq;
        final int lpar;
        final int rpar;
        final int orfst;
        final int orlng;
        final int more0;
        final int more1;
        final int eith;
        final int lookaheadpos;
        final int lookaheadneg;
        final int any;
        SpecialSet(Class<? extends Lexer> cls)
        {
	        try{
                this.id    = cls.getField("CCG_SPECIAL_ID").getInt(null);
                this.seq   = cls.getField("CCG_SPECIAL_SEQ").getInt(null);
                this.anyseq   = cls.getField("CCG_SPECIAL_ANYSEQ").getInt(null);
                this.lpar  = cls.getField("CCG_SPECIAL_LPAR").getInt(null);
                this.rpar  = cls.getField("CCG_SPECIAL_RPAR").getInt(null);
                this.orfst = cls.getField("CCG_SPECIAL_ORFST").getInt(null);
                this.orlng = cls.getField("CCG_SPECIAL_ORLNG").getInt(null);
                this.more0 = cls.getField("CCG_SPECIAL_MORE0").getInt(null);
                this.more1 = cls.getField("CCG_SPECIAL_MORE1").getInt(null);
                this.eith  = cls.getField("CCG_SPECIAL_EITH").getInt(null);
                this.lookaheadpos  = cls.getField("CCG_SPECIAL_LAP").getInt(null);
                this.lookaheadneg  = cls.getField("CCG_SPECIAL_LAN").getInt(null);
                this.any   = cls.getField("CCG_SPECIAL_ANY").getInt(null);
            }
            catch(ReflectiveOperationException e)
            {
                assert false;
                throw new AssertionError(e);
            }
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

    static class BlindSet
    {
        static final BlindSet OTHERWISE_SET = new BlindSet(BlindLevel.ONLY_TOKEN_TYPE) {
            @Override
            BlindLevel findBlindLevel(BlindLevel blindLevel)
            {
                return BlindLevel.ONLY_TOKEN_TYPE;
            }
        };

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

        BlindLevel findBlindLevel(BlindLevel blindLevel)
        {
            return minLevel == null || blindLevel.value >= minLevel.value
                    ? blindLevel: minLevel;
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
    private static List<GrepToken> filterPreprocessor(List<GrepToken> tokens, int directiveType)
    {
        final Iterator<GrepToken> it = tokens.iterator();
        outer:while(it.hasNext())
        {
            GrepToken token = it.next();
            if(token.getType() == directiveType)
            {
                if (pElse.matcher(token.getText()).matches())
                {
                    do{
                        it.remove();
                        if(!it.hasNext())
                        {
                            break outer;
                        }
                        token = it.next();
                    } while (token.getType() != directiveType || !pEndif.matcher(token.getText()).matches());
                }
                it.remove();
            }
        }
        return tokens;
    }

    // remove NEWLINE before DEDENT
    private static List<GrepToken> filterPythonDedent(List<GrepToken> tokens, int newline, int indent, int dedent)
    {
        final Iterator<GrepToken> it0 = tokens.iterator();
        while(it0.hasNext() && it0.next().getType() == newline)
        {
            it0.remove();
        }

        final ListIterator<GrepToken> it1 = tokens.listIterator(tokens.size());
        while(it1.hasPrevious() && it1.previous().getType() == newline)
        {
            it1.remove();
        }

        final ListIterator<GrepToken> it = tokens.listIterator(tokens.size());
        while(it.hasPrevious())
        {
            final int tt = it.previous().getType();
            if(tt == indent || tt == dedent)
            {
                if(it.hasPrevious())
                {
                    if(it.previous().getType() == newline)
                    {
                        it.remove();
                    }
                    else
                    {
                        it.next();
                    }
                }
            }
        }
        return tokens;
    }

    private static List<GrepToken> filterJavaDollar(List<GrepToken> tokens)
    {
        tokens.stream()
            .filter(t -> t.getType() == Java9QueryLexer.Identifier || t.getType() == Java9QueryLexer.CCG_SPECIAL_ID)
            .forEach(t -> t.setText(t.getText().replace("\\$", "$")));
        return tokens;
    }
}
