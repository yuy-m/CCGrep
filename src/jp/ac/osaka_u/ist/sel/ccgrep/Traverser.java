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
    private final Predicate<String> fileMatcher;
    private final Predicate<String> directoryMatcher;
    // final int maxCloneCount;

    private int fileCount = 0;

    Traverser(IDetector detector, Predicate<String> fileMatcher, Predicate<String> directoryMatcher)
    {
        this.detector = detector;
        this.fileMatcher = fileMatcher;
        this.directoryMatcher = directoryMatcher;
    }

    Traverser(IDetector detector, Predicate<String> fileMatcher)
    {
        this(detector, fileMatcher, (__) -> true);
    }

    public List<Clone> traverse(Path haystackPath)
    {
        if(Files.isDirectory(haystackPath))
        {
            if(directoryMatcher.test(haystackPath.toString()))
            {
                final List<Clone> clones = new ArrayList<>();
                try{
                    Files.list(haystackPath)
                        .parallel()
                        .map(this::traverse)
                        .forEachOrdered(clones::addAll);
                }
                catch(IOException e)
                {
                    System.err.println(e.getMessage());
                }
                return clones;
            }
        }
        else if("-".equals(haystackPath.toString()) || fileMatcher.test(haystackPath.toString()))
        {
            ++fileCount;
            return detector.detect(haystackPath.toString());
        }
        return Collections.emptyList();
    }
}
