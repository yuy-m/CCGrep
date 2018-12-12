package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class Select<T> extends AbstractParser<T>
{
    public Select(List<IParser<T>> parsers)
    {
        super(parsers);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            final List<T> l = am.matches(range);
            final int newPos = range.getPosition();
            if(l != null)
            {
                return l;
            }
            if(pos < newPos)
            {
                return null;
            }
        }
        return null;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            final INode<T> n = am.parse(range);
            final int newPos = range.getPosition();
            if(n != null)
            {
                return n;
            }
            if(pos < newPos)
            {
                return null;
            }
        }
        return null;
    }
}
