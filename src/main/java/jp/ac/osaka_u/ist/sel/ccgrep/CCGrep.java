package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.Optional;
import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;


public class CCGrep
{
    private final CCGrepOption option;

    private final Language language;
    private final BlindLevel blindLevel;
    private GrepCode needle;
    private final IDetector detector;

    public CCGrep(CCGrepOption option) throws CCGrepException
    {
        this.option = option;
        this.language = findLanguage();
        debugLogger.println("language: " + language);

        this.blindLevel = BlindLevel.findByName(option.blindLevelName)
            .orElseThrow(() ->
                new CCGrepException("BlindLevel `" + option.blindLevelName + "` is not supported.")
            );
        debugLogger.println("blind level: " + blindLevel);

        final ITokenizer tokenizer = new AntlrTokenizer(language);
        this.detector = createDetector(tokenizer);
    }

    private Language findLanguage() throws CCGrepException
    {
        if(option.languageName != null)
        {
            return Language.findByName(option.languageName)
                .orElseThrow(() -> new CCGrepException("The language " + option.languageName + " is not supported."));
        }
        else if(option.needleType == CCGrepOption.NEEDLE_FILE)
        {
            return Language.findByFileNameWithExtension(option.needle)
                .orElseThrow(() -> new CCGrepException(
                    "No language found from the extension of " + option.needle
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
        final ITokenizer.Result nResult = tokenizeNeedle(tokenizer, option.needleType, option.needle);
        this.needle = nResult.code;
        final List<GrepToken> nTokens = nResult.tokens;

        debugLogger.println("finish.");
        nTokens.forEach(debugLogger::println);

        debugLogger.println("The query has " + nTokens.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, nTokens, blindLevel, option.fixedIds, option.isFileMatchingEnabled);
    }

    private ITokenizer.Result tokenizeNeedle(ITokenizer tokenizer, int needleType, String needle) throws CCGrepException
    {
        final Optional<ITokenizer.Result> tResult =
            needleType == CCGrepOption.NEEDLE_CODE? Optional.of(tokenizer.extractQueryFromString(needle))
          : needleType == CCGrepOption.NEEDLE_FILE? tokenizer.extractQueryFromFile(needle)
          : needleType == CCGrepOption.NEEDLE_STDIN? tokenizer.extractQueryFromFile("-")
          : null;
        return tResult.orElseThrow(() -> new CCGrepException("ccgrep: Cannot tokenize the query"));
    }

    public int grep()
    {
        debugLogger.println("traversing...");
        final CloneList.Statistic stat =
            new Traverser(
                detector, option.isRecursiveEnabled,
                option.isIgnoreExtensionEnabled? null: language.getExtensions(),
                option.includePatterns, option.excludePatterns,
                createPrinter(),
                option.isParallelEnabled
            )
            .traverse(option.haystackNames, option.maxCount);
        debugLogger.println("finish.");
        debugLogger.println(stat.countAllClone() + " clone(s) found.");

        if(option.isTimeEnabled)
        {
            System.err.printf("%.3f\n", stat.countTimeAsSeconds());
        }

        return stat.countAllClone() == 0? 1: 0;
    }

    private IPrinter createPrinter()
    {
        final PrintOption po = new PrintOption(language, option.printOption);
        return option.isJsonEnabled? new JsonPrinter(po, needle, language, blindLevel)
             : option.isXmlEnabled? new XmlPrinter(po, needle, language, blindLevel)
             : new GrepPrinter(po);
    }
}

