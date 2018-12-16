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
import jp.ac.osaka_u.ist.sel.ccgrep.printer.IPrinter;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> fileMatcher;
    private final IPrinter verbosePrinter;

    public Traverser(
        IDetector detector, boolean isRecursiveEnabled,
        Predicate<String> fileMatcher, IPrinter verbosePrinter
    )
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.fileMatcher = fileMatcher;
        this.verbosePrinter = verbosePrinter;
    }

    public CloneList.Statistic traverse(List<String> haystackNames, int maxCount)
    {
        try(Stream<String> s1 = haystackNames.stream().flatMap(this::fileStream))
        {
            verbosePrinter.printHeader();
            ///*
            final Iterator<String> it = s1.iterator();
            final CloneList.Statistic stat = new CloneList.Statistic();
            boolean needDelim = false;
            while(it.hasNext() && (maxCount < 0 || stat.countAllClone() < maxCount))
            {
                final int restCount = maxCount < 0? -1: maxCount - stat.countAllClone();
                final CloneList cl = detector.detect(it.next(), restCount);
                stat.add(cl);
                needDelim |= verbosePrinter.printFile(cl, needDelim);
            }
            /*
            final CloneList.Statistic stat = new CloneList.Statistic();
            final boolean[] needDelim = {false};
            s1
                .map(fileName -> detector.detect(fileName, -1))
                .forEachOrdered(cl -> {
                    stat.add(cl);
                    needDelim[0] |= verbosePrinter.printFile(cl, needDelim[0]);
                });//*/
            verbosePrinter.printFooter(stat);
            verbosePrinter.printNewLine();
            return stat;
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
