package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Parsers.*;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;


public class AnyTokenSequence
    extends AbstractParser<GrepToken>
{
    private static final Map<Language, Inner> innerMemo = new HashMap<>();

    public AnyTokenSequence(Language language, IParser<GrepToken> terminator)
    {
        super(
            repeat(
                sequence(
                    discard(lookahead(not(terminator))),
                    innerMemo.computeIfAbsent(language, Inner::new)
                )
            )
        );
    }

    @Override
    public List<GrepToken> matches(Range<GrepToken> range)
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
            final List<IParser<GrepToken>> ps = new ArrayList<>();
            language.bracketPairs.forEach(bp -> {
                final int op = bp.open;
                final int cl = bp.close;
                ps.add(
                    sequence(
                        value(t -> op == t.front().getType()),
                        repeat(this),
                        value(t -> cl == t.front().getType())
                    )
                );
            });
            ps.add(value(t -> !language.isCloseBracket(t.front().getType())));
            this.innerMatcher = select(ps);
        }

        @Override
        public List<GrepToken> matches(Range<GrepToken> range)
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
