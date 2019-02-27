package jp.ac.osaka_u.ist.sel.ccgrep.printer;

import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.Clone;
import jp.ac.osaka_u.ist.sel.ccgrep.model.CloneList;


public interface IPrinter
{
    void print(List<CloneList> clones);

    void printHeader();

    boolean isFilePrintable(CloneList clonePerFile);
    void printFile(CloneList clonePerFile);
    void printFileInLoop(CloneList clonePerFile, boolean withDelimiter);
    void printFileDelimiter();

    void printClone(Clone clone);
    boolean printClone(Clone clone, boolean withDelimiter);
    void printCloneDelimiter();

    void printFooter(CloneList.Statistic statistic);

    void printNewLine();
}
