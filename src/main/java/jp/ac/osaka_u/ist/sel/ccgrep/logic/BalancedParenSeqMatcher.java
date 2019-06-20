package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;


public class BalancedParenSeqMatcher extends AbstractParser<GrepToken>
{
    private static final Map<Language, Inner> innerMemo = new HashMap<>();

    public BalancedParenSeqMatcher(Language language, IParser<GrepToken> terminator)
    {
        super(
            sequence(
                repeat(0, sequence(
                    discard(lookahead(not(terminator))),
                    innerMemo.computeIfAbsent(language, Inner::new)
                )),
                terminator
            )
        );
    }

    @Override
    public boolean matches(Range<GrepToken> range)
    {
        return getParser(0).matches(range);
    }

    @Override
    public IParser<GrepToken> parse(Range<GrepToken> range)
    {
        throw new UnsupportedOperationException();
    }

    private static class Inner implements IParser<GrepToken>
    {
        private IParser<GrepToken> innerMatcher;
        Inner(Language language)
        {
            final ArrayList<IParser<GrepToken>> ps = new ArrayList<>();
            language.bracketPairs.forEach(bp -> {
                final int op = bp.open;
                final int cl = bp.close;
                ps.add(
                    sequence(
                        value(r -> op == r.front().getType()),
                        repeat(0, this),
                        value(r -> cl == r.front().getType())
                    )
                );
            });
            ps.add(value(r -> !language.isCloseBracket(r.front().getType())));
            this.innerMatcher = selectFirstEarly(ps);
        }

        @Override
        public boolean matches(Range<GrepToken> range)
        {
            return innerMatcher.matches(range);
        }

        @Override
        public IParser<GrepToken> parse(Range<GrepToken> range)
        {
            throw new UnsupportedOperationException();
        }
    }
}
