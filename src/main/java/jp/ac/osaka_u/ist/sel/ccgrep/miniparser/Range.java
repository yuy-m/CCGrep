package jp.ac.osaka_u.ist.sel.ccgrep.miniparser;

import java.util.List;
import java.util.NoSuchElementException;


public class Range<T>
{
    private final List<T> list; // assume immutable
    private int position;
    private int startPosition;

    public Range(List<T> list)
    {
        this(list, 0);
    }

    public Range(List<T> list, int startPosition)
    {
        this.list = list;
        this.position = startPosition;
        this.startPosition = startPosition;
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

    public int getPosition()
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

    public List<T> getMatchedList()
    {
        return list.subList(startPosition, getPosition());
    }

    public T getMatchedFirst()
    {
        return list.get(startPosition);
    }

    public T getMatchedLast()
    {
        return list.get(getPosition() - 1);
    }
}
