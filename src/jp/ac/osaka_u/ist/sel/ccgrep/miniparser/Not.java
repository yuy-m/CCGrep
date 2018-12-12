package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.Collections;


public class Not<T> extends AbstractParser<T>
{
    public Not(IParser<T> parser)
    {
        super(parser);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final List<T> l = getParser(0).matches(range);
        return l != null? null: Collections.emptyList();
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final INode<T> n = getParser(0).parse(range);
        return n != null? null: INode.leaf();
    }
}
