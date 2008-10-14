/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.abstractionLayerNG;

import jSmugmugBackup.abstractionLayerNG.data.*;
import java.io.File;
import java.util.Vector;

public interface ISmugmugConnectorNG
{
	void login(String userEmail, String password);
	void logout();

	Vector<ICategory> getTree();
	void getImages();
	
	void uploadFile(int albumID, File file);
	void downloadFile(int imageID, File fileName);
	void verifyFile();
	void deleteFile();
}
