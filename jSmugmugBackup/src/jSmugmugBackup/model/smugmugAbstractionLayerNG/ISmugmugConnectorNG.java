/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.smugmugAbstractionLayerNG;

import jSmugmugBackup.accountLayerNG.*;

import java.io.File;
import java.util.Vector;

public interface ISmugmugConnectorNG
{
	void login(String userEmail, String password);
	void relogin();
	void logout();

	Vector<ICategory> getTree();
	void getImages(int albumID);
	void createCategory(String name);
	void createSubcategory(int categoryID, String name);
	void createAlbum(int categoryID, int subCategoryID, String name);
	
	void uploadFile(int albumID, File file);
	void downloadFile(int imageID, File fileName);
	void verifyFile();
	void deleteFile();
}
