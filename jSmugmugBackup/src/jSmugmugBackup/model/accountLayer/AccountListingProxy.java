/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.queue.*;
import jSmugmugBackup.model.smugmugLayer.*;
import jSmugmugBackup.view.Logger;
import jSmugmugBackup.view.login.ILoginView;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

public class AccountListingProxy implements IAccountListingProxy
{
	private Logger log = null;
	private ISmugmugConnectorNG connector = null;
	private ITransferQueue transferQueue = null;
	private ILoginView loginMethod = null;
	private Vector<ICategory> categoryList = null;
	
	public AccountListingProxy()
	{
		this.log = Logger.getInstance();
		this.connector = new SmugmugConnectorNG();
		this.transferQueue = new TransferQueue();
	}
	
	public void setLoginMethod(ILoginView loginToken)
	{
		this.loginMethod = loginToken;		
	}
	
	public void login()
	{
		String userEmail = this.loginMethod.requestUserEmail();
		String password = this.loginMethod.requestPassword();
		
		this.connector.login(userEmail, password);
	}
	
	public void logout()
	{
		this.connector.logout();
	}

	public String getNickName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public Vector<ICategory> getCategoryList()
	{
		if (this.categoryList == null)
		{
			this.categoryList = this.connector.getTree();
		}
		
		return this.categoryList;
	}

	public void uploadAlbum(String categoryName, String subcategoryName, String albumName, File pics_dir)
	{
    	this.log.printLogLine("-----------------------------------------------");
    	this.log.printLogLine("enqueuing album: " + categoryName + "/" + subcategoryName + "/" + albumName + " ... dir: " + pics_dir);

    	int uploadCount = 0;
        File[] fileList = pics_dir.listFiles(Constants.supportedFileTypesFilter);
        if (fileList == null) { return; /* Either dir does not exist or is not a directory */ }
        Arrays.sort(fileList, new Constants.FileComparator()); //sort files
        

    	//create category, subcategory and album
        int categoryID = this.getCategoryID(categoryName);
        int subCategoryID = this.getSubcategoryID(categoryID, subcategoryName);
        int albumID = this.getAlbumID(categoryID, subCategoryID, albumName);

        
        for (int i=0; i<fileList.length; i++)
        {
//        	try
//        	{
//				ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, albumID, fileList[i]);
//				this.transferQueue.add(item);
//                uploadCount++;
//			}
//        	catch (TransferQueueException e) { e.printStackTrace(); }
        }

        this.log.printLogLine("  ... added " + uploadCount + " files to album: " + categoryName + "/" + subcategoryName + "/" + albumName);

	}
	
	//----------- private ----------
	private void addCategory(int id, String name)
	{
		this.categoryList.add( new Category(id, name) );
	}
	
	private void addSubcategory(int categoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				c.addSubcategory(new Subcategory(id, name) );
				return;
			}
		}
		
		System.out.println("addSubcategory: ERROR!");
	}

	private void addAlbum(int categoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				c.addAlbum( new Album(id, name) );
				return;
			}
		}
		
		System.out.println("addAlbum: ERROR!");
	}
	
	private void addAlbum(int categoryID, int subcategoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getID() == subcategoryID)
					{
						s.addAlbum( new Album(id, name) );
						return;
					}
				}
			}
		}
		
		System.out.println("addAlbum: ERROR!");		
	}
	
	private void addImage(int albumID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						a.addImage(new Image(id, name));
						return;
					}
				}

			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					a.addImage(new Image(id, name));
					return;
				}
			}
		}
		
		System.out.println("addImage: ERROR!");
	}


	private int getCategoryID(String categoryName)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getName().equals(categoryName)) return c.getID();
		}
		
		return 0;
	}
	
	private int getSubcategoryID(int categoryID, String subcategoryName)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getName().equals(subcategoryName)) return s.getID();
				}
			}
		}
		
		return 0;
	}
	
	private int getAlbumID(int categoryID, int subcategoryID, String albumName)
	{
		return 0;
	}
	

	
}
