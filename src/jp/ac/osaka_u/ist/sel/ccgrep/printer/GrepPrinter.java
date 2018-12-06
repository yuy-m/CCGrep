package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.io.PrintStream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class GrepPrinter implements IPrinter
{
    final List<CloneList> clones;
    final PrintStream stream;

    public GrepPrinter(List<CloneList> clones, PrintStream stream)
    {
        this.clones = clones;
        this.stream = stream;
    }

    public GrepPrinter(List<CloneList> clones)
    {
        this(clones, System.out);
    }

    @Override
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
                .filter(clonesByFile -> !clonesByFile.isEmpty())
                .map(clonesByFile -> clonesByFile.getFileName())
                .forEach(stream::println);
        }
        else
        {
            clones.stream()
                .filter(clonesByFile -> !clonesByFile.isEmpty())
                .forEach(clonesByFile -> {
                    clonesByFile.forEach(clone -> printCloneln(clone, option));
                    clonesByFile.code.clearCodeCache();
                });
        }
    }

    private void printCloneln(Clone clone, PrintOption option)
    {
        if(option.isCodeEnabled)
        {
            final List<String> lines =  clone.getCodeByLine();
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
                        final String ln = (clone.getStartLine() + idx) + ":";
                        stream.print(option.isEscapeEnabled? option.language.blockCommented(ln): ln);
                    }
                    stream.println(lines.get(idx));
                });
        }
        else
        {
            final List<String> lines =  clone.getCodeByLine(1);
            final StringJoiner sj = new StringJoiner(":", option.isEscapeEnabled? option.language.blockCommentBegin(): "", "");
            if(option.isFileNameEnabled)
            {
                sj.add(clone.getFileName());
            }
            if(option.isLineEnabled)
            {
                sj.add(String.valueOf(clone.getStartLine()));
            }
            sj.add(option.isEscapeEnabled
                ? option.language.blockCommentEnd() + lines.get(0)
                : lines.get(0)
            );
            stream.println(sj);
        }
    }
}
