package jp.ac.osaka_u.ist.sel.ccgrep;


import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.cli.*;


public class Frontend
{
    boolean isHelpEnabled = false;
    boolean isRecursiveEnabled = false;
    boolean isLogEnabled = false;
    String blindLevelName = "";
    String languageName = null;
    String printOption = "";
    List<String> fixedIds = Collections.emptyList();
    boolean isJsonEnabled = false;
    boolean isTimeEnabled = false;
    int maxCount = -1;

    String needleFileName = null;
    String needleCode = null;
    List<String> haystackNames;

    public static Frontend process(String[] args)
    {
        final Frontend fe = new Frontend();
        if(args.length == 0)
        {
            args = new String[]{"-h"};
        }
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
            if(cl.hasOption("json"))
            {
                fe.isJsonEnabled = true;
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
                fe.needleFileName = cl.getOptionValue("file");
            }
            if(cl.hasOption("fix"))
            {
                fe.fixedIds = Arrays.asList(cl.getOptionValue("fix")
                                .split("\\|"));
            }
            List<String> restArgs = cl.getArgList();
            if(fe.needleFileName == null)
            {
                if(!restArgs.isEmpty())
                {
                    fe.needleCode = restArgs.get(0);
                    restArgs = restArgs.subList(1, restArgs.size());
                }
                else
                {
                    System.err.println("Illegal arguments.");
                    showHelp();
                    return null;
                }
            }
            fe.haystackNames =
                !restArgs.isEmpty()? restArgs
                : fe.isRecursiveEnabled? Collections.singletonList(".")
                : Collections.singletonList("-");
            return fe;
        }
        catch(final ParseException e)
        {
            System.err.println("Illegal command line argument:" + e.getMessage());
            return null;
        }
    }

    public static void showHelp()
    {
        final String appName = "ccgrep";
        final String appSyntax = appName + " [OPTIONS]... QUERY_CODE [TARGETS]..." + System.lineSeparator()
                                + "       " + appName + " [OPTIONS]... -f QUERY_FILE [TARGETS]...";

        final String header = "\nCode clone detector like grep command.\n\n"
                            + "Example: ccgrep -r -p n -f query.java target/\n"
                            + "         ccgrep -r -p fn 'int a = 1;' target/\n"
                            + "         (use single quote to suppress variable expansion)\n\n";

                            final String footer = "\nWhen TARGETS is -, read standard input."
            + " With no TARGETS, read . if a command-line -r is given, - otherwise."
            + " Exit status is 0 if any clone is detected, 1 otherwise;"
            + " if any error occurs, the exit status is 2.";

        new HelpFormatter().printHelp(appSyntax, header, options, footer);
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
                + " When `c` set, print ONLY the count of clones."
                + " When `l` set, print ONLY file name per matched files."
                + " When `h` set, NOT print file names."
                + " When `n` set, print line numbers."
                + " When `f` set, print whole code of clones."
                + " When `e` set, comment out the file name and line numbers."
            )
            .hasArg()
            .argName("OPTION")
            .build()
        )
        .addOption(
            Option.builder("f")
            .longOpt("file")
            .desc("obtain query from file. CANNOT give query as code string at once.")
            .hasArg()
            .argName("FILES")
            .build()
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
            .longOpt("json")
            .desc("print clones with JSON format.")
            .build()
        )
        .addOption(
            Option.builder()
            .longOpt("fix")
            .desc("specify identifier(s) to match exactly the same ones (e.g. 'getValue|string').")
            .hasArg()
            .argName("IDS")
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
        );
}
