package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Consumer;

public class CloneList
{
    public final String filename;
    public final List<Clone> clones;
    public CloneList(String filename, List<Clone> clones)
    {
        this.filename = filename;
        this.clones = clones;
    }

    public CloneList(String filename)
    {
        this(filename, Collections.emptyList());
    }

    public int size()
    {
        return clones.size();
    }

    public boolean isEmpty()
    {
        return clones.isEmpty();
    }

    public Stream<Clone> stream()
    {
        return clones.stream();
    }

    public void forEach(Consumer<Clone> action)
    {
        clones.forEach(action);
    }
}
