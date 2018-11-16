package ccgrep;

import java.util.List;

public interface IDetector
{
    public List<Clone> detect(String haystackFileName);
}
