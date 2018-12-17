package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class GrepCode
{
    private final String fileName;
    private final int tokenCount;
    private List<String> cache;
    private final boolean isWithCode;

    private static final Pattern lineSplitPattern = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    public GrepCode(String fileName, int tokenCount, String withCode)
    {
        this.fileName = fileName;
        this.tokenCount = tokenCount;
        this.isWithCode = withCode != null;
        if(isWithCode)
        {
            this.cache = lineSplitPattern.splitAsStream(withCode)
                                .collect(Collectors.toList());
        }
    }

    public void clearCodeCache()
    {
        if(!isWithCode && cache != null)
        {
            cache.clear();
            cache = null;
        }
    }

    public int countTokens()
    {
        return tokenCount;
    }

    public int countLines()
    {
        return getCodeByLine().size();
    }

    public List<String> getCodeByLine()
    {
        if(cache == null)
        {
            try
            {
                cache = Files.readAllLines(Paths.get(getFileName()));
            }
            catch(IOException e)
            {
                // TODO: should be always the head of line.
                System.err.println("Error: cannot read file " + getFileName());
                cache = Collections.emptyList();
            }
        }
        return cache;
    }

    public List<String> getCodeByLine(int fromLine, int toLine)
    {
        return getCodeByLine().subList(
            Math.max(0, fromLine - 1),
            Math.min(cache.size(), toLine)
        );
    }

    public String getFileName()
    {
        return fileName;
    }
}
