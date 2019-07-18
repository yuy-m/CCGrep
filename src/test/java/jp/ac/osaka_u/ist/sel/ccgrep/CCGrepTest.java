package jp.ac.osaka_u.ist.sel.ccgrep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.*;
import java.nio.file.*;


public class CCGrepTest
{
    private final String targetFileName = "________test_target.java";
    private final Path targetFilePath = Paths.get(targetFileName);
    private final String target =
          "package jp.ac.osaka_u.ist.sel.ccgrep.model;\n"
        + "import org.antlr.v4.runtime.Token;\n"
        + "public class GrepToken {\n"
        + "  @Override public String toString() {\n"
        + "    return getLine() + \",\" + getColumn() + \",\" + getType() + \",\" + getText();\n"
        + "  }\n"
        + "  public String getText() { return text; }\n"
        + "  public void setText(String text) { this.text = text; }\n"
        + "  public int getLine() { return line; }\n"
        + "  public int getColumn() { return column; }\n"
        + "  private String text;\n"
        + "  private final int line;\n"
        + "  private final int column;\n"
        + "  public GrepToken(Token token) {\n"
        + "    this.text = token.getText();\n"
        + "    this.line = token.getLine();\n"
        + "    this.column = token.getCharPositionInLine() + 1;\n"
        + "  }\n"
        + "}\n";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void before() throws IOException
    {
        Files.write(targetFilePath, target.getBytes());
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void after() throws IOException
    {
        System.setOut(null);
        System.setErr(null);
        Files.delete(targetFilePath);
    }

    @Test(expected = Test.None.class)
    public void Test1() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "T f() { return a; }", targetFileName, "-pn"
        });
        assertThat(option).isNotNull();
        assertThat(new CCGrep(option).grep())
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + ":7:  public String getText() { return text; }\n"
            + targetFileName + ":9:  public int getLine() { return line; }\n"
            + targetFileName + ":10:  public int getColumn() { return column; }\n"
        );
    }

    @Test(expected = Test.None.class)
    public void Test1m() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "T f() { return a; }", targetFileName, "-pn", "-m2"
        });
        assertThat(option).isNotNull();
        assertThat(new CCGrep(option).grep())
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + ":7:  public String getText() { return text; }\n"
            + targetFileName + ":9:  public int getLine() { return line; }\n"
        );
    }

    @Test(expected = Test.None.class)
    public void Test2() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "$( $this.a = $$; $) $+", targetFileName, "-pnf", "--no-overlap"
        });
        assertThat(option).isNotNull();
        assertThat(new CCGrep(option).grep())
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + "\n"
            + "8:  public void setText(String text) { this.text = text; }\n"
            + targetFileName + "\n"
            + "15:    this.text = token.getText();\n"
            + "16:    this.line = token.getLine();\n"
            + "17:    this.column = token.getCharPositionInLine() + 1;\n"
        );
    }
}
