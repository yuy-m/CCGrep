package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;


public class Discard<T> extends AbstractParser<T>
{
    public Discard(IParser<T> parser)
    {
        super(parser);
    }

    @Override
    public boolean matches(Range<T> range)
    {
        return getParser(0).matches(range);
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final INode<T> n = getParser(0).parse(range);
        return n == null? null: INode.leaf();
    }
}
