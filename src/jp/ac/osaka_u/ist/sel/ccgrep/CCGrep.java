package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.*;
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
        DEBUG = fe.isLogEnabled;
        final CCGrep ccgrep = new CCGrep(fe);

        final long st = System.nanoTime();
        final int returnCode = ccgrep.grep();
        final long et = System.nanoTime();

        final long milli = (et - st) / 1000000;
        // System.err.println((milli / 1000.0) + " sec");

        System.exit(returnCode);
    }

    private final Frontend frontend;

    CCGrep(Frontend frontend)
    {
        this.frontend = frontend;
    }

    int grep()
    {
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

        final String[] fixedIds = frontend.fixedIds.split("\\|");

        final IDetector detector = frontend.needleFileName == null
            ? TokenSequenceDetector.withNeedleFromCode(tokenizer, frontend.needleCode, blindLevel, fixedIds)
            : TokenSequenceDetector.withNeedleFromFile(tokenizer, frontend.needleFileName, blindLevel, fixedIds);
        if(detector == null)
        {
            return 2;
        }

        debugLogger.println("traversing...");
        final Traverser traverser = new Traverser(detector, frontend.isRecursiveEnabled, language::matchesExtension);
        final List<CloneList> clones = traverser.traverse(frontend.haystackNames);
        debugLogger.println("finish.");
        debugLogger.println(clones.size() + " clone(s) found.");

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
        debugLogger.println("printing...");
        final IPrinter printer = frontend.isJsonEnabled
            ? new JsonPrinter(clones)
            : new GrepPrinter(clones);
        printer.println(new PrintOption(language, frontend.printOption));
        debugLogger.println("finish.");
    }
}

