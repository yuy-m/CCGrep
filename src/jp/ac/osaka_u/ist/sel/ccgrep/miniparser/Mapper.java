package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.function.Function;


public class Mapper<T, To extends INode> implements IParser<T>
{
    final IParser<T> parser;
    final Function<? super INode, ? extends To> mapper;

    public Mapper(IParser<T> parser, Function<? super INode, ? extends To> mapper)
    {
        this.parser = parser;
        this.mapper = mapper;
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        return parser.matches(range);
    }

    @Override
    public To parse(Range<T> range)
    {
        final INode n = parser.parse(range);
        return n == null
            ? null
            : mapper.apply(n);
    }
}
