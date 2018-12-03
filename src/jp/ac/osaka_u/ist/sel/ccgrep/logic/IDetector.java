package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public interface IDetector
{
    CloneList detect(String haystackFileName);
}
