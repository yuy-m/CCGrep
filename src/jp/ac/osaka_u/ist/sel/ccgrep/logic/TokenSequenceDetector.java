package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.logger.Logger.debugLogger;


public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    public final List<GrepToken> needle;
    final BlindLevel blindLevel;

    private final HashMap<String, String> defaultIdmap = new HashMap<>();

    public TokenSequenceDetector(ITokenizer tokenizer, List<GrepToken> needle, BlindLevel blindLevel, List<String> fixedIds)
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        this.needle = needle;
        fixedIds.forEach(id -> defaultIdmap.put(id, id));
        needle.forEach(debugLogger::println);
    }

    public static TokenSequenceDetector withNeedleFromCode(ITokenizer tokenizer, String needleCode, BlindLevel blindLevel, List<String> fixedIds)
    {
        return withNeedleImpl(tokenizer, blindLevel, fixedIds, () -> tokenizer.extractAsListFromString(needleCode));
    }

    public static TokenSequenceDetector withNeedleFromFile(ITokenizer tokenizer, String needleName, BlindLevel blindLevel, List<String> fixedIds)
    {
        return withNeedleImpl(tokenizer, blindLevel, fixedIds, () -> tokenizer.extractAsListFromFile(needleName));
    }

    private static TokenSequenceDetector withNeedleImpl(
        ITokenizer tokenizer, BlindLevel blindLevel, List<String> fixedIds, Supplier<List<GrepToken>> needleSupplier)
    {
        debugLogger.print("tokenizing query...");
        final List<GrepToken> needle = needleSupplier.get();
        debugLogger.println("finish.");
        if(needle.size() == 0)
        {
            System.err.println("Error: No token found in the query.");
            return null;
        }
        final int specialSeq = tokenizer.getLanguage().specialSeq();
        if(needle.get(0).getType() == specialSeq || needle.get(needle.size() - 1).getType() == specialSeq)
        {
            System.err.println("Error: Query cannot start/end with special token `$$`.");
            return null;
        }
        debugLogger.println("The query has " + needle.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, needle, blindLevel, fixedIds);
    }

    @Override
    public CloneList detect(final String haystackFileName)
    {
        debugLogger.print(" tokenizing " + haystackFileName + "...");
        final List<GrepToken> haystack = tokenizer.extractAsListFromFile(haystackFileName);
        debugLogger.print("(" + haystack.size() + ").detecting ...");
        if(needle.size() > haystack.size())
        {
            debugLogger.println("(0) finish.");
            return new CloneList(haystackFileName);
        }

        final List<Clone> clones =
            IntStream.range(0, haystack.size() - needle.size() + 1)
                .mapToObj(idx -> haystack.subList(idx, haystack.size()))
                .map(subHaystack -> matchClone(subHaystack))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        debugLogger.println("(" + clones.size() + ") finish.");
        return new CloneList(haystackFileName, clones);
    }

    private Clone matchClone(List<GrepToken> subHaystack)
    {
        final Map<String, String> idmap = blindLevel.createConstraint(defaultIdmap);

        // Chaotic Implementation!
        int needleIdx = 0;
        int haystackIdx = 0;
        outer:for(;
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
                    continue outer;
                }
            }
            if(!getLanguage()
                .checkTokenEquality(needle.get(needleIdx), subHaystack.get(haystackIdx), blindLevel, idmap))
            {
                return null;
            }
        }
        return needleIdx < needle.size()
            ? null
            : new Clone(
                subHaystack.get(0),
                subHaystack.get(haystackIdx - 1)
            );
        /*return IntStream.range(0, needle.size())
                .allMatch(idx -> getLanguage()
                            .checkTokenEquality(needle.get(idx), subHaystack.get(idx), blindLevel, idmap)); //*/
    }

    private int matchSpecialSequence(GrepToken nt, List<GrepToken> subHaystack, int haystackIdx, Map<String, String> idmap)
    {
        final ArrayDeque<Integer> brackets = new ArrayDeque<>();
        while(haystackIdx < subHaystack.size())
        {
            final GrepToken ht = subHaystack.get(haystackIdx);
            if(brackets.isEmpty())
            {
                if(getLanguage().checkTokenEquality(nt, ht, blindLevel, idmap))
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
                if(!getLanguage().isBracketPair(brackets.removeLast(), ht.getType()))
                {
                    return -1;
                }
            }
            else if(getLanguage().isOpenBracket(ht.getType()))
            {
                brackets.addLast(ht.getType());
            }
            ++haystackIdx;
        }
        return -1;
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}
