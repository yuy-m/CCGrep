package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.ArrayList;


public class Sequence<T> implements IParser<T>
{
    final List<IParser<T>> parsers;

    public Sequence(List<IParser<T>> parsers)
    {
        this.parsers = parsers;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final int pos = range.getPosition();
        final List<T> list = new ArrayList<>();
        for(final IParser<T> p: parsers)
        {
            final List<T> l = p.matches(range);
            if(l == null)
            {
                range.setPosition(pos);
                return null;
            }
            list.addAll(l);
        }
        return list;
    }

    @Override
    public INode parse(Range<T> range)
    {
        final int pos = range.getPosition();
        final List<INode> list = new ArrayList<>();
        for(final IParser<T> p: parsers)
        {
            final INode n = p.parse(range);
            if(n == null)
            {
                range.setPosition(pos);
                return null;
            }
            list.add(n);
        }
        return new INode.SequenceNode(list);
    }
}
