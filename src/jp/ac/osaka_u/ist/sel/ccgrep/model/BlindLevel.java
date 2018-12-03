package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.*;
import java.util.function.*;


public enum BlindLevel
{
    NONE(
        new String[]{"none"},
        0,
        __ -> Collections.emptyMap(),
        (t1, t2, __) -> t1.equals(t2)
    ),
    /**
     * a+a matches b+b
     *     not matches b+c
     * a+b matches c+b
     *     not matches c+c
     */
    CONSISTENT(
        new String[]{"consistent", "doubly", ""},
        50,
        HashMap::new,
        (t1, t2, c) -> {
            if(c.containsValue(t2.getText()))
            {
                return false;
            }
            else
            {
                c.put(t1.getText(), t2.getText());
                return true;
            }
        }
    ),
    /**
     * a+a matches b+b
     *     not matches b+c
     * a+b matches c+b, c+c
     */
    SINGLY(
        new String[]{"singly"},
        75,
        HashMap::new,
        (t1, t2, c) -> {
            c.put(t1.getText(), t2.getText());
            return true;
        }
    ),
    FULL(
        new String[]{"full"},
        100,
        HashMap::new,
        (_1, _2, _3) -> true
    );

    private final String[] names;
    public final int value;
    private final EqualityStrategy equalityStrategy;
    private final Function<Map<String, String>, Map<String, String>> constraintCreater;
    BlindLevel(
        String[] names, int value,
        Function<Map<String, String>, Map<String, String>> constraintCreater,
        EqualityStrategy equalityStrategy)
    {
        this.names = names;
        this.value = value;
        this.equalityStrategy = equalityStrategy;
        this.constraintCreater = constraintCreater;
    }

    public static BlindLevel findByName(String levelName)
    {
        return Arrays.stream(BlindLevel.values())
            .filter(bl -> Arrays.stream(bl.names).anyMatch(levelName::equalsIgnoreCase))
            .findFirst()
            .orElse(null);
    }

    public boolean checkTokenEquality(GrepToken token1, GrepToken token2, Map<String, String> constraint)
    {
        return equalityStrategy.test(token1, token2, constraint);
    }

    public Map<String, String> createConstraint(Map<String, String> from)
    {
        return constraintCreater.apply(from);
    }

    private interface EqualityStrategy
    {
        // assert token1.equals(token2);
        // equals -> test, !test -> !equals
        boolean test(GrepToken token1, GrepToken token2, Map<String, String> constraint);
    }
}
