/*
 * Created on Oct 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import jSmugmugBackup.config.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger
{
	// Protected constructor is sufficient to suppress unauthorized calls to the constructor
	protected Logger()
	{
    	try
    	{
    		//create a new and empty logfile
    		FileWriter out = new FileWriter(new File(Constants.logfile), false);
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
	IView view = null;
	
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
    		FileWriter out = new FileWriter(new File(Constants.logfile), true);
    		out.write(text);
			out.close();
		}
    	catch (IOException e) { e.printStackTrace(); }    	
    }
	
    public void printLogLine(String text)
    {
    	this.printLog(text + "\n");
    }
}
