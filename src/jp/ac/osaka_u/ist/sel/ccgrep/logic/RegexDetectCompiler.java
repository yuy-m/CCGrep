package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

import jp.ac.osaka_u.ist.sel.ccgrep.model.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.IParser;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.INode;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Range;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;


enum RegexDetectCompiler implements IParser<GrepToken>
{
    /**
     * ROOT     -> OR_LONG !.
     * OR_LONG  -> OR_FIRST ('$|' OR_FIRST)*
     * OR_FIRST -> SEQ ('$/' SEQ)*
     * SEQ      -> MORE0*
     * MORE0    -> SINGLE '$*'?
     * SINGLE   -> SP_SEQ / PAREN / NORMAL
     * SP_SEQ   -> '$$' !( '$$' / '$*' / '$(' / '$)' / '$|') &.
     * PAREN    -> '$(' OR_LONG '$)'
     * NORMAL   -> !( '$|' / '$)') .
     */
    ROOT{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                OR_LONG,
                not(any())
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> n.getChild(0);
        }
    },
    OR_LONG{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                OR_FIRST,
                repeat(sequence(
                    value(r -> language.isSpecialOrLng(r.front())),
                    OR_FIRST
                ))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                int[] a = {0};
                final ArrayList<IParser<GrepToken>> l = new ArrayList<>();
                l.add(rmConstr(n.getCastedChild(0)));
                n.getChild(1).getChildren().stream()
                    .map(n1 -> rmConstr(n1.getCastedChild(1)))
                    .forEach(l::add);
                return selectLongest(l);
            };
        }
    },
    OR_FIRST{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                SEQ,
                repeat(sequence(
                    value(r -> language.isSpecialOrFst(r.front())),
                    SEQ
                ))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                int[] a = {0};
                final ArrayList<IParser<GrepToken>> l = new ArrayList<>();
                l.add(rmConstr(n.getCastedChild(0)));
                n.getChild(1).getChildren().stream()
                    .map(n1 -> rmConstr(n1.getCastedChild(1)))
                    .forEach(l::add);
                return selectFirst(l);
            };
        }
    },
    SEQ{
        @Override
        protected IParser<GrepToken> from()
        {
            return repeat(MORE0);
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> sequence(n.getCastedChildren());
        }
    },
    MORE0{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                SINGLE,
                either(value(r -> language.isSpecialMore0(r.front())))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final IParser<GrepToken> p = n.getCastedChild(0);
                return n.getChild(1).countChildren() == 0
                    ? p
                    : repeat(rmConstr(p));
            };
        }
    },
    SINGLE{
        @Override
        protected IParser<GrepToken> from()
        {
            return selectFirst(SP_SEQ, PAREN, NORMAL);
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return Function.identity();
        }
    },
    SP_SEQ{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                value(r -> language.isSpecialSeq(r.front())),
                lookahead(
                    value(r -> !language.isSpecialSeq(r.front())
                            && !language.isSpecialMore0(r.front())
                            && !language.isSpecialLpar(r.front())
                            && !language.isSpecialRpar(r.front())
                            && !language.isSpecialOrFst(r.front())
                            && !language.isSpecialOrLng(r.front())
                    )
                )
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> new AnyTokenSequence(language, n.getChild(1).getValue());
        }
    },
    PAREN{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                value(r -> language.isSpecialLpar(r.front())),
                OR_LONG,
                value(r -> language.isSpecialRpar(r.front()))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> n.getChild(1);
        }
    },
    NORMAL{
        @Override
        protected IParser<GrepToken> from()
        {
            return value(r -> !language.isSpecialRpar(r.front())
                            && !language.isSpecialOrFst(r.front())
                            && !language.isSpecialOrLng(r.front())
                    );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> value(n.getValue());
        }
    };

    private static Language language;

    public static void setLanguage(Language language)
    {
        if(RegexDetectCompiler.language != null)
        {
            throw new IllegalStateException("language already set.");
        }
        RegexDetectCompiler.language = Objects.requireNonNull(language);
    }

    private IParser<GrepToken> parser;
    private IParser<GrepToken> getParser()
    {
        if(parser == null)
        {
            parser = mapper(from(), to());
        }
        return parser;
    }

    @Override
    public INode<GrepToken> parse(Range<GrepToken> range)
    {
        return getParser().parse(range);
    }

    protected abstract IParser<GrepToken> from();
    protected abstract Function<INode<GrepToken>, INode<GrepToken>> to();

    static IParser<GrepToken> compile(List<GrepToken> needle)
    {
        if(language == null)
        {
            throw new IllegalStateException("set language before.");
        }

        final IParser<GrepToken> p = ROOT.parse(new Range<GrepToken>(needle)).casted();
        if(p.matches(new GrepRange(
            Collections.emptyList(),
            BlindLevel.NONE,
            Collections.emptyMap()
        )))
        {
            return null;
        }
        return p;
    }

    protected static IParser<GrepToken> rmConstr(IParser<GrepToken> p)
    {
        return new IParser<GrepToken>() {
            @Override
            public boolean matches(Range<GrepToken> range)
            {
                final GrepRange gr = ((GrepRange)range);
                final HashMap<String, String> saved = new HashMap<>(gr.getConstraint());
                final boolean b = p.matches(range);
                gr.replaceConstraint(saved);
                return b;
            }
            @Override
            public INode<GrepToken> parse(Range<GrepToken> range)
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
