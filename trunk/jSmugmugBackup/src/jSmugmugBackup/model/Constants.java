/*
 * Created on Sep 27, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model;

import java.io.*;
import java.text.Collator;
import java.util.Comparator;

public class Constants
{
	public static final String version = "0.3 (dev)";
    public static final String logfile = "jSmugmugBackup.log";
    public static final int retryWait  = 20000; //time to wait before retrying
    
    public static final String smugmugUserAgentString = "jSmugmugBackup/v" + Constants.version;
	public static final String smugmugServerURL       = "https://api.smugmug.com/hack/json/1.2.0/";
	//public static final String smugmugAPIKey          = "EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb"; //not mine yet - from SmugFig API
	public static final String smugmugAPIKey          = "bGLKncnGHUfZIwICUtqWsW3ejE1RYztJ";
	public static final String smugmugAPIVersion      = "1.2.0"; 	
	
	public static final int uploadFileSizeLimit        = 512*1024*1024; //512MB
	public static final String uploadIgnoreFilePostfix = ".jSmugmugBackup-upload-ignore.tag";
	
	public static final String pixelFilename = "res/pixel.jpg";
	
	//...hope thats all possible types
	public static final String[] supportedFileTypes_Images = {".jpg", ".jpeg", ".png", ".gif", ".tiff"};
	public static final String[] supportedFileTypes_Videos = {".avi", ".mp4", ".mpg", ".mpeg", ".mov", ".m4a", ".m4v", ".wmv", /*".xvid",*/ ".flv", ".3gp"};

	public static final FilenameFilter supportedFileTypesFilter = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			//check if it's an image
			for (String fileEnding : Constants.supportedFileTypes_Images)
			{
				if (name.toLowerCase().endsWith(fileEnding)) return true;
			}
			
			//check if it's a video
			for (String fileEnding : Constants.supportedFileTypes_Videos)
			{
				if (name.toLowerCase().endsWith(fileEnding)) return true;
			}
			
			//it neither an image nor a video
			return false;
		}
	};
	
	public static final FileFilter directoryFileFilter = new FileFilter()
	{
        public boolean accept(File file) { return file.isDirectory(); }
    };

	public static final class FileComparator implements Comparator<File>
    {
    	private Collator c = Collator.getInstance();
    	
		public int compare(File o1, File o2)
		{
    		if(o1 == o2) return 0;
    		
    		File f1 = (File) o1;
    		File f2 = (File) o2;
    		
    		if(f1.isDirectory() && f2.isFile()) return -1;
    		if(f1.isFile() && f2.isDirectory()) return 1;
    		
    		return c.compare(f1.getName(), f2.getName());
		}
    }

}
