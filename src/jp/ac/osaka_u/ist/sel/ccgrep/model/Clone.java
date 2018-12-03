package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.List;


public class Clone
{
    public final GrepToken start;
    public final GrepToken end;
    public Clone(GrepToken start, GrepToken end)
    {
        this.start = start;
        this.end = end;
    }

    public List<String> getCodeByLine()
    {
        return getCodeByLine(end.getLine() - start.getLine() + 1);
    }

    public List<String> getCodeByLine(int countLines)
    {
        return start.getCodeByLine(countLines);
    }

    public String getFileName()
    {
        return start.getFileName();
    }

    public int getStartLine()
    {
        return start.getLine();
    }

    public int getEndLine()
    {
        return end.getLine();
    }

    public int getStartColumn()
    {
        return start.getCharPositionInLine();
    }

    public int getEndColumn()
    {
        return end.getCharPositionInLine() + end.getText().length();
    }

    @Override
    public String toString()
    {
        return getFileName() + ":" + start + "-" + end;
    }
}