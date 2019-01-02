package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.ac.osaka_u.ist.sel.ccgrep.CCGrepException;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.IParser;
import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;

public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    private final IParser<GrepToken> matcher;
    final BlindLevel blindLevel;

    private final HashMap<String, String> defaultIdmap = new HashMap<>();

    public TokenSequenceDetector(
        ITokenizer tokenizer, List<GrepToken> needle,
        BlindLevel blindLevel, List<String> fixedIds
    ) throws CCGrepException
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        RegexDetectCompiler.setLanguage(tokenizer.getLanguage());
        this.matcher = RegexDetectCompiler.compile(needle);
        fixedIds.forEach(id -> defaultIdmap.put(id, id));
    }

    @Override
    public CloneList detect(final String haystackFileName, int maxCount)
    {
        debugLogger.print(" tokenizing " + haystackFileName + "...");
        return tokenizer.extractFromFile(haystackFileName)
            .map(haystackResult -> {
                final List<GrepToken> hTokens = haystackResult.tokens;
                final GrepCode hCode = haystackResult.code;

                debugLogger.print("(" + hTokens.size() + " tokens),detecting...");

                final Stream<Clone> stream = IntStream.range(0, hTokens.size())
                        .mapToObj(idx -> matchClone(hCode, hTokens, idx))
                        .filter(Objects::nonNull);

                final List<Clone> clones =
                    (maxCount < 0? stream: stream.limit(maxCount))
                        .collect(Collectors.toList());

                debugLogger.println("(" + clones.size() + " clones)finish.");

                return new CloneList(hCode, clones);
            })
            .orElseGet(() -> CloneList.empty(haystackFileName));
    }

    private Clone matchClone(GrepCode code, List<GrepToken> haystack, int index)
    {
        final Map<String, String> idmap = blindLevel.createConstraint(defaultIdmap);
        final GrepRange range = new GrepRange(haystack, index, blindLevel, idmap);
        return matcher.matches(range)
            ? new Clone(code, range.getMatchedFirst(), range.getMatchedLast())
            : null;
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}
