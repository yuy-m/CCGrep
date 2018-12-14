package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jp.ac.osaka_u.ist.sel.ccgrep.miniparser.Range;
import jp.ac.osaka_u.ist.sel.ccgrep.model.GrepToken;
import jp.ac.osaka_u.ist.sel.ccgrep.model.BlindLevel;


public class GrepRange extends Range<GrepToken>
{
    final BlindLevel blindLevel;
    private Map<String, String> constraint;
    GrepRange(
        List<GrepToken> list, int position,
        BlindLevel blindLevel, Map<String, String> constraint)
    {
        super(list, position);
        this.blindLevel = blindLevel;
        this.constraint = constraint;
    }
    GrepRange(
        List<GrepToken> list, BlindLevel blindLevel, Map<String, String> constraint)
    {
        this(list, 0, blindLevel, constraint);
    }

    @Override
    public boolean matches(GrepToken t)
    {
        return t.matchesBlindly(front(), blindLevel, constraint);
    }

    Map<String, String> getConstraint()
    {
        return constraint;
    }

    void replaceConstraint(Map<String, String> c)
    {
        constraint = c;
    }
}
