package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.Collection;

import jp.ac.osaka_u.ist.sel.ccgrep.logic.AntlrTokenizer;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.ITokenizer;
import jp.ac.osaka_u.ist.sel.ccgrep.logic.TokenSequenceDetector;
import jp.ac.osaka_u.ist.sel.ccgrep.model.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.model.CloneList;
import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;

public class CCGrep
{
    private TokenSequenceDetector detector;
    private int maxCount = -1;

    public CCGrep(String queryCode) throws CCGrepException
    {
        this(Language.JAVA9, queryCode);
    }

    public CCGrep(Language language, String queryCode) throws CCGrepException
    {
        final ITokenizer tokenizer = new AntlrTokenizer(language);
        final ITokenizer.Result nResult = tokenizer.tokenizeQueryFromString(queryCode);
        this.detector = new TokenSequenceDetector(tokenizer, nResult.tokens);
    }

    public void setBlindLevel(BlindLevel blindLevel)
    {
        detector.setBlindLevel(blindLevel);
    }

    public void setFixedIds(Collection<String> fixedIds)
    {
        detector.setFixedIds(fixedIds);
    }

    public void setMaxCount(int maxCount)
    {
        if(maxCount < 0)
        {
            throw new IllegalArgumentException("maxCount must not be negative.");
        }
        this.maxCount = maxCount;
    }

    public void removeMaxCount()
    {
        this.maxCount = -1;
    }

    public void enableFileMatching(boolean enable)
    {
        detector.enableFileMatching(enable);
    }
    public void enableNoOverlap(boolean enable)
    {
        detector.enableNoOverlap(enable);
    }

    public CloneList search(String targetCode)
    {
        return detector.detectString(targetCode, maxCount);
    }
}

