package jp.ac.osaka_u.ist.sel.ccgrep.printer;

import java.util.List;
import java.util.function.Consumer;
import java.io.PrintStream;
import java.util.stream.IntStream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.Clone;
import jp.ac.osaka_u.ist.sel.ccgrep.model.CloneList;


abstract class AbstractPrinter implements IPrinter
{
    protected final PrintOption option;
    protected final PrintStream stream;

    protected AbstractPrinter(PrintOption option, PrintStream stream)
    {
        this.option = option;
        this.stream = stream;
    }

    protected AbstractPrinter(PrintOption option)
    {
        this(option, System.out);
    }

    @Override
    public void printNewLine()
    {
        stream.println();
    }

    @Override
    public void println(List<CloneList> clones)
    {
        print(clones);
        stream.println();
    }

    @Override
    public void print(List<CloneList> clones)
    {
        printHeader();
        final boolean[] needDelim = {false};
        clones.forEach(clonePerFile -> {
            needDelim[0] |= printFile(clonePerFile, needDelim[0]);
        });
        printFooter(new CloneList.Statistic(clones));
    }

    @Override
    public void printFile(CloneList clonePerFile)
    {
        printFileHeader(clonePerFile);
        final boolean[] needDelim = {false};
        clonePerFile.forEach(clone -> {
            needDelim[0] |= printClone(clone, needDelim[0]);
        });
        printFileFooter(clonePerFile);
        clonePerFile.getCode().clearCodeCache();
    }

    @Override
    public boolean printFile(CloneList clonePerFile, boolean withDelimiter)
    {
        if(withDelimiter)
        {
            printFileDelimiter();
        }
        printFile(clonePerFile);
        return true;
    }

    @Override
    public boolean printClone(Clone clone, boolean withDelimiter)
    {
        if(withDelimiter)
        {
            printCloneDelimiter();
        }
        printClone(clone);
        return true;
    }

    abstract protected void printFileHeader(CloneList clonePerFile);
    abstract protected void printFileFooter(CloneList clonePerFile);
}
