package ccgrep;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public class Tokenizer
{
    final Language language;
    public Tokenizer(Language language)
    {
        this.language = language;
    }
    public Tokenizer(String languageName)
    {
        this(Language.findByName(languageName));
    }

    /**
     * @return the language
     */
    public Language getLanguage()
    {
        return language;
    }

    public List<GrepToken> extract(Path path)
    {
        return path.toString().equals("-")
            ? extractFromStdin()
            : extract(path.toString());
    }

    private List<GrepToken> extractFromStdin()
    {
        try{
            return extract(CharStreams.fromStream(System.in), "stdin");
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<GrepToken> extract(String filename)
    {
        try{
            return extract(CharStreams.fromFileName(filename), filename);
        }
        catch(IOException e)
        {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<GrepToken> extract(CharStream stream, String filename)
    {
        final Lexer lexer = getLanguage().createLexer(stream);
        lexer.removeErrorListeners();
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final List<GrepToken> tokenList = new ArrayList<>();
        while (tokens.LA(1) != Token.EOF)
        {
            final Token token = tokens.LT(1);
            tokenList.add(new GrepToken(token, filename, language)/*.getText()*/);
            tokens.consume();
        }
        return tokenList;
    }
}
