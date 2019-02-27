package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.Optional;
import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;


public class CCGrep
{
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
        catch(CCGrepException e)
        {
            System.err.println(e.getMessage());
        }
        System.exit(returnCode);
    }

    private final Frontend frontend;

    private final Language language;
    private final BlindLevel blindLevel;
    private GrepCode needle;
    private final IDetector detector;

    public CCGrep(Frontend frontend) throws CCGrepException
    {
        this.frontend = frontend;
        this.language = findLanguage();
        debugLogger.println("language: " + language);

        this.blindLevel = BlindLevel.findByName(frontend.blindLevelName)
            .orElseThrow(() ->
                new CCGrepException("BlindLevel `" + frontend.blindLevelName + "` is not supported.")
            );
        debugLogger.println("blind level: " + blindLevel);

        final ITokenizer tokenizer = new AntlrTokenizer(language);
        this.detector = createDetector(tokenizer);
    }

    private Language findLanguage() throws CCGrepException
    {
        if(frontend.languageName != null)
        {
            return Language.findByName(frontend.languageName)
                .orElseThrow(() -> new CCGrepException("The language " + frontend.languageName + " is not supported."));
        }
        else if(frontend.needleType == Frontend.NEEDLE_FILE)
        {
            return Language.findByFileNameWithExtension(frontend.needle)
                .orElseThrow(() -> new CCGrepException(
                    "No language found from the extension of " + frontend.needle
                    + System.lineSeparator() + "specify languge by `-l LANG` or `-f FILE.EXT`"
                ));
        }
        else
        {
            return Language.getDefaultLanguage();
        }
    }

    private IDetector createDetector(ITokenizer tokenizer) throws CCGrepException
    {
        debugLogger.print("tokenizing query...");
        final ITokenizer.Result nResult = tokenizeNeedle(tokenizer, frontend.needleType, frontend.needle);
        this.needle = nResult.code;
        final List<GrepToken> nTokens = nResult.tokens;

        debugLogger.println("finish.");
        nTokens.forEach(debugLogger::println);

        debugLogger.println("The query has " + nTokens.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, nTokens, blindLevel, frontend.fixedIds, frontend.isFileMatchingEnabled);
    }

    private ITokenizer.Result tokenizeNeedle(ITokenizer tokenizer, int needleType, String needle) throws CCGrepException
    {
        final Optional<ITokenizer.Result> tResult =
            needleType == Frontend.NEEDLE_CODE? Optional.of(tokenizer.extractFromString(needle))
          : needleType == Frontend.NEEDLE_FILE? tokenizer.extractFromFile(needle)
          : needleType == Frontend.NEEDLE_STDIN? tokenizer.extractFromFile("-")
          : null;
        return tResult.orElseThrow(() -> new CCGrepException("ccgrep: Cannot tokenize the query"));
    }

    public int grep()
    {
        debugLogger.println("traversing...");
        final CloneList.Statistic stat =
            new Traverser(
                detector, frontend.isRecursiveEnabled,
                language.getExtensions(), frontend.includePatterns, frontend.excludePatterns,
                createPrinter()
            )
            .traverse(frontend.haystackNames, frontend.maxCount);
        debugLogger.println("finish.");
        debugLogger.println(stat.countAllClone() + " clone(s) found.");

        if(frontend.isTimeEnabled)
        {
            System.err.printf("%.3f\n", stat.countTimeAsSeconds());
        }

        return stat.countAllClone() == 0? 1: 0;
    }

    private IPrinter createPrinter()
    {
        final PrintOption po = new PrintOption(language, frontend.printOption);
        return frontend.isJsonEnabled? new JsonPrinter(po, needle, language, blindLevel)
             : frontend.isXmlEnabled? new XmlPrinter(po, needle, language, blindLevel)
             : new GrepPrinter(po);
    }
}

