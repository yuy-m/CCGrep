package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

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
        if(option.isCountOnlyEnabled)
        {
            printHeader();
            printFooter(new CloneList.Statistic(clones));
        }
        else
        {
            super.print(clones);
        }
    }

    @Override
    public void printHeader()
    {}

    @Override
    public void printFooter(CloneList.Statistic statistic)
    {
        if(option.isCountOnlyEnabled)
        {
            stream.println(statistic.countAllClone());
        }
    }

    @Override
    public boolean isFilePrintable(CloneList clonePerFile)
    {
        return !option.isCountOnlyEnabled && !clonePerFile.isEmpty();
    }

    @Override
    public void printFile(CloneList clonePerFile)
    {
        if(option.isFileNameOnlyEnabled)
        {
            printlnFileHeader(clonePerFile);
            stream.println(clonePerFile.getFileName());
            printlnFileFooter(clonePerFile);
        }
        else
        {
            super.printFile(clonePerFile);
        }
    }
    @Override
    protected void printlnFileHeader(CloneList clonePerFile)
    {}
    @Override
    protected void printlnFileFooter(CloneList clonePerFile)
    {}

    @Override
    public void printFileDelimiter()
    {}

    @Override
    public void printCloneDelimiter()
    {}

    @Override
    public void printClone(Clone clone)
    {
        if(option.isCodeEnabled)
        {
            printCloneWithFullCode(clone);
        }
        else
        {
            printCloneWithOneLine(clone);
        }
    }

    private void printCloneWithFullCode(Clone clone)
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
        stream.println(sb);
    }

    private void printCloneWithOneLine(Clone clone)
    {
        final List<String> lines =  clone.getCodeByLine(1);
        final StringJoiner sj = new StringJoiner(":", option.isEscapeEnabled? option.language.blockCommentBegin(): "", "");
        if(option.isFileNameEnabled)
        {
            sj.add(clone.getFileName());
        }
        if(option.isLineEnabled)
        {
            sj.add(
                option.isLinePairEnabled
                    ? clone.getStartLine() + "-" + clone.getEndLine()
                    : "" + clone.getStartLine()
            );
        }
        sj.add(option.isEscapeEnabled
            ? option.language.blockCommentEnd() + lines.get(0)
            : lines.get(0)
        );
        stream.println(sj);
    }
}