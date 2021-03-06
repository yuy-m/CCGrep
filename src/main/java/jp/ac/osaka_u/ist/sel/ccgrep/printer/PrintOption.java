package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class PrintOption
{
    final Language language;

    final boolean isCountOnlyEnabled;
    final boolean isCountByFileOnlyEnabled;
    final boolean isFileNameOnlyEnabled;

    final boolean isCodeEnabled;
    final boolean isFileNameEnabled;
    final boolean isLineEnabled;
    final boolean isLinePairEnabled;
    final boolean isEscapeEnabled;
    final boolean isMatchingOnlyEnabled;
    final boolean isRemoveNewLineEnabled;

    final boolean isMinimalEnabled;

    public PrintOption(Language language, String opts)
    {
        this.language = language;

        this.isCountOnlyEnabled = opts.contains("C");
        this.isCountByFileOnlyEnabled = opts.contains("c");
        this.isFileNameOnlyEnabled = opts.contains("l");

        this.isCodeEnabled = opts.contains("f") || opts.contains("r");
        this.isFileNameEnabled = !opts.contains("h");
        this.isMatchingOnlyEnabled = opts.contains("o");

        this.isLineEnabled = opts.contains("n") || opts.contains("N");
        this.isLinePairEnabled = opts.contains("N");

        this.isEscapeEnabled = opts.contains("e");

        this.isRemoveNewLineEnabled = opts.contains("r");

        this.isMinimalEnabled = opts.contains("m");
    }
}
