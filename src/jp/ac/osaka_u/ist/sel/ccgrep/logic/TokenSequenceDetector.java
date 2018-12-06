package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.logger.Logger.debugLogger;


public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    private final List<GrepToken> needle;
    final BlindLevel blindLevel;

    private final HashMap<String, String> defaultIdmap = new HashMap<>();

    public TokenSequenceDetector(ITokenizer tokenizer, List<GrepToken> needle, BlindLevel blindLevel, List<String> fixedIds)
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        this.needle = needle;
        fixedIds.forEach(id -> defaultIdmap.put(id, id));
    }

    @Override
    public CloneList detect(final String haystackFileName)
    {
        debugLogger.print(" tokenizing " + haystackFileName + "...");
        final ITokenizer.TokenizerResult haystackResult = tokenizer.extractFromFile(haystackFileName);
        final List<GrepToken> hTokens = haystackResult.tokens;
        final GrepCode hCode = haystackResult.code;

        debugLogger.print("(" + hTokens.size() + " tokens),detecting...");

        final List<Clone> clones =
            IntStream.range(0, hTokens.size())
                .mapToObj(idx -> hTokens.subList(idx, hTokens.size()))
                .map(subHaystack -> matchClone(hCode, subHaystack))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        debugLogger.println("(" + clones.size() + " clones)finish.");

        return new CloneList(hCode, clones);
    }

    private Clone matchClone(GrepCode code, List<GrepToken> subHaystack)
    {
        final Map<String, String> idmap = blindLevel.createConstraint(defaultIdmap);

        // Chaotic Implementation!
        int needleIdx = 0;
        int haystackIdx = 0;
        for(;
            needleIdx < needle.size() && haystackIdx < subHaystack.size();
            ++needleIdx, ++haystackIdx
        )
        {
            if(needle.get(needleIdx).getType() == getLanguage().specialSeq())
            {
                do
                {
                    ++needleIdx;
                } while(needle.get(needleIdx).getType() == getLanguage().specialSeq());

                haystackIdx = matchSpecialSequence(needle.get(needleIdx), subHaystack, haystackIdx, idmap);
                if(haystackIdx == -1)
                {
                    return null;
                }
                else
                {
                    continue;
                }
            }
            if(!needle.get(needleIdx).matchesBlindly(subHaystack.get(haystackIdx), blindLevel, idmap))
            {
                return null;
            }
        }
        return needleIdx < needle.size()
            ? null
            : new Clone(
                code,
                subHaystack.get(0),
                subHaystack.get(haystackIdx - 1)
            );
        /*return IntStream.range(0, needle.size())
                .allMatch(idx -> needle.get(idx).matchesBlindly(subHaystack.get(idx), blindLevel, idmap)); //*/
    }

    private int matchSpecialSequence(GrepToken nt, List<GrepToken> subHaystack, int haystackIdx, Map<String, String> idmap)
    {
        for(final ArrayDeque<Integer> brackets = new ArrayDeque<>();
            haystackIdx < subHaystack.size();
            ++haystackIdx)
        {
            final GrepToken ht = subHaystack.get(haystackIdx);
            if(brackets.isEmpty())
            {
                if(nt.matchesBlindly(ht, blindLevel, idmap))
                {
                    return haystackIdx;
                }
                else if(getLanguage().isCloseBracket(ht.getType()))
                {
                    return -1;
                }
                else if(getLanguage().isOpenBracket(ht.getType()))
                {
                    brackets.addLast(ht.getType());
                }
            }
            else if(getLanguage().isCloseBracket(ht.getType()))
            {
                final int lastBracket = brackets.removeLast();
                if(!getLanguage().isBracketPair(lastBracket, ht.getType()))
                {
                    return -1;
                }
            }
            else if(getLanguage().isOpenBracket(ht.getType()))
            {
                brackets.addLast(ht.getType());
            }
        }
        return -1;
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}
