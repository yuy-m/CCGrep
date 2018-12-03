package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.logger.Logger.debugLogger;

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
        debugLogger.enable(fe.isLogEnabled);

        final CCGrep ccgrep = new CCGrep(fe);

        final int returnCode = ccgrep.grep();

        System.exit(returnCode);
    }

    private final Frontend frontend;

    CCGrep(Frontend frontend)
    {
        this.frontend = frontend;
    }

    private void printTimeln(String format, long s, long e)
    {
        if(frontend.isTimeEnabled)
        {
            printTime(format, s, e);
            System.err.println();
        }
    }

    private void printTime(String format, long s, long e)
    {
        if(frontend.isTimeEnabled)
        {
            final long milli = (e - s) / 1000000;
            System.err.print(String.format(format, milli / 1000.0));
        }
    }


    int grep()
    {
        final long t0 = System.nanoTime();
        final Language language = findLanguage();
        if(language == null)
        {
            return 2;
        }
        debugLogger.println("language: " + language);

        final ITokenizer tokenizer = new AntlrTokenizer(language);

        final BlindLevel blindLevel = BlindLevel.findByName(frontend.blindLevelName);
        if(blindLevel == null)
        {
            System.err.println("BlindLevel `" + frontend.blindLevelName + "` is not supported.");
            return 2;
        }
        debugLogger.println("blind level: " + blindLevel);

        final IDetector detector = frontend.needleFileName == null
            ? TokenSequenceDetector.withNeedleFromCode(tokenizer, frontend.needleCode, blindLevel, frontend.fixedIds)
            : TokenSequenceDetector.withNeedleFromFile(tokenizer, frontend.needleFileName, blindLevel, frontend.fixedIds);
        if(detector == null)
        {
            return 2;
        }

        final long t1 = System.nanoTime();
        printTimeln("preparing: %5f", t0, t1);

        debugLogger.println("traversing...");
        final Traverser traverser = new Traverser(detector, frontend.isRecursiveEnabled, language::matchesExtension);
        final List<CloneList> clones = traverser.traverse(frontend.haystackNames);
        debugLogger.println("finish.");
        debugLogger.println(clones.size() + " clone(s) found.");

        final long t2 = System.nanoTime();
        printTimeln("detecting: %5f", t1, t2);

        printResult(clones, ((TokenSequenceDetector)detector).needle, language, blindLevel);

        final long t3 = System.nanoTime();
        printTimeln("printing : %5f", t2, t3);

        printTimeln("all      : %5f", t0, t3);

        if(frontend.isTimeEnabled)
        {
            final long all = t3 - t0;
            System.err.println(String.format(
                "ratio: %2.1f:%2.1f:%2.1f",
                100.0 * (t1 - t0) / all,
                100.0 * (t2 - t1) / all,
                100.0 * (t3 - t2) / all
            ));
        }

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

    private void printResult(List<CloneList> clones, List<GrepToken> needle, Language language, BlindLevel blindLevel)
    {
        debugLogger.println("printing...");
        final IPrinter printer = frontend.isJsonEnabled
            ? new JsonPrinter(clones, needle, language, blindLevel)
            : new GrepPrinter(clones);
        printer.println(new PrintOption(language, frontend.printOption));
        debugLogger.println("finish.");
    }
}

