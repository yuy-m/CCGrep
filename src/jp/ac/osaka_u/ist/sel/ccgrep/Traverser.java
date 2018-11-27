package jp.ac.osaka_u.ist.sel.ccgrep;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.ArrayList;
import java.util.Arrays;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> fileMatcher;
    private final Predicate<String> directoryMatcher = (__) -> true;

    private int fileCount = 0;

    Traverser(IDetector detector, boolean isRecursiveEnabled, Predicate<String> fileMatcher)
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.fileMatcher = fileMatcher;
    }

    int getFileCount()
    {
        return fileCount;
    }

    public List<Clone> traverse(Path haystackPath)
    {
        return traverseImpl(haystackPath, true);
    }

    public final List<Clone> traverseImpl(Path haystackPath, boolean alwaysMatch)
    {
        if(Files.isDirectory(haystackPath))
        {
            if(alwaysMatch || (isRecursiveEnabled && directoryMatcher.test(haystackPath.toString())))
            {
                final List<Clone> clones = new ArrayList<>();
                try{
                    Files.list(haystackPath)
                        .parallel()
                        .map(dirPath -> traverseImpl(dirPath, false))
                        .forEachOrdered(clones::addAll);
                }
                catch(IOException e)
                {
                    System.err.println(e.getMessage());
                }
                return clones;
            }
        }
        else if(alwaysMatch || "-".equals(haystackPath.toString()) || fileMatcher.test(haystackPath.toString()))
        {
            ++fileCount;
            return detector.detect(haystackPath.toString());
        }
        return Collections.emptyList();
    }
}
