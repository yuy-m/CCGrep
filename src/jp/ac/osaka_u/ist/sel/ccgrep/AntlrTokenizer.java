package jp.ac.osaka_u.ist.sel.ccgrep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;

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
    public List<GrepToken> extractAsListFromString(String code)
    {
        return toList(extractAsStreamFromString(code));
    }

    public CommonTokenStream extractAsStreamFromString(String code)
    {
        return extractAsStream(CharStreams.fromString(code, "<string>"));
    }

    @Override
    public List<GrepToken> extractAsListFromFile(String filename)
    {
        final CommonTokenStream cts = extractAsStreamFromFile(filename);
        return cts != null? toList(cts): Collections.emptyList();
    }

    public CommonTokenStream extractAsStreamFromFile(String filename)
    {
        try{
            if(filename.equals("-"))
            {
                final String code =
                    new BufferedReader(new InputStreamReader(System.in))
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator()));
                return extractAsStream(CharStreams.fromString(code, "<standard-input>"));
            }
            else
            {
                return extractAsStream(CharStreams.fromFileName(filename));
            }
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private List<GrepToken> toList(TokenStream tokens)
    {
        final List<GrepToken> tokenList = new ArrayList<>();
        while (tokens.LA(1) != Token.EOF)
        {
            final Token token = tokens.LT(1);
            tokenList.add(new GrepToken(token));
            tokens.consume();
        }
        return tokenList;
    }

    public CommonTokenStream extractAsStream(CharStream stream)
    {
        final Lexer lexer = getLanguage().createLexer(stream);
        lexer.removeErrorListeners();
        return new CommonTokenStream(lexer);
    }
}
