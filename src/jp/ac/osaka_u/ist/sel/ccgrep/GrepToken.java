package jp.ac.osaka_u.ist.sel.ccgrep;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;

class GrepToken
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
        if(obj == this)
        {
            return true;
        }
        if(obj instanceof GrepToken)
        {
            final GrepToken rhs = (GrepToken)obj;
            return rhs.getType() == getType() && rhs.getText().equals(this.getText());
        }
        return false;
    }
    public boolean equalsAsSpecialTo(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        if(obj instanceof GrepToken)
        {
            final GrepToken rhs = (GrepToken)obj;
            return getText().substring(1).equals(rhs.getText());
        }
        return false;
    }
    @Override
    public String toString()
    {
        return "[" + getLine() + ":" + getCharPositionInLine() + ":(" + getType() + ")`" + getText() + "`]";
    }

    void printDetail()
    {
        // System.out.println("getChannel           : " + getChannel());
        System.out.println("getCharPositionInLine: " + getCharPositionInLine());
        // System.out.println("getInputStream       : " + getInputStream());
        System.out.println("getLine              : " + getLine());
        System.out.println("getStartIndex        : " + getStartIndex());
        System.out.println("getStopIndex         : " + getStopIndex());
        System.out.println("getText              : " + getText());
        System.out.println("getTokenIndex        : " + getTokenIndex());
        // System.out.println("getTokenSource       : " + getTokenSource());
        System.out.println("getType              : " + getType());
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

