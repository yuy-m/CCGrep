package jp.ac.osaka_u.ist.sel.ccgrep.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;


import org.apache.commons.io.FilenameUtils;

import jp.ac.osaka_u.ist.sel.ccgrep.model.*;
import jp.ac.osaka_u.ist.sel.ccgrep.printer.IPrinter;
import static jp.ac.osaka_u.ist.sel.ccgrep.util.Logger.*;


public class Traverser
{
    private final IDetector detector;
    private final boolean isRecursiveEnabled;
    private final Predicate<String> extensionMatcher;
    private final Predicate<String> includeMatcher;
    private final Predicate<String> excludeMatcher;
    private final IPrinter verbosePrinter;
    private final boolean isParallelEnabled;

    public Traverser(
        IDetector detector, boolean isRecursiveEnabled,
        List<String> extensions, List<String> includePatterns, List<String> excludePatterns,
        IPrinter verbosePrinter,
        boolean isParallelEnabled
    )
    {
        this.detector = detector;
        this.isRecursiveEnabled = isRecursiveEnabled;
        this.extensionMatcher = extensions == null
            ? fileName -> isTextFile(Paths.get(fileName))
            : fileName -> FilenameUtils.isExtension(fileName, extensions);
        this.includeMatcher = includePatterns.isEmpty()
            ? fileName -> true
            : fileName -> includePatterns.stream().anyMatch(
                            p -> FilenameUtils.wildcardMatchOnSystem(fileName, p));
        this.excludeMatcher = fileName ->
                excludePatterns.stream().anyMatch(
                    p -> FilenameUtils.wildcardMatchOnSystem(fileName, p));
        this.verbosePrinter = verbosePrinter;
        this.isParallelEnabled = isParallelEnabled;
    }

    public CloneList.Statistic traverse(List<String> targetNames, int maxCount)
    {
        verbosePrinter.printHeader();
        final CloneList.Statistic stat = new CloneList.Statistic();
        stat.startStopwatch();

        final List<String> fileNames = targetNames.stream()
            .flatMap(this::fileStream)
            .collect(Collectors.toList());

        if(maxCount < 0)
        {
            final boolean[] needDelim = {false};
            (isParallelEnabled? fileNames.parallelStream(): fileNames.stream())
                .map(fileName -> detector.detectFile(fileName, -1))
                .forEachOrdered(cloneList -> {
                    if(verbosePrinter.isFilePrintable(cloneList))
                    {
                        verbosePrinter.printFileInLoop(cloneList, needDelim[0]);
                        needDelim[0] = true;
                    }
                    stat.add(cloneList);
                });
        }
        else
        {
            boolean needDelim = false;
            for(String fileName: fileNames)
            {
                if(stat.countAllClone() >= maxCount)
                {
                    break;
                }
                final int restCount = maxCount - stat.countAllClone();
                final CloneList cloneList = detector.detectFile(fileName, restCount);
                if(verbosePrinter.isFilePrintable(cloneList))
                {
                    verbosePrinter.printFileInLoop(cloneList, needDelim);
                    needDelim = true;
                }
                stat.add(cloneList);
            }
        }

        stat.stopStopwatch();
        verbosePrinter.printFooter(stat);
        return stat;
    }

    private Stream<String> fileStream(String targetName)
    {
        if("-".equals(targetName))
        {
            return Stream.of(targetName);
        }
        Path targetPath;
        try{
            targetPath = Paths.get(targetName);
        }
        catch(InvalidPathException e)
        {
            errorLogger.println("ccgrep: " + targetName + ": No such file or directory");
            return Stream.empty();
        }
        if(!Files.isReadable(targetPath))
        {
            errorLogger.println("ccgrep: " + targetName + ": Cannot be read");
            return Stream.empty();
        }
        else if(!Files.isDirectory(targetPath))
        {
            final String name = FilenameUtils.getName(targetName);
            if( ( includeMatcher.test(targetName) ||  includeMatcher.test(name))
             && (!excludeMatcher.test(targetName) && !excludeMatcher.test(name))
             && (extensionMatcher.test(targetName) || isTextFile(targetPath))
            )
            {
                return Stream.of(targetName);
            }
            return Stream.empty();
        }
        else if(isRecursiveEnabled)
        {
            return list(targetPath)
                .filter(p -> extensionMatcher.test(p.toString()))
                .filter(p -> !excludeMatcher.test(FilenameUtils.getName(p.toString())))
                .filter(p -> includeMatcher.test(FilenameUtils.getName(p.toString())))
                .filter(p -> {
                    if(Files.isReadable(p))
                    {
                        return true;
                    }
                    else
                    {
                        errorLogger.println("ccgrep: " + p + ": Cannot be read");
                        return false;
                    }
                })
                .map(p -> p.toString());
        }
        else
        {
            errorLogger.println("ccgrep: " + targetName + ": Is a directory");
            return Stream.empty();
        }
    }

    private Stream<Path> list(Path path)
    {
        try{
            return Files.list(path)
                .flatMap(p -> Files.isDirectory(p)? list(p): Stream.of(p));
        }
        catch(AccessDeniedException e)
        {
            errorLogger.println("ccgrep: " + path + ": Permission denied");
            return Stream.empty();
        }
        catch(FileSystemException e)
        {
            errorLogger.println("ccgrep: " + path + ": " + e.getMessage().replace("\r", "").replace("\n", ""));
            return Stream.empty();
        }
        catch(IOException e)
        {
            errorLogger.println("ccgrep: " + path + ": " + e + ": " + e.getMessage().replace("\r", "").replace("\n", ""));
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
