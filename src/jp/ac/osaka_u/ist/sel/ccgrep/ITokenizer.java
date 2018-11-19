package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;

public interface ITokenizer
{
    public Language getLanguage();
    public List<GrepToken> extractAsListFromString(String code);
    public List<GrepToken> extractAsListFromFile(String filename);
}
