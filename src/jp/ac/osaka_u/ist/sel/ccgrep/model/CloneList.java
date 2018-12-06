package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Consumer;


public class CloneList
{
    public final GrepCode code;
    public final List<Clone> clones;
    public CloneList(GrepCode code, List<Clone> clones)
    {
        this.code = code;
        this.clones = clones;
    }

    public CloneList(GrepCode code)
    {
        this(code, Collections.emptyList());
    }

    public GrepCode getCode()
    {
        return code;
    }

    public String getFileName()
    {
        return code.getFileName();
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
