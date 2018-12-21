package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;


public class GrepCode
{
    private final String fileName;
    private final int tokenCount;
    private List<String> cache;
    private static final List<String> failedCode = Collections.singletonList("<CODE FETCH FAILED>");
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
        if(!isWithCode)
        {
            cache = null;
        }
    }

    public int countTokens()
    {
        return tokenCount;
    }

    public int countLines()
    {
        if(cache != null)
        {
            return getCodeByLine().size();
        }
        try
        {
            return Files.lines(Paths.get(getFileName()))
                        .mapToInt(__ -> 1)
                        .sum();
        }
        catch(IOException|UncheckedIOException e)
        {
            try
            {
                return Files.lines(Paths.get(getFileName()), Charset.forName("ISO_8859_1"))
                            .mapToInt(__ -> 1)
                            .sum();
            }
            catch(IOException|UncheckedIOException e2)
            {
                System.err.println("Error: cannot read file " + getFileName());
                return 0;
            }
        }
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
                try
                {
                    cache = Files.readAllLines(Paths.get(getFileName()), Charset.forName("ISO_8859_1"));
                }
                catch(IOException e2)
                {
                    System.err.println("Error: cannot read file " + getFileName());
                    return failedCode;
                }
            }
        }
        return cache;
    }

    public List<String> getCodeByLine(int fromLine, int toLine)
    {
        final List<String> code = getCodeByLine();
        if(code == failedCode)
        {
            return code;
        }
        final int s = Math.max(0, fromLine - 1);
        final int e = Math.min(code.size(), toLine);
        return s <= e
            ? code.subList(s, e)
            : failedCode;
    }

    public String getFileName()
    {
        return fileName;
    }
}
