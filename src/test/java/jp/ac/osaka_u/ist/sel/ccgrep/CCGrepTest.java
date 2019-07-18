package jp.ac.osaka_u.ist.sel.ccgrep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.io.*;
import java.nio.file.*;


public class CCGrepTest
{
    private static final String targetFileName = "__ccgrep_test_target.java";
    private static final Path targetFilePath = Paths.get(targetFileName);
    private static final List<String> target = Arrays.asList(
        "package jp.ac.osaka_u.ist.sel.ccgrep.model;",
        "import org.antlr.v4.runtime.Token;",
        "public class GrepToken {",
        "  @Override public String toString() {",
        "    return getLine() + \",\" + getColumn() + \",\" + getType() + \",\" + getText();",
        "  }",
        "  public String getText() { return text; }",
        "  public void setText(String text) { this.text = text; }",
        "  public int getLine() { return line; }",
        "  public int getColumn() { return column; }",
        "  private String text;",
        "  private final int line;",
        "  private final int column;",
        "  public GrepToken(Token token) {",
        "    this.text = token.getText();",
        "    this.line = token.getLine();",
        "    this.column = token.getCharPositionInLine() + 1;",
        "  }",
        "}",
        ""
    );

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void before() throws IOException
    {
        Files.write(targetFilePath, target);
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

    @Test
    public void testLinePrint() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "T f() { return a; }", targetFileName, "-pn"
        });
        assertThat(option).isNotNull();
        final int[] result = {-1};
        assertThatCode(() -> result[0] = new CCGrep(option).grep())
            .doesNotThrowAnyException();
        assertThat(result[0])
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + ":7:" + target.get(6) + "\n"
            + targetFileName + ":9:" + target.get(8) + "\n"
            + targetFileName + ":10:" + target.get(9) + "\n"
        );
        assertThat(errContent.toString()).isEqualToNormalizingNewlines("");
    }

    @Test
    public void testMaxNum() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "T f() { return a; }", targetFileName, "-pn", "-m2"
        });
        assertThat(option).isNotNull();
        final int[] result = {-1};
        assertThatCode(() -> result[0] = new CCGrep(option).grep())
            .doesNotThrowAnyException();
        assertThat(result[0])
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + ":7:" + target.get(6) + "\n"
            + targetFileName + ":9:" + target.get(8) + "\n"
        );
        assertThat(errContent.toString()).isEqualToNormalizingNewlines("");
    }

    @Test
    public void testFullPrint() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "$( $this.a = $$; $) $+", targetFileName, "-pnf", "--no-overlap"
        });
        assertThat(option).isNotNull();
        final int[] result = {-1};
        assertThatCode(() -> result[0] = new CCGrep(option).grep())
            .doesNotThrowAnyException();
        assertThat(result[0])
            .isEqualTo(0);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines(
              targetFileName + "\n"
            + "8:" + target.get(7) + "\n"
            + targetFileName + "\n"
            + "15:" + target.get(14) + "\n"
            + "16:" + target.get(15) + "\n"
            + "17:" + target.get(16) + "\n"
        );
        assertThat(errContent.toString()).isEqualToNormalizingNewlines("");
    }

    @Test
    public void testNotFound() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "for($$){$$}", targetFileName
        });
        assertThat(option).isNotNull();
        final int[] result = {-1};
        assertThatCode(() -> result[0] = new CCGrep(option).grep())
            .doesNotThrowAnyException();
        assertThat(result[0])
            .isEqualTo(1);
        assertThat(outContent.toString()).isEqualToNormalizingNewlines("");
        assertThat(errContent.toString()).isEqualToNormalizingNewlines("");
    }

    @Test
    public void TestQueryError() throws CCGrepException
    {
        final CCGrepOption option = CommandLineFrontend.process(new String[]{
            "$)", targetFileName
        });
        assertThat(option).isNotNull();
        assertThatExceptionOfType(CCGrepException.class)
            .isThrownBy(() -> new CCGrep(option).grep());
    }
}
