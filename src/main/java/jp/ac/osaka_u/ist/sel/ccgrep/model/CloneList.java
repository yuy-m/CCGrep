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

    public static CloneList empty(String name)
    {
        return new CloneList(new GrepCode(name, 0, 0, null));
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

    public Clone get(int index)
    {
        return clones.get(index);
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

        private long startTime;
        private long endTime;
        private int stopwatchState = 0; // 0:not start, 1:started, 2:stopped

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
        public void startStopwatch()
        {
            if(stopwatchState != 0)
            {
                throw new IllegalStateException();
            }
            stopwatchState = 1;
            startTime = System.nanoTime();
        }
        public void stopStopwatch()
        {
            final long t = System.nanoTime();
            if(stopwatchState != 1)
            {
                throw new IllegalStateException();
            }
            endTime = t;
            stopwatchState = 2;
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
        public double countTimeAsSeconds()
        {
            if(stopwatchState != 2)
            {
                throw new IllegalStateException();
            }
            return (endTime - startTime) / 1.0e+9;
        }
    }
}
