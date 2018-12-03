package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.util.List;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface ITokenizer
{
    Language getLanguage();
    List<GrepToken> extractAsListFromString(String code);
    List<GrepToken> extractAsListFromFile(String filename);
}
