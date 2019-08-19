package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


public enum BlindLevel
{
    NONE(
        new String[]{"none"},
        Integer.MAX_VALUE,
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
    ONLY_TOKEN_TYPE(
        new String[]{},
        85,
        HashMap::new,
        (t1, t2, __) -> t1.getType() == t2.getType()
    ),
    FULL(
        new String[]{"full"},
        100,
        HashMap::new,
        (_1, _2, _3) -> true
    );

    private final String[] names;
    public final int value;
    private final Matcher matcher;
    private final Function<Map<String, String>, Map<String, String>> constraintCreater;
    BlindLevel(
        String[] names, int value,
        Function<Map<String, String>, Map<String, String>> constraintCreater,
        Matcher matcher)
    {
        this.names = names;
        this.value = value;
        this.constraintCreater = constraintCreater;
        this.matcher = matcher;
    }

    public static Optional<BlindLevel> findByName(String levelName)
    {
        return Arrays.stream(BlindLevel.values())
            .filter(bl -> Arrays.stream(bl.names).anyMatch(levelName::equalsIgnoreCase))
            .findFirst();
    }

    public final boolean matches(GrepToken token1, GrepToken token2, Map<String, String> constraint)
    {
        return matcher.matches(token1, token2, constraint);
    }

    public final Map<String, String> createConstraint(Map<String, String> from)
    {
        return constraintCreater.apply(from);
    }

    private interface Matcher
    {
        // assert token1.equals(token2);
        // equals -> test, !test -> !equals
        boolean matches(GrepToken token1, GrepToken token2, Map<String, String> constraint);
    }
}
