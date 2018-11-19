package jp.ac.osaka_u.ist.sel.ccgrep;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jargp.*;

import jp.ac.osaka_u.ist.sel.ccgrep.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.GrepPrinter;
import jp.ac.osaka_u.ist.sel.ccgrep.TokenSequenceDetector;

public class CCGrep
{
    static boolean DEBUG = false;
    public static void main(String[] args)
    {
        final CCGrep ccgrep = new CCGrep();

        final ArgumentProcessor ap = new ArgumentProcessor(pd);
        ap.processArgs(args, ccgrep);

        if(ccgrep.help)
        {
            showHelp(ap);
            System.exit(0);
        }

        if(!ap.getArgs().hasNext())
        {
            showHelp(ap);
            System.exit(2);
        }
        final String languageName = ap.getArgs().next();
        if(ccgrep.needleFileName == null)
        {
            ccgrep.needleFileName = ap.getArgs().next();
        }
        String[] haystackNames;
        if(ap.getArgs().hasNext())
        {
            final List<String> l = new ArrayList<>();
            while(ap.getArgs().hasNext())
            {
                l.add(ap.getArgs().next());
            }
            haystackNames = new String[l.size()];
            l.toArray(haystackNames);
        }
        else
        {
            haystackNames = new String[]{"-"};
        }

        final long st = System.nanoTime();
        final int returnCode = ccgrep.grep(languageName, haystackNames);
        final long et = System.nanoTime();

        final long milli = (et - st) / 1000000;
        System.err.println((milli / 1000.0) + " sec");

        System.exit(returnCode);
    }

    private static final ParameterDef[] pd = new ParameterDef[]{
        new StringDef('b', "blindLevelName", "blind identifiers and basic types."),
        new BoolDef('l', "isFileNameOnlyEnabled", "print only file name of clones.", true),
        new BoolDef('N', "isLineNumberEnabled", "NOT print line number.", false),
        new BoolDef('e', "isEscapeEnabled", "comment out printed information other than source code.", true),
        new BoolDef('c', "isCountEnabled", "print only count of clones.", true),
        new StringDef('f', "needleFileName", "obtain needle from file. CANNOT give needle as code string at once."),
        //new IntDef('m', "maxCount", "stop after NN clones."),
        new BoolDef('h', "help", "show help", true)
    };

    private static void showHelp(ArgumentProcessor ap)
    {
        System.out.println("ccgrep.sh [options]... language needleCode haystackFiles...");
        System.out.println(" language : c, c++, Java, Python3");
        ap.listParameters(80, System.out);
    }

    static void debugprint(String msg)
    {
        if(DEBUG)
        {
            System.err.print(msg);
        }
    }

    static void debugprintln(String msg)
    {
        if(DEBUG)
        {
            System.err.println(msg);
        }
    }

    String blindLevelName = "";
    boolean isCountEnabled = false;
    boolean isLineNumberEnabled = true;
    boolean isFileNameOnlyEnabled = false;
    boolean isEscapeEnabled = false;
    String needleFileName = null;
    String needleCode = null;
    int maxCount = -1;

    boolean help;

    int grep(String languageName, String[] haystackNames)
    {
        final Language language = Language.findByName(languageName);

        final ITokenizer tokenizer = new AntlrTokenizer(language);

        final BlindLevel blindLevel = BlindLevel.findByName(blindLevelName);

        final IDetector detector = needleFileName == null
            ? TokenSequenceDetector.withNeedleFromCode(tokenizer, needleCode, blindLevel)
            : TokenSequenceDetector.withNeedleFromFile(tokenizer, needleFileName, blindLevel);

        debugprintln("traversing...");
        final Traverser traverser = new Traverser(detector, language::matchesExtension);
        final List<Clone> clones = Arrays.stream(haystackNames)
            .map(Paths::get)
            .map(traverser::traverse)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        debugprintln("finish.");
        debugprintln(clones.size() + " clone(s) found.");

        printResult(clones, language);
        return clones.isEmpty()? 1: 0;
    }

    void printResult(List<Clone> clones, Language language)
    {
        debugprint("printing...");
        if(isCountEnabled)
        {
            System.out.println(clones.size());
        }
        else
        {
            new GrepPrinter(clones)
                .println(
                    new GrepPrinter.Option(language)
                        .enableFileName(true)
                        .enableLine(isLineNumberEnabled)
                        .enableCode(!isFileNameOnlyEnabled)
                        .enableEscape(isEscapeEnabled)
                );
        }
        debugprintln("finish.");
    }
}

