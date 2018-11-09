package ccgrep;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import antlr.c.CLexer;
import antlr.cpp14.CPP14Lexer;
import antlr.java9.Java9Lexer;
import antlr.python3.Python3Lexer;

public enum Language
{
    C(
        new String[]{"c"},
        new String[]{".c", ".h"},
        CLexer::new,
        new int[]{CLexer.Char, CLexer.Double, CLexer.Float, CLexer.Int, CLexer.Long, CLexer.Short, CLexer.Void, CLexer.Bool, CLexer.Identifier}
    ),
    CPP14(
        new String[]{"cpp", "cpp14"},
        new String[]{".cpp", ".h", ".hpp"},
        CPP14Lexer::new,
        new int[]{CPP14Lexer.Auto, CPP14Lexer.Bool, CPP14Lexer.Char, CPP14Lexer.Char16, CPP14Lexer.Char32, CPP14Lexer.Double, CPP14Lexer.Float, CPP14Lexer.Int, CPP14Lexer.Long, CPP14Lexer.Short, CPP14Lexer.Void, CPP14Lexer.Wchar, CPP14Lexer.Identifier}
    ),
    JAVA9(
        new String[]{"java", "java9"},
        new String[]{".java"},
        Java9Lexer::new,
        new int[]{Java9Lexer.BOOLEAN, Java9Lexer.BYTE, Java9Lexer.CHAR, Java9Lexer.DOUBLE, Java9Lexer.FLOAT, Java9Lexer.INT, Java9Lexer.LONG, Java9Lexer.SHORT, Java9Lexer.VOID, Java9Lexer.Identifier}
    ),
    PYTHON3(
        new String[]{"python", "python3"},
        new String[]{".py"},
        Python3Lexer::new,
        new int[]{Python3Lexer.NAME}
    );

    private final String[] names;
    private final String[] extensions;
    private final Function<CharStream, Lexer> lexerCreater;
    private final int[] blindableTokens;
    private boolean _isBlindEnabled;

    Language(String[] names, String[] extensions, Function<CharStream, Lexer> lexerCreater, int[] blindables)
    {
        Arrays.sort(names);
        Arrays.sort(extensions);
        Arrays.sort(blindables);
        this.names = names;
        this.extensions = extensions;
        this.lexerCreater = lexerCreater;
        this.blindableTokens = blindables;
    }

    public static Language findByName(String languageName)
    {
        return Arrays.stream(Language.values())
            .filter(l -> Arrays.binarySearch(l.names, languageName) >= 0)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Language `" + languageName + "` is not supported."));
    }

    public Lexer createLexer(CharStream stream)
    {
        return lexerCreater.apply(stream);
    }

    public void enableBlind(boolean enable)
    {
        this._isBlindEnabled = enable;
    }

    public boolean isBlindEnabled()
    {
        return this._isBlindEnabled;
    }

    public boolean isBlindableToken(final int tokenType)
    {
        return isBlindEnabled() && Arrays.binarySearch(blindableTokens, tokenType) >= 0;
    }

    public boolean matchesExtension(String filename)
    {
        return Arrays.stream(extensions)
            .anyMatch(filename::endsWith);
    }
}
