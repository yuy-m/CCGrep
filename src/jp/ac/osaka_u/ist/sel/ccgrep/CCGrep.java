package jp.ac.osaka_u.ist.sel.ccgrep;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.GrepPrinter;
import jp.ac.osaka_u.ist.sel.ccgrep.Language;
import jp.ac.osaka_u.ist.sel.ccgrep.TokenSequenceDetector;

public class CCGrep
{
    static boolean DEBUG = false;
    public static void main(String[] args)
    {
        final Frontend fe = Frontend.process(args);
        if(fe == null)
        {
            System.exit(2);
        }
        else if(fe.isHelpEnabled)
        {
            System.exit(0);
        }
        DEBUG = fe.isLogEnabled;
        final CCGrep ccgrep = new CCGrep(fe);

        final long st = System.nanoTime();
        final int returnCode = ccgrep.grep();
        final long et = System.nanoTime();

        final long milli = (et - st) / 1000000;
        // System.err.println((milli / 1000.0) + " sec");

        System.exit(returnCode);
    }

    static void debugprint(Object msg)
    {
        if(DEBUG)
        {
            System.err.print(msg.toString());
        }
    }

    static void debugprintln(Object msg)
    {
        if(DEBUG)
        {
            System.err.println(msg.toString());
        }
    }

    static void debugprintln()
    {
        if(DEBUG)
        {
            System.err.println();
        }
    }

    private final Frontend frontend;

    CCGrep(Frontend frontend)
    {
        this.frontend = frontend;
    }

    public int grep()
    {
        final Language language = findLanguage();
        if(language == null)
        {
            return 2;
        }
        debugprintln("language: " + language);

        final ITokenizer tokenizer = new AntlrTokenizer(language);

        final BlindLevel blindLevel = BlindLevel.findByName(frontend.blindLevelName);
        debugprintln("blind level: " + blindLevel);

        final IDetector detector = frontend.needleFileName == null
            ? TokenSequenceDetector.withNeedleFromCode(tokenizer, frontend.needleCode, blindLevel)
            : TokenSequenceDetector.withNeedleFromFile(tokenizer, frontend.needleFileName, blindLevel);

        debugprintln("traversing...");
        final Traverser traverser = new Traverser(detector, frontend.isRecursiveEnabled, language::matchesExtension);
        final List<CloneList> clones = frontend.haystackNames.stream()
            .map(Paths::get)
            .map(traverser::traverse)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        debugprintln("finish.");
        debugprintln(clones.size() + " clone(s) found.");

        printResult(clones, language);
        return clones.isEmpty()? 1: 0;
    }

    private Language findLanguage()
    {
        if(frontend.languageName != null)
        {
            final Language l = Language.findByName(frontend.languageName);
            if(l == null)
            {
                System.err.println("The language " + frontend.languageName + " is not supported.");
            }
            return l;
        }
        else if(frontend.needleFileName != null)
        {
            final Language l = Language.findByFileNameWithExtension(frontend.needleFileName);
            if(l == null)
            {
                System.err.println("No language found from the extension of " + frontend.needleFileName);
                System.err.println("specify languge by `-l LANG` or `-f FILE.EXT`");
            }
            return l;
        }
        else
        {
            return Language.findByName("java");
        }
    }

    private void printResult(List<CloneList> clones, Language language)
    {
        debugprintln("printing...");
        if(frontend.printOption.contains("c"))
        {
            System.out.println(clones.size());
        }
        else
        {
            new GrepPrinter(clones)
                .println(new PrintOption(language, frontend.printOption));
        }
        debugprintln("finish.");
    }
}

