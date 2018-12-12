package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class Lookahead<T> extends AbstractParser<T>
{
    public Lookahead(IParser<T> parser)
    {
        super(parser);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final int pos = range.getPosition();
        final List<T> l = getParser(0).matches(range);
        range.setPosition(pos);
        return l == null? null: l;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        final INode<T> n = getParser(0).parse(range);
        range.setPosition(pos);
        return n == null? null: n;
    }
}
