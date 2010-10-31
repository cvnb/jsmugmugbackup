/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.IAlbum;
import jSmugmugBackup.model.accountLayer.IImage;
import jSmugmugBackup.view.*;
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

    private boolean dirty;
    private int dirtyUpdateCount;

    public SmugmugLocalAlbumCache(String userIDString)
    {
        this.config = GlobalConfig.getInstance();
        this.log = Logger.getInstance();        
        this.cacheFilename = this.config.getConstantAlbumCacheFilenamePrefix() + userIDString;

        this.initCache();
        this.loadCacheFromDisk();
    }

    public void putAlbum(IAlbum album)
    {
        if (this.cache.containsKey(album.getID())) { this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: album can not be added twice to the cache!"); }

        this.cache.put(album.getID(), album);

        this.dirty = true;
        this.dirtyUpdateCount++;
        if (this.dirtyUpdateCount >= this.config.getConstantCacheWritingPolicy()) { this.saveCacheToDisk();}
    }
    public void removeAlbum(int albumID)
    {
        if (this.exists(albumID))
        {
            this.cache.remove(albumID);

            this.dirty = true;
            this.dirtyUpdateCount++;
            if (this.dirtyUpdateCount >= this.config.getConstantCacheWritingPolicy()) { this.saveCacheToDisk();}
        }
    }
    public IAlbum getCachedAlbum(int albumID)
    {
        return this.cache.get(albumID);
    }
    public boolean checkIfCachedAlbumIsUptodate(int albumID, int imageCount, String lastUpdated, String albumName)
    {
        IAlbum album = this.getCachedAlbum(albumID);

        //check if album exists
        if (album == null) { return false; }

        //check if album is still valid
        if ( (album.getImageCount() == imageCount) && (album.getLastUpdatedString().equals(lastUpdated)) && (album.getName().equals(albumName)) )
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
    public void forceSaveCacheToDisk()
    {
        this.saveCacheToDisk();
    }

    //--------------------------------------------------------------------------
    private void initCache()
    {
        //init an empty cache
        this.cache = new Hashtable<Integer, IAlbum>();
        this.dirty = false;
        this.dirtyUpdateCount = 0;
    }
    private void loadCacheFromDisk()
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

                //this.log.printLog("DEBUG: loaded album cache from disk (" + this.cache.size() + " albums) ... ");
            }
            catch (ClassNotFoundException ex)
            {
                //ex.printStackTrace();
                this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: class not found exception thrown while loading the cache from disk, starting with an empty cache!");
                this.initCache();
            }
            catch (IOException ex)
            {
                //ex.printStackTrace();
                this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: I/O exception thrown while loading the cache from disk, starting with an empty cache!");
                this.initCache();
            }
        }
        else
        {
            this.log.printLog(LogLevelEnum.Warning, "WARNING: no local cache file found, starting empty (this may take a while!) ... ");
            this.initCache();
        } 
    }
    private void saveCacheToDisk()
    {
        //invoking the garbage collector - maybe this helps keeping the cache file small
        System.gc();

        File file = new File(this.cacheFilename);
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        try
        {
            fos = new FileOutputStream(file, false);
            out = new ObjectOutputStream(fos);
            out.writeObject(this.cache);
            out.close();

            this.dirty = false;
            this.dirtyUpdateCount = 0;

            //this.log.printLog("DEBUG: saved album cache to disk (" + this.cache.size() + " albums) ... ");
        }
        catch (IOException ex)
        {
            this.log.printLogLine(LogLevelEnum.Warning, 0, "ERROR: an IOException occured during serialization!");
            ex.printStackTrace();
        }
    }

}
