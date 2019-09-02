package jp.ac.osaka_u.ist.sel.ccgrep;

import org.junit.Test;

import jp.ac.osaka_u.ist.sel.ccgrep.model.Clone;
import jp.ac.osaka_u.ist.sel.ccgrep.model.CloneList;

import static org.assertj.core.api.Assertions.*;


public class CCGrepTest
{
    private static final String target =
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

    private static boolean equalsClone(Clone clone, int startLine, int endLine, int startColumn, int endColumn)
    {
        return clone.getStartLine() == startLine
            && clone.getEndLine() == endLine
            && clone.getStartColumn() == startColumn
            && clone.getEndColumn() == endColumn;
    }

    @Test
    public void testLinePrint() throws CCGrepException
    {
        CCGrep ccgrep = new CCGrep("T f() { return a; }");

        CloneList[] result = {null};
        assertThatCode(() -> result[0] = ccgrep.search(target))
            .doesNotThrowAnyException();

        assertThat(result[0].size()).isEqualTo(3);
        assertThat(equalsClone(result[0].get(0), 7, 7, 10, 42)).isTrue();
        assertThat(equalsClone(result[0].get(1), 9, 9, 10, 39)).isTrue();
        assertThat(equalsClone(result[0].get(2), 10, 10, 10, 43)).isTrue();
    }

    @Test
    public void testMaxNum() throws CCGrepException
    {
        CCGrep ccgrep = new CCGrep("T f() { return a; }");
        ccgrep.setMaxCount(2);

        CloneList[] result = {null};
        assertThatCode(() -> result[0] = ccgrep.search(target))
            .doesNotThrowAnyException();

        assertThat(result[0].size()).isEqualTo(2);
        assertThat(equalsClone(result[0].get(0), 7, 7, 10, 42)).isTrue();
        assertThat(equalsClone(result[0].get(1), 9, 9, 10, 39)).isTrue();
    }

    @Test
    public void testFullPrint() throws CCGrepException
    {
        CCGrep ccgrep = new CCGrep("$( $this.a = $$; $) $+");
        ccgrep.enableNoOverlap(true);

        CloneList[] result = {null};
        assertThatCode(() -> result[0] = ccgrep.search(target))
            .doesNotThrowAnyException();

        assertThat(result[0].size()).isEqualTo(2);
        assertThat(equalsClone(result[0].get(0), 8, 8, 38, 54)).isTrue();
        assertThat(equalsClone(result[0].get(1), 15, 17, 5, 52)).isTrue();
    }

    @Test
    public void testNotFound() throws CCGrepException
    {
        CCGrep ccgrep = new CCGrep("for($$){$$}");

        CloneList[] result = {null};
        assertThatCode(() -> result[0] = ccgrep.search(target))
            .doesNotThrowAnyException();

        assertThat(result[0].size()).isEqualTo(0);
    }

    @Test
    public void TestQueryError() throws CCGrepException
    {
        assertThatExceptionOfType(CCGrepException.class)
            .isThrownBy(() -> new CCGrep("$)"));
    }
}
