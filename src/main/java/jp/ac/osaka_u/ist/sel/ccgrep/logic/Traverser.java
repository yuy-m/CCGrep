package jp.ac.osaka_u.ist.sel.ccgrep.logic;


import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.IPrinter;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> extensionMatcher;
    private final Predicate<String> includeMatcher;
    private final Predicate<String> excludeMatcher;
    private final IPrinter verbosePrinter;

    public Traverser(
        IDetector detector, boolean isRecursiveEnabled,
        List<String> extensions, List<String> includePatterns, List<String> excludePatterns,
        IPrinter verbosePrinter
    )
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.extensionMatcher = fileName -> FilenameUtils.isExtension(fileName, extensions);
        this.includeMatcher = includePatterns.isEmpty()
            ? fileName -> true
            : fileName -> includePatterns.stream().anyMatch(
                            p -> FilenameUtils.wildcardMatchOnSystem(fileName, p));
        this.excludeMatcher = fileName ->
                excludePatterns.stream().anyMatch(
                    p -> FilenameUtils.wildcardMatchOnSystem(fileName, p));
        this.verbosePrinter = verbosePrinter;
    }

    public CloneList.Statistic traverse(List<String> haystackNames, int maxCount)
    {
        try(Stream<String> s1 = haystackNames.stream().flatMap(this::fileStream))
        {
            verbosePrinter.printHeader();
            final CloneList.Statistic stat = new CloneList.Statistic();
            stat.startStopwatch();
            ///*
            final Iterator<String> it = s1.iterator();
            boolean needDelim = false;
            while(it.hasNext() && (maxCount < 0 || stat.countAllClone() < maxCount))
            {
                final int restCount = maxCount < 0? -1: maxCount - stat.countAllClone();
                final CloneList cl = detector.detect(it.next(), restCount);
                if(verbosePrinter.isFilePrintable(cl))
                {
                    verbosePrinter.printFileInLoop(cl, needDelim);
                    needDelim = true;
                }
                stat.add(cl);
            } //*/

            /*
            final boolean[] needDelim = {false};
            s1
                .map(fileName -> detector.detect(fileName, -1))
                .forEachOrdered(cl -> {
                    stat.add(cl);
                    needDelim[0] |= verbosePrinter.printFile(cl, needDelim[0]);
                });//*/

            stat.stopStopwatch();
            verbosePrinter.printFooter(stat);
            return stat;
        }
    }

    private Stream<String> fileStream(String haystackName)
    {
        if("-".equals(haystackName))
        {
            return Stream.of(haystackName);
        }
        Path haystackPath;
        try{
            haystackPath = Paths.get(haystackName);
        }
        catch(InvalidPathException e)
        {
            System.err.println("ccgrep: " + haystackName + ": No such file or directory");
            return Stream.empty();
        }
        if(!Files.exists(haystackPath))
        {
            System.err.println("ccgrep: " + haystackName + ": No such file or directory");
            return Stream.empty();
        }
        if(!Files.isDirectory(haystackPath))
        {
            final String name = FilenameUtils.getName(haystackName);
            if( ( includeMatcher.test(haystackName) ||  includeMatcher.test(name))
             && (!excludeMatcher.test(haystackName) && !excludeMatcher.test(name))
             && (extensionMatcher.test(haystackName) || isTextFile(haystackPath))
            )
            {
                return Stream.of(haystackName);
            }
            return Stream.empty();
        }
        else if(isRecursiveEnabled)
        {
            try{
                return Files.walk(haystackPath)
                    .map(path -> path.toString())
                    .filter(fileName -> extensionMatcher.test(fileName))
                    .filter(fileName -> !excludeMatcher.test(FilenameUtils.getName(fileName)))
                    .filter(fileName -> includeMatcher.test(FilenameUtils.getName(fileName)));
            }
            catch(IOException e)
            {
                System.err.println("ccgrep : " + e.getMessage() + ": Cannot read");
                return Stream.empty();
            }
        }
        else
        {
            System.err.println("ccgrep: " + haystackName + ": Is a directory");
            return Stream.empty();
        }
    }

    private static boolean isTextFile(Path path)
    {
        try(InputStream is = Files.newInputStream(path);
            InputStream bis = new BufferedInputStream(is))
        {
            final byte[] b = new byte[1];
            while(bis.read(b, 0, 1) > 0)
            {
                if(b[0] == 0)
                {
                    return false;
                }
            }
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }
}
