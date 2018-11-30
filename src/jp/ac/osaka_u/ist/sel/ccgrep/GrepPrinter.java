package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.io.PrintStream;

public class GrepPrinter
{
    final List<Clone> clones;
    final PrintStream stream;
    GrepPrinter(List<Clone> clones, PrintStream stream)
    {
        this.clones = clones;
        this.stream = stream;
    }
    GrepPrinter(List<Clone> clones)
    {
        this(clones, System.out);
    }
    void println(Option option)
    {
        clones.forEach(clone -> printCloneln(clone, option));
    }
    void printCloneln(Clone clone, Option option)
    {
        final List<String> lines = clone.getCodeByLine();

        if(option.grepLikeEnabled)
        {
            final StringJoiner sj = new StringJoiner(":", option.escapeEnabled? option.language.blockCommentBegin(): "", "");
            if(option.fileNameEnabled)
            {
                sj.add(clone.filename);
            }
            if(option.lineEnabled)
            {
                final String s = String.valueOf(clone.start.getLine());
                sj.add(s);
            }
            if(option.codeEnabled)
            {
                sj.add((option.escapeEnabled? option.language.blockCommentEnd(): "") + lines.get(0));
                stream.println(sj);
            }
            else if(option.escapeEnabled)
            {
                stream.print(sj);
                stream.println(option.language.blockCommentEnd());
            }
        }
        else
        {
            if(option.fileNameEnabled)
            {
                final String fn = "file:" + clone.filename + " (" + getRangeString(clone) + ")";
                stream.println(option.escapeEnabled? option.language.lineCommented(fn): fn);
            }
            if(option.codeEnabled)
            {
                IntStream.range(0, lines.size())
                    .forEach(idx -> {
                        if(option.lineEnabled)
                        {
                            final String ln = (clone.start.getLine() + idx) + ":";
                            stream.print(option.escapeEnabled? option.language.blockCommented(ln): ln);
                        }
                        stream.println(lines.get(idx));
                    });
            }
        }
    }

    private static String getRangeString(Clone clone)
    {
        return clone.start.getLine() + "." + clone.start.getCharPositionInLine()
            + "-"
            + clone.end.getLine() + "." + (clone.end.getCharPositionInLine() + clone.end.getText().length());
    }

    public static class Option
    {
        Language language;
        boolean grepLikeEnabled = true;
        boolean fileNameEnabled = true;
        boolean lineEnabled = true;
        boolean codeEnabled = true;
        boolean escapeEnabled = false;
        public Option(Language language)
        {
            this.language = language;
        }
        public Option enableGrepLike(boolean enabled)
        {
            this.grepLikeEnabled = enabled;
            return this;
        }
        public Option enableFileName(boolean enabled)
        {
            this.fileNameEnabled = enabled;
            return this;
        }
        public Option enableLine(boolean enabled)
        {
            this.lineEnabled = enabled;
            return this;
        }
        public Option enableCode(boolean enabled)
        {
            this.codeEnabled = enabled;
            return this;
        }
        public Option enableEscape(boolean enabled)
        {
            this.escapeEnabled = enabled;
            return this;
        }
    }
}
