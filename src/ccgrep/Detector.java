package ccgrep;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Detector
{
    final Tokenizer tokenizer;
    final List<GrepToken> needle;

    Detector(Tokenizer tokenizer, String needleFileName)
    {
        this.tokenizer = tokenizer;
        CCGrep.debugprint("tokenizing needle...");
        this.needle = tokenizer.extract(needleFileName);
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

    public List<Clone> detect(final String haystackFileName)
    {
        CCGrep.debugprint(" detecting " + haystackFileName + "...");
        final List<GrepToken> haystack = tokenizer.extract(haystackFileName);
        CCGrep.debugprint("(" + haystack.size() + ")");
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