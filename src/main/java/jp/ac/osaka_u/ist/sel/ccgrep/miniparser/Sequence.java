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
    public boolean matches(Range<T> range)
    {
        return getParsers().stream()
            .allMatch(p -> p.matches(range));
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final ArrayList<INode<T>> list = new ArrayList<>();
        for(final IParser<T> p: getParsers())
        {
            final INode<T> n = p.parse(range);
            if(n == null)
            {
                return null;
            }
            list.add(n);
        }
        return INode.nodeOf(list);
    }
}
