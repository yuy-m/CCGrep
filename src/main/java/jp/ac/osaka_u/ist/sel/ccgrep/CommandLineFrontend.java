package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.Optional;
import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.*;


public class CommandLineFrontend
{
    public static void main(String[] args)
    {
        final CCGrepOption option = CommandLineProcessor.process(args);
        if(option == null)
        {
            System.exit(2);
        }
        else if(option.isHelpEnabled)
        {
            System.exit(0);
        }
        debugLogger.enable(option.isLogEnabled);
        errorLogger.enable(option.isErrorMessageEnabled);

        int returnCode = 2;
        try{
            final CommandLineFrontend ccgrep = new CommandLineFrontend(option);
            returnCode = ccgrep.grep();
        }
        catch(CCGrepException e)
        {
            System.err.println(e.getMessage());
        }
        System.exit(returnCode);
    }

    private final CCGrepOption option;

    private final Language language;
    private final BlindLevel blindLevel;
    private GrepCode query;
    private final IDetector detector;

    public CommandLineFrontend(CCGrepOption option) throws CCGrepException
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
        else if(option.queryType == CCGrepOption.QUERY_FILE)
        {
            return Language.findByFileNameWithExtension(option.query)
                .orElseThrow(() -> new CCGrepException(
                    "No language found from the extension of " + option.query
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
        final ITokenizer.Result nResult = tokenizeQuery(tokenizer, option.queryType, option.query);
        this.query = nResult.code;
        final List<GrepToken> nTokens = nResult.tokens;

        debugLogger.println("finish.");
        nTokens.forEach(debugLogger::println);

        debugLogger.println("The query has " + nTokens.size() + " token(s).");
        return new TokenSequenceDetector(
            tokenizer, nTokens, blindLevel,
            option.fixedIds,
            option.isFileMatchingEnabled,
            option.isNoOverlapEnabled
        );
    }

    private ITokenizer.Result tokenizeQuery(ITokenizer tokenizer, int queryType, String query) throws CCGrepException
    {
        final Optional<ITokenizer.Result> tResult =
            queryType == CCGrepOption.QUERY_CODE? Optional.of(tokenizer.tokenizeQueryFromString(query))
          : queryType == CCGrepOption.QUERY_FILE? tokenizer.tokenizeQueryFromFile(query)
          : queryType == CCGrepOption.QUERY_STDIN? tokenizer.tokenizeQueryFromFile("-")
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
            .traverse(option.targetNames, option.maxCount);
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
        return option.isJsonEnabled? new JsonPrinter(po, query, language, blindLevel)
             : option.isXmlEnabled? new XmlPrinter(po, query, language, blindLevel)
             : new GrepPrinter(po);
    }
}

