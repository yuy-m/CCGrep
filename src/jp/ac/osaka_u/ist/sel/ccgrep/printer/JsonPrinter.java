package jp.ac.osaka_u.ist.sel.ccgrep.printer;


import java.util.StringJoiner;
import java.util.List;
import java.io.PrintStream;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class JsonPrinter implements IPrinter
{
    final List<CloneList> clones;
    final GrepCode needle;
    final Language language;
    final BlindLevel blindLevel;
    final PrintStream stream;

    public JsonPrinter(List<CloneList> clones, GrepCode needle, Language language, BlindLevel blindLevel, PrintStream stream)
    {
        this.clones = clones;
        this.needle = needle;
        this.language = language;
        this.blindLevel = blindLevel;
        this.stream = stream;
    }

    public JsonPrinter(List<CloneList> clones, GrepCode needle, Language language, BlindLevel blindLevel)
    {
        this(clones, needle, language, blindLevel, System.out);
    }

    @Override
    public void println(PrintOption option)
    {
        final String qtext = needle.getCodeByLine().stream()
            .map(s -> escaped(s))
            .collect(Collectors.joining("\\n"));

        // ignore option
        final String s = clones.stream()
            //.filter(clonesByFile -> !clonesByFile.isEmpty())
            .map(clonesByFile -> makeCloneByFileJson(clonesByFile))
            .collect(Collectors.joining(
                ',' + System.lineSeparator(),

                "{" + System.lineSeparator()
              + " \"language\":\"" + language+ "\"," + System.lineSeparator()
              + " \"blindLevel\":\"" + blindLevel + "\"," + System.lineSeparator()
              + " \"queryCode\":\"" + qtext + "\"," + System.lineSeparator()
              + " \"countAllFile\":" + clones.size() + "," + System.lineSeparator()
              + " \"countCloneFile\":" + clones.stream().filter(l -> !l.isEmpty()).count() + "," + System.lineSeparator()
              + " \"countAllClone\":" + clones.stream().mapToInt(l -> l.size()).sum() + "," + System.lineSeparator()
              + " \"clonesPerFile\":[" + System.lineSeparator(),

                System.lineSeparator()
              + " ]" + System.lineSeparator()
              + "}"
            ));
        stream.println(s);
    }

    private String makeCloneByFileJson(CloneList cloneByFile)
    {
        final String s = cloneByFile.stream()
            .map(clone -> makeCloneJson(clone).toString())
            .collect(Collectors.joining(
                ',' + System.lineSeparator(),

                "  {" + System.lineSeparator()
              + "   \"fileName\":\"" + escaped(cloneByFile.getFileName()) + "\"," + System.lineSeparator()
              + "   \"countLine\":" + cloneByFile.getCode().countLines() + "," + System.lineSeparator()
              + "   \"countToken\":" + cloneByFile.getCode().countTokens() + "," + System.lineSeparator()
              + "   \"countClone\":" + cloneByFile.size() + "," + System.lineSeparator()
              + "   \"clones\":[" + System.lineSeparator(),

              System.lineSeparator()
              + "   ]" + System.lineSeparator()
              + "  }"
            ));
        cloneByFile.getCode().clearCodeCache();
        return s;
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