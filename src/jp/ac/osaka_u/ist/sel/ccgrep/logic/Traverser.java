package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> fileMatcher;
    private final Predicate<String> directoryMatcher = (__) -> true;

    private int fileCount = 0;

    public Traverser(IDetector detector, boolean isRecursiveEnabled, Predicate<String> fileMatcher)
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.fileMatcher = fileMatcher;
    }

    int getFileCount()
    {
        return fileCount;
    }

    public List<CloneList> traverse(List<String> haystackNames)
    {
        return haystackNames.stream()
            .map(Paths::get)
            .map(path -> traverseImpl(path, true))
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());
    }

    private final List<CloneList> traverseImpl(Path haystackPath, boolean alwaysMatch)
    {
        if(Files.isDirectory(haystackPath))
        {
            if(alwaysMatch || (isRecursiveEnabled && directoryMatcher.test(haystackPath.toString())))
            {
                try{
                    return Files.list(haystackPath)
                        .parallel()
                        .flatMap(dirPath -> traverseImpl(dirPath, false).stream())
                        .collect(Collectors.toList());
                }
                catch(IOException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
        else if(alwaysMatch || "-".equals(haystackPath.toString()) || fileMatcher.test(haystackPath.toString()))
        {
            ++fileCount;
            final CloneList cl = detector.detect(haystackPath.toString());
            return cl.isEmpty()
                ? Collections.emptyList()
                : Collections.singletonList(cl);
        }
        return Collections.emptyList();
    }
}
