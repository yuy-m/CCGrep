package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;


public class GrepToken
{
    private final Token token;
    public GrepToken(Token token)
    {
        this.token = token;
    }

    public String getText()
    {
        return token.getText();
    }

    public String getFileName()
    {
        return token.getInputStream().getSourceName();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this? true
            : obj instanceof GrepToken? ((GrepToken)obj).getText().equals(this.getText())
            : obj instanceof String? this.getText().equals((String)obj)
            : false;
    }

    public boolean equalsAsSpecialTo(Object obj)
    {
        return obj == this? true
            : obj instanceof GrepToken? getText().substring(1).equals(((GrepToken)obj).getText())
            : obj instanceof String? getText().substring(1).equals((String)obj)
            : false;
    }

    public List<String> getCodeByLine(int countLines)
    {
        try{
            return Files.lines(Paths.get(getFileName()))
                .skip(getLine() - 1)
                .limit(countLines)
                .collect(Collectors.toList());
        }
        catch(Exception e)
        {
            return getCodeByLine2(countLines);
        }
    }

    private static final Pattern splitPattern = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");
    // slowly but surely
    private List<String> getCodeByLine2(int countLines)
    {
        // all source code of the file.
        final String text = getInputStream()
            .getText(new Interval(
                0,
                getInputStream().size()
            ));
        // extract clone lines
        return splitPattern
            .splitAsStream(text)
            .skip(getLine() - 1)
            .limit(countLines)
            .collect(Collectors.toList());
    }

    @Override
    public String toString()
    {
        return "[" + getLine() + ":" + getCharPositionInLine() + ":(" + getType() + ")`" + getText() + "`]";
    }

    public int getLine()
    {
        return token.getLine();
    }
    public int getCharPositionInLine()
    {
        return token.getCharPositionInLine();
    }
    public int getStartIndex()
    {
        return token.getStartIndex();
    }
    public int getStopIndex()
    {
        return token.getStopIndex();
    }
    CharStream getInputStream()
    {
        return token.getInputStream();
    }
    int getTokenIndex()
    {
        return token.getTokenIndex();
    }
    public int getType()
    {
        return token.getType();
    }
}

