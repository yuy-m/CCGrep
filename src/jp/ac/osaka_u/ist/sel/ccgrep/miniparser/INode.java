package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.function.Function;

public interface INode
{
    public static EmptyNode empty()
    {
        return EmptyNode.instance;
    }

    public static <T> OneNode<T> of(T one)
    {
        return new OneNode(one);
    }

    public static <T extends INode> SequenceNode<T> of(List<T> nodes)
    {
        return new SequenceNode(nodes);
    }

    public class EmptyNode implements INode
    {
        private static final EmptyNode instance = new EmptyNode();

        private EmptyNode()
        {}
    }

    default <T extends INode> SequenceNode<T> asSeq()
    {
        return (SequenceNode<T>)this;
    }

    default <T> OneNode<T> asOne()
    {
        return (OneNode<T>)this;
    }

    public class OneNode<T> implements INode
    {
        private final T value;

        OneNode(T value)
        {
            this.value = value;
        }

        public T get()
        {
            return value;
        }
    }

    public class SequenceNode<T extends INode> implements INode
    {
        private final List<T> list;

        SequenceNode(List<T> list)
        {
            this.list = list;
        }

        public List<T> getList()
        {
            return list;
        }

        public T get(int index)
        {
            return list.get(index);
        }

        public <U extends INode> SequenceNode<U> getAsSeq(int index)
        {
            return get(index).<U>asSeq();
        }

        public <U> OneNode<U> getAsOne(int index)
        {
            return get(index).<U>asOne();
        }

        public int size()
        {
            return list.size();
        }
    }
}
