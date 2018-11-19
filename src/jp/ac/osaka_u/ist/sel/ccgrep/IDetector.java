package jp.ac.osaka_u.ist.sel.ccgrep;

import java.util.List;

public interface IDetector
{
    public List<Clone> detect(String haystackFileName);
}
