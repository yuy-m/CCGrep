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

    public List<String> getCodeByLine(boolean isMatchingOnly)
    {
        return getCodeByLine(isMatchingOnly, countLine());
    }

    public List<String> getCodeByLine(boolean isMatchingOnly, int countLines)
    {
        assert countLines > 0;
        final List<String> lines = code.getCodeByLine(start.getLine(), start.getLine() + countLines - 1);
        return isMatchingOnly? removeNonMatching(lines): lines;
    }

    private List<String> removeNonMatching(List<String> lines)
    {
        if(getStartLine() == getEndLine())
        {
            lines.set(
                0,
                lines.get(0).substring(getStartColumn() - 1, getEndColumn())
            );
        }
        else
        {
            lines.set(
                0,
                lines.get(0).substring(getStartColumn() - 1)
            );
            final int idxEnd = getEndLine() - getStartLine();
            if(idxEnd < lines.size())
            {
                lines.set(
                    idxEnd,
                    lines.get(idxEnd).substring(0, getEndColumn())
                );
            }
        }
        return lines;
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
        return start.getColumn();
    }

    public int getEndColumn()
    {
        return end.getColumn() + end.getText().length() - 1;
    }

    public int getStartTokenIndex()
    {
        return start.getTokenIndex();
    }

    public int getEndTokenIndex()
    {
        return end.getTokenIndex();
    }

    public int countLine()
    {
        return end.getLine() - start.getLine() + 1;
    }

    @Override
    public String toString()
    {
        return getFileName() + ":" + start + "-" + end;
    }
}