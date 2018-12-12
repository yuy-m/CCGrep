package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.Collections;
import java.util.List;


public abstract class AbstractParser<T>
    extends INode.InnerNode<T>
    implements IParser<T>
{
    public AbstractParser(List<IParser<T>> parsers)
    {
        super((List<INode<T>>)(List)parsers);
    }
    public AbstractParser(IParser<T> parser)
    {
        this(Collections.singletonList(parser));
    }
    public AbstractParser()
    {
        super(Collections.emptyList());
    }

    public IParser<T> getParser(int index)
    {
        return getCastedChild(index);
    }

    public List<IParser<T>> getParsers()
    {
        return getCastedChildren();
    }
}
