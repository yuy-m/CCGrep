package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.function.Predicate;


public class Value<T> extends AbstractParser<T>
{
    private final Predicate<? super Range<T>> pred;

    public Value(Predicate<? super Range<T>> pred)
    {
        super();
        this.pred = pred;
    }

    public Value(T t)
    {
        this(r -> r.matches(t));
    }

    @SuppressWarnings("rawtypes")
    private static final Value ANY = new Value<>(r -> true);

    @SuppressWarnings("unchecked")
    public static <U> Value<U> any()
    {
        return (Value<U>)ANY;
    }

    @Override
    public boolean matches(Range<T> range)
    {
        if(range.empty())
        {
            return false;
        }
        final boolean r = pred.test(range);
        if(r)
        {
            range.popFront();
        }
        return r;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        if(range.empty())
        {
            return null;
        }
        else if(pred.test(range))
        {
            final T t = range.front();
            range.popFront();
            return INode.leafWith(t);
        }
        return null;
    }
}
