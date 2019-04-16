package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.cli.*;

import jp.ac.osaka_u.ist.sel.ccgrep.model.Language;

public class Frontend
{
    boolean isHelpEnabled = false;
    boolean isRecursiveEnabled = false;
    boolean isLogEnabled = false;
    boolean isErrorMessageEnabled = true;
    String blindLevelName = "";
    String languageName = null;
    String printOption = "";
    List<String> fixedIds = Collections.emptyList();
    boolean isJsonEnabled = false;
    boolean isXmlEnabled = false;
    boolean isTimeEnabled = false;
    int maxCount = -1;
    boolean isFileMatchingEnabled = false;
    List<String> includePatterns = Collections.emptyList();
    List<String> excludePatterns = Collections.emptyList();
    boolean isIgnoreExtensionEnabled = false;

    String needle = null;
    int needleType = -1;
    static final int NEEDLE_CODE = 0;
    static final int NEEDLE_FILE = 1;
    static final int NEEDLE_STDIN = 2;
    List<String> haystackNames;

    public static Frontend process(String[] args)
    {
        final Frontend fe = new Frontend();
        try {
            final CommandLine cl = new DefaultParser().parse(options, args);

            if(cl.hasOption("help"))
            {
                fe.isHelpEnabled = true;
                showHelp();
                return fe;
            }
            if(cl.hasOption("log"))
            {
                fe.isLogEnabled = true;
            }
            if(cl.hasOption("no-messages"))
            {
                fe.isErrorMessageEnabled = false;
            }
            if(cl.hasOption("json"))
            {
                fe.isJsonEnabled = true;
            }
            if(cl.hasOption("xml"))
            {
                fe.isXmlEnabled = true;
            }
            if(cl.hasOption("time"))
            {
                fe.isTimeEnabled = true;
            }
            if(cl.hasOption("recursive"))
            {
                fe.isRecursiveEnabled = true;
            }
            if(cl.hasOption("blind"))
            {
                fe.blindLevelName = cl.getOptionValue("blind");
            }
            if(cl.hasOption("max-count"))
            {
                fe.maxCount = Integer.parseUnsignedInt(cl.getOptionValue("max-count"));
            }
            if(cl.hasOption("file-match"))
            {
                fe.isFileMatchingEnabled = true;
            }
            if(cl.hasOption("language"))
            {
                fe.languageName = cl.getOptionValue("language");
            }
            if(cl.hasOption("print"))
            {
                fe.printOption = cl.getOptionValue("print");
            }
            if(cl.hasOption("file"))
            {
                fe.needle = cl.getOptionValue("file");
                fe.needleType = NEEDLE_FILE;
            }
            if(cl.hasOption("stdin-query"))
            {
                fe.needle = cl.getOptionValue("stdin-query");
                fe.needleType = NEEDLE_STDIN;
            }
            if(cl.hasOption("e"))
            {
                fe.needle = String.join(" $| ", cl.getOptionValues("e"));
                fe.needleType = NEEDLE_CODE;
            }
            if(cl.hasOption("fix"))
            {
                fe.fixedIds = Arrays.asList(cl.getOptionValues("fix"));
            }
            if(cl.hasOption("include"))
            {
                fe.includePatterns = Arrays.asList(cl.getOptionValues("include"));
            }
            if(cl.hasOption("exclude"))
            {
                fe.excludePatterns = Arrays.asList(cl.getOptionValues("exclude"));
            }
            if(cl.hasOption("ignore-extension"))
            {
                fe.isIgnoreExtensionEnabled = true;
            }
            List<String> restArgs = cl.getArgList();
            if(fe.needleType == -1)
            {
                if(!restArgs.isEmpty())
                {
                    fe.needle = restArgs.get(0);
                    fe.needleType = NEEDLE_CODE;
                    restArgs = restArgs.subList(1, restArgs.size());
                }
                else
                {
                    showErrorHelp(null);
                    return null;
                }
            }
            fe.haystackNames =
                !restArgs.isEmpty()? restArgs
                : fe.isRecursiveEnabled? Collections.singletonList(".")
                : Collections.singletonList("-");
            final long stdinCnt = fe.haystackNames.stream()
                                    .filter("-"::equals)
                                    .count();
            if(stdinCnt >= 2 || (fe.needleType == NEEDLE_STDIN && stdinCnt != 0))
            {
                showErrorHelp("Do not use standard input more than once.");
                return null;
            }
            return fe;
        }
        catch(final ParseException e)
        {
            showErrorHelp(e.getMessage());
            return null;
        }
    }

    private static final String appName = "ccgrep";
    private static final String appSyntax1 = appName + " [OPTIONS]... QUERY_CODE [TARGETS]...";
    private static final String appSyntax2 = appName + " [OPTIONS]... -f QUERY_FILE [TARGETS]...";

    public static void showErrorHelp(String msg)
    {
        if(msg != null)
        {
            System.err.println("ccgrep: " + msg);
        }
        System.err.println(appSyntax1);
        System.err.println(appSyntax2);
        System.err.println("Try '" + appName + " --help' for more information.");
    }

    public static void showHelp()
    {
        final String header = "\nCode clone detector like grep command.\n\n"
                            + "Example: ccgrep -r -p n -f query.java target/\n"
                            + "         ccgrep -r -p fn 'int a = 1;' target/\n"
                            + "         (use single quote to suppress variable expansion)\n\n";

        final String footer = "\nWhen TARGETS is -, read standard input."
            + " With no TARGETS, read . if a command-line -r is given, - otherwise."
            + " Exit status is 0 if any clone is detected, 1 otherwise;"
            + " if any error occurs, the exit status is 2.\n\n"
            + "Files specified invividually are searched"
            + " only when they have extensions shown below or are text files."
            + " When -r is given, files in specified directories are searched"
            + " only when they have the extensions:\n"
            + Arrays.stream(Language.values())
                .map(l -> String.format(" %-10s : %s", l, String.join(",", l.getExtensions())))
                .collect(Collectors.joining("\n"));

        new HelpFormatter().printHelp(
            appSyntax1 + "\n       " + appSyntax2,
            header, options, footer
        );
    }

    private static final Options options = new Options()
        .addOption(
            Option.builder("b")
            .longOpt("blind")
            .desc("set blind level. none(Type 1) / consistent(p-match)(by default) / full(Type 2).")
            .hasArg()
            .argName("LEVEL")
            .build()
        )
        .addOption(
            Option.builder("l")
            .longOpt("language")
            .desc("set target language. c / c++ / java(by default) / python3."
                + " With `-f` option, the language can be inferred from the file extension.")
            .hasArg()
            .argName("LANG")
            .build()
        )
        .addOption(
            Option.builder("p")
            .longOpt("print")
            .desc("set printing option c/l/n/f/e like `-p fn`."
                + " If `c` is given, print the count of clones file by file."
                + " If `C` is given, print ONLY the count of all clones."
                + " If `l` is given, print ONLY file name per matched files."
                + " If `h` is given, NOT print file names."
                + " If `n` is given, print line numbers."
                + " If `N` is given, print pairs of start and end line number."
                + " If `f` is given, print whole code of clones."
                + " If `o` is given, print only the matched parts of a clone code."
                + " If `e` is given, comment out the file name and line numbers."
                + " If `m` is given, in XML/JSON, NOT print clone-code or files-without-clone."
            )
            .hasArg()
            .argName("OPTION")
            .build()
        )
        .addOptionGroup(
            new OptionGroup()
                .addOption(
                    Option.builder("f")
                    .longOpt("file")
                    .desc("obtain query from file. CANNOT give query as code string at once.")
                    .hasArg()
                    .argName("FILES")
                    .build()
                )
                .addOption(
                    Option.builder("s")
                    .longOpt("stdin-query")
                    .desc("obtain query from standard input. CANNOT give query as code string at once.")
                    .build()
                )
                .addOption(
                    Option.builder("e")
                    .desc("use PATTERN for matching.")
                    .hasArg()
                    .argName("PATTERN")
                    .build()
                )
        )
        .addOption(
            Option.builder("r")
            .longOpt("recursive")
            .desc("traverse directories recursively.")
            .build()
        )
        .addOption(
            Option.builder("h")
            .longOpt("help")
            .desc("show help.")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("log")
            .desc("print debug log to standard error.")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("no-messages")
            .desc("suppress error messages.")
            .build()
        )
        .addOptionGroup(
            new OptionGroup()
                .addOption(
                    Option.builder()
                    .longOpt("json")
                    .desc("print clones with JSON format.")
                    .build()
                )
                .addOption(
                    Option.builder()
                    .longOpt("xml")
                    .desc("print clones with XML format.")
                    .build()
                )
        )
        .addOption(
            Option.builder()
            .longOpt("fix")
            .desc("specify identifier to match exactly the same one.")
            .hasArg()
            .argName("ID")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("time")
            .desc("print processing time to standard error.")
            .build()
        )
        .addOption(
            Option.builder("m")
            .longOpt("max-count")
            .desc("stop after NUM clones.")
            .hasArg()
            .argName("NUM")
            .build()
        )
        .addOption(
            Option.builder("x")
            .longOpt("file-match")
            .desc("force QUERY to match only whole file.")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("include")
            .desc("search ONLY files that match FILE_PATTERN.")
            .hasArg()
            .argName("FILE_PATTERN")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("exclude")
            .desc("skip files matching FILE_PATTERN.")
            .hasArg()
            .argName("FILE_PATTERN")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("ignore-extension")
            .desc("search all files ignoring file extensions.")
            .build()
        );
}
