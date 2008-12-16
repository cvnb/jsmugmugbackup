/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.config;

import java.io.File;
import java.io.IOException;
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
   private final String constantXMLConfigFilename = "config.xml";

   // constants --> getter
   private final String constantVersion = "0.4 (dev)";


   // persistent options --> getter, xml
   private String persistentLogfile = null;
   private boolean persistentCheckMD5Sums;
   //private String  persistentDefaultUsername = ""; //don't activate this yet
   //private boolean persistentCaseSensitiveImageNames = true;
   //private boolean persistentCaseSensitiveFolderNames = true;
   //private String  persistentDefaultUploadVisibility = "private";
   

   // runtime config --> getter and setter
   private String rtconfigSession = "";


   private void loadConfig()
   {
       //todo: load from xml-file

       Document doc = null;
       try
       {
           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
           DocumentBuilder db = dbf.newDocumentBuilder();

           doc = db.parse(new File(this.constantXMLConfigFilename));

       } catch (Exception ex) { Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex); }

       NodeList nodes = null;
       
       // persistentLogfile
       nodes = doc.getElementsByTagName("logfile");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.constantXMLConfigFilename); System.exit(1); }
       this.persistentLogfile = nodes.item(0).getTextContent();
       //System.out.println("persistentLogfile: " + this.persistentLogfile);

       // persistentCheckMD5Sums
       nodes = doc.getElementsByTagName("checkMD5Sums");
       if (nodes.getLength() != 1) { System.out.println("ERROR: an error occured while loading " + this.constantXMLConfigFilename); System.exit(1); }
       this.persistentCheckMD5Sums = Boolean.parseBoolean(nodes.item(0).getTextContent());
       //System.out.println("persistentCheckMD5Sums: " + this.persistentCheckMD5Sums);

   }

   private void storeConfig()
   {
       //todo: write to xml-file
   }

   // constant getter
   public String getConstantVersion() { return this.constantVersion; }

   //persistant getter
   public boolean getPersistentCheckMD5Sums() { return this.persistentCheckMD5Sums; }
   public String getPersistentLogfile() { return persistentLogfile; }

   //runtime getters and setters
   public String getRtconfigSession() { return this.rtconfigSession; }
   public void setRtconfigSession(String rtconfigSession) { this.rtconfigSession = rtconfigSession; }






}
