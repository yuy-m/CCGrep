package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface ITokenizer
{
    Language getLanguage();
    Result extractFromString(String code);
    Optional<Result> extractFromFile(String filename);

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
