package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.NoSuchElementException;


public class Range<T>
{
    private final List<? extends T> list; // assume immutable
    private int position;

    public Range(List<? extends T> list)
    {
        this(list, 0);
    }

    public Range(List<? extends T> list, int position)
    {
        this.list = list;
        this.position = position;
    }

    public boolean empty()
    {
        return position == list.size();
    }

    public T front()
    {
        return list.get(position);
    }

    void popFront()
    {
        if(empty())
        {
            throw new NoSuchElementException();
        }
        else
        {
            ++position;
        }
    }

    int getPosition()
    {
        return position;
    }

    void setPosition(int position)
    {
        this.position = position;
    }

    public boolean matches(T t)
    {
        return t.equals(front());
    }
}
