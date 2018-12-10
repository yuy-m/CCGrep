package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.*;
import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.debugLogger;

public class TokenSequenceDetector implements IDetector
{
    final ITokenizer tokenizer;
    private final IParser<GrepToken> matcher;
    final BlindLevel blindLevel;

    private final HashMap<String, String> defaultIdmap = new HashMap<>();

    public TokenSequenceDetector(ITokenizer tokenizer, List<GrepToken> needle, BlindLevel blindLevel, List<String> fixedIds)
    {
        this.tokenizer = tokenizer;
        this.blindLevel = blindLevel;
        this.matcher = compile(needle, tokenizer.getLanguage());
        fixedIds.forEach(id -> defaultIdmap.put(id, id));
    }

    @Override
    public CloneList detect(final String haystackFileName, int maxCount)
    {
        debugLogger.print(" tokenizing " + haystackFileName + "...");
        final ITokenizer.TokenizerResult haystackResult = tokenizer.extractFromFile(haystackFileName);
        final List<GrepToken> hTokens = haystackResult.tokens;
        final GrepCode hCode = haystackResult.code;

        debugLogger.print("(" + hTokens.size() + " tokens),detecting...");

        final Stream<Clone> stream = IntStream.range(0, hTokens.size())
                .mapToObj(idx -> hTokens.subList(idx, hTokens.size()))
                .map(subHaystack -> matchClone(hCode, subHaystack))
                .filter(Objects::nonNull);

        final List<Clone> clones =
            (maxCount < 0? stream: stream.limit(maxCount))
                .collect(Collectors.toList());

        debugLogger.println("(" + clones.size() + " clones)finish.");

        return new CloneList(hCode, clones);
    }

    private Clone matchClone(GrepCode code, List<GrepToken> subHaystack)
    {
        final Map<String, String> idmap = blindLevel.createConstraint(defaultIdmap);
        final List<GrepToken> l = matcher.matches(new GrepRange(subHaystack, blindLevel, idmap));
        return l == null ? null : new Clone(code, l.get(0), l.get(l.size() - 1));
    }

    private Language getLanguage()
    {
        return tokenizer.getLanguage();
    }

    private static IParser<GrepToken> compile(List<GrepToken> needle, Language language)
    {
        final One<GrepToken> isSpSeq = new One<>(r -> r.front().getType() == language.specialSeq());

        final Mapper<GrepToken, IParser<GrepToken>> spSeqCompiler =
            new Mapper<>(
                new Sequence<GrepToken>(Arrays.asList(
                    isSpSeq,
                    new Discard<>(new Repeat<>(isSpSeq)),
                    new Lookahead<>(One.any())
                )),
                n -> new AnyTokenSequence(
                        language,
                        new One<>(n.asSeq().<GrepToken>getAsOne(2).get())
                    )
            );

        final Mapper<GrepToken, IParser<GrepToken>> normalCompiler =
            new Mapper<>(
                new One<GrepToken>(a -> true),
                n -> new One<>(n.<GrepToken>asOne().get())
            );

        final Mapper<GrepToken, IParser<GrepToken>> compiler =
            new Mapper<>(
                new Repeat<GrepToken>(new Select<>(Arrays.asList(spSeqCompiler, normalCompiler))),
                n -> new Sequence<>(n.<IParser<GrepToken>>asSeq().getList())
            );
        return compiler.parse(new Range<GrepToken>(needle)); //*/

        /*final Iterator<GrepToken> it = needle.iterator();
        final List<IParser<GrepToken>> list = new ArrayList<>();
        while (it.hasNext())
        {
            final GrepToken t = it.next();
            if (t.getType() == language.specialSeq())
            {
                final GrepToken t1 = it.next();
                final IParser<GrepToken> am = new One<>(t1);
                list.add(new AnyTokenSequence(language, am));
                list.add(am);
            }
            else
            {
                list.add(new One<GrepToken>(t));
            }
        }
        return new Sequence<>(list);//*/
    }
}
