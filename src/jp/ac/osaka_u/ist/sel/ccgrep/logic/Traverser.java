package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> fileMatcher;

    public Traverser(IDetector detector, boolean isRecursiveEnabled, Predicate<String> fileMatcher)
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.fileMatcher = fileMatcher;
    }

    public List<CloneList> traverse(List<String> haystackNames)
    {
        try(Stream<String> s = haystackNames.stream())
        {
            return s
                .flatMap(this::traverseImpl)
                .collect(Collectors.toList());
        }
    }

    private final Stream<CloneList> traverseImpl(String haystackName)
    {
        Path haystackPath;
        if("-".equals(haystackName)
            || !Files.isDirectory(haystackPath = Paths.get(haystackName))
        )
        {
            return Stream.of(detector.detect(haystackName));
        }
        else
        {
            try{
                return Files.walk(haystackPath, isRecursiveEnabled? Integer.MAX_VALUE: 1)
                    .map(path -> path.toString())
                    .filter(fileMatcher::test)
                    .map(detector::detect);
            }
            catch(IOException e)
            {
                System.err.println("Error: cannot read file " + e.getMessage());
                return Stream.empty();
            }
        }
    }
}
