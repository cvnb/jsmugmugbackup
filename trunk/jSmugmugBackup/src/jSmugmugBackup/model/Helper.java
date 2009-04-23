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
import java.util.Vector;

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
    
	public static String getCurrentTimeString()
	{
		Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
	}
	
	public static String getDurationTimeString(long time)
	{				
		int hours   = (int)(time / 1000 / 60 / 60);
		int minutes = (int)(time / 1000 / 60);
		int seconds = (int)(time / 1000);
		
		return new String(hours + "h" + (minutes-(hours*60)) + "m" + (seconds-(minutes*60)) + "s");
	}
	
    public static void pause(long millisecs)
    {
    	try { Thread.sleep(millisecs); }
    	catch (InterruptedException e) {}
    }

    public static String getKeywords(Vector<String> tags)
    {
        String keywords;
        if (tags == null) { keywords = null; }
        else
        {
            keywords = "";
            for (String tag : tags) { keywords += "; " + Helper.encodeAsASCII(tag); }
            if (keywords.length() > 2) { keywords = keywords.substring(2); } //remove the leading two characters
        }

        return keywords;
    }

    public static Vector<String> getTags(String keywords)
    {
        Vector<String> tags = null;

        if ((keywords == null) || (keywords.equals(""))) { tags = null; }
        else
        {
            String[] tagsArray = keywords.split("; ");
            tags = new Vector<String>();

            //copy array into a Vector
            for (int i=0; i < tagsArray.length; i++) { tags.add(tagsArray[i]); }
        }

        return tags;
    }

    public static String encodeForURL(String str)
    {
        //todo: generalize

    	String encodedStr = str;

    	encodedStr = encodedStr.replace("%", "%25"); //do this first

    	encodedStr = encodedStr.replace(" ", "%20"); //space character
    	encodedStr = encodedStr.replace("#", "%23"); //not sure, if it is nesseciary
    	encodedStr = encodedStr.replace("+", "%2B");
    	encodedStr = encodedStr.replace("<", "%3C");
    	encodedStr = encodedStr.replace(">", "%3E");
        encodedStr = encodedStr.replace("[", "%5B");
    	encodedStr = encodedStr.replace("]", "%5D");
    	encodedStr = encodedStr.replace("?", "%3F");
        encodedStr = encodedStr.replace("&", "%26");
        encodedStr = encodedStr.replace("$", "%24");
        encodedStr = encodedStr.replace(",", "%2C");
        encodedStr = encodedStr.replace(";", "%3B");
        encodedStr = encodedStr.replace(":", "%3A");
        encodedStr = encodedStr.replace("/", "%2F");
        encodedStr = encodedStr.replace("=", "%3D");
        encodedStr = encodedStr.replace("@", "%40");


        //encoding german special characters
        encodedStr = encodedStr.replace("ß", "%DF");
        encodedStr = encodedStr.replace("ä", "%E4");
        encodedStr = encodedStr.replace("Ä", "%C4");
        encodedStr = encodedStr.replace("ö", "%F6");
        encodedStr = encodedStr.replace("Ö", "%D6");
        encodedStr = encodedStr.replace("ü", "%FC");
        encodedStr = encodedStr.replace("Ü", "%DC");


        //todo: french characters

    	//this.log.printLogLine("encodeForURL: " + str + " --> " + encodedStr);

    	return encodedStr;
    }

    public static String encodeAsASCII(String str)
    {
//        /*
//        // a little hack to check if a string contains any weired characters
//        try { s.getBytes("US-ASCII"); return true; }
//        catch (UnsupportedEncodingException ex) { return false; }
//        */

        if (str == null) return null;


        String encodedStr = str;

        //encoding german special characters
        encodedStr = encodedStr.replace("ß", "ss");
        encodedStr = encodedStr.replace("ä", "ae");
        encodedStr = encodedStr.replace("Ä", "Ae");
        encodedStr = encodedStr.replace("ö", "oe");
        encodedStr = encodedStr.replace("Ö", "Oe");
        encodedStr = encodedStr.replace("ü", "ue");
        encodedStr = encodedStr.replace("Ü", "Ue");

        //encoding french special characters
        encodedStr = encodedStr.replace("é", "e");
        encodedStr = encodedStr.replace("É", "E");
        encodedStr = encodedStr.replace("è", "e");
        encodedStr = encodedStr.replace("È", "E");
        encodedStr = encodedStr.replace("à", "a");
        encodedStr = encodedStr.replace("À", "A");
        encodedStr = encodedStr.replace("ù", "u");
        encodedStr = encodedStr.replace("Ù", "U");
        encodedStr = encodedStr.replace("â", "a");
        encodedStr = encodedStr.replace("Â", "A");
        encodedStr = encodedStr.replace("ê", "e");
        encodedStr = encodedStr.replace("Ê", "E");
        encodedStr = encodedStr.replace("î", "i");
        encodedStr = encodedStr.replace("Î", "I");
        encodedStr = encodedStr.replace("ô", "o");
        encodedStr = encodedStr.replace("Ô", "O");
        encodedStr = encodedStr.replace("û", "u");
        encodedStr = encodedStr.replace("Û", "U");
        encodedStr = encodedStr.replace("ë", "e");
        encodedStr = encodedStr.replace("Ë", "E");
        encodedStr = encodedStr.replace("ï", "i");
        encodedStr = encodedStr.replace("Ï", "I");
        encodedStr = encodedStr.replace("ç", "c");
        encodedStr = encodedStr.replace("Ç", "C");
        encodedStr = encodedStr.replace("æ", "ae");
        encodedStr = encodedStr.replace("Æ", "Ae");
        encodedStr = encodedStr.replace("œ", "oe");
        encodedStr = encodedStr.replace("Œ", "Oe");


        return encodedStr;
    }

    public static String extractFilenameFromURL(String url)
    {
        return url.substring(url.lastIndexOf("/")+1);
    }
}
