package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;
import java.util.Collections;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface ITokenizer
{
    Language getLanguage();
    Result extractFromString(String code);
    Result extractFromFile(String filename);

    public static class Result
    {
        public GrepCode code;
        public List<GrepToken> tokens;
        Result(GrepCode code, List<GrepToken> tokens)
        {
            this.code = code;
            this.tokens = tokens;
        }

        static Result empty(String name)
        {
            return new Result(new GrepCode(name, 0, null), Collections.emptyList());
        }
    }
}
