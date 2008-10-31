/*
 * Created on Oct 31, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper
{
    public static String computeMD5Hash(File file)
    {    	
		//read local file
		byte[] buffer = new byte[(int)file.length()];
		InputStream is = null;
    	try
    	{
			is = new FileInputStream(file);
			is.read(buffer); //null pointer exception???
			is.close();
		}
    	catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }

		//compute md5 from local file
		String md5sum = null;
		try { md5sum = AeSimpleMD5.MD5(buffer); }
		catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
    	
    	return md5sum;
    }
    
	public static String getTimeString()
	{
		Date date = new Date();
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
	}
	
    public static void pause(long millisecs)
    {
    	try { Thread.sleep(millisecs); }
    	catch (InterruptedException e) {}
    }
}
