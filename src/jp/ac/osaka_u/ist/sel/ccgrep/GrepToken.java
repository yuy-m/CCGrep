package jp.ac.osaka_u.ist.sel.ccgrep;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;

class GrepToken
{
    final Token token;
    final String filename;
    final Language language;
    public GrepToken(Token token, String filename, Language language)
    {
        this.token = token;
        this.filename = filename;
        this.language = language;
    }
    public String getText()
    {
        return token.getText();
    }
    public String getFileName()
    {
        return filename;
    }
    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(obj instanceof GrepToken)
        {
            final GrepToken rhs = (GrepToken)obj;
            return rhs.getText().equals(this.getText());
        }
        return false;
    }
    @Override
    public String toString()
    {
        return "[" + getLine() + ":" + getCharPositionInLine() + ":`" + getText() + "`]";
    }

    static void printToken(Token t)
    {
        System.out.println("getChannel           : " + t.getChannel());
        System.out.println("getCharPositionInLine: " + t.getCharPositionInLine());
        // System.out.println("getInputStream       : " + t.getInputStream());
        System.out.println("getLine              : " + t.getLine());
        System.out.println("getStartIndex        : " + t.getStartIndex());
        System.out.println("getStopIndex         : " + t.getStopIndex());
        System.out.println("getText              : " + t.getText());
        System.out.println("getTokenIndex        : " + t.getTokenIndex());
        // System.out.println("getTokenSource       : " + t.getTokenSource());
        System.out.println("getType              : " + t.getType());
        System.out.println();
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
    int getType()
    {
        return token.getType();
    }
}

