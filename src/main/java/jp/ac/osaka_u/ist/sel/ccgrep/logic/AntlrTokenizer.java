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
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.*;


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
    public Result extractQueryFromString(String code)
    {
        return extract(
            getLanguage().createQueryLexer(CharStreams.fromString(code, "<string>")),
            code
        );
    }

    @Override
    public Optional<Result> extractFromFile(String filename)
    {
        return extractFromFileInner(filename, false);
    }

    @Override
    public Optional<Result> extractQueryFromFile(String filename)
    {
        return extractFromFileInner(filename, true);
    }

    private Optional<Result> extractFromFileInner(String filename, boolean isQuery)
    {
        try{
            String code = null;
            CharStream cs;
            if(filename.equals("-"))
            {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
                {
                    code = br.lines()
                            .collect(Collectors.joining(System.lineSeparator()));
                    cs = CharStreams.fromString(code, "<standard-input>");
                }
            }
            else
            {
                cs = CharStreams.fromFileName(filename);
            }
            return Optional.of(
                extract(
                    isQuery
                        ?getLanguage().createQueryLexer(cs)
                        : getLanguage().createLexer(cs),
                    code
                )
            );
        }
        catch(IOException e)
        {
            errorLogger.println(
                "ccgrep: " + e.getMessage()
                + (filename.equals(e.getMessage())? ": Cannot be read": "")
            );
        }
        catch(Exception e)
        {
            errorLogger.println("ccgrep: " + filename + ": Cannot be read: " + e);
        }
        return Optional.empty();
    }

    private Result extract(Lexer lexer, String code)
    {
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
