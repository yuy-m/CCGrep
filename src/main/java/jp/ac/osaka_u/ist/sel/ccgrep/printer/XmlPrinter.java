package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.StringJoiner;
import java.util.List;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class XmlPrinter extends AbstractPrinter
{
    final GrepCode needle;
    final Language language;
    final BlindLevel blindLevel;

    public XmlPrinter(PrintOption option, GrepCode needle, Language language, BlindLevel blindLevel)
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
            .collect(Collectors.joining(System.lineSeparator()));
        stream.println(
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + System.lineSeparator()
            + "<ccgrep" + System.lineSeparator()
            + " startTime=\"" + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "\"" + System.lineSeparator()
            + " language=\"" + language+ "\" blindLevel=\"" + blindLevel + "\" >" + System.lineSeparator()
            + " <queryCode xml:space=\"preserve\" >" + qtext + "</queryCode>" + System.lineSeparator()
            + " <clonesPerFile>"
        );
    }

    @Override
    public void printFooter(CloneList.Statistic statistic)
    {
        stream.println(
              " </clonesPerFile>" + System.lineSeparator()
            + " <result" + System.lineSeparator()
            + "  countAllFile=\"" + statistic.countAllFile() + "\"" + System.lineSeparator()
            + "  countAllLine=\"" + statistic.countAllLine() + "\"" + System.lineSeparator()
            + "  countAllToken=\"" + statistic.countAllToken() + "\"" + System.lineSeparator()
            + "  countCloneFile=\"" + statistic.countCloneFile() + "\"" + System.lineSeparator()
            + "  countAllClone=\"" + statistic.countAllClone() + "\"" + System.lineSeparator()
            + "  procTime=" + String.format("\"%.3f\"", statistic.countTimeAsSeconds()) + " />" + System.lineSeparator()
            + "</ccgrep>"
        );
    }

    @Override
    public void printFileDelimiter()
    {}

    @Override
    public boolean isFilePrintable(CloneList clonePerFile)
    {
        return !option.isMinimalEnabled || !clonePerFile.isEmpty();
    }

    @Override
    public void printlnFileHeader(CloneList clonePerFile)
    {
        final String s =
              "  <file" + System.lineSeparator()
            + "   fileName=\"" + escaped(clonePerFile.getFileName())  + "\"" + System.lineSeparator()
            + "   countLine=\""  + clonePerFile.getCode().countLines()  + "\" "
            +    "countToken=\"" + clonePerFile.getCode().countTokens() + "\" "
            +    "countClone=\"" + clonePerFile.size()                  + "\" >";
        stream.println(s);
    }
    @Override
    public void printlnFileFooter(CloneList clonePerFile)
    {
        stream.println("  </file>");
    }

    @Override
    public void printCloneDelimiter()
    {}

    @Override
    public void printClone(Clone clone)
    {
        final String header =
              "    <clone" + System.lineSeparator()
            + "     startLine=\""   + clone.getStartLine()   + "\" "
            +      "startColumn=\"" + clone.getStartColumn() + "\" "
            +      "startToken=\"" + (clone.getStartTokenIndex() + 1) + "\" "
            +      "endLine=\""     + clone.getEndLine()     + "\" "
            +      "endColumn=\""   + clone.getEndColumn()   + "\" "
            +      "endToken=\""   + (clone.getEndTokenIndex() + 1)   + "\"" + System.lineSeparator()
            + "     xml:space=\"preserve\" >";
        final String footer = "</clone>";
        if(option.isMinimalEnabled)
        {
            stream.println(header + footer);
        }
        else
        {
            final String str = clone.getCodeByLine().stream()
                .map(s -> escaped(s))
                .collect(Collectors.joining(System.lineSeparator(), header, footer));
            stream.println(str);
        }
    }

    private static String escaped(String s)
    {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}