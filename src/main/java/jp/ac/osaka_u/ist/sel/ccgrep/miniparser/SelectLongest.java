package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class SelectLongest<T> extends AbstractParser<T>
{
    public SelectLongest(List<IParser<T>> parsers)
    {
        super(parsers);
    }

    @Override
    public boolean matches(Range<T> range)
    {
        final int pos = range.getPosition();
        int resultPos = -1;
        int lastLength = 0;
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            final boolean b = am.matches(range);
            final int newPos = range.getPosition();
            if(b && lastLength < newPos - pos)
            {
                resultPos = newPos;
            }
        }
        if(resultPos >= 0)
        {
            range.setPosition(resultPos);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        INode<T> result = null;
        int lastLength = -1;
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            final INode<T> n = am.parse(range);
            if(n != null && lastLength < range.getPosition() - pos)
            {
                result = n;
            }
        }
        return result;
    }
}
