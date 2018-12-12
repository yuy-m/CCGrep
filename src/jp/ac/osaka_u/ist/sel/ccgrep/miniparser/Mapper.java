package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.function.Function;


public class Mapper<T> extends AbstractParser<T>
{
    //final Function<? super INode<T>, ? extends R> mapper;

    public Mapper(IParser<T> parser, Function<? super INode<T>, ? extends INode<T>> mapper)
    {
        super(new IParser<T>(){
            @Override
            public INode<T> parse(Range<T> range)
            {
                final INode<T> n = parser.parse(range);
                return n == null
                    ? null
                    : mapper.apply(n);
            }
            @Override
            public List<T> matches(Range<T> range)
            {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Override
    public List<T> matches(Range<T> range)
    {
        return getParser(0).matches(range);
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        return getParser(0).parse(range);
        /*return n == null
            ? null
            : mapper.apply(n);*/
    }
}
