package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;
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
    void printlnMinimal(boolean enableFileName, boolean enableLine)
    {
        clones.forEach(stream::println);
    }
    void println(boolean enableFileName, boolean enableLine, boolean enableCode)
    {
        for(Clone clone: clones)
        {
            printCloneln(clone, enableFileName, enableLine, enableCode);
        }
    }
    void printCloneln(Clone clone, boolean enableFileName, boolean enableLine, boolean enableCode)
    {
        final List<String> lines = clone.getCodeByLine();
        if(enableFileName)
        {
            stream.println("file:" + clone.filename);
        }
        if(enableCode)
        {
            for(int idx = 0; idx < lines.size(); ++idx)
            {
                if(enableLine)
                {
                    stream.print((clone.start.getLine() + idx) + ":");
                }
                stream.println(lines.get(idx));
            }
        }
    }
}
