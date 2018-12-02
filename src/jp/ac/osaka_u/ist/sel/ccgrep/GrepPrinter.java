package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.io.PrintStream;

public class GrepPrinter
{
    final List<CloneList> clones;
    final PrintStream stream;
    GrepPrinter(List<CloneList> clones, PrintStream stream)
    {
        this.clones = clones;
        this.stream = stream;
    }
    GrepPrinter(List<CloneList> clones)
    {
        this(clones, System.out);
    }
    public void println(PrintOption option)
    {
        if(option.isCountOnlyEnabled)
        {
            stream.println(
                clones.stream()
                    .mapToInt(clonesByFile -> clonesByFile.size())
                    .sum()
            );
        }
        else if(option.isFileNameOnlyEnabled)
        {
            clones.stream()
                .map(clonesByFile -> clonesByFile.filename)
                .forEach(stream::println);
        }
        else
        {
            clones.forEach(
                clonesByFile -> clonesByFile.forEach(
                    clone -> printCloneln(clone, option)
                )
            );
        }
    }
    private void printCloneln(Clone clone, PrintOption option)
    {
        final List<String> lines = clone.getCodeByLine();

        if(option.isCodeEnabled)
        {
            if(option.isFileNameEnabled)
            {
                stream.println(option.isEscapeEnabled
                    ? option.language.lineCommented(clone.getFileName())
                    : clone.getFileName());
            }
            IntStream.range(0, lines.size())
                .forEach(idx -> {
                    if(option.isLineEnabled)
                    {
                        final String ln = (clone.start.getLine() + idx) + ":";
                        stream.print(option.isEscapeEnabled? option.language.blockCommented(ln): ln);
                    }
                    stream.println(lines.get(idx));
                });
        }
        else
        {
            final StringJoiner sj = new StringJoiner(":", option.isEscapeEnabled? option.language.blockCommentBegin(): "", "");
            if(option.isFileNameEnabled)
            {
                sj.add(clone.getFileName());
            }
            if(option.isLineEnabled)
            {
                sj.add(String.valueOf(clone.start.getLine()));
            }
            sj.add(option.isEscapeEnabled
                ? option.language.blockCommentEnd() + lines.get(0)
                : lines.get(0)
            );
            stream.println(sj);
        }
    }

    private static String getRangeString(Clone clone)
    {
        return clone.start.getLine() + "." + clone.start.getCharPositionInLine()
            + "-"
            + clone.end.getLine() + "." + (clone.end.getCharPositionInLine() + clone.end.getText().length());
    }
}
