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
    private final int lineCount;
    private List<String> cache;
    private static final List<String> failedCode = Collections.singletonList("<CODE FETCH FAILED>");
    private final boolean isWithCode;

    private static final Pattern lineSplitPattern = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    public GrepCode(String fileName, int tokenCount, int lineCount, String withCode)
    {
        this.fileName = fileName;
        this.tokenCount = tokenCount;
        this.lineCount = lineCount;
        this.isWithCode = withCode != null;
        if(isWithCode)
        {
            this.cache = lineSplitPattern.splitAsStream(withCode)
                                .collect(Collectors.toList());
        }
    }

    public GrepCode(String fileName, List<GrepToken> tokens, String withCode)
    {
        this(fileName, tokens.size(), countLinesFromTokens(tokens), withCode);
    }

    private static int countLinesFromTokens(List<GrepToken> tokens)
    {
        if(tokens.isEmpty())
        {
            return 0;
        }
        int lineCount = 1;
        int lastLine = tokens.get(0).getLine();
        for(GrepToken token: tokens)
        {
            if(token.getLine() != lastLine)
            {
                ++lineCount;
                lastLine = token.getLine();
            }
        }
        return lineCount;
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
        return lineCount;
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
                    System.err.println("ccgrep: " + getFileName() + ": Cannot read");
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
