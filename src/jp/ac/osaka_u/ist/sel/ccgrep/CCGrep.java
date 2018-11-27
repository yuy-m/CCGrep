package jp.ac.osaka_u.ist.sel.ccgrep;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jargp.*;

import jp.ac.osaka_u.ist.sel.ccgrep.BlindLevel;
import jp.ac.osaka_u.ist.sel.ccgrep.GrepPrinter;
import jp.ac.osaka_u.ist.sel.ccgrep.Language;
import jp.ac.osaka_u.ist.sel.ccgrep.TokenSequenceDetector;

public class CCGrep
{
    static final boolean DEBUG = false;
    public static void main(String[] args)
    {
        final int returnCode = new CCGrep().frontend(args);
        System.exit(returnCode);
    }

    private static final ParameterDef[] pd = new ParameterDef[]{
        new StringDef('b', "blindLevelName", "set blind level none / consistent(by default) / full."),
        new StringDef('l', "languageName", "set target language c / c++ / java(by default) / python3."),
        new StringDef('p', "printOption", "set printing option l/N/f/e/c like `-p lN`. When `l` set, print only file names. When `N` set, Not print line numbers. When `f` set, print whole code of clones. When `e` set, comment out the file name and line numbers. When `c` set, print only the count of clones."),
        new StringDef('f', "needleFileName", "obtain needle from file. CANNOT give needle as code string at once."),
        //new IntDef('m', "maxCount", "stop after NN clones."),
        new BoolDef('h', "help", "show help", true)
    };

    private static void showHelp(ArgumentProcessor ap)
    {
        System.out.println("ccgrep.sh [options]... -f needleFile haystackFiles...");
        System.out.println("ccgrep.sh [options]... needleCode haystackFiles...");
        System.out.println(" For Windows, use ccgrep.bat instead.");
        ap.listParameters(80, System.out);
    }

    static void debugprint(Object msg)
    {
        if(DEBUG)
        {
            System.err.print(msg.toString());
        }
    }

    static void debugprintln(Object msg)
    {
        if(DEBUG)
        {
            System.err.println(msg.toString());
        }
    }

    static void debugprintln()
    {
        if(DEBUG)
        {
            System.err.println();
        }
    }

    String languageName = null;
    String printOption = "";
    String blindLevelName = "";
    String needleFileName = null;
    String needleCode = null;
    // int maxCount = -1;

    boolean help;

    int frontend(String[] args)
    {
        final ArgumentProcessor ap = new ArgumentProcessor(pd);
        ap.processArgs(args, this);

        if(this.help)
        {
            showHelp(ap);
            return 0;
        }

        if(this.needleFileName == null)
        {
            if(ap.getArgs().hasNext())
            {
                this.needleCode = ap.getArgs().next();
            }
            else
            {
                System.err.println("Illegal arguments.");
                showHelp(ap);
                return 2;
            }
        }
        String[] haystackNames;
        if(ap.getArgs().hasNext())
        {
            final List<String> l = new ArrayList<>();
            while(ap.getArgs().hasNext())
            {
                l.add(ap.getArgs().next());
            }
            haystackNames = new String[l.size()];
            l.toArray(haystackNames);
        }
        else
        {
            haystackNames = new String[]{"."};
        }

        final long st = System.nanoTime();
        final int returnCode = grep(haystackNames);
        final long et = System.nanoTime();

        final long milli = (et - st) / 1000000;
        // System.err.println((milli / 1000.0) + " sec");

        return returnCode;
    }

    int grep(String[] haystackNames)
    {
        final Language language = findLanguage();
        if(language == null)
        {
            return 2;
        }
        debugprintln("language: " + language);

        final ITokenizer tokenizer = new AntlrTokenizer(language);

        final BlindLevel blindLevel = BlindLevel.findByName(blindLevelName);
        debugprintln("blind level: " + blindLevel);

        final IDetector detector = needleFileName == null
            ? TokenSequenceDetector.withNeedleFromCode(tokenizer, needleCode, blindLevel)
            : TokenSequenceDetector.withNeedleFromFile(tokenizer, needleFileName, blindLevel);

        debugprintln("traversing...");
        final Traverser traverser = new Traverser(detector, language::matchesExtension);
        final List<Clone> clones = Arrays.stream(haystackNames)
            .map(Paths::get)
            .map(traverser::traverse)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        debugprintln("finish.");
        if(traverser.getFileCount() == 0)
        {
            debugprintln("no file found.");
            return 2;
        }
        debugprintln(clones.size() + " clone(s) found.");

        printResult(clones, language);
        return clones.isEmpty()? 1: 0;
    }

    private Language findLanguage()
    {
        if(languageName != null)
        {
            try{
                return Language.findByName(languageName);
            }
            catch(NoSuchElementException e)
            {
                System.err.println("The language " + languageName + " is not supported.");
                return null;
            }
        }
        else if(needleFileName != null)
        {
            try{
                return Language.findByFileNameWithExtension(needleFileName);
            }
            catch(NoSuchElementException e)
            {
                System.err.println("No language found from the extension of " + needleFileName);
                System.err.println("specify languge by `-l LANG` or `-f FILE.EXT`");
                return null;
            }
        }
        else
        {
            return Language.findByName("java");
        }
    }

    private void printResult(List<Clone> clones, Language language)
    {
        debugprint("printing...");
        if(printOption.contains("c"))
        {
            System.out.println(clones.size());
        }
        else
        {
            new GrepPrinter(clones)
                .println(
                    new GrepPrinter.Option(language)
                        .enableGrepLike(!printOption.contains("f"))
                        .enableFileName(true)
                        .enableLine(!printOption.contains("N"))
                        .enableCode(!printOption.contains("l"))
                        .enableEscape(printOption.contains("e"))
                );
        }
        debugprintln("finish.");
    }
}

