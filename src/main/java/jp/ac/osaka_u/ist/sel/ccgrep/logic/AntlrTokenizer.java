package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.errorLogger;


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
    public Result extractFromString(String code)
    {
        return extract(CharStreams.fromString(code, "<string>"), code);
    }

    @Override
    public Optional<Result> extractFromFile(String filename)
    {
        try{
            if(filename.equals("-"))
            {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
                {
                    final String code = br.lines()
                            .collect(Collectors.joining(System.lineSeparator()));
                    return Optional.of(extract(CharStreams.fromString(code, "<standard-input>"), code));
                }
            }
            else
            {
                return Optional.of(extract(CharStreams.fromFileName(filename), null));
            }
        }
        catch(IOException e)
        {
            errorLogger.println(
                "ccgrep: " + e.getMessage()
                + (filename.equals(e.getMessage())? ": No such file or directory": "")
            );
        }
        return Optional.empty();
    }

    private Result extract(CharStream stream, String code)
    {
        final Lexer lexer = getLanguage().createLexer(stream);
        lexer.removeErrorListeners();
        final CommonTokenStream cts = new CommonTokenStream(lexer);

        final List<GrepToken> tokenList = language.filter(toList(cts));
        return new Result(new GrepCode(cts.getSourceName(), tokenList, code), tokenList);
    }

    private final ArrayList<GrepToken> toList(CommonTokenStream cts)
    {
        final ArrayList<GrepToken> tokenList = new ArrayList<>();
        while (cts.LA(1) != Token.EOF)
        {
            final Token token = cts.LT(1);
            tokenList.add(new GrepToken(token, language));
            cts.consume();
        }
        return tokenList;
    }
}
