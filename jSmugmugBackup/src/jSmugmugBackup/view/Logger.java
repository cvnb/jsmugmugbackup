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
	
    public void printLog(String text)
    {
    	//write to view
    	if (this.view != null) this.view.printLog(text);
    	
    	//write to file
    	try
    	{
    		FileWriter out = new FileWriter(new File(this.config.getPersistentLogfile()), true);
    		out.write(text);
			out.close();
		}
    	catch (IOException e) { e.printStackTrace(); }    	
    }
	
    public void printLogLine(String text)
    {
    	this.printLog(text + "\n");
    }

    public void printLogFixedWidth(String text, int width)
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

        this.printLog(result);
    }

    public void printLogFixedWidthRAL(String text, int width)
    {
        String result = null;

        if (text.length() < width)
        {
            result = text;
            for (int i = text.length(); i < width; i++) { result = " " + result; }
        }
        else { result = text; }

        this.printLog(result);
    }


    /*
    // Verbose logging is disabled for the moment
    public void printVerboseLog(String text)
    {
        if (this.config.getConstantVerboseLogging()) { this.printLog(text); }
    }

    public void printVerboseLogLine(String text)
    {
        if (this.config.getConstantVerboseLogging()) { this.printLogLine(text); }
    }
    */

}
