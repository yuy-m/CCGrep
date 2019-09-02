package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import jp.ac.osaka_u.ist.sel.ccgrep.model.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.IParser;
import jp.ac.osaka_u.ist.sel.ccgrep.CCGrepException;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.INode;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Range;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;


class RegexDetectCompiler
{
    /**
     * ROOT     -> OR_LONG !.
     * OR_LONG  -> OR_FIRST ('$|' OR_FIRST)*
     * OR_FIRST -> SEQ ('$/' SEQ)*
     * SEQ      -> ANYSEQ+
     * ANYSEQ   -> ('$$' / '$#')? SUFFIX
     * SUFFIX   -> PREFIX ('$*' / '$+' / '$?')?
     * PREFIX   -> ('$=' / '$!')? SINGLE
     * SINGLE   -> PAREN / ANY / SPID / ONE
     * PAREN    -> '$(' OR_LONG '$)'
     * ANY      -> '$.'
     * ONE      -> .
     */
    private final Rule ROOT = new Rule() {
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
    };
    private final Rule OR_LONG = new Rule() {
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
    };
    private final Rule OR_FIRST = new Rule() {
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
    };
    private final Rule SEQ = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return repeatFullMatch(1, ANYSEQ);
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> n.countChildren() == 1
                ? n.getCastedChild(0)
                : sequence(n.getCastedChildren());
        }
    };
    private final Rule ANYSEQ = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                either(
                    value(r -> language.isSpecialSeq(r.front())
                            || language.isSpecialAnySeq(r.front())
                    )
                ),
                SUFFIX
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final IParser<GrepToken> p = n.getCastedChild(1);
                if(n.getChild(0).countChildren() == 0)
                {
                    return p;
                }
                else
                {
                    final GrepToken t = n.getChild(0).getChild(0).getValue();
                    return language.isSpecialSeq(t) ? new BalancedParenSeqMatcher(language, p)
                         : language.isSpecialAnySeq(t) ? sequence(
                                repeat(0, sequence(
                                    discard(lookahead(not(p))),
                                    any()
                                )),
                                p
                            )
                         : null;
                }
            };
        }
    };
    private final Rule SUFFIX = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                PREFIX,
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
    };
    private final Rule PREFIX = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                either(
                    value(r -> language.isSpecialLookaheadPos(r.front())
                            || language.isSpecialLookaheadNeg(r.front())
                    )
                ),
                SINGLE
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final IParser<GrepToken> p = n.getCastedChild(1);
                if(n.getChild(0).countChildren() == 0)
                {
                    return p;
                }
                else
                {
                    final GrepToken t = n.getChild(0).getChild(0).getValue();
                    final IParser<GrepToken> p1 = rmConstr(p, false);
                    return language.isSpecialLookaheadPos(t)? lookahead(p1)
                         : language.isSpecialLookaheadNeg(t)? lookahead(not(p1))
                         : null;
                }
            };
        }
    };
    private final Rule SINGLE = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return selectFirstEarly(PAREN, ANY, SPID, ONE);
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return Function.identity();
        }
    };
    private final Rule PAREN = new Rule() {
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
    };
    private final Rule ANY = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return value(r -> language.isSpecialAny(r.front()));
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> any();
        }
    };
    private final Rule SPID = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return value(r -> language.isSpecialId(r.front()));
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> value(n.getValue());
        }
    };
    private final Rule ONE = new Rule() {
        @Override
        protected IParser<GrepToken> from()
        {
            return value(r -> !language.isSomeSpecial(r.front()));
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> value(n.getValue());
        }
    };

    private abstract class Rule implements IParser<GrepToken>
    {
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
    }

    private final Language language;

    RegexDetectCompiler(Language language)
    {
        this.language = language;
    }

    IParser<GrepToken> compile(List<GrepToken> query) throws CCGrepException
    {
        final Range<GrepToken> cRange = new Range<GrepToken>(query);
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
