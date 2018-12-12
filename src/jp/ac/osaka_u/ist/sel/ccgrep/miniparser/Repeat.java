package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.ArrayList;
import java.util.List;


public class Repeat<T> extends AbstractParser<T>
{
    private final int min;
    private final int max;

    public Repeat(int min, int max, IParser<T> parser)
    {
        super(parser);
        if(min < 0 || max == 0 || (max >= 0 && min > max))
        {
            throw new IllegalArgumentException();
        }
        this.min = min;
        this.max = max;
    }

    public Repeat(int min, IParser<T> parser)
    {
        this(min, -1, parser);
    }

    public Repeat(IParser<T> parser)
    {
        this(0, parser);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        final List<T> list = new ArrayList<>();
        int count = 0;
        while(true)
        {
            final int pos = range.getPosition();
            final List<T> l = getParser(0).matches(range);
            if(l == null)
            {
                if(count < min)
                {
                    return null;
                }
                else
                {
                    range.setPosition(pos);
                    return list;
                }
            }
            list.addAll(l);
            ++count;
            if(count >= min && (max > 0 && count == max))
            {
                return list;
            }
        }
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final List<INode<T>> list = new ArrayList<>();
        int count = 0;
        while(true)
        {
            final int pos = range.getPosition();
            final INode<T> n = getParser(0).parse(range);
            if(n == null)
            {
                if(count < min)
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
