package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.List;
import java.util.Collections;


public final class CCGrepOption
{
    boolean isHelpEnabled = false;
    boolean isRecursiveEnabled = false;
    boolean isLogEnabled = false;
    boolean isErrorMessageEnabled = true;
    String blindLevelName = "";
    String languageName = null;
    String printOption = "";
    List<String> fixedIds = Collections.emptyList();
    boolean isJsonEnabled = false;
    boolean isXmlEnabled = false;
    boolean isTimeEnabled = false;
    int maxCount = -1;
    boolean isFileMatchingEnabled = false;
    List<String> includePatterns = Collections.emptyList();
    List<String> excludePatterns = Collections.emptyList();
    boolean isIgnoreExtensionEnabled = false;
    boolean isParallelEnabled = false;
    boolean isNoOverlapEnabled = false;

    String query = "";
    int queryType = QUERY_NONE;
    public static final int QUERY_NONE = -1;
    public static final int QUERY_CODE = 0;
    public static final int QUERY_FILE = 1;
    public static final int QUERY_STDIN = 2;
    List<String> targetNames = Collections.emptyList();

    /** --help */
    public void enableHelp(boolean enable)
    {
        this.isHelpEnabled = enable;
    }

    /** --recursive */
    public void enableRecursive(boolean enable)
    {
        this.isRecursiveEnabled = enable;
    }

    /** --log */
    public void enableLog(boolean enable)
    {
        this.isLogEnabled = enable;
    }

    /** --no-messages */
    public void enableErrorMessage(boolean enable)
    {
        this.isErrorMessageEnabled = enable;
    }

    /** --blind=blindLevelName */
    public void setBlindLevelName(String blindLevelName)
    {
        this.blindLevelName = requireNonNullElse(blindLevelName, "");
    }

    /** --language=languageName */
    public void setLanguageName(String languageName)
    {
        this.languageName = languageName;
    }

    /** --print=printOption */
    public void setPrintOption(String printOption)
    {
        this.printOption = printOption;
    }

    /** --fix=fixedId1 --fix=fixedId2 */
    public void setFixedIds(List<String> fixedIds)
    {
        this.fixedIds = requireNonNullElse(fixedIds, Collections.emptyList());
    }

    /** --json */
    public void enableJson(boolean enable)
    {
        this.isJsonEnabled = enable;
    }

    /** --xml */
    public void enableXml(boolean enable)
    {
        this.isXmlEnabled = enable;
    }

    /** --time */
    public void enableTime(boolean enable)
    {
        this.isTimeEnabled = enable;
    }

    /** --max-count=maxCount */
    public void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }

    /** --file-match */
    public void enableFileMatching(boolean enable)
    {
        this.isFileMatchingEnabled = enable;
    }

    /** --include=includePatterns */
    public void setIncludePatterns(List<String> includePatterns)
    {
        this.includePatterns = requireNonNullElse(includePatterns, Collections.emptyList());
    }

    /** --exclude=excludePatterns */
    public void setExcludePatterns(List<String> excludePatterns)
    {
        this.excludePatterns = requireNonNullElse(excludePatterns, Collections.emptyList());
    }

    /** --ignore-extension */
    public void enableIgnoreExtension(boolean enable)
    {
        this.isIgnoreExtensionEnabled = enable;
    }

    /** --parallel */
    public void enableParallel(boolean enable)
    {
        this.isParallelEnabled = enable;
    }

    public void enableNoOverlap(boolean enable)
    {
        this.isNoOverlapEnabled = enable;
    }

    /** -e=text */
    public void setQueryCode(String text)
    {
        this.query = requireNonNullElse(text, "");
        this.queryType = QUERY_CODE;
    }

    /** --file=fileName */
    public void setQueryFileName(String fileName)
    {
        this.query = requireNonNullElse(fileName, "");
        this.queryType = QUERY_FILE;
    }

    /** --stdin-query */
    public void setQueryStdin()
    {
        this.query = null;
        this.queryType = QUERY_STDIN;
    }

    public int getQueryType()
    {
        return this.queryType;
    }

    /** File names that are searched, '-' is STDIN */
    public void setTargetNames(List<String> targetNames) throws CCGrepException
    {
        this.targetNames = requireNonNullElse(targetNames, Collections.emptyList());
        final long stdinCnt = this.targetNames.stream()
            .filter("-"::equals)
            .count();
        if(this.queryType == QUERY_STDIN && stdinCnt != 0)
        {
            throw new CCGrepException("Standard input cannot be used for both query and target.");
        }
        if(stdinCnt >= 2)
        {
            throw new CCGrepException("Standard input cannot be used multiply for target.");
        }
    }

    private static <T> T requireNonNullElse(T value, T defaultValue)
    {
        return value == null? defaultValue: value;
    }
}
