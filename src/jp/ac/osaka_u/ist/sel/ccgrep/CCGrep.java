package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;


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

        int returnCode = 2;
        try{
            final CCGrep ccgrep = new CCGrep(fe);
            returnCode = ccgrep.grep();
        }
        catch(PreparingException e)
        {
            System.err.println(e.getMessage());
        }
        System.exit(returnCode);
    }

    private final Frontend frontend;
    private final long[] times = new long[3];

    private final Language language;
    private final BlindLevel blindLevel;
    private GrepCode needle;
    private final IDetector detector;

    public CCGrep(Frontend frontend) throws PreparingException
    {
        times[0] = System.nanoTime();
        this.frontend = frontend;
        final long t0 = System.nanoTime();
        this.language = findLanguage();
        debugLogger.println("language: " + language);

        this.blindLevel = BlindLevel.findByName(frontend.blindLevelName)
            .orElseThrow(() ->
                new PreparingException("BlindLevel `" + frontend.blindLevelName + "` is not supported.")
            );
        debugLogger.println("blind level: " + blindLevel);

        final ITokenizer tokenizer = new AntlrTokenizer(language);
        this.detector = createDetector(tokenizer);

        times[0] = System.nanoTime() - times[0];
    }

    private static double secondTime(long t)
    {
        return t / 1.0e+9;
    }

    private Language findLanguage() throws PreparingException
    {
        if(frontend.languageName != null)
        {
            return Language.findByName(frontend.languageName)
                .orElseThrow(() -> new PreparingException("The language " + frontend.languageName + " is not supported."));
        }
        else if(frontend.needleFileName != null)
        {
            return Language.findByFileNameWithExtension(frontend.needleFileName)
                .orElseThrow(() -> new PreparingException(
                    "No language found from the extension of " + frontend.needleFileName
                    + System.lineSeparator() + "specify languge by `-l LANG` or `-f FILE.EXT`"
                ));
        }
        else
        {
            return Language.getDefaultLanguage();
        }
    }

    private IDetector createDetector(ITokenizer tokenizer) throws PreparingException
    {
        debugLogger.print("tokenizing query...");
        final ITokenizer.TokenizerResult needleResult = frontend.needleFileName == null
            ? tokenizer.extractFromString(frontend.needleCode)
            : tokenizer.extractFromFile(frontend.needleFileName);
        this.needle = needleResult.code;
        final List<GrepToken> nTokens = needleResult.tokens;

        debugLogger.println("finish.");
        if(nTokens.size() == 0)
        {
            throw new PreparingException("Error: No token found in the query.");
        }

        final int specialSeq = tokenizer.getLanguage().specialSeq();
        if(nTokens.get(0).getType() == specialSeq
            || nTokens.get(nTokens.size() - 1).getType() == specialSeq)
        {
            throw new PreparingException("Error: Query cannot start/end with special token `$$`.");
        }

        nTokens.forEach(debugLogger::println);

        debugLogger.println("The query has " + nTokens.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, nTokens, blindLevel, frontend.fixedIds);
    }

    private static double getUsingMemoryMB()
    {
        return(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
    }

    private static final boolean MEMORY = false;
    public int grep()
    {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        if(MEMORY) System.err.printf("%7.3f\n", getUsingMemoryMB());

        times[1] = System.nanoTime();

        debugLogger.println("traversing...");
        final List<CloneList> clones = new Traverser(detector, frontend.isRecursiveEnabled, language::matchesExtension)
                                    .traverse(frontend.haystackNames, frontend.maxCount);
        debugLogger.println("finish.");
        debugLogger.println(clones.size() + " clone(s) found.");

        times[1] = System.nanoTime() - times[1];

        if(MEMORY) System.err.printf("%7.3f\n", getUsingMemoryMB());

        times[2] = System.nanoTime();
        printResult(clones, needle);
        times[2] = System.nanoTime() - times[2];

        if(MEMORY) System.err.printf("%7.3f\n", getUsingMemoryMB());

        if(frontend.isTimeEnabled)
        {
            final long all = times[0] + times[1] + times[2];
            System.err.print( "          :  seconds :     %\n");
            System.err.printf("preparing : %8.3f : %5.1f\n", secondTime(times[0]), 100.0 * times[0] / all);
            System.err.printf("detecting : %8.3f : %5.1f\n", secondTime(times[1]), 100.0 * times[1] / all);
            System.err.printf(" printing : %8.3f : %5.1f\n", secondTime(times[2]), 100.0 * times[2] / all);
            System.err.printf("      all : %8.3f : %5.1f\n", secondTime(all), 100.0);
        }

        return clones.stream().filter(cl -> !cl.isEmpty()).count() == 0? 1: 0;
    }

    private void printResult(List<CloneList> clones, GrepCode needle)
    {
        debugLogger.println("printing...");
        final IPrinter printer = frontend.isJsonEnabled
            ? new JsonPrinter(clones, needle, language, blindLevel)
            : new GrepPrinter(clones);
        printer.println(new PrintOption(language, frontend.printOption));
        debugLogger.println("finish.");
    }

    @SuppressWarnings("serial")
    private static class PreparingException extends Exception
    {
        PreparingException(String msg)
        {
            super(msg);
        }
    }
}

