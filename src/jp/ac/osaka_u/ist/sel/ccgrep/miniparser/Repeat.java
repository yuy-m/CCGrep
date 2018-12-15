package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.ArrayList;


public class Repeat<T> extends AbstractParser<T>
{
    private final int min;
    private final int max;
    private final boolean isFullMatchOnlyEnabled;

    public Repeat(int min, int max, IParser<T> parser, boolean isFullMatchOnlyEnabled)
    {
        super(parser);
        if(min < 0 || max == 0 || (max >= 0 && min > max))
        {
            throw new IllegalArgumentException();
        }
        this.min = min;
        this.max = max;
        this.isFullMatchOnlyEnabled = isFullMatchOnlyEnabled;
    }

    public Repeat(int min, IParser<T> parser, boolean isFullMatchOnlyEnabled)
    {
        this(min, -1, parser, isFullMatchOnlyEnabled);
    }

    @Override
    public boolean matches(Range<T> range)
    {
        int count = 0;
        while(true)
        {
            final int pos = range.getPosition();
            if(!getParser(0).matches(range))
            {
                if(count < min
                    || (isFullMatchOnlyEnabled && pos < range.getPosition()))
                {
                    return false;
                }
                else
                {
                    range.setPosition(pos);
                    return true;
                }
            }
            ++count;
            if(count >= min && (max > 0 && count == max))
            {
                return true;
            }
        }
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final ArrayList<INode<T>> list = new ArrayList<>();
        int count = 0;
        while(true)
        {
            final int pos = range.getPosition();
            final INode<T> n = getParser(0).parse(range);
            if(n == null)
            {
                if(count < min
                    || (isFullMatchOnlyEnabled && pos < range.getPosition()))
                {
                    return null;
                }
                else
                {
                    range.setPosition(pos);
                    return INode.nodeOf(list);
                }
            }
            list.add(n);
            ++count;
            if(count >= min && (max > 0 && count == max))
            {
                return INode.nodeOf(list);
            }
        }
    }
}
