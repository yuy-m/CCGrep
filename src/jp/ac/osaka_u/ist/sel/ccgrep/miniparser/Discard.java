package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.Collections;
import java.util.List;


public class Discard<T> implements IParser<T>
{
    final IParser<T> parser;

    public Discard(IParser<T> parser)
    {
        this.parser = parser;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final List<T> l = parser.matches(range);
        return l == null? null: Collections.emptyList();
    }

    @Override
    public INode parse(Range<T> range)
    {
        final INode n = parser.parse(range);
        return n == null? null: INode.empty();
    }
}
