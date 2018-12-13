package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Map;

import org.antlr.v4.runtime.Token;


public class GrepToken
{
    private final Language language;

    private final String text;
    private final int line;
    private final int charPositionInLine;
    private final int startIndex;
    private final int stopIndex;
    private final int tokenIndex;
    private final int type;

    public GrepToken(Token token, Language language)
    {
        this.language = language;

        this.text = token.getText();
        this.line = token.getLine();
        this.charPositionInLine = token.getCharPositionInLine();
        this.startIndex = token.getStartIndex();
        this.stopIndex = token.getStopIndex();
        this.tokenIndex = token.getTokenIndex();
        this.type = token.getType();
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

    public boolean matchesBlindly(GrepToken rhs, BlindLevel blindLevel, Map<String, String> constraint)
    {
        if(getLanguage().isSpecialId(this))
        {
            return equalsAsSpecialTo(rhs);
        }
        final String t = constraint.get(getText());
        return t != null
            ? rhs.equals(t)
            : getLanguage().findBlindLevel(this, rhs, blindLevel)
                    .matches(this, rhs, constraint);
    }

    @Override
    public String toString()
    {
        return "[" + getLine() + ":" + getCharPositionInLine() + ":(" + getType() + ")`" + getText() + "`]";
    }

    public Language getLanguage()
    {
        return language;
    }

    public String getText()
    {
        return text;
    }

    public int getLine()
    {
        return line;
    }
    public int getCharPositionInLine()
    {
        return charPositionInLine;
    }
    public int getStartIndex()
    {
        return startIndex;
    }
    public int getStopIndex()
    {
        return stopIndex;
    }
    public int getTokenIndex()
    {
        return tokenIndex;
    }
    public int getType()
    {
        return type;
    }

    @Override
    public int hashCode()
    {
        return text.hashCode();
    }
}

