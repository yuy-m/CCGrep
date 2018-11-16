package ccgrep;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    final List<GrepToken> needle;

    public TokenSequenceDetector(ITokenizer tokenizer, String needleName, boolean fromCode)
    {
        this.tokenizer = tokenizer;
        CCGrep.debugprint("tokenizing needle...");
        this.needle = fromCode
            ? tokenizer.extractAsListFromString(needleName)
            : tokenizer.extractAsListFromFile(needleName);
        // needle.forEach(CCGrep::printToken);
        CCGrep.debugprintln("finish.");
        if(needle.size() == 0)
        {
            CCGrep.debugprintln("Error: No token found in the needle.");
            System.exit(1);
        }
        // needle.forEach(CCGrep::debugprintln);
        CCGrep.debugprintln("the needle has " + needle.size() + " token(s).");
    }

    @Override
    public List<Clone> detect(final String haystackFileName)
    {
        CCGrep.debugprint(" tokenizing " + haystackFileName + "...");
        final List<GrepToken> haystack = tokenizer.extractAsListFromFile(haystackFileName);
        CCGrep.debugprint("(" + haystack.size() + ")");
        CCGrep.debugprint(" detecting " + haystackFileName + "...");
        if(needle.size() > haystack.size())
        {
            return Collections.emptyList();
        }
        final List<Clone> clones = new ArrayList<>();
        IntStream.range(0, haystack.size() - needle.size() + 1)
            .parallel()
            .filter(idx -> haystack.subList(idx, idx + needle.size()).equals(needle))
            .mapToObj(idx ->
                new Clone(
                    haystack.get(idx).getFileName(),
                    haystack.get(idx),
                    haystack.get(idx + needle.size() - 1)
                )
            )
            .forEachOrdered(clones::add);
        CCGrep.debugprintln("finish.");
        return clones;
    }

    public Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}