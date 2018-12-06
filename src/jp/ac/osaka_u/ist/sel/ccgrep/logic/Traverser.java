package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    public List<CloneList> traverse(List<String> haystackNames, int maxCount)
    {
        try(Stream<String> s1 = haystackNames.stream();
            Stream<String> s2 = s1.flatMap(this::fileStream))
        {
            int count = 0;
            Iterator<String> it = s2.iterator();
            final ArrayList<CloneList> ls = new ArrayList<>();
            while(it.hasNext() && (maxCount < 0 || count < maxCount))
            {
                final CloneList cl = detector.detect(it.next(), maxCount < 0? -1: maxCount - count);
                count += cl.size();
                ls.add(cl);
            }
            return ls;

            /*final int[] count = {0};
            final Stream<CloneList> s3 = StreamUtil.takeWhile(
                    s2.map(fileName -> {
                        final CloneList cl = detector.detect(fileName, maxCount < 0? -1: maxCount - count[0]);
                        count[0] += cl.size();
                        System.err.printf("1>%8d %8d +%8d\n", maxCount < 0? -1: maxCount - count[0], count[0], cl.size());
                        return cl;
                    }),
                    cl -> {
                        System.err.printf("2>%8d %8d +%8d\n", maxCount < 0? -1: maxCount - count[0], count[0], cl.size());
                        return maxCount < 0 || count[0] < maxCount;
                    }
                );

            return s3.collect(Collectors.toList());*/
        }
    }

    private Stream<String> fileStream(String haystackName)
    {
        Path haystackPath;
        if("-".equals(haystackName)
            || !Files.isDirectory(haystackPath = Paths.get(haystackName))
        )
        {
            return Stream.of(haystackName);
        }
        else
        {
            try{
                return Files.walk(haystackPath, isRecursiveEnabled? Integer.MAX_VALUE: 1)
                    .map(path -> path.toString())
                    .filter(fileMatcher::test);
            }
            catch(IOException e)
            {
                System.err.println("Error: cannot read file " + e.getMessage());
                return Stream.empty();
            }
        }
    }
}
