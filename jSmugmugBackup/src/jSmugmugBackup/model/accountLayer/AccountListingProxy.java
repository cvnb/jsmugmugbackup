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
import jSmugmugBackup.view.*;
import jSmugmugBackup.view.login.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class AccountListingProxy implements IAccountListingProxy
{
	private Logger log = null;
	private ISmugmugConnectorNG connector = null;
	private ITransferQueue transferQueue = null;
	private ILoginView loginMethod = null;
	private Vector<ICategory> categoryList = null;
	
	private long transferedBytes = 0;
	
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
	
	public void init()
	{		
		this.categoryList = this.connector.getTree();
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


	public Vector<ICategory> getCategoryList()
	{
		return this.categoryList;
	}

	public void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir)
	{
		
    	//this.log.printLogLine("-----------------------------------------------");
    	this.log.printLogLine(this.getTimeString() + " enqueuing album: " + categoryName + "/" + subcategoryName + "/" + albumName + " ... dir: " + pics_dir);

    	int uploadCount = 0;
        File[] fileList = pics_dir.listFiles(Constants.supportedFileTypesFilter);
        if (fileList == null) { return; /* Either dir does not exist or is not a directory */ }
        Arrays.sort(fileList, new Constants.FileComparator()); //sort files
        

    	//get or create category
        int categoryID;
        categoryID = this.getCategoryID(categoryName);
        if (categoryID == 0) //category doesn't exist, so we create one
        {
        	categoryID = this.connector.createCategory(categoryName); //create on smugmug
        	this.addCategory(categoryID, categoryName); //add to local structure
        }
        
        //get or create subcategory
        int subCategoryID = 0;
        if (subcategoryName != null)
        {
	        subCategoryID = this.getSubcategoryID(categoryID, subcategoryName);
	        if (subCategoryID == 0) //subcategory doesn't exist, so we create one
	        {
	        	subCategoryID = this.connector.createSubcategory(categoryID, subcategoryName); //create on smugmug
	        	this.addSubcategory(categoryID, subCategoryID, subcategoryName); //add to local structure
	        }
        }
        
        int albumID;
        albumID = this.getAlbumID(categoryID, subCategoryID, albumName);
        if (albumID == 0) //album doesn't exist, so create one
        {
        	albumID = this.connector.createAlbum(categoryID, subCategoryID, albumName);
        	this.addAlbum(categoryID, subCategoryID, albumID, albumName);
        }

        //this.log.printLogLine("categoryID=" + categoryID + ", subcategoryID=" + subCategoryID + ", albumID=" + albumID);
        
        
        for (int i=0; i<fileList.length; i++)
        {
            // check if file is smaller than 512 MB
            if ( fileList[i].length() <= (512*1024*1024) )
            {	
	        	String fileMD5 = this.computeMD5Hash(fileList[i]);
	        	
	        	int imageID;
	        	imageID = this.getImageID(categoryID, subCategoryID, albumID, fileMD5);
	        	if (imageID == 0) //image doesn't exist
	        	{
		        	try
		        	{
						ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, albumID, fileList[i]);
						this.transferQueue.add(item);
		                uploadCount++;
					}
		        	catch (TransferQueueException e) { e.printStackTrace(); }
	        	}
	        	else this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " already exists on smugmug ... skipping");
            }
            else this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " filesize greater than 512 MB is not supported ... skipping");

        }
        

        this.log.printLogLine("  ... added " + uploadCount + " files to album: " + categoryName + "/" + subcategoryName + "/" + albumName);

	}
	
	public void startProcessingQueue()
	{
		this.transferQueue.startSyncProcessing();
		
		//collect Results
		Vector<ITransferQueueItem> processedItemList = this.transferQueue.getProcessedItemList();
		for (ITransferQueueItem item : processedItemList)
		{
			this.transferedBytes += item.getResults().getTransferedBytes();
			
			//add imageid to local data
		}
		
	}

	public long getTransferedBytes() { return this.transferedBytes; }
	
	
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
	
	private void addAlbum(int categoryID, int subcategoryID, int id, String name)
	{
		if (subcategoryID == 0) { this.addAlbum(subcategoryID, id, name); return; }
		
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
		if (subcategoryID == 0) { return this.getAlbumID(categoryID, albumName); }
		
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getID() == subcategoryID)
					{
						for (IAlbum a : s.getAlbumList())
						{
							if (a.getName().equals(albumName)) return a.getID();
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	private int getAlbumID(int categoryID, String albumName)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (IAlbum a : c.getAlbumList())
				{
					if (a.getName().equals(albumName)) return a.getID();
				}
			}
		}
		
		return 0;
	}

	private int getImageID(int categoryID, int subcategoryID, int albumID, String imageMD5)
	{
		if (subcategoryID == 0) { return this.getImageID(categoryID, albumID, imageMD5); }
		
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getID() == subcategoryID)
					{
						for (IAlbum a : s.getAlbumList())
						{
							if (a.getID() == albumID)
							{
								for (IImage i : a.getImageList())
								{
									if (i.getMD5().equals(imageMD5)) return i.getID();
								}
							}
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	private int getImageID(int categoryID, int albumID, String imageMD5)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (IAlbum a : c.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						for (IImage i : a.getImageList())
						{
							if (i.getMD5().equals(imageMD5)) return i.getID();
						}
					}
				}
			}
		}
		
		return 0;
	}

    private String computeMD5Hash(File file)
    {    	
		//read local file
		byte[] buffer = new byte[(int)file.length()];
		InputStream is = null;
    	try
    	{
			is = new FileInputStream(file);
			is.read(buffer); //null pointer exception???
			is.close();
		}
    	catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }

		//compute md5 from local file
		String md5sum = null;
		try { md5sum = AeSimpleMD5.MD5(buffer); }
		catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
    	
    	return md5sum;
    }

	private String getTimeString()
	{
		Date date = new Date();
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
	}

	
}
