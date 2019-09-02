package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface IDetector
{
    CloneList detect(String targetFileName, int maxCount);
    default CloneList detect(String targetFileName)
    {
        return detect(targetFileName, -1);
    }
}
