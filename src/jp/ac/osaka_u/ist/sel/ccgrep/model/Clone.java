package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.List;


public class Clone
{
    private final GrepCode code;
    public final GrepToken start;
    public final GrepToken end;
    public Clone(GrepCode code, GrepToken start, GrepToken end)
    {
        this.code = code;
        this.start = start;
        this.end = end;
    }

    public List<String> getCodeByLine()
    {
        return code.getCodeByLine(start.getLine(), end.getLine());
    }

    public List<String> getCodeByLine(int countLines)
    {
        return code.getCodeByLine(start.getLine(), start.getLine() + countLines - 1);
    }

    public String getFileName()
    {
        return code.getFileName();
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