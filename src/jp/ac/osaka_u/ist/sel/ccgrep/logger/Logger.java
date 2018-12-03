package jp.ac.osaka_u.ist.sel.ccgrep.logger;


import java.io.PrintStream;


public class Logger
{
    public static final Logger debugLogger = new Logger(System.err);

    private final PrintStream stream;
    private boolean isEnabled = false;

    private Logger(PrintStream stream)
    {
        this.stream = stream;
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
