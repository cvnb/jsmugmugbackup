/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.IAlbum;
import jSmugmugBackup.model.accountLayer.IImage;
import jSmugmugBackup.view.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.logging.Level;


/**
 *
 * @author paul
 */
public class SmugmugLocalAlbumCache implements ISmugmugLocalAlbumCache
{
    private GlobalConfig config = null;
    private Logger log = null;
    private Hashtable<Integer, IAlbum> cache = null;
    private String cacheFilename = null;

    public SmugmugLocalAlbumCache(String userIDString)
    {
        this.config = GlobalConfig.getInstance();
        this.log = Logger.getInstance();
        this.cache = new Hashtable<Integer, IAlbum>();
        this.cacheFilename = this.config.getConstantAlbumCacheFilenamePrefix() + userIDString;
    }


    public void putAlbum(IAlbum album)
    {
        if (this.cache.containsKey(album.getID())) { this.log.printLogLine("ERROR: album can not be added twice to the cache!"); }

        this.cache.put(album.getID(), album);
    }

    public void removeAlbum(int albumID)
    {
        if (this.exists(albumID)) { this.cache.remove(albumID); }
    }

    public IAlbum getCachedAlbum(int albumID)
    {
        return this.cache.get(albumID);
    }

    public boolean validateCachedAlbum(int albumID, int imageCount, String lastUpdated)
    {
        IAlbum album = this.getCachedAlbum(albumID);

        //check if album exists
        if (album == null) { return false; }

        //check if album is still valid
        if ( (album.getImageCount() == imageCount) && (album.getLastUpdatedString().equals(lastUpdated)) )
        {
            return true;
        }
        else
        {
            this.cache.remove(albumID);
            return false;
        }
    }

    public boolean exists(int albumID)
    {
        return this.cache.containsKey(albumID);
    }

    public void saveCacheToDisk()
    {
        File file = new File(this.cacheFilename);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        try
        {
            fos = new FileOutputStream(file, false);
            out = new ObjectOutputStream(fos);
            out.writeObject(this.cache);
            out.close();
        }
        catch (IOException ex)
        {
            this.log.printLogLine("ERROR: an IOException occured during serialization!");
            ex.printStackTrace();
        }
    }

    public void loadCacheFromDisk()
    {
        File file = new File(this.cacheFilename);
        FileInputStream fis = null;
        ObjectInputStream in = null;

        if (file.exists())
        {
            try
            {
                fis = new FileInputStream(file);
                in = new ObjectInputStream(fis);
                this.cache = (Hashtable<Integer, IAlbum>)in.readObject();
                in.close();
            }
            catch (ClassNotFoundException ex)
            {
                //ex.printStackTrace();

                this.log.printLogLine("ERROR: class not found exception thrown while loading the cache from disk, starting with an empty cache!");
                this.cache = new Hashtable<Integer, IAlbum>();
            }
            catch (IOException ex)
            {
                //ex.printStackTrace();

                this.log.printLogLine("ERROR: I/O exception thrown while loading the cache from disk, starting with an empty cache!");
                this.cache = new Hashtable<Integer, IAlbum>();
            }

            /*
            int imageCount = 0;
            for (IAlbum a : this.cache.values())
            {
                this.log.printLogLine("album: " + a.getFullName() + " (" + a.getImageCount() + ")");
                for (IImage i : a.getImageList())
                {
                    this.log.printLogLine("   image: " + i.getFullName() + " (" + i.getID() + ")");
                    imageCount++;
                }
            }
            this.log.printLogLine("imageCount: " + imageCount);
            */

        }
        else
        {
            this.log.printLogLine("WARNING: no local cache file found, starting empty!");
            //init an empty cache
            this.cache = new Hashtable<Integer, IAlbum>();
        }
 
    }



}
