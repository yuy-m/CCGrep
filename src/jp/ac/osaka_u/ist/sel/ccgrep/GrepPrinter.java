package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
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
        if(option.fileNameEnabled)
        {
            final String fn = "file:" + clone.filename;
            stream.print(option.escapeEnabled? option.language.lineCommented(fn): fn);
        }
        if(option.codeEnabled)
        {
            stream.println();
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
        else if(option.lineEnabled)
        {
            stream.println(" (" + clone.start.getLine() + "-" + clone.end.getLine() + ")");
        }
    }
    public static class Option
    {
        Language language;
        boolean fileNameEnabled = true;
        boolean lineEnabled = true;
        boolean codeEnabled = true;
        boolean escapeEnabled = false;
        public Option(Language language)
        {
            this.language = language;
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
