package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.ArrayList;


public class Sequence<T> extends AbstractParser<T>
{
    public Sequence(List<IParser<T>> parsers)
    {
        super(parsers);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final List<T> list = new ArrayList<>();
        for(final IParser<T> p: getParsers())
        {
            final List<T> l = p.matches(range);
            if(l == null)
            {
                return null;
            }
            list.addAll(l);
        }
        return list;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        final List<INode<T>> list = new ArrayList<>();
        for(final IParser<T> p: getParsers())
        {
            final INode<T> n = p.parse(range);
            if(n == null)
            {
                range.setPosition(pos);
                return null;
            }
            list.add(n);
        }
        return INode.nodeOf(list);
    }
}
