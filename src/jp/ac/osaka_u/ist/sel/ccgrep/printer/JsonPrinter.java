package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.StringJoiner;
import java.util.List;
import java.io.PrintStream;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class JsonPrinter extends AbstractPrinter
{
    final GrepCode needle;
    final Language language;
    final BlindLevel blindLevel;

    public JsonPrinter(PrintOption option, GrepCode needle, Language language, BlindLevel blindLevel)
    {
        super(option);
        this.needle = needle;
        this.language = language;
        this.blindLevel = blindLevel;
    }

    @Override
    public void printHeader()
    {
        final String qtext = needle.getCodeByLine().stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n"));
        stream.println(
            "{" + System.lineSeparator()
            + " \"language\":\"" + language+ "\"," + System.lineSeparator()
            + " \"blindLevel\":\"" + blindLevel + "\"," + System.lineSeparator()
            + " \"queryCode\":\"" + qtext + "\"," + System.lineSeparator()
            + " \"clonesPerFile\":["
        );
    }

    @Override
    public void printFooter(CloneList.Statistic statistic)
    {
        stream.print(
            System.lineSeparator()
            + " ]," + System.lineSeparator()
            + " \"countAllFile\":" + statistic.countAllFile() + "," + System.lineSeparator()
            + " \"countAllLine\":" + statistic.countAllLine() + "," + System.lineSeparator()
            + " \"countAllToken\":" + statistic.countAllToken() + "," + System.lineSeparator()
            + " \"countCloneFile\":" + statistic.countCloneFile() + "," + System.lineSeparator()
            + " \"countAllClone\":" + statistic.countAllClone() + System.lineSeparator()
            + "}"
        );
    }

    @Override
    public void printFileDelimiter()
    {
        stream.println(",");
    }

    @Override
    public void printFileHeader(CloneList clonePerFile)
    {
        final String s = "  {" + System.lineSeparator()
            + "   \"fileName\":\"" + escaped(clonePerFile.getFileName())  + "\"," + System.lineSeparator()
            + "   \"countLine\":"  + clonePerFile.getCode().countLines()  + "," + System.lineSeparator()
            + "   \"countToken\":" + clonePerFile.getCode().countTokens() + "," + System.lineSeparator()
            + "   \"countClone\":" + clonePerFile.size()                  + "," + System.lineSeparator()
            + "   \"clones\":[";
        stream.println(s);
    }
    @Override
    public void printFileFooter(CloneList clonePerFile)
    {
        stream.print(
            System.lineSeparator()
            + "   ]" + System.lineSeparator()
            + "  }"
        );
    }

    @Override
    public void printCloneDelimiter()
    {
        stream.println(",");
    }

    @Override
    public void printClone(Clone clone)
    {
        final String header =
              "    {" + System.lineSeparator()
            + "     \"startLine\":"   + clone.getStartLine()   + "," + System.lineSeparator()
            + "     \"startColumn\":" + clone.getStartColumn() + "," + System.lineSeparator()
            + "     \"endLine\":"     + clone.getEndLine()     + "," + System.lineSeparator()
            + "     \"endColumn\":"   + clone.getEndColumn()   + "," + System.lineSeparator()
            + "     \"code\":\"";
        final String footer =
              "\"" + System.lineSeparator()
            + "    }";
        final String str = clone.getCodeByLine().stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n", header, footer));
        stream.print(str);
    }

    private static String escaped(String s)
    {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("/", "\\/")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}