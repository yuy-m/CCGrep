package ccgrep;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.ArrayList;
import java.util.Arrays;


public class Traverser
{
    final Detector detector;
    // final int maxCloneCount;

    private int fileCount = 0;

    Traverser(Detector detector/*, int maxCloneCount*/)
    {
        this.detector = detector;
        // this.maxCloneCount = maxCloneCount;
    }

    // "-" means stdin
    public List<Clone> traverse(String[] haystackNames)
    {
        final List<Clone> clones = new ArrayList<>();
        Arrays.stream(haystackNames)
            .parallel()
            .map(Paths::get)
            .map(this::traverse)
            .forEachOrdered(clones::addAll);
        return clones;
    }

    /*private boolean isMaxCount()
    {
        return maxCloneCount >= 0 && fileCount.size() >= maxCloneCount;
    }*/

    public List<Clone> traverse(Path haystackPath)
    {
        /*if(isMaxCount())
        {
            return Collections.emptyList();
        }*/
        if(Files.isDirectory(haystackPath))
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
        else if(haystackPath.toString().equals("-") || detector.getLanguage().matchesExtension(haystackPath.toString()))
        {
            ++fileCount;
            return detector.detect(haystackPath.toString());
        }
        return Collections.emptyList();
    }
}
