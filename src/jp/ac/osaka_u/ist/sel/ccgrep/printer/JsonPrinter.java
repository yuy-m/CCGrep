package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.StringJoiner;
import java.util.List;
import java.io.PrintStream;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class JsonPrinter implements IPrinter
{
    final List<CloneList> clones;
    final List<GrepToken> needle;
    final Language language;
    final BlindLevel blindLevel;
    final PrintStream stream;

    public JsonPrinter(List<CloneList> clones, List<GrepToken> needle, Language language, BlindLevel blindLevel, PrintStream stream)
    {
        this.clones = clones;
        this.needle = needle;
        this.language = language;
        this.blindLevel = blindLevel;
        this.stream = stream;
    }

    public JsonPrinter(List<CloneList> clones, List<GrepToken> needle, Language language, BlindLevel blindLevel)
    {
        this(clones, needle, language, blindLevel, System.out);
    }

    @Override
    public void println(PrintOption option)
    {
        final int lcnt = needle.get(needle.size() - 1).getLine() - needle.get(0).getLine() + 1;
        final String qtext = needle.get(0).getCodeByLine(lcnt).stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n"));

        // ignore option
        final String s = clones.stream()
            .filter(clonesByFile -> !clonesByFile.isEmpty())
            .map(clonesByFile -> makeCloneByFileJson(clonesByFile))
            .collect(Collectors.joining(
                ',' + System.lineSeparator(),

                "{" + System.lineSeparator()
              + " \"language\":\"" + language+ "\"," + System.lineSeparator()
              + " \"blindLevel\":\"" + blindLevel + "\"," + System.lineSeparator()
              + " \"query\":\"" + qtext + "\"," + System.lineSeparator()
              + " \"clonesPerFile\":[" + System.lineSeparator(),

                System.lineSeparator()
              + " ]" + System.lineSeparator()
              + "}"
            ));
        stream.println(s);
    }

    private String makeCloneByFileJson(CloneList cloneByFile)
    {
        return cloneByFile.stream()
            .map(clonesByFile -> makeCloneJson(clonesByFile))
            .map(s -> s.toString())
            .collect(Collectors.joining(
                ',' + System.lineSeparator(),

                "  {" + System.lineSeparator()
              + "   \"file\":\"" + escaped(cloneByFile.filename) + "\"," + System.lineSeparator()
              + "   \"clones\":[" + System.lineSeparator(),

              System.lineSeparator()
              + "   ]" + System.lineSeparator()
              + "  }"
            ));
    }

    private StringJoiner makeCloneJson(Clone clone)
    {
        final StringJoiner sj = new StringJoiner(
            ',' + System.lineSeparator(),
            "    {" + System.lineSeparator(),
            System.lineSeparator()
          + "    }"
        );
        sj.add("     \"startLine\":" + clone.getStartLine());
        sj.add("     \"startColumn\":" + clone.getStartColumn());
        sj.add("     \"endLine\":" + clone.getEndLine());
        sj.add("     \"endColumn\":" + clone.getEndColumn());
        sj.add(clone.getCodeByLine().stream()
                .map(s -> escaped(s))
                .collect(Collectors.joining(
                    "\\n",
                    "     \"code\":\"",
                    "\""))
        );
        return sj;
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