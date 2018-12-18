package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.StringJoiner;
import java.util.List;
import java.io.PrintStream;
import java.time.ZonedDateTime;
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
            + " \"startTime\":\"" + ZonedDateTime.now() + "\"," + System.lineSeparator()
            + " \"language\":\"" + language+ "\", "
            +  "\"blindLevel\":\"" + blindLevel + "\"," + System.lineSeparator()
            + " \"queryCode\":\"" + qtext + "\"," + System.lineSeparator()
            + " \"clonesPerFile\":["
        );
    }

    @Override
    public void printFooter(CloneList.Statistic statistic)
    {
        stream.println(
              " ]," + System.lineSeparator()
            + " \"countAllFile\":" + statistic.countAllFile() + "," + System.lineSeparator()
            + " \"countAllLine\":" + statistic.countAllLine() + "," + System.lineSeparator()
            + " \"countAllToken\":" + statistic.countAllToken() + "," + System.lineSeparator()
            + " \"countCloneFile\":" + statistic.countCloneFile() + "," + System.lineSeparator()
            + " \"countAllClone\":" + statistic.countAllClone() + "," + System.lineSeparator()
            + " \"procTime\":" + String.format("%.3f", statistic.countTimeAsSeconds()) + System.lineSeparator()
            + "}"
        );
    }

    @Override
    public void printFileDelimiter()
    {
        stream.print("  ,");
    }

    @Override
    public boolean isFilePrintable(CloneList clonePerFile)
    {
        return true;
    }

    @Override
    public void printlnFileHeader(CloneList clonePerFile)
    {
        final String s = "  {" + System.lineSeparator()
            + "   \"fileName\":\"" + escaped(clonePerFile.getFileName())  + "\"," + System.lineSeparator()
            + "   \"countLine\":"  + clonePerFile.getCode().countLines()  + ", "
            +    "\"countToken\":" + clonePerFile.getCode().countTokens() + ", "
            +    "\"countClone\":" + clonePerFile.size()                  + "," + System.lineSeparator()
            + "   \"clones\":[";
        stream.println(s);
    }
    @Override
    public void printlnFileFooter(CloneList clonePerFile)
    {
        stream.println(
              "   ]" + System.lineSeparator()
            + "  }"
        );
    }

    @Override
    public void printCloneDelimiter()
    {
        stream.print("    ,");
    }

    @Override
    public void printClone(Clone clone)
    {
        final String header =
              "    {" + System.lineSeparator()
            + "     \"startLine\":"   + clone.getStartLine()   + ", "
            +      "\"startColumn\":" + clone.getStartColumn() + "," + System.lineSeparator()
            + "     \"endLine\":"     + clone.getEndLine()     + ", "
            +      "\"endColumn\":"   + clone.getEndColumn()   + "," + System.lineSeparator()
            + "     \"code\":\"";
        final String footer =
              "\"" + System.lineSeparator()
            + "    }";
        final String str = clone.getCodeByLine().stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n", header, footer));
        stream.println(str);
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