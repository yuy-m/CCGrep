package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public final class Parsers
{
    private Parsers()
    {}

    public static <T> Discard<T> discard(IParser<T> parser)
    {
        return new Discard<T>(parser);
    }
    public static <T> Lookahead<T> lookahead(IParser<T> parser)
    {
        return new Lookahead<T>(parser);
    }
    public static <T> Mapper<T> mapper(
        IParser<T> parser, Function<? super INode<T>, ? extends INode<T>> map)
    {
        return new Mapper<T>(parser, map);
    }
    public static <T> Not<T> not(IParser<T> parser)
    {
        return new Not<T>(parser);
    }
    public static <T> Repeat<T> repeat(IParser<T> parser)
    {
        return new Repeat<T>(parser);
    }
    public static <T> Select<T> selectEarly(List<IParser<T>> parsers)
    {
        return new Select<T>(parsers, true);
    }
    public static <T> Select<T> select(List<IParser<T>> parsers)
    {
        return new Select<T>(parsers, false);
    }
    @SafeVarargs
    public static <T> Select<T> selectEarly(IParser<T>... parsers)
    {
        return selectEarly(Arrays.<IParser<T>>asList(parsers));
    }
    @SafeVarargs
    public static <T> Select<T> select(IParser<T>... parsers)
    {
        return select(Arrays.<IParser<T>>asList(parsers));
    }
    public static <T> Sequence<T> sequence(List<IParser<T>> parsers)
    {
        return new Sequence<T>(parsers);
    }
    @SafeVarargs
    public static <T> Sequence<T> sequence(IParser<T>... parsers)
    {
        return sequence(Arrays.<IParser<T>>asList(parsers));
    }
    public static <T> Value<T> testValue(Predicate<? super Range<T>> pred)
    {
        return new Value<T>(pred, false);
    }
    public static <T> Value<T> value(Predicate<? super Range<T>> pred)
    {
        return new Value<T>(pred, true);
    }
    public static <T> Value<T> testValue(T value)
    {
        return new Value<T>(value, false);
    }
    public static <T> Value<T> value(T value)
    {
        return new Value<T>(value, true);
    }
}
