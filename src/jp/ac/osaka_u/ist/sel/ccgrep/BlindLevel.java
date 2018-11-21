package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Supplier;


public enum BlindLevel
{
    NONE(
        0,
        (t1, t2, c) -> t1.equals(t2)
    ),
    CONSISTENT(
        50,
        (t1, t2, c) -> {
            final GrepToken t = c.putIfAbsent(t1.getText(), t2);
            return  t == null? true: t.equals(t2);
        }
    ),
    FULL(
        100,
        (t1, t2, c) -> true
    );

    public final int value;
    private final EqualityStrategy equalityStrategy;
    BlindLevel(int value, EqualityStrategy equalityStrategy)
    {
        this.value = value;
        this.equalityStrategy = equalityStrategy;
    }

    public static BlindLevel findByName(String levelName)
    {
        if("none".equalsIgnoreCase(levelName))
        {
            return BlindLevel.NONE;
        }
        else if("consistent".equalsIgnoreCase(levelName) || "".equals(levelName))
        {
            return BlindLevel.CONSISTENT;
        }
        else if("full".equalsIgnoreCase(levelName))
        {
            return BlindLevel.FULL;
        }
        else
        {
            throw new NoSuchElementException("BlindLevel `" + levelName + "` is not supported.");
        }
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
