package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public interface IParser<T> extends INode<T>
{
    /// To get matched list, use Range#getMatchedList()
    default boolean matches(Range<T> range)
    {
        return parse(range) != null;
    }
    INode<T> parse(Range<T> range);

    default T getValue()
    {
        throw new UnsupportedOperationException();
    }
    default List<INode<T>> getChildren()
    {
        throw new UnsupportedOperationException();
    }
}
