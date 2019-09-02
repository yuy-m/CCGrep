package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jp.ac.osaka_u.ist.sel.ccgrep.CCGrepException;
import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.IParser;
import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;

public class TokenSequenceDetector implements IDetector
{
    private final ITokenizer tokenizer;
    private final IParser<GrepToken> matcher;
    private final BlindLevel blindLevel;
    private final boolean isFileMatchingEnabled;
    private final boolean isNoOverlapEnabled;

    private final HashMap<String, String> defaultIdmap = new HashMap<>();

    public TokenSequenceDetector(
        ITokenizer tokenizer, List<GrepToken> query,
        BlindLevel blindLevel, List<String> fixedIds,
        boolean isFileMatchingEnabled,
        boolean isNoOverlapEnabled
    ) throws CCGrepException
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        this.matcher = new RegexDetectCompiler(tokenizer.getLanguage()).compile(query);
        fixedIds.forEach(id -> defaultIdmap.put(id, id));
        this.isFileMatchingEnabled = isFileMatchingEnabled;
        this.isNoOverlapEnabled = isNoOverlapEnabled;
    }

    @Override
    public CloneList detect(final String targetFileName, int maxCount)
    {
        debugLogger.print(" tokenizing " + targetFileName + "...");
        return tokenizer.extractFromFile(targetFileName).map(targetResult -> {
            final List<GrepToken> hTokens = targetResult.tokens;
            final GrepCode hCode = targetResult.code;

            debugLogger.print("(" + hTokens.size() + " tokens),detecting...");

            List<Clone> clones;
            if(isFileMatchingEnabled)
            {
                final Clone clone = matchClone(hCode, hTokens, 0);
                clones = clone != null
                    && clone.getStartTokenIndex() == hTokens.get(0).getTokenIndex()
                    && clone.getEndTokenIndex() == hTokens.get(hTokens.size() - 1).getTokenIndex()
                    ? Collections.singletonList(clone)
                    : Collections.emptyList();
            }
            else
            {
                clones = new ArrayList<>();
                for(int idx = 0; idx < hTokens.size() && (maxCount < 0 || clones.size() < maxCount); ++idx)
                {
                    final Clone clone = matchClone(hCode, hTokens, idx);
                    if(clone == null)
                    {
                        continue;
                    }
                    clones.add(clone);

                    if(!isNoOverlapEnabled)
                    {
                        continue;
                    }
                    while(idx < hTokens.size() && hTokens.get(idx) != clone.end)
                    {
                        ++idx;
                    }
                }
            }
            debugLogger.println("(" + clones.size() + " clones)finish.");
            return new CloneList(hCode, clones);
        })
        .orElseGet(() -> CloneList.empty(targetFileName));
    }

    private Clone matchClone(GrepCode code, List<GrepToken> target, int index)
    {
        final Map<String, String> idmap = blindLevel.createConstraint(defaultIdmap);
        final GrepRange range = new GrepRange(target, index, blindLevel, idmap);
        return matcher.matches(range)
            ? new Clone(code, range.getMatchedFirst(), range.getMatchedLast())
            : null;
    }
}
