package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
import java.util.Arrays;

import org.antlr.v4.runtime.misc.Interval;

public class Clone
{
    public final String filename;
    public final GrepToken start;
    public final GrepToken end;
    public Clone(String filename, GrepToken start, GrepToken end)
    {
        this.filename = filename;
        this.start = start;
        this.end = end;
    }
    public List<String> getCodeByLine()
    {
        final String text = start.getInputStream()
            .getText(new Interval(
                0,
                start.getInputStream().size()
            ));
        return Arrays.asList(text.split("\r\n|[\n\r\u2028\u2029\u0085]"))
            .subList(start.getLine() - 1, end.getLine());
    }

    @Override
    public String toString()
    {
        return filename + ":" + start + "-" + end;
    }
}