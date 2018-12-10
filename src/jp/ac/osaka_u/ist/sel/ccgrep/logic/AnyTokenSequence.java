package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.*;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;


public class AnyTokenSequence implements IParser<GrepToken>
{
    private final IParser<GrepToken> parser;
    private static final Map<Language, Inner> innerMemo = new HashMap<>();

    public AnyTokenSequence(Language language, IParser<GrepToken> terminator)
    {
        final Inner inner = innerMemo.computeIfAbsent(language, Inner::new);
        this.parser = new Repeat<>(
            new Sequence<>(Arrays.asList(
                new Not<>(new Discard<>(new Lookahead<>(terminator))),
                inner
            ))
        );
    }

    @Override
    public List<GrepToken> matches(Range<GrepToken> range)
    {
        return parser.matches(range);
    }

    @Override
    public INode parse(Range<GrepToken> range)
    {
        throw new UnsupportedOperationException();
    }

    private static class Inner implements IParser<GrepToken>
    {
        private IParser<GrepToken> innerParser;
        Inner(Language language)
        {
            final List<IParser<GrepToken>> ps = new ArrayList<>();
            language.bracketPairs.forEach(bp -> {
                final int op = bp.open;
                final int cl = bp.close;
                ps.add(
                    new Sequence<>(Arrays.asList(
                        new One<>(t -> op == t.front().getType()),
                        new Repeat<>(this),
                        new One<>(t -> cl == t.front().getType())
                    ))
                );
            });
            ps.add(new One<>(t -> !language.isCloseBracket(t.front().getType())));
            this.innerParser = new Select<>(ps);
        }

        @Override
        public List<GrepToken> matches(Range<GrepToken> range)
        {
            return innerParser.matches(range);
        }

        @Override
        public INode parse(Range<GrepToken> range)
        {
            throw new UnsupportedOperationException();
        }
    }
}
