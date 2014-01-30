/*
 * Created on Oct 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;


import jSmugmugBackup.config.GlobalConfig;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger
{
    // Protected constructor is sufficient to suppress unauthorized calls to the constructor
    protected Logger()
    {
        this.config = GlobalConfig.getInstance();


        if (this.config.getPersistentAppendLogfile() == false)
        {
            try
            {
                //create a new and empty logfile
                FileWriter out = new FileWriter(new File(this.config.getPersistentLogfile()), false);
                out.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }



        //write separator to file (only once per program execution)
        try
        {
            FileWriter out = new FileWriter(new File(this.config.getPersistentLogfile()), true);
            out.write("\n----------------------------------------------------------------------------------------------------\n");
            out.close();
        }
        catch (IOException e) { e.printStackTrace(); }

    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.instance , not before.
     */
    private static class LoggerHolder
    {
        private final static Logger INSTANCE = new Logger();
    }

    public static Logger getInstance()
    {
        return LoggerHolder.INSTANCE;
    }

    //------------------------------------
    private GlobalConfig config = null;
    private IView view = null;

    public void registerView(IView view)
    {
        this.view = view;
    }

    public void printLog(LogLevelEnum loglevel, String text)
    {
        this.printLog(loglevel, 0, text);
    }

    public void printLogLine(LogLevelEnum loglevel, int tabstop, String text)
    {
        this.printLog(loglevel, tabstop, text + "\n");
    }

    public void printLogFixedWidth(LogLevelEnum loglevel, int tabstop, String text, int width)
    {
        String result = null;

        if (text.length() < width)
        {
            result = text;
            for (int i = text.length(); i < width; i++) { result += " "; }
        }
        else if (text.length() > width)
        {
            if (width >= 8) { result = text.substring(0, width-3) + "..."; }
            else { result = text.substring(0, width-1); }
        }
        else { result = text; }

        this.printLog(loglevel, tabstop, result);
    }

    public void printLogFixedWidthRAL(LogLevelEnum loglevel, int tabstop, String text, int width)
    {
        String result = null;

        if (text.length() < width)
        {
            result = text;
            for (int i = text.length(); i < width; i++) { result = " " + result; }
        }
        else { result = text; }

        this.printLog(loglevel, tabstop, result);
    }

    private void printLog(LogLevelEnum loglevel, int tabstop, String text)
    {
        String outText = this.addOutputPrefix(loglevel, tabstop, text);

        //write to view
        if (this.view != null)
        {
            if (loglevel.compareTo(this.config.getPersistentLogVerbosity()) >= 0)
            {
                this.view.printLog(outText);
            }
        }

        //write to file
        try
        {
            FileWriter out = new FileWriter(new File(this.config.getPersistentLogfile()), true);
            out.write(outText);
            out.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private String addOutputPrefix(LogLevelEnum loglevel, int tabstop, String text)
    {
        if (loglevel.compareTo(LogLevelEnum.Debug) == 0)          { return "DEBUG:     " + text; }
        else if (loglevel.compareTo(LogLevelEnum.Info) == 0)      { return "INFO:      " + text; }
        else if (loglevel.compareTo(LogLevelEnum.Warning) == 0)   { return "WARNING:   " + text; }
        else if (loglevel.compareTo(LogLevelEnum.Error) == 0)     { return "ERROR:     " + text; }
        else if (loglevel.compareTo(LogLevelEnum.Exception) == 0) { return "EXCEPTION: " + text; }
        else if (loglevel.compareTo(LogLevelEnum.Message) == 0)   { return text; }

        for (int i = 0; i < tabstop; i++)
        {
            text = "   " + text;
        }

        return "ERROR: unknown loglevel (" + text + ")";
    }


}
