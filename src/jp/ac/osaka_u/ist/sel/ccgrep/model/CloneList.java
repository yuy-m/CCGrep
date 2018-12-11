package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Consumer;


public class CloneList
{
    private final GrepCode code;
    private  final List<Clone> clones;
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

    public static class Statistic
    {
        private int countAllFile = 0;
        private int countAllLine = 0;
        private int countAllToken = 0;
        private int countCloneFile = 0;
        private int countAllClone = 0;
        public Statistic()
        {}
        public Statistic(List<CloneList> clones)
        {
            clones.forEach(this::add);
        }
        public void add(CloneList cloneList)
        {
            ++countAllFile;
            countAllLine += cloneList.getCode().countLines();
            countAllToken += cloneList.getCode().countTokens();
            if(!cloneList.isEmpty())
            {
                ++countCloneFile;
                countAllClone += cloneList.size();
            }
        }
        public int countAllFile()
        {
            return countAllFile;
        }
        public int countAllLine()
        {
            return countAllLine;
        }
        public int countAllToken()
        {
            return countAllToken;
        }
        public int countCloneFile()
        {
            return countCloneFile;
        }
        public int countAllClone()
        {
            return countAllClone;
        }
    }
}
