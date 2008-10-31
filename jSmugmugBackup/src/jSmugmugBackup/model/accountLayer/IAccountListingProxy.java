/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.view.login.ILoginView;

import java.io.File;
import java.util.Vector;

public interface IAccountListingProxy
{
	void setLoginMethod(ILoginView loginMethod);
	void init();
	void login();
	void logout();
	
	//Vector<ICategory> getCategoryList();
	Vector<IAlbum> matchAlbums(String categoryName, String subcategoryName, String albumName);

	Vector<ICategory> getAccountListing(String categoryName, String subcategoryName, String albumName);
	void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir);
	void enqueueAlbumForDownload(int albumID, String targetBaseDir);
	void verifyAlbum(int albumID, String targetBaseDir);
	
	void resortCategoryAlbums(int categoryID);
	void resortSubcategoryAlbums(int subcategoryID);
	
	void startProcessingQueue();
	long getTransferedBytes();
}