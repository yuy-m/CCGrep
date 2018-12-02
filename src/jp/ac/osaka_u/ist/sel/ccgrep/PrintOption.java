package jp.ac.osaka_u.ist.sel.ccgrep;


public class PrintOption
{
    final Language language;

    final boolean isCountOnlyEnabled;
    final boolean isFileNameOnlyEnabled;

    final boolean isCodeEnabled;
    final boolean isFileNameEnabled;
    final boolean isLineEnabled;
    final boolean isEscapeEnabled;

    public PrintOption(Language language, String opts)
    {
        this.language = language;

        this.isCountOnlyEnabled = opts.contains("c");
        this.isFileNameOnlyEnabled = opts.contains("l");

        this.isCodeEnabled = opts.contains("f");
        this.isFileNameEnabled = !opts.contains("h");
        this.isLineEnabled = opts.contains("n");
        this.isEscapeEnabled = opts.contains("e");
    }
}
