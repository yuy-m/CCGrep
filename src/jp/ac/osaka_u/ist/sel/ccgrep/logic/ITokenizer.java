package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface ITokenizer
{
    Language getLanguage();
    TokenizerResult extractFromString(String code);
    TokenizerResult extractFromFile(String filename);

    public static class TokenizerResult
    {
        public GrepCode code;
        public List<GrepToken> tokens;
        TokenizerResult(GrepCode code, List<GrepToken> tokens)
        {
            this.code = code;
            this.tokens = tokens;
        }
    }
}
