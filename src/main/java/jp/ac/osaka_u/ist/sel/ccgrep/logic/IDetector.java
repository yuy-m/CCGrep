package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface IDetector
{
    CloneList detectString(String targetCode, int maxCount);
    default CloneList detectString(String targetCode)
    {
        return detectFile(targetCode, -1);
    }
    CloneList detectFile(String targetFileName, int maxCount);
    default CloneList detectFile(String targetFileName)
    {
        return detectFile(targetFileName, -1);
    }
}
