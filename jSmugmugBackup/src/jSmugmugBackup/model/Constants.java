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
	public static final String version = "0.2";
    public static final String logfile = "jSmugmugBackup.log";
    public static final int retryWait = 20000; //time to wait before retrying

    
	public static final String SmugmugServerURL = "https://api.smugmug.com/hack/json/1.2.0/";
	public static final String SmugmugAPIKey = "EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb"; //not mine yet - from SmugFig API
	public static final String SmugmugAPIVersion = "1.2.0";
	
	public static final int UploadFileSizeLimit = 512*1024*1024; //512MB
	public static final String UploadIgnoreFilePostfix = ".jSmugmugBackup-upload-ignore.tag";
	
	//...hope thats all possible types
	public static final String[] supportedFileTypes = {".jpg", ".jpeg", ".png", ".gif", ".tiff",
		                                               ".avi", ".mp4", ".mpg", ".mpeg", ".mov", ".m4a", ".m4v", ".wmv", /*".xvid",*/ ".flv", ".3gp"};

	public static final FilenameFilter supportedFileTypesFilter = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			for (String fileEnding : Constants.supportedFileTypes)
			{
				if (name.toLowerCase().endsWith(fileEnding)) return true;
			}
			return false;
		}
	};
	
	public static final FileFilter directoryFileFilter = new FileFilter()
	{
        public boolean accept(File file) { return file.isDirectory(); }
    };

    public static final class FileComparator implements Comparator
    {
    	private Collator c = Collator.getInstance();
    	
    	public int compare(Object o1, Object o2)
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
