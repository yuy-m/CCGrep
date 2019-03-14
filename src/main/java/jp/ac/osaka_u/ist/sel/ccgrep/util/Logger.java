package jp.ac.osaka_u.ist.sel.ccgrep.util;


import java.io.PrintStream;


public class Logger
{
    public static final Logger debugLogger = new Logger(System.err, false);
    public static final Logger errorLogger = new Logger(System.err, true);

    private final PrintStream stream;
    private boolean isEnabled;

    private Logger(PrintStream stream, boolean isEnabled)
    {
        this.stream = stream;
        this.isEnabled = isEnabled;
    }

    public void enable(boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public void print(Object msg)
    {
        if(isEnabled)
        {
            stream.print(msg.toString());
        }
    }

    public void println(Object msg)
    {
        if(isEnabled)
        {
            stream.println(msg.toString());
        }
    }

    public void println()
    {
        if(isEnabled)
        {
            stream.println();
        }
    }
}
