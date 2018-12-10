package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.Collections;


public class Not<T> implements IParser<T>
{
    final IParser<T> parser;

    public Not(IParser<T> parser)
    {
        this.parser = parser;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final List<T> l = parser.matches(range);
        return l != null? null: Collections.emptyList();
    }

    @Override
    public INode parse(Range<T> range)
    {
        final INode n = parser.parse(range);
        return n != null? null: INode.empty();
    }
}
