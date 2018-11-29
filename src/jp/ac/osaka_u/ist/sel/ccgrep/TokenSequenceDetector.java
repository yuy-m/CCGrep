package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    final List<GrepToken> needle;
    final BlindLevel blindLevel;

    public TokenSequenceDetector(ITokenizer tokenizer, List<GrepToken> needle, BlindLevel blindLevel)
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        this.needle = needle;
        needle.forEach(CCGrep::debugprintln);
    }

    public static TokenSequenceDetector withNeedleFromCode(ITokenizer tokenizer, String needleCode, BlindLevel blindLevel)
    {
        return withNeedleImpl(tokenizer, blindLevel, () -> tokenizer.extractAsListFromString(needleCode));
    }

    public static TokenSequenceDetector withNeedleFromFile(ITokenizer tokenizer, String needleName, BlindLevel blindLevel)
    {
        return withNeedleImpl(tokenizer, blindLevel, () -> tokenizer.extractAsListFromFile(needleName));
    }

    private static TokenSequenceDetector withNeedleImpl(ITokenizer tokenizer, BlindLevel blindLevel, Supplier<List<GrepToken>> needleSupplier)
    {
        CCGrep.debugprint("tokenizing query...");
        final List<GrepToken> needle = needleSupplier.get();
        CCGrep.debugprintln("finish.");
        if(needle.size() == 0)
        {
            System.err.println("Error: No token found in the query.");
            System.exit(2);
        }
        final int specialSeq = tokenizer.getLanguage().specialSeq();
        if(needle.get(0).getType() == specialSeq || needle.get(needle.size() - 1).getType() == specialSeq)
        {
            System.err.println("Error: Query cannot start/end with special token `$$`.");
            System.exit(2);
        }
        CCGrep.debugprintln("The query has " + needle.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, needle, blindLevel);
    }

    @Override
    public List<Clone> detect(final String haystackFileName)
    {
        CCGrep.debugprint(" tokenizing " + haystackFileName + "...");
        final List<GrepToken> haystack = tokenizer.extractAsListFromFile(haystackFileName);
        CCGrep.debugprint("(" + haystack.size() + ").detecting ...");
        if(needle.size() > haystack.size())
        {
            CCGrep.debugprintln("(0) finish.");
            return Collections.emptyList();
        }

        final List<Clone> clones = new ArrayList<>();
        IntStream.range(0, haystack.size() - needle.size() + 1)
            .parallel()
            .mapToObj(idx -> haystack.subList(idx, haystack.size()))
            .map(subHaystack -> submatch(subHaystack))
            .filter(Objects::nonNull)
            .forEachOrdered(clones::add);
        CCGrep.debugprintln("(" + clones.size() + ") finish.");
        return clones;
    }

    private Clone submatch(List<GrepToken> subHaystack)
    {
        final Map<String, GrepToken> idmap =
            blindLevel == BlindLevel.CONSISTENT
                ? new HashMap<>(): Collections.emptyMap();

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
                subHaystack.get(0).getFileName(),
                subHaystack.get(0),
                subHaystack.get(haystackIdx - 1)
            );
        /*return IntStream.range(0, needle.size())
                .allMatch(idx -> getLanguage()
                            .checkTokenEquality(needle.get(idx), subHaystack.get(idx), blindLevel, idmap)); //*/
    }

    private int matchSpecialSequence(GrepToken nt, List<GrepToken> subHaystack, int haystackIdx, Map<String, GrepToken> idmap)
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
