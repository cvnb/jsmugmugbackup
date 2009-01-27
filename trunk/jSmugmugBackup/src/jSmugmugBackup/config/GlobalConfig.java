/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.config;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author paul
 */
public class GlobalConfig
{
   // Protected constructor is sufficient to suppress unauthorized calls to the constructor
   protected GlobalConfig()
   {
       this.loadConfig();
   }

   /**
    * SingletonHolder is loaded on the first execution of Singleton.getInstance()
    * or the first access to SingletonHolder.INSTANCE , not before.
    */
   private static class GlobalConfigHolder
   {
     private final static GlobalConfig INSTANCE = new GlobalConfig();
   }

   public static GlobalConfig getInstance() {
     return GlobalConfigHolder.INSTANCE;
   }

   //------------------------------------------------------------
   // intern constants, not getter
   private final String internconstantXMLConfigFilename = "config.xml";

   // constants --> getter
   private final String constantVersion                 = "0.5 (dev)";
   private final String constantSmugmugUserAgentString  = "jSmugmugBackup/v" + this.constantVersion;
   private final String constantSmugmugServerURL        = "https://api.smugmug.com/hack/json/1.2.0/";
   private final String constantSmugmugAPIKey           = "bGLKncnGHUfZIwICUtqWsW3ejE1RYztJ";
   private final String constantSmugmugAPIVersion       = "1.2.0";
   private final int    constantRetryWait               = 20000; //time to wait before retrying
   private final boolean constantHeavyRelogin           = true; // perform relogin for each queue item, this might improve stability during long lasting queue operations
   //private final boolean constantVerboseLogging         = true; //disabled for the moment
   private final int    constantUploadFileSizeLimit     = 512*1024*1024; //512MB
   private final String constantUploadIgnoreFilePostfix = ".jSmugmugBackup-upload-ignore.tag";
   private final String constantPixelFilename           = "res/pixel.jpg";
   private final String[] constantSupportedFileTypes_Images = {".jpg", ".jpeg", ".png", ".gif", ".tiff"};
   private final String[] constantSupportedFileTypes_Videos = {".avi", ".mp4", ".mpg", ".mpeg", ".mov", ".m4a", ".m4v", ".wmv", /*".xvid",*/ ".flv", ".3gp"}; //...hope thats all possible types
   private final String constantHelpNotes =
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
	    " - when downloading, existing files will be overriden without notice\n" +
        " - it seems like Smugmug can only handle filenames with ASCII characters\n" +
        "   german (Umlaute) and french special characters are beeing converted, but\n" +
        "   when downloading these images again, the special characters are lost\n" +
        " - verify only reports md5 checksum errors if they occur with an image, checksum errors\n" +
        "   with videos are not reported because videos are converted by smugmug after uploading\n" +
        "   which makes md5 checks fail on all videos\n" +
        " - <to be continued>\n";

   // constant objects
   private final FilenameFilter constantSupportedFileTypesFilter = new FilenameFilter()
   {
       public boolean accept(File dir, String name)
       {
           //check if it's an image
           for (String fileEnding :  constantSupportedFileTypes_Images)
           {
               if (name.toLowerCase().endsWith(fileEnding)) return true;
           }

           //check if it's a video
           for (String fileEnding : constantSupportedFileTypes_Videos)
           {
               if (name.toLowerCase().endsWith(fileEnding)) return true;
           }

           //it neither an image nor a video
           return false;
       }
   };

   private final FileFilter constantDirectoryFileFilter = new FileFilter()
   {
       public boolean accept(File file) { return file.isDirectory(); }
   };

   private final class FileComparator implements Comparator<File>
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
   private final FileComparator constantFileComparator = new FileComparator();


   // persistent options --> getter, xml
   private String persistentLogfile = null;
   private boolean persistentCheckMD5Sums;
   //private String  persistentDefaultUsername = ""; //don't activate this yet
   //private boolean persistentCaseSensitiveImageNames = true;
   //private boolean persistentCaseSensitiveFolderNames = true;
   //private String  persistentDefaultUploadVisibility = "private";
   

   // runtime config --> getter and setter
   private String rtconfigLoginSessionID = "";
//   private String rtconfigLoginUsername = null;
//   private String rtconfigLoginPassword = null;


   private void loadConfig()
   {
       //todo: load from xml-file

       Document doc = null;
       try
       {
           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
           DocumentBuilder db = dbf.newDocumentBuilder();

           doc = db.parse(new File(this.internconstantXMLConfigFilename));

       } catch (Exception ex) { Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex); }

       NodeList nodes = null;
       
       // persistentLogfile
       nodes = doc.getElementsByTagName("logfile");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentLogfile = nodes.item(0).getTextContent();
       //System.out.println("persistentLogfile: " + this.persistentLogfile);

       // persistentCheckMD5Sums
       nodes = doc.getElementsByTagName("checkMD5Sums");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentCheckMD5Sums = Boolean.parseBoolean(nodes.item(0).getTextContent());
       //System.out.println("persistentCheckMD5Sums: " + this.persistentCheckMD5Sums);

   }

   private void storeConfig()
   {
       //todo: write to xml-file
   }

   // constant getter
   public String   getConstantVersion()                   { return this.constantVersion; }
   public String   getConstantSmugmugUserAgentString()    { return this.constantSmugmugUserAgentString; }
   public String   getConstantSmugmugServerURL()          { return this.constantSmugmugServerURL; }
   public String   getConstantSmugmugAPIKey()             { return this.constantSmugmugAPIKey; }
   public String   getConstantSmugmugAPIVersion()         { return this.constantSmugmugAPIVersion; }
   public int      getConstantRetryWait()                 { return this.constantRetryWait; }
   public boolean  getConstantHeavyRelogin()              { return this.constantHeavyRelogin; }
   //public boolean  getConstantVerboseLogging()            { return this.constantVerboseLogging; } //disabled for the moment
   public int      getConstantUploadFileSizeLimit()       { return this.constantUploadFileSizeLimit; }
   public String   getConstantUploadIgnoreFilePostfix()   { return this.constantUploadIgnoreFilePostfix; }
   public String   getConstantPixelFilename()             { return this.constantPixelFilename; }
   public String[] getConstantSupportedFileTypes_Images() { return this.constantSupportedFileTypes_Images; }
   public String[] getConstantSupportedFileTypes_Videos() { return this.constantSupportedFileTypes_Videos; }
   public String   getConstantHelpNotes()                 { return this.constantHelpNotes; }

   public FilenameFilter getConstantSupportedFileTypesFilter() { return this.constantSupportedFileTypesFilter; }
   public FileFilter     getConstantDirectoryFileFilter()      { return this.constantDirectoryFileFilter; }
   public FileComparator getConstantFileComparator()           { return this.constantFileComparator; }


   //persistant getter
   public boolean getPersistentCheckMD5Sums() { return this.persistentCheckMD5Sums; }
   public String getPersistentLogfile() { return persistentLogfile; }


   //runtime getters and setters
   public String getRtconfigLoginSessionID() { return this.rtconfigLoginSessionID; }
   public void setRtconfigLoginSessionID(String rtconfigLoginSessionID) { this.rtconfigLoginSessionID = rtconfigLoginSessionID; }


//   //special accessors for username and password - can be retrieved only once
//   public void pushRtconfigLoginUsername(String username) { this.rtconfigLoginUsername = username; }
//   public String popRtconfigLoginUsername()
//   {
//       String username = this.rtconfigLoginUsername;
//       this.rtconfigLoginUsername = null;
//       return username;
//   }
//   public void pushRtconfigLoginPassword(String password) { this.rtconfigLoginPassword = password; }
//   public String popRtconfigLoginPassword()
//   {
//       String password = this.rtconfigLoginPassword;
//       this.rtconfigLoginPassword = null;
//       return password;
//   }




}
