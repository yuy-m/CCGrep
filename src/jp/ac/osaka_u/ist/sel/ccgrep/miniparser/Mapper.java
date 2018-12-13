package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.function.Function;


public class Mapper<T> extends AbstractParser<T>
{
    public Mapper(IParser<T> parser, Function<? super INode<T>, ? extends INode<T>> mapper)
    {
        super(range -> {
            final INode<T> n = parser.parse(range);
            return n == null
                ? null
                : mapper.apply(n);
        });
    }

    @Override
    public boolean matches(Range<T> range)
    {
        return getParser(0).matches(range);
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        return getParser(0).parse(range);
    }
}
