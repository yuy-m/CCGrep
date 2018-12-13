package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;


public class Lookahead<T> extends AbstractParser<T>
{
    public Lookahead(IParser<T> parser)
    {
        super(parser);
    }

    @Override
    public boolean matches(Range<T> range)
    {
        final int pos = range.getPosition();
        final boolean r = getParser(0).matches(range);
        range.setPosition(pos);
        return r;
    }

    @Override
    public INode<T> parse(Range<T> range)
    {
        final int pos = range.getPosition();
        final INode<T> n = getParser(0).parse(range);
        range.setPosition(pos);
        return n == null? null: n;
    }
}
