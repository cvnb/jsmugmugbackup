/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.view.ILoginView;

import java.io.File;
import java.util.Vector;

public interface IAccountListingProxy
{
	//void setLoginMethod(ILoginView loginMethod);
//	void init();
//	void init(File accoutDataFile);
	void serialize(File accoutDataFile);
	Number login(String userEmail, String password);
	void logout();
	

	IRootElement getAccountTree(String categoryName, String subcategoryName, String albumName, String albumKeywords);
    Vector<IAlbum> getAccountAlbumList(String categoryName, String subcategoryName, String albumName, String albumKeywords);

    void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir, String albumKeywords);
	void enqueueAlbumForDownload(int albumID, String targetBaseDir);
	void verifyAlbum(int albumID, String targetBaseDir);
	void sort(String categoryName, String subcategoryName);
	//void resortCategoryAlbums(int categoryID);
	//void resortSubcategoryAlbums(int subcategoryID);
	
	void startProcessingQueue();
	long getTransferedBytes();
}
