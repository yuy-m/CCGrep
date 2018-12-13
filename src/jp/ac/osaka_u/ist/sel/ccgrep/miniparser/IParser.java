package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;


public interface IParser<T> extends INode<T>
{
    /// To get matched list, use Range#getMatchedList()
    default boolean matches(Range<T> range)
    {
        return parse(range) != null;
    }
    INode<T> parse(Range<T> range);
}
