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
    public static <T> Repeat<T> repeat(int min, int max, IParser<T> parser)
    {
        return new Repeat<T>(min, max, parser, false);
    }
    public static <T> Repeat<T> repeat(int min, IParser<T> parser)
    {
        return new Repeat<T>(min, parser, false);
    }
    public static <T> Repeat<T> repeatFullMatch(int min, int max, IParser<T> parser)
    {
        return new Repeat<T>(min, max, parser, true);
    }
    public static <T> Repeat<T> repeatFullMatch(int min, IParser<T> parser)
    {
        return new Repeat<T>(min, parser, true);
    }
    public static <T> Repeat<T> either(IParser<T> parser)
    {
        return repeat(0, 1, parser);
    }
    public static <T> IParser<T> selectLongest(List<IParser<T>> parsers)
    {
        return parsers.size() == 1
            ? parsers.get(0)
            : new SelectLongest<T>(parsers);
    }
    @SafeVarargs
    public static <T> IParser<T> selectLongest(IParser<T>... parsers)
    {
        return selectLongest(Arrays.<IParser<T>>asList(parsers));
    }
    private static <T> IParser<T> selectFirstImpl(List<IParser<T>> parsers, boolean early)
    {
        return parsers.size() == 1
            ? parsers.get(0)
            : new SelectFirst<T>(parsers, early);
    }
    public static <T> IParser<T> selectFirstEarly(List<IParser<T>> parsers)
    {
        return selectFirstImpl(parsers, true);
    }
    public static <T> IParser<T> selectFirst(List<IParser<T>> parsers)
    {
        return selectFirstImpl(parsers, false);
    }
    @SafeVarargs
    public static <T> IParser<T> selectFirstEarly(IParser<T>... parsers)
    {
        return selectFirstEarly(Arrays.<IParser<T>>asList(parsers));
    }
    @SafeVarargs
    public static <T> IParser<T> selectFirst(IParser<T>... parsers)
    {
        return selectFirst(Arrays.<IParser<T>>asList(parsers));
    }
    public static <T> IParser<T> sequence(List<IParser<T>> parsers)
    {
        return new Sequence<T>(parsers);
    }
    @SafeVarargs
    public static <T> IParser<T> sequence(IParser<T>... parsers)
    {
        return sequence(Arrays.<IParser<T>>asList(parsers));
    }
    public static <T> Value<T> value(Predicate<? super Range<T>> pred)
    {
        return new Value<T>(pred);
    }
    public static <T> Value<T> value(T value)
    {
        return new Value<T>(value);
    }
    public static <T> Value<T> any()
    {
        return Value.any();
    }
}
