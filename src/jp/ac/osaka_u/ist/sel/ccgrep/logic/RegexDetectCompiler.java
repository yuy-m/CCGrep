package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;


enum RegexDetectCompiler implements IParser<GrepToken>
{
    /**
     * ROOT   -> OR !.
     * OR     -> SEQ ('$|' SEQ)*
     * SEQ    -> MORE0+
     * MORE0  -> SINGLE '$*'?
     * SINGLE -> SP_SEQ / PAREN / NORMAL
     * SP_SEQ -> '$$' !( '$$' / '$*' / '$(' / '$)' / '$|') &.
     * PAREN  -> '$(' OR '$)'
     * NORMAL -> !( '$|' / '$)') .
     */
    ROOT{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                OR,
                not(Value.any())
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> n.getChild(0);
        }
    },
    OR{
        @Override
        protected IParser<GrepToken> from()
        {
            return sequence(
                SEQ,
                repeat(sequence(
                    value(r -> language.isSpecialOr(r.front())),
                    SEQ
                ))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final ArrayList<IParser<GrepToken>> l = new ArrayList<>();
                l.add(n.getCastedChild(0));
                n.getChild(1).getChildren().stream()
                    .forEach(n1 -> l.add(n1.getCastedChild(1)));
                return select(l);
            };
        }
    },
    SEQ{
        @Override
        protected IParser<GrepToken> from()
        {
            return repeat(1, MORE0);
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
                repeat(0, 1, value(r -> language.isSpecialMore0(r.front())))
            );
        }
        @Override
        protected Function<INode<GrepToken>, INode<GrepToken>> to()
        {
            return n -> {
                final IParser<GrepToken> p = n.getCastedChild(0);
                return n.getChild(1).countChildren() == 0
                    ? p
                    : repeat(p);
            };
        }
    },
    SINGLE{
        @Override
        protected IParser<GrepToken> from()
        {
            return select(SP_SEQ, PAREN, NORMAL);
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
                            && !language.isSpecialOr(r.front())
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
                OR,
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
                            && !language.isSpecialOr(r.front())
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
            parser = mapper(
                from()/*r -> {
                    System.err.println("<"+name()+" name=\""
                        +(r.empty()?null:r.front())+"\">");
                    INode<GrepToken> z = from().parse(r);
                    System.err.println("<"+(z!=null)+"/>");
                    System.err.println("</"+name()+">");
                    return z;
                }*/,
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

    static IParser<GrepToken> compile(List<GrepToken> needle)
    {
        if(language == null)
        {
            throw new IllegalStateException("set language before.");
        }

        final IParser<GrepToken> p = ROOT.parse(new Range<GrepToken>(needle)).casted();
        if(p.matches(new Range<>(Collections.emptyList())))
        {
            return null;
        }
        return p;
    }
}
