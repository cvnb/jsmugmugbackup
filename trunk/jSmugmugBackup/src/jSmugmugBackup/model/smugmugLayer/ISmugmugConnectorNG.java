/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.model.accountLayer.*;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public interface ISmugmugConnectorNG
{
	boolean login(String userEmail, String password);
	void relogin();
	void logout();

	Vector<ICategory> getTree();
	void getImages(int albumID); //???
	Hashtable<String, String> getImageInfo(int imageID);
	
	int createCategory(String name);
	int createSubcategory(int categoryID, String name);
	int createAlbum(int categoryID, int subCategoryID, String name);
	
	/*
	void renameCategory(int categoryID, String newName);
	void renameSubcategory(int subcategoryID, String newName);
	void renameAlbum(int albumID, String newName);
	void setAlbumPosition(int albumID, int newPosition);
	*/
	
	int uploadFile(int albumID, File file);
	void downloadFile(int imageID, File fileName);
	void downloadFile(String imageURL, File fileName);
	//void verifyFile();
	void deleteFile(int imageID);
	
	long getTransferedBytes();
}
