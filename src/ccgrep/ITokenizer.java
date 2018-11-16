package ccgrep;

import java.util.List;
import org.antlr.v4.runtime.CommonTokenStream;

public interface ITokenizer
{
    public Language getLanguage();
    public List<GrepToken> extractAsListFromString(String code);
    public CommonTokenStream extractAsStreamFromString(String code);
    public List<GrepToken> extractAsListFromFile(String filename);
    public CommonTokenStream extractAsStreamFromFile(String filename);
}
