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
import jp.ac.osaka_u.ist.sel.ccgrep.CCGrepException;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.INode;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Range;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;


enum RegexDetectCompiler implements IParser<GrepToken>
{
    /**
     * ROOT     -> OR_LONG !.
     * OR_LONG  -> OR_FIRST ('$|' OR_FIRST)*
     * OR_FIRST -> SEQ ('$/' SEQ)*
     * SEQ      -> SUFFIX+
     * SUFFIX   -> SINGLE '$*'?
     * SINGLE   -> SP_SEQ / PAREN / ONE
     * SP_SEQ   -> '$$' !( '$$' / '$*' / '$(' / '$)' / '$|') &.
     * PAREN    -> '$(' OR_LONG '$)'
     * ONE      -> !( '$|' / '$)') .
     */
    ROOT{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                OR_LONG,
                lookahead(not(any()))
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
                repeatFullMatch(0, sequence(
                    value(r -> language.isSpecialOrLng(r.front())),
                    OR_FIRST
                ))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final ArrayList<IParser<GrepToken>> l = new ArrayList<>();
                l.add(rmConstr(n.getCastedChild(0), true));
                n.getChild(1).getChildren().stream()
                    .map(n1 -> rmConstr(n1.getCastedChild(1), true))
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
                repeatFullMatch(0, sequence(
                    value(r -> language.isSpecialOrFst(r.front())),
                    SEQ
                ))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final ArrayList<IParser<GrepToken>> l = new ArrayList<>();
                l.add(rmConstr(n.getCastedChild(0), true));
                n.getChild(1).getChildren().stream()
                    .map(n1 -> rmConstr(n1.getCastedChild(1), true))
                    .forEach(l::add);
                return selectFirst(l);
            };
        }
    },
    SEQ{
        @Override
        protected IParser<GrepToken> from()
        {
            return repeatFullMatch(1, SUFFIX);
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> n.countChildren() == 1
                ? n.getCastedChild(0)
                : sequence(n.getCastedChildren());
        }
    },
    SUFFIX{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                SINGLE,
                either(
                    value(r -> language.isSpecialMore0(r.front())
                            || language.isSpecialMore1(r.front())
                            || language.isSpecialEith(r.front())
                    )
                )
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final IParser<GrepToken> p = n.getCastedChild(0);
                if(n.getChild(1).countChildren() == 0)
                {
                    return p;
                }
                else
                {
                    final GrepToken t = n.getChild(1).getChild(0).getValue();
                    final IParser<GrepToken> p1 = rmConstr(p, false);
                    return language.isSpecialMore0(t)? repeat(0, p1)
                         : language.isSpecialMore1(t)? repeat(1, p1)
                         : language.isSpecialEith(t) ? either(p1)
                         : null;
                }
            };
        }
    },
    SINGLE{
        @Override
        protected IParser<GrepToken> from()
        {
            return selectFirstEarly(SP_SEQ, PAREN, ONE);
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
                            && !language.isSpecialMore1(r.front())
                            && !language.isSpecialEith(r.front())
                            && !language.isSpecialLpar(r.front())
                            && !language.isSpecialRpar(r.front())
                            && !language.isSpecialOrFst(r.front())
                            && !language.isSpecialOrLng(r.front())
                            && !language.isSpecialAny(r.front())
                    )
                )
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> new BalancedParenSeqMatcher(language, n.getChild(1).getValue());
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
    ONE{
        @Override
        protected IParser<GrepToken> from()
        {
            return value(r -> !language.isSpecialSeq(r.front())
                            && !language.isSpecialMore0(r.front())
                            && !language.isSpecialMore1(r.front())
                            && !language.isSpecialEith(r.front())
                            && !language.isSpecialLpar(r.front())
                            && !language.isSpecialRpar(r.front())
                            && !language.isSpecialOrFst(r.front())
                            && !language.isSpecialOrLng(r.front())
                    );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> language.isSpecialAny(n.getValue())
                ? any()
                : value(n.getValue());
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
            parser = mapper(
                from(),
                /*r -> {
                    System.err.println("<"+name()
                        +" value=\""+(r.empty()?null:r.front())+"\">");
                    final int pos = r.getPosition();
                    final INode<GrepToken> n = from().parse(r);
                    System.err.println("<pos s=\""+pos+"\" e=\""+r.getPosition()
                        +"\" r=\""+(n != null)+"\" v=\""+(r.empty()?null:r.front())+"\"/>");
                    System.err.println("</" + name() + ">");
                    return n;
                },//*/
                to()
            );
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

    static IParser<GrepToken> compile(List<GrepToken> needle) throws CCGrepException
    {
        if(language == null)
        {
            throw new IllegalStateException("set language before.");
        }

        final Range<GrepToken> cRange = new Range<GrepToken>(needle);
        final INode<GrepToken> n = ROOT.parse(cRange);
        if(n == null)
        {
            if(cRange.empty())
            {
                throw new CCGrepException("Query syntax is invalid: at the end of the query.");
            }
            else
            {
                throw new CCGrepException(
                    "Query syntax is invalid: at line " +
                    + cRange.front().getLine()
                    + ", column " + cRange.front().getColumn()
                    + ", token `" + cRange.front().getText() + "`.");
            }
        }
        final IParser<GrepToken> p = n.casted();
        if(p.matches(new GrepRange(
            Collections.emptyList(),
            BlindLevel.NONE,
            Collections.emptyMap()
        )))
        {
            throw new CCGrepException("Query syntax is invalid: can match empty sequence.");
        }
        return p;
    }

    protected static IParser<GrepToken> rmConstr(IParser<GrepToken> p, boolean keepWhenSucc)
    {
        return new IParser<GrepToken>() {
            @Override
            public boolean matches(Range<GrepToken> range)
            {
                final GrepRange gr = ((GrepRange)range);
                final HashMap<String, String> saved = new HashMap<>(gr.getConstraint());
                final boolean isSucc = p.matches(range);
                if(!isSucc || !keepWhenSucc)
                {
                    gr.replaceConstraint(saved);
                }
                return isSucc;
            }
            @Override
            public INode<GrepToken> parse(Range<GrepToken> range)
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
