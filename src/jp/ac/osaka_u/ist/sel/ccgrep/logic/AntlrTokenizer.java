package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class AntlrTokenizer implements ITokenizer
{
    private final Language language;
    public AntlrTokenizer(Language language)
    {
        this.language = language;
    }

    @Override
    public Language getLanguage()
    {
        return language;
    }

    @Override
    public TokenizerResult extractFromString(String code)
    {
        return extract(CharStreams.fromString(code, "<string>"), code);
    }

    @Override
    public TokenizerResult extractFromFile(String filename)
    {
        try{
            if(filename.equals("-"))
            {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
                {
                    final String code = br.lines()
                            .collect(Collectors.joining(System.lineSeparator()));
                    return extract(CharStreams.fromString(code, "<standard-input>"), code);
                }
            }
            else
            {
                return extract(CharStreams.fromFileName(filename), null);
            }
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private TokenizerResult extract(CharStream stream, String code)
    {
        final Lexer lexer = getLanguage().createLexer(stream);
        lexer.removeErrorListeners();
        final CommonTokenStream cts = new CommonTokenStream(lexer);

        final ArrayList<GrepToken> tokenList = new ArrayList<>();
        while (cts.LA(1) != Token.EOF)
        {
            final Token token = cts.LT(1);
            tokenList.add(new GrepToken(token, language));
            cts.consume();
        }
        return new TokenizerResult(new GrepCode(cts.getSourceName(), code), tokenList);
    }
}
