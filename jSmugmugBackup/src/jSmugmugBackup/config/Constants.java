/*
 * Created on Sep 27, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.config;

import java.io.*;
import java.text.Collator;
import java.util.Comparator;

public class Constants
{
	public static final String version = "0.4 (dev)";
    public static final String logfile = "jSmugmugBackup.log";
    public static final int retryWait  = 20000; //time to wait before retrying
    
    public static final String smugmugUserAgentString = "jSmugmugBackup/v" + Constants.version;
	public static final String smugmugServerURL       = "https://api.smugmug.com/hack/json/1.2.0/";
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

	public static final String helpNotes =
		"notes:\n" +
	    " - Names on smugmug are handled as if they were unique, although it is for instance\n" +
	    "   possible to have two albums with the same name on smugmug. jSmugmugBackup will\n" +
	    "   always use the first name the matches and ignore the second entity.\n" +
	    " - on your local machine category names are mapped to subfolders, subcategory names\n" +
	    "   to subsubfolders and albums to subsubsubfolders.\n" +
	    " - If you want a certain file not to be uploaded, for instance because it takes too long\n" +
	    "   with your internet connection, you can set an ignore tag. Just create a file with\n" +
	    "   the name of the file you don't want jSmugmugBackup to upload plus\n" +
	    "   \".jSmugmugBackup-upload-ignore.tag\". Example: for the file \"mybigvideo.avi\"\n" +
	    "   create a file with the name \"mybigvideo.avi.jSmugmugBackup-upload-ignore.tag\"\n" +
	    " - uploaded albums are by default private, not searchable through smugmug and not searchable\n" +
	    "   through google ... right now, the only way to change that is the smugmug website\n" +
	    " - <to be continued>\n";
		
}
