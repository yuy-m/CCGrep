package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
import java.util.Arrays;

import org.antlr.v4.runtime.misc.Interval;

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
        return getCodeByLine(0, 0);
    }

    public List<String> getCodeByLine(int beforeContextCount, int afterContextCount)
    {
        final String text = start.getInputStream()
            .getText(new Interval(
                0,
                start.getInputStream().size()
            ));
        return Arrays.asList(text.split("\r\n|[\n\r\u2028\u2029\u0085]"))
            .subList(start.getLine() - 1, end.getLine());
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