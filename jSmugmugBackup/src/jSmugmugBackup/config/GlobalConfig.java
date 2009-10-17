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
   private final String constantVersion                     = "1.1 (dev)";
   private final String constantSmugmugUserAgentString      = "jSmugmugBackup/v" + this.constantVersion;
   private final String constantSmugmugServerURL_120        = "https://api.smugmug.com/hack/json/1.2.0/";
   private final String constantSmugmugServerURL_122        = "https://api.smugmug.com/services/api/json/1.2.2/";
   private final String constantSmugmugAPIKey               = "bGLKncnGHUfZIwICUtqWsW3ejE1RYztJ";
   private final String constantSmugmugAPIVersion_120       = "1.2.0";
   private final String constantSmugmugAPIVersion_122       = "1.2.2";
   private final int    constantRetryWait                   = 20000; //time to wait before retrying (20sec)
   private final boolean constantHeavyRelogin               = true; // perform relogin for each queue item, this might improve stability during long lasting queue operations
   private final boolean constantVerifyMD5ForVideos         = false; // since md5 verification usually fails for videos, this will sspeed up the process
   //private final boolean constantVerboseLogging         = true; //disabled for the moment
   private final int    constantUploadFileSizeLimit         = 600*1024*1024; //600MB
   private final int    constantStatisticsHistoryMonth      = 3; // assert: < 12; how many month of statistics should jSmugmugBackup try to retrieve
   private final String constantUploadIgnoreFilePostfix     = ".jSmugmugBackup-upload-ignore.tag";
   private final String constantPixelFilename               = "res/pixel.jpg";
   private final String constantAlbumCacheFilenamePrefix    = "jSmugmugBackup.albumCache.";
   private final String constantTempDownloadFilename        = "jSmugmugBackup.download.temp";
   private final String[] constantSupportedFileTypes_Images = {".jpg", ".jpeg", ".png", ".gif", ".tiff"};
   private final String[] constantSupportedFileTypes_Videos = {".avi", ".mp4", ".mpg", ".mpeg", ".mov", ".m4a", ".m4v", ".wmv", /*".xvid",*/ ".flv", ".3gp", ".ogv"}; //...hope thats all possible types
   private final String constantHelpNotes =
		"notes:\n" +
        " - It is strongly suggestend to stick with the commandline view, since the GUI is currently\n" +
        "   V E R Y buggy!\n" +
        " - usage examples: on linux and MacOS use \"jSmugmugBackup.sh\", on Windows \"jSmugmugBackup.bat\"\n" +
        "      ./jSmugmugBackup.sh --upload --user=me --dir=/home/john/pics/\n" +
        "            ... uploads whole directory structure\n" +
        "      ./jSmugmugBackup.sh --upload --user=me --category=public --album=\"my hollidays\" --dir=/home/john/pics/public/paris\n" +
        "            ... uploads all images in /home/john/pics/public/paris to an album named \"my hollidays\" in the category \"public\"\n" +
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
        " - it seems like Smugmug can only handle filenames with ASCII characters\n" +
        "   german (Umlaute) and french special characters are beeing converted, but\n" +
        "   when downloading these images again, the special characters are lost\n" +
        " - there is no support for international characters other than german and frech (might not\n" +
        "   be complete)\n" +
        " - verify only reports md5 checksum errors if they occur with an image, checksum errors\n" +
        "   with videos are not reported because videos are converted by smugmug after uploading\n" +
        "   which makes md5 checks fail on all videos\n" +
        " - if you want to dowwnload someones public galleries, login using his nickname and\n" +
        "   \"anonymous\" as password (album passwords are currently not supported, sorry)\n" +
        " - for downloading, we always download the best resolution available\n" +
        " - share button and external linking are now disabled by default for new albums created\n" +
        "   with jSmugmugBackup\n" +
        /*
        " - resuming downloading works (well resuming per file, not in the middle of one) as long \n" +
        "   as there are no videos ... videos will always be downloaded a second time, no idea how\n" +
        "   to fix that\n" +
        " - there is also a bug that prevents us from resuming certain galleries, the same problem\n" +
        "   is responsible for retrying downloading twice before giving up because of a size error\n" +
        */
        " - when downloading from users that don't allow originals, the reported download queue size\n" +
        "   is most definitively wrong, and the remaining time counter might get confused too - no fix\n" +
        "   for that at the moment, sorry\n" +
        " - statistics: nesseceary functions are more or less implemented, but the smugmug API seems\n" +
        "   to return zero for everything ... there's not much I can do about it atm, sorry\n" +
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
   private String  persistentLogfile = null;
   private boolean persistentCheckMD5Sums;
   private boolean persistentCacheAccountInfo;
   private boolean persistentAppendLogfile;
   //private String  persistentDefaultUsername = ""; //don't activate this yet
   //private boolean persistentCaseSensitiveImageNames = true;
   //private boolean persistentCaseSensitiveFolderNames = true;
   //private String  persistentDefaultUploadVisibility = "private";
   
   private boolean persistentAlbumGeography;
   private boolean persistentAlbumClean;
   private boolean persistentAlbumExif;
   private boolean persistentAlbumFilenames;
   private boolean persistentAlbumSquareThumbs;
   private String persistentAlbumSortMethod;
   private boolean persistentAlbumSortDirection;
   private String persistentAlbumPassword;
   private String persistentAlbumPasswordHint;
   private boolean persistentAlbumPublic;
   private boolean persistentAlbumWorldSearchable;
   private boolean persistentAlbumSmugSearchable;
   private boolean persistentAlbumExternal;
   private boolean persistentAlbumHideOwner;
   private boolean persistentAlbumCanRank;
   private boolean persistentAlbumFriendEdit;
   private boolean persistentAlbumFamilyEdit;
   private boolean persistentAlbumComments;
   private boolean persistentAlbumShare;
   private boolean persistentAlbumPrintable;




   // runtime config --> getter and setter
   private String rtconfigLoginSessionID = "";
//   private String rtconfigLoginUsername = null;
//   private String rtconfigLoginPassword = null;
   private boolean rtconfigAnonymousLogin = false;


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

       // persistentCachefilePrefix
       nodes = doc.getElementsByTagName("cacheAccountInfo");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentCacheAccountInfo = Boolean.parseBoolean(nodes.item(0).getTextContent());
       //System.out.println("persistentCachefilePrefix: " + this.persistentCachefilePrefix);

       // persistentAppendLogfile
       nodes = doc.getElementsByTagName("appendLogfile");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAppendLogfile = Boolean.parseBoolean(nodes.item(0).getTextContent());
       //System.out.println("persistentAppendLogfile: " + this.persistentAppendLogfile);

       
       //loading album defaults
       nodes = doc.getElementsByTagName("albumGeography");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumGeography = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumClean");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumClean = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumExif");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumExif = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumFilenames");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumFilenames = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumSquareThumbs");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumSquareThumbs = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumSortMethod");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumSortMethod = nodes.item(0).getTextContent();

       nodes = doc.getElementsByTagName("albumSortDirection");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumSortDirection = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumPassword");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumPassword = nodes.item(0).getTextContent();

       nodes = doc.getElementsByTagName("albumPasswordHint");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumPasswordHint = nodes.item(0).getTextContent();

       nodes = doc.getElementsByTagName("albumPublic");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumPublic = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumWorldSearchable");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumWorldSearchable = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumSmugSearchable");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumSmugSearchable = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumExternal");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumExternal = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumHideOwner");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumHideOwner = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumCanRank");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumCanRank = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumFriendEdit");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumFriendEdit = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumFamilyEdit");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumFamilyEdit = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumComments");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumComments = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumShare");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumShare = Boolean.parseBoolean(nodes.item(0).getTextContent());

       nodes = doc.getElementsByTagName("albumPrintable");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.internconstantXMLConfigFilename); System.exit(1); }
       this.persistentAlbumPrintable = Boolean.parseBoolean(nodes.item(0).getTextContent());
       
   }

   private void storeConfig()
   {
       //todo: write to xml-file
   }

   // constant getter
   public String   getConstantVersion()                   { return this.constantVersion; }
   public String   getConstantSmugmugUserAgentString()    { return this.constantSmugmugUserAgentString; }
   public String   getConstantSmugmugServerURL_120()      { return this.constantSmugmugServerURL_120; }
   public String   getConstantSmugmugServerURL_122()      { return this.constantSmugmugServerURL_122; }
   public String   getConstantSmugmugAPIKey()             { return this.constantSmugmugAPIKey; }
   public String   getConstantSmugmugAPIVersion_120()     { return this.constantSmugmugAPIVersion_120; }
   public String   getConstantSmugmugAPIVersion_122()     { return this.constantSmugmugAPIVersion_122; }
   public int      getConstantRetryWait()                 { return this.constantRetryWait; }
   public boolean  getConstantHeavyRelogin()              { return this.constantHeavyRelogin; }
   public boolean  getConstantVerifyMD5ForVideos()              { return this.constantVerifyMD5ForVideos; }
   //public boolean  getConstantVerboseLogging()            { return this.constantVerboseLogging; } //disabled for the moment
   public int      getConstantUploadFileSizeLimit()       { return this.constantUploadFileSizeLimit; }
   public int      getConstantStatisticsHistoryMonth()    { return this.constantStatisticsHistoryMonth; }
   public String   getConstantUploadIgnoreFilePostfix()   { return this.constantUploadIgnoreFilePostfix; }
   public String   getConstantPixelFilename()             { return this.constantPixelFilename; }
   public String   getConstantAlbumCacheFilenamePrefix()  { return this.constantAlbumCacheFilenamePrefix; }
   public String   getConstantTempDownloadFilename()      { return this.constantTempDownloadFilename; }
   public String[] getConstantSupportedFileTypes_Images() { return this.constantSupportedFileTypes_Images; }
   public String[] getConstantSupportedFileTypes_Videos() { return this.constantSupportedFileTypes_Videos; }
   public String   getConstantHelpNotes()                 { return this.constantHelpNotes; }

   public FilenameFilter getConstantSupportedFileTypesFilter() { return this.constantSupportedFileTypesFilter; }
   public FileFilter     getConstantDirectoryFileFilter()      { return this.constantDirectoryFileFilter; }
   public FileComparator getConstantFileComparator()           { return this.constantFileComparator; }


   //persistant getter
   public boolean getPersistentCheckMD5Sums() { return this.persistentCheckMD5Sums; }
   public String getPersistentLogfile() { return persistentLogfile; }
   public boolean getPersistentCacheAccountInfo() { return this.persistentCacheAccountInfo; }
   public boolean getPersistentAppendLogfile() { return this.persistentAppendLogfile; }
   
   public boolean getPersistentAlbumGeography()       { return persistentAlbumGeography; }
   public boolean getPersistentAlbumClean()           { return persistentAlbumClean; }
   public boolean getPersistentAlbumExif()            { return persistentAlbumExif; }
   public boolean getPersistentAlbumFilenames()       { return persistentAlbumFilenames; }
   public boolean getPersistentAlbumSquareThumbs()    { return persistentAlbumSquareThumbs; }   
   public String  getPersistentAlbumSortMethod()      { return persistentAlbumSortMethod; }
   public boolean getPersistentAlbumSortDirection()   { return persistentAlbumSortDirection; }
   public String  getPersistentAlbumPassword()        { return persistentAlbumPassword; }
   public String  getPersistentAlbumPasswordHint()    { return persistentAlbumPasswordHint; }   
   public boolean getPersistentAlbumPublic()          { return persistentAlbumPublic; }
   public boolean getPersistentAlbumWorldSearchable() { return persistentAlbumWorldSearchable; }
   public boolean getPersistentAlbumSmugSearchable()  { return persistentAlbumSmugSearchable; }
   public boolean getPersistentAlbumExternal()        { return persistentAlbumExternal; }
   public boolean getPersistentAlbumHideOwner()       { return persistentAlbumHideOwner; }   
   public boolean getPersistentAlbumCanRank()         { return persistentAlbumCanRank; }
   public boolean getPersistentAlbumFriendEdit()      { return persistentAlbumFriendEdit; }
   public boolean getPersistentAlbumFamilyEdit()      { return persistentAlbumFamilyEdit; }
   public boolean getPersistentAlbumComments()        { return persistentAlbumComments; }
   public boolean getPersistentAlbumShare()           { return persistentAlbumShare; }
   public boolean getPersistentAlbumPrintable()       { return persistentAlbumPrintable; }


   //runtime getters and setters
   public String getRtconfigLoginSessionID() { return this.rtconfigLoginSessionID; }
   public void setRtconfigLoginSessionID(String rtconfigLoginSessionID) { this.rtconfigLoginSessionID = rtconfigLoginSessionID; }

   public boolean getRtconfigAnonymousLogin() { return this.rtconfigAnonymousLogin; }
   public void setRtconfigAnonymousLogin(boolean rtconfigAnonymousLogin) { this.rtconfigAnonymousLogin = rtconfigAnonymousLogin; }


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
