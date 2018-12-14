package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.function.Predicate;


public class Value<T> extends AbstractParser<T>
{
    private final Predicate<? super Range<T>> pred;
    private final boolean consumeIfFailure;

    public Value(Predicate<? super Range<T>> pred, boolean consumeIfFailure)
    {
        super();
        this.pred = pred;
        this.consumeIfFailure = consumeIfFailure;
    }

    public Value(Predicate<? super Range<T>> pred)
    {
        this(pred, true);
    }

    public Value(T t, boolean consumeIfFailure)
    {
        this(r -> r.matches(t), consumeIfFailure);
    }

    public Value(T t)
    {
        this(t, true);
    }

    @SuppressWarnings("rawtypes")
    private static final Value ANY = new Value<>(r -> true, true);
    @SuppressWarnings("rawtypes")
    private static final Value TEST_ANY = new Value<>(r -> true, false);

    @SuppressWarnings("unchecked")
    public static <U> Value<U> any()
    {
        return (Value<U>)ANY;
    }
    @SuppressWarnings("unchecked")
    public static <U> Value<U> testAny()
    {
        return (Value<U>)TEST_ANY;
    }

    @Override
    public boolean matches(Range<T> range)
    {
        if(range.empty())
        {
            return false;
        }
        final boolean r = pred.test(range);
        if(r || consumeIfFailure)
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
        else if(consumeIfFailure)
        {
            range.popFront();
        }
        return null;
    }
}
