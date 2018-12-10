package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class Lookahead<T> implements IParser<T>
{
    final IParser<T> parser;

    public Lookahead(IParser<T> parser)
    {
        this.parser = parser;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final int pos = range.getPosition();
        final List<T> l = parser.matches(range);
        range.setPosition(pos);
        return l == null? null: l;
    }

    @Override
    public INode parse(Range<T> range)
    {
        final int pos = range.getPosition();
        final INode n = parser.parse(range);
        range.setPosition(pos);
        return n == null? null: n;
    }
}
