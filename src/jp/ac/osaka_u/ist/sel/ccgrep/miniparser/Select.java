package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public class Select<T> extends AbstractParser<T>
{
    private final boolean isEarlyStopEnabled;
    public Select(List<IParser<T>> parsers, boolean isEarlyStopEnabled)
    {
        super(parsers);
        this.isEarlyStopEnabled = isEarlyStopEnabled;
    }

    public Select(List<IParser<T>> parsers)
    {
        this(parsers, false);
    }


    @Override
    public boolean matches(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            if(am.matches(range))
            {
                return true;
            }
            if(isEarlyStopEnabled && pos < range.getPosition())
            {
                return false;
            }
        }
        return false;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        for(final IParser<T> am: getParsers())
        {
            range.setPosition(pos);
            final INode<T> n = am.parse(range);
            if(n != null)
            {
                return n;
            }
            if(isEarlyStopEnabled && pos < range.getPosition())
            {
                return null;
            }
        }
        return null;
    }
}
