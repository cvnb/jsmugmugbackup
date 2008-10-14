/*
 * Created on Sep 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.abstractionLayer;


import java.io.File;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;

import jSmugmugBackup.model.data.AccountListing;
import jSmugmugBackup.model.login.ISmugmugLogin;

public interface ISmugMugConnector
{
	public void setLoginToken(ISmugmugLogin loginToken);
	public void login();
	public void logout();
	
	public AccountListing getAccountStructure();

	public GUID getCategoryGUID(String name);
	public GUID getSubCategoryGUID(GUID categoryGUID, String name);
	public GUID getAlbumGUID(GUID categoryGUID, GUID subCategoryGUID, String name, String description);
	
	public boolean uploadFile(GUID albumGUID, File fileDescriptor);
	public boolean downloadFile(GUID imageGUID, String fileName);
	public boolean verifyFile();	
	public boolean deleteFile(GUID imageGUID);
	
	public long getTransferedBytes();	
}
