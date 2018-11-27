package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Supplier;


public enum BlindLevel
{
    NONE(
        new String[]{"none"},
        0,
        (t1, t2, c) -> t1.equals(t2)
    ),
    CONSISTENT(
        new String[]{"consistent", ""},
        50,
        (t1, t2, c) -> {
            if(c.containsKey(t1.getText()))
            {
                return c.get(t1.getText()).equals(t2);
            }
            else if(c.containsValue(t2))
            {
                return false;
            }
            else
            {
                c.put(t1.getText(), t2);
                return true;
            }
        }
    ),
    FULL(
        new String[]{"full"},
        100,
        (t1, t2, c) -> true
    );

    private final String[] names;
    public final int value;
    private final EqualityStrategy equalityStrategy;
    BlindLevel(String[] names, int value, EqualityStrategy equalityStrategy)
    {
        this.names = names;
        this.value = value;
        this.equalityStrategy = equalityStrategy;
    }

    public static BlindLevel findByName(String levelName)
    {
        return Arrays.stream(BlindLevel.values())
            .filter(bl -> Arrays.stream(bl.names).anyMatch(levelName::equalsIgnoreCase))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("BlindLevel `" + levelName + "` is not supported."));
    }

    public boolean checkTokenEquality(GrepToken token1, GrepToken token2, Map<String, GrepToken> constraint)
    {
        return equalityStrategy.test(token1, token2, constraint);
    }

    private interface EqualityStrategy
    {
        // assert token1.equals(token2);
        // equals -> test, !test -> !equals
        boolean test(GrepToken token1, GrepToken token2, Map<String, GrepToken> constraint);
    }
}
