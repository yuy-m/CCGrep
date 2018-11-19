package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    final List<GrepToken> needle;

    final BlindLevel blindLevel;

    public TokenSequenceDetector(ITokenizer tokenizer, String needleName, boolean fromCode, int blindMode)
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindMode;
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
            .filter(idx -> submatch(haystack.subList(idx, idx + needle.size())))
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

    /*private boolean submatch(List<GrepToken> subHaystack)
    {
        return needle.equals(subHaystack);
    }*/

    private boolean submatch(List<GrepToken> subHaystack)
    {
        final Iterator<GrepToken> ni = needle.iterator();
        final Iterator<GrepToken> hi = subHaystack.iterator();
        final Map<String, GrepToken> idmap = blindLevel == BlindLevel.CONSISTENT? new HashMap<>(): null;
        while(ni.hasNext() && hi.hasNext())
        {
            final GrepToken nt = ni.next();
            final GrepToken ht = hi.next();
            switch(blindLevel)
            {
            case NONE:
                if(!nt.equals(ht))
                {
                    return false;
                }
                break;
            case FULL:
                if(
                    !getLanguage().isBlindableIdentifier(nt.getType())
                || !getLanguage().isBlindableIdentifier(ht.getType())
                )
                {
                    if(!nt.equals(ht))
                    {
                        return false;
                    }
                }
                break;
            case CONSISTENT:
                if(getLanguage().isBlindableIdentifier(nt.getType())
                    && getLanguage().isBlindableIdentifier(ht.getType()))
                {
                    idmap.putIfAbsent(nt.getText(), ht);
                    if(!idmap.get(nt.getText()).equals(ht))
                    {
                        return false;
                    }
                }
                else if(!nt.equals(ht))
                {
                    return false;
                }
                break;
            default:
                assert false;
            }
        }
        return true;
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }
}
