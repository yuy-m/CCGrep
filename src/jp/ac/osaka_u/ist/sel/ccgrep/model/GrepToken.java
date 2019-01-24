package jp.ac.osaka_u.ist.sel.ccgrep.model;


import java.util.Map;

import org.antlr.v4.runtime.Token;


public class GrepToken
{
    private final Language language;
    private final Language.BlindSet blindSetCache;

    private final String text;
    private final int line;
    private final int column;
    private final int startIndex;
    private final int stopIndex;
    private final int tokenIndex;
    private final int type;

    public GrepToken(Token token, Language language)
    {
        this.language = language;
        this.blindSetCache = language.findBlindSet(this);

        this.text = token.getText();
        this.line = token.getLine();
        this.column = token.getCharPositionInLine() + 1;
        this.startIndex = token.getStartIndex();
        this.stopIndex = token.getStopIndex();
        this.tokenIndex = token.getTokenIndex();
        this.type = token.getType();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this
            || (obj instanceof GrepToken && eq(getText(), ((GrepToken)obj).getText()));
    }

    public boolean equalsAsSpecialTo(Object obj)
    {
        return obj == this
            || (obj instanceof GrepToken && eq(getSpecialStringText(), ((GrepToken)obj).getText()));
    }

    private String specialStringText = null;
    private String getSpecialStringText()
    {
        if(specialStringText == null)
        {
            specialStringText = getText().substring(1);
        }
        return specialStringText;
    }

    private boolean eq(String s1, String s2)
    {
        return s1.equals(s2);
    }

    public boolean matchesBlindly(GrepToken rhs, BlindLevel blindLevel, Map<String, String> constraint)
    {
        if(getLanguage().isSpecialId(this))
        {
            return equalsAsSpecialTo(rhs);
        }
        final String t = constraint.get(getText());
        return t != null
            ? eq(t, rhs.getText())
            : getLanguage().findBlindLevel(this.blindSetCache, rhs.blindSetCache, blindLevel)
                    .matches(this, rhs, constraint);
    }

    @Override
    public String toString()
    {
        return "[" + getLine() + ":" + getColumn() + ":(" + getType() + ")`" + getText() + "`]";
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
    public int getColumn()
    {
        return column;
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

