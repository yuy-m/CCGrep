package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.Optional;
import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface ITokenizer
{
    Language getLanguage();
    Result tokenizeFromString(String code);
    Result tokenizeQueryFromString(String code);
    Optional<Result> tokenizeFromFile(String filename);
    Optional<Result> tokenizeQueryFromFile(String filename);

    public static class Result
    {
        public final GrepCode code;
        public final List<GrepToken> tokens;
        Result(GrepCode code, List<GrepToken> tokens)
        {
            this.code = code;
            this.tokens = tokens;
        }
    }
}
