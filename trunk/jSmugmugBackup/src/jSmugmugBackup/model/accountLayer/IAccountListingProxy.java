/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.model.ResolutionEnum;
import jSmugmugBackup.view.ILoginView;

import java.io.File;
import java.util.Vector;

public interface IAccountListingProxy
{
	Number login(String userEmail, String password);
	void logout();
	

	IRootElement getAccountTree(String categoryName, String subcategoryName, String albumName, String albumKeywords, String albumPassword);
    Vector<IAlbum> getAccountAlbumList(String categoryName, String subcategoryName, String albumName, String albumKeywords);

    void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir, String albumKeywords);
	void enqueueAlbumForDownload(int albumID, String albumKey, String albumPassword, String targetBaseDir, /*ResolutionEnum minResolution,*/ ResolutionEnum maxResolution);
	//void enqueueAlbumFromURLForDownload(int albumID, String albumKey, String targetDir);
    void verifyAlbum(int albumID, String targetBaseDir);
	void sort(String categoryName, String subcategoryName);
    void autotag(String categoryName, String subcategoryName, String albumName);
    Vector<IAlbum> statistics(String categoryName, String subcategoryName, String albumName);
	
	void startSyncProcessingQueue();

    void startASyncProcessingQueue();
    void finishASyncProcessingQueue();

    long getTransferedBytes();
}
