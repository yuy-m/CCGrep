package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.List;

public interface INode<T>
{
    T getValue();
    List<INode<T>> getChildren();
    default int countChildren()
    {
        return getChildren().size();
    }
    default INode<T> getChild(int index)
    {
        return getChildren().get(0);
    }
    default <C extends INode<T>> C getCastedChild(int index)
    {
        return getChild(index).casted();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <C extends INode<T>> List<C> getCastedChildren()
    {
        return (List<C>)(List)getChildren();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <C extends INode<T>> C casted()
    {
        return (C)(INode)this;
    }

    @SuppressWarnings("unchecked")
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

        @Override
        public int countChildren()
        {
            return 0;
        }

        @Override
        public U getValue()
        {
            throw new NoSuchElementException();
        }

        @Override
        public INode<U> getChild(int index)
        {
            throw new NoSuchElementException();
        }

        @Override
        public List<INode<U>> getChildren()
        {
            return Collections.emptyList();
        }
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

        protected InnerNode(INode<U> child)
        {
            this(Collections.singletonList(child));
        }

        protected InnerNode()
        {
            this(Collections.emptyList());
        }

        @Override
        public U getValue()
        {
            throw new NoSuchElementException();
        }

        @Override
        public int countChildren()
        {
            return children.size();
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
