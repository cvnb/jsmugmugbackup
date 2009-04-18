/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.model.accountLayer.IAlbum;
import java.io.File;

/**
 *
 * @author paul
 */
public interface ISmugmugLocalAlbumCache
{
    void loadCacheFromDisk();
    void saveCacheToDisk();

    void putAlbum(IAlbum album);
    void removeAlbum(int albumID);
    boolean exists(int albumID);
    IAlbum getCachedAlbum(int albumID);
    boolean validateCachedAlbum(int albumID, int imageCount, String lastUpdated);
}
