package ccgrep;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.jargp.*;

public class CCGrep
{
    static boolean DEBUG = false;
    public static void main(String[] args)
    {
        final CCGrep ccgrep = new CCGrep();

        final ArgumentProcessor ap = new ArgumentProcessor(
            new ParameterDef[]{
                new BoolDef('b', "isBlindEnabled", "blind identifiers and basic types.", true),
                new BoolDef('l', "isFileNameOnlyEnabled", "print only file name of clones.", true),
                new BoolDef('N', "isLineNumberEnabled", "NOT print line number.", false),
                new BoolDef('c', "isCountEnabled", "print only count of clones.", true),
                new IntDef('m', "maxCount", "stop after NN clones."),
                new BoolDef('h', "help", "show help", true)
            }
        );
        ap.processArgs(args, ccgrep);

        if(ccgrep.help)
        {
            ap.listParameters(80, System.out);
            System.exit(0);
        }

        int argidx = 0;
        final String languageName = ap.getArgs().next();
        final String needleFileName = ap.getArgs().next();
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
            haystackNames = new String[]{"-"};
        }

        final long st = System.nanoTime();
        final int returnCode = ccgrep.grep(languageName, needleFileName, haystackNames);
        final long et = System.nanoTime();

        final long milli = (et - st) / 1000000;
        System.err.println((milli / 1000.0) + " sec");

        System.exit(returnCode);
    }

    static void debugprint(String msg)
    {
        if(DEBUG)
        {
            System.err.print(msg);
        }
    }

    static void debugprintln(String msg)
    {
        if(DEBUG)
        {
            System.err.println(msg);
        }
    }

    boolean isBlindEnabled;
    boolean isCountEnabled;
    boolean isLineNumberEnabled = true;
    boolean isFileNameOnlyEnabled;
    int maxCount = -1;

    boolean help;

    int grep(String languageName, String needleFileName, String[] haystackNames)
    {
        final Language language = Language.findByName(languageName);
        language.enableBlind(isBlindEnabled);

        final Tokenizer tokenizer = new Tokenizer(language);

        final Detector detector = new Detector(tokenizer, needleFileName);

        final List<Clone> clones = traverseHaystack(detector, haystackNames);

        printResult(clones);
        return clones.isEmpty()? 1: 0;
    }

    List<Clone> traverseHaystack(Detector detector, String[] haystackNames)
    {
        debugprintln("traversing...");
        final List<Clone> clones =
            new Traverser(detector/*, maxCount*/)
                .traverse(haystackNames);
        debugprintln("finish.");
        debugprintln(clones.size() + " clone(s) found.");
        return clones;
    }

    void printResult(List<Clone> clones)
    {
        debugprint("printing...");
        if(isCountEnabled)
        {
            System.out.println(clones.size());
        }
        else
        {
            new GrepPrinter(clones).println(true, isLineNumberEnabled, !isFileNameOnlyEnabled);
        }
        debugprintln("finish.");
    }
}

