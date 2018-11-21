package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.*;
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
        CCGrep.debugprint("tokenizing needle...");
        final List<GrepToken> needle = tokenizer.extractAsListFromString(needleCode);
        CCGrep.debugprintln("finish.");
        if(needle.size() == 0)
        {
            CCGrep.debugprintln("Error: No token found in the needle.");
            System.exit(1);
        }
        CCGrep.debugprintln("the needle has " + needle.size() + " token(s).");
        return new TokenSequenceDetector(tokenizer, needle, blindLevel);
    }

    public static TokenSequenceDetector withNeedleFromFile(ITokenizer tokenizer, String needleName, BlindLevel blindLevel)
    {
        CCGrep.debugprint("tokenizing needle...");
        final List<GrepToken> needle = tokenizer.extractAsListFromFile(needleName);
        CCGrep.debugprintln("finish.");
        if(needle.size() == 0)
        {
            CCGrep.debugprintln("Error: No token found in the needle.");
            System.exit(1);
        }
        CCGrep.debugprintln("the needle has " + needle.size() + " token(s).");
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
            .mapToObj(idx -> haystack.subList(idx, idx + needle.size()))
            .filter(subHaystack -> submatch(subHaystack))
            .map(subHaystack ->
                new Clone(
                    subHaystack.get(0).getFileName(),
                    subHaystack.get(0),
                    subHaystack.get(needle.size() - 1)
                )
            )
            .forEachOrdered(clones::add);
        CCGrep.debugprintln("(" + clones.size() + ") finish.");
        return clones;
    }

    private boolean submatch(List<GrepToken> subHaystack)
    {
        final Map<String, GrepToken> idmap =
            blindLevel == BlindLevel.CONSISTENT
                ? new HashMap<>(): Collections.emptyMap();
        return IntStream.range(0, needle.size())
                .allMatch(idx -> getLanguage()
                            .checkTokenEquality(needle.get(idx), subHaystack.get(idx), blindLevel, idmap));
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}
