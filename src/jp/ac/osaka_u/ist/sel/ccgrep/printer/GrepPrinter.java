package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.io.PrintStream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class GrepPrinter extends AbstractPrinter
{
    public GrepPrinter(PrintOption option)
    {
        super(option);
    }

    @Override
    public void print(List<CloneList> clones)
    {
        final List<CloneList> cll = clones.stream()
                .filter(cl -> !cl.isEmpty())
                .collect(Collectors.toList());
        printHeader();
        if(!option.isCountOnlyEnabled)
        {
            final boolean[] needDelim = {false};
            cll.forEach(clonePerFile -> {
                needDelim[0] |= printFile(clonePerFile, needDelim[0]);
            });
        }
        printFooter(new CloneList.Statistic(clones));
    }

    @Override
    public void printHeader()
    {}

    @Override
    public void printFooter(CloneList.Statistic statistic)
    {
        if(option.isCountOnlyEnabled)
        {
            stream.print(statistic.countAllClone());
        }
        if(option.isTimeEnabled)
        {
            stream.printf("%.3f", statistic.countTimeAsSeconds());
        }
    }

    @Override
    public boolean printFile(CloneList clonePerFile, boolean withDelimiter)
    {
        if(option.isCountOnlyEnabled || clonePerFile.isEmpty())
        {
            return false;
        }
        return super.printFile(clonePerFile, withDelimiter);
    }

    @Override
    public void printFile(CloneList clonePerFile)
    {
        if(option.isFileNameOnlyEnabled)
        {
            printFileHeader(clonePerFile);
            stream.print(clonePerFile.getFileName());
            printFileFooter(clonePerFile);
        }
        else
        {
            super.printFile(clonePerFile);
        }
    }
    @Override
    protected void printFileHeader(CloneList clonePerFile)
    {}
    @Override
    protected void printFileFooter(CloneList clonePerFile)
    {}

    @Override
    public void printFileDelimiter()
    {
        stream.println();
    }

    @Override
    public void printCloneDelimiter()
    {
        stream.println();
    }

    @Override
    public void printClone(Clone clone)
    {
        if(option.isCodeEnabled)
        {
            printCloneWhenCodeEnabled(clone);
        }
        else
        {
            printCloneWhenCodeDisabled(clone);
        }
    }

    private void printCloneWhenCodeEnabled(Clone clone)
    {
        final List<String> lines =  clone.getCodeByLine();
        final StringBuilder sb = new StringBuilder();
        if(option.isFileNameEnabled)
        {
            if(option.isEscapeEnabled)
            {
                sb.append(option.language.lineComment());
            }
            sb.append(clone.getFileName()).append(System.lineSeparator());
        }
        final String code = IntStream.range(0, lines.size())
            .mapToObj(idx -> {
                if(!option.isLineEnabled)
                {
                    return lines.get(idx);
                }
                else
                {
                    final StringBuilder sb1 = new StringBuilder();
                    if(option.isEscapeEnabled)
                    {
                        sb1.append(option.language.blockCommentBegin());
                    }
                    sb1.append(clone.getStartLine() + idx).append(":");
                    if(option.isEscapeEnabled)
                    {
                        sb1.append(option.language.blockCommentEnd());
                    }
                    sb1.append(lines.get(idx));
                    return sb1;
                }
            })
            .collect(Collectors.joining(System.lineSeparator()));
        sb.append(code);
        stream.print(sb);
    }

    private void printCloneWhenCodeDisabled(Clone clone)
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
        stream.print(sj);
    }
}
