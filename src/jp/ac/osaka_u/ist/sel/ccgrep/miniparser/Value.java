package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.Collections;
import java.util.List;
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
    public static <U> Value<U> any()
    {
        return (Value<U>)ANY;
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
    public INode<T> parse(Range<T> range)
    {
        final List<T> l = matches(range);
        return l == null? null: INode.leafWith(l.get(0));
    }
}
