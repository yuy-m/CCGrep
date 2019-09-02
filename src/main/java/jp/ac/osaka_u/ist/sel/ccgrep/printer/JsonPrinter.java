package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class JsonPrinter extends AbstractPrinter
{
    final GrepCode query;
    final Language language;
    final BlindLevel blindLevel;

    public JsonPrinter(PrintOption option, GrepCode query, Language language, BlindLevel blindLevel)
    {
        super(option);
        this.query = query;
        this.language = language;
        this.blindLevel = blindLevel;
    }

    @Override
    public void printHeader()
    {
        final String qtext = query.getCodeByLine().stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n"));
        stream.println(
            "{" + System.lineSeparator()
            + " \"startTime\":\"" + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "\"," + System.lineSeparator()
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
        return !option.isMinimalEnabled || !clonePerFile.isEmpty();
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
            +      "\"startColumn\":" + clone.getStartColumn() + ", "
            +      "\"startToken\":" + (clone.getStartTokenIndex() + 1) + ", "
            +      "\"endLine\":"     + clone.getEndLine()     + ", "
            +      "\"endColumn\":"   + clone.getEndColumn()   + ", "
            +      "\"endToken\":" + (clone.getEndTokenIndex() + 1) + "," + System.lineSeparator()
            + "     \"code\":\"";
        final String footer =
              "\"" + System.lineSeparator()
            + "    }";
        if(option.isMinimalEnabled)
        {
            stream.println(header + footer);
        }
        else
        {
            final String str = clone.getCodeByLine(option.isMatchingOnlyEnabled).stream()
                .map(s -> escaped(s))
                .collect(Collectors.joining("\\n", header, footer));
            stream.println(str);
        }
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