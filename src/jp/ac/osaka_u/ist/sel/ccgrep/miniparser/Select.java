package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class Select<T> implements IParser<T>
{
    final List<IParser<T>> parsers;

    public Select(List<IParser<T>> parsers)
    {
        this.parsers = parsers;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: parsers)
        {
            final List<T> l = am.matches(range);
            final int newPos = range.getPosition();
            if(l != null)
            {
                return l;
            }
            range.setPosition(pos);
            if(pos < newPos)
            {
                return null;
            }
        }
        return null;
    }

    @Override
    public INode parse(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: parsers)
        {
            final INode n = am.parse(range);
            final int newPos = range.getPosition();
            if(n != null)
            {
                return n;
            }
            range.setPosition(pos);
            if(pos < newPos)
            {
                return null;
            }
        }
        return null;
    }
}
