package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface INode<T>
{
    default T getValue()
    {
        throw new NoSuchElementException();
    }
    default INode<T> getChild(int index)
    {
        throw new NoSuchElementException();
    }
    default List<INode<T>> getChildren()
    {
        throw new NoSuchElementException();
    }
    default <C extends INode<T>> C getCastedChild(int index)
    {
        return getChild(index).casted();
    }
    default <C extends INode<T>> List<C> getCastedChildren()
    {
        return (List<C>)(List)getChildren();
    }
    default <C extends INode<T>> C casted()
    {
        return (C)(INode)this;
    }

    public static <U> LeafNode<U> leaf()
    {
        return (LeafNode<U>)LeafNode.INSTANCE;
    }

    public static <U> ValueNode<U> leafWith(U one)
    {
        return new ValueNode<>(one);
    }

    public static <U> INode<U> nodeOf(List<INode<U>> children)
    {
        return children.isEmpty()? INode.leaf()
            : new InnerNode<>(children);
    }

    public class LeafNode<U> implements INode<U>
    {
        @SuppressWarnings("rawtypes")
        private static final LeafNode INSTANCE = new LeafNode<>();

        protected LeafNode()
        {}
    }

    public class ValueNode<U> extends LeafNode<U>
    {
        private final U value;

        protected ValueNode(U value)
        {
            this.value = value;
        }

        @Override
        public U getValue()
        {
            return value;
        }
    }

    public class InnerNode<U> implements INode<U>
    {
        private final List<INode<U>> children;

        protected InnerNode(List<INode<U>> children)
        {
            this.children = children;
        }

        @Override
        public INode<U> getChild(int index)
        {
            return getChildren().get(index);
        }

        @Override
        public List<INode<U>> getChildren()
        {
            return children;
        }
    }
}
