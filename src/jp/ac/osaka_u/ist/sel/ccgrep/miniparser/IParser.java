package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;


public interface IParser<T> extends INode
{
    List<T> matches(Range<T> range);
    INode parse(Range<T> range);
}
