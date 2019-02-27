package jp.ac.osaka_u.ist.sel.ccgrep.printer;

import java.util.List;
import java.io.PrintStream;

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
    public void print(List<CloneList> clones)
    {
        printHeader();
        final boolean[] needDelim = {false};
        clones.stream()
            .filter(this::isFilePrintable)
            .forEach(clonePerFile -> {
                printFileInLoop(clonePerFile, needDelim[0]);
                needDelim[0] = true;
            });
        printFooter(new CloneList.Statistic(clones));
    }

    @Override
    public void printFile(CloneList clonePerFile)
    {
        printlnFileHeader(clonePerFile);
        final boolean[] needDelim = {false};
        clonePerFile.forEach(clone -> {
            printClone(clone, needDelim[0]);
            needDelim[0] = true;
        });
        printlnFileFooter(clonePerFile);
        clonePerFile.getCode().clearCodeCache();
    }

    @Override
    public void printFileInLoop(CloneList clonePerFile, boolean withDelimiter)
    {
        if(!isFilePrintable(clonePerFile))
        {
            throw new IllegalStateException();
        }
        if(withDelimiter)
        {
            printFileDelimiter();
        }
        printFile(clonePerFile);
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

    abstract protected void printlnFileHeader(CloneList clonePerFile);
    abstract protected void printlnFileFooter(CloneList clonePerFile);
}
