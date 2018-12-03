package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

// import org.antlr.v4.runtime.misc.Interval;


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

    private static final Pattern splitPattern = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");
    public List<String> getCodeByLine(int countLines)
    {
        try
        {
            return Files.lines(Paths.get(start.getFileName()))
                .skip(start.getLine() - 1)
                .limit(countLines)
                .collect(Collectors.toList());
        }
        catch(IOException e)
        {
            System.err.println("Error: cannot read file '" + start.getFileName() + '\'');
            return Collections.singletonList("<!error: cannot read file!>");
        }
        // all source code of the file.
        /*final String text = start.getInputStream()
            .getText(new Interval(
                0,
                start.getInputStream().size()
            ));

        // extract clone lines
        return splitPattern
            .splitAsStream(text)
            .skip(start.getLine() - 1)
            .limit(countLines)
            .collect(Collectors.toList()); //*/
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