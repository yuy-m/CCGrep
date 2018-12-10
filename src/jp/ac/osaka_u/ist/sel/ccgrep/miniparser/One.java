package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


public class One<T> implements IParser<T>
{
    private final Predicate<Range<T>> pred;
    public One(Predicate<Range<T>> pred)
    {
        this.pred = pred;
    }

    public One(T t)
    {
        this(r -> r.matches(t));
    }

    public static <U> One<U> any()
    {
        return new One<U>(r -> true);
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        if(range.empty())
        {
            return null;
        }
        else if(pred.test(range))
        {
            final T t = range.front();
            range.popFront();
            return Collections.singletonList(t);
        }
        else
        {
            return null;
        }
    }

    @Override
    public INode parse(Range<T> range)
    {
        final List<T> l = matches(range);
        return l == null? null: INode.of(l.get(0));
    }
}
