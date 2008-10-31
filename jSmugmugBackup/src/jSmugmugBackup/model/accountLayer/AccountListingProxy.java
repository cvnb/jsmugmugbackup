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

import java.io.*;
import java.util.*;

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

	public Vector<IAlbum> matchAlbums(String categoryName, String subcategoryName, String albumName)
	{
		Vector<IAlbum> selectedAlbums = new Vector<IAlbum>();
		
		//decend to all album lists of all Subcategories and Categories
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					
					//here, we should walk over all albums that belong to all subcategories
					//note: if a parameter is null, we assume it's a wildcard
					if ( (categoryName == null) || (c.getName().equals(categoryName)) )
					{
						if ( (subcategoryName == null) || (s.getName().equals(subcategoryName)) )
						{
							if ( (albumName == null) || (a.getName().equals(albumName)) )
							{
								/*
								String album_dir;
								album_dir = c.getName() + "/" + s.getName() + "/" + a.getName();
								
								//this.log.printLogLine("  matched album: " + a.getName() + " - " + album_dir);
								this.log.printLogLine("  matched album: " + album_dir);
								selectedAlbumHashtable.put(a.getGUID(), album_dir);
								*/
								selectedAlbums.add(a);
							}
						}
					}
					
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				//here, we walk over all albums which have no subcategory
				if (subcategoryName == null) //hence, subcategoryName must be null
				{
					if ( (categoryName == null) || (c.getName().equals(categoryName)) )
					{
						if ( (albumName == null) || (a.getName().equals(albumName)) )
						{
							/*
							String album_dir;
							album_dir = c.getName() + "/" + a.getName();
							
							this.log.printLogLine("Model.matchAlbumsOnSmugmug() -    selecting album: " + a.getName() + " - " + album_dir);
							selectedAlbumHashtable.put(a.getGUID(), album_dir);
							*/
							selectedAlbums.add(a);
						}
					}
				}

			}
		}
		
		//this.log.printLogLine("matched albums: " + selectedAlbums.size() );
		
		return selectedAlbums;		
	}

	public Vector<ICategory> getAccountListing(String categoryName, String subcategoryName, String albumName)
	{
		Vector<ICategory> result = new Vector<ICategory>();
		
		//find matching albums
		Vector<IAlbum> albumList = this.matchAlbums(categoryName, subcategoryName, albumName);
		
		// for all albums in the albumList:
		// - find it's parent category
		// - find it's parent subcategory (if existing)
		// - add album, with correct category and subcategory to result
		for (IAlbum intern_a : albumList)
		{
			//this.log.printLogLine("intern_a.getName()=" + intern_a.getName());
			ICategory intern_c = this.getAlbumCategory(intern_a.getID());
			
			//check if category already exists in result set, if not create it
			boolean result_c_exists = false;
			ICategory result_c = null;
			for (ICategory c : result)
			{
				if (c.getID() == intern_c.getID())
				{
					result_c = c;
					result_c_exists = true;
				}
			}
			if (result_c == null) { result_c = new Category( intern_c.getID(), intern_c.getName() ); } //create new category empty with the same name, because we don't want to copy subcategories and albums
			
			
			ISubcategory intern_s = this.getAlbumSubcategory(intern_a.getID());
			if ( intern_s != null ) //album has a subcategory
			{
				//check if subcategory already exists in result set, if not create it
				boolean result_s_exists = false;
				ISubcategory result_s = null;
				for (ICategory c : result)
				{
					for (ISubcategory s : c.getSubcategoryList())
					{
						if (s.getID() == intern_s.getID())
						{
							result_s = s;
							result_s_exists = true;
						}
					}
				}
				if ( result_s == null ) { result_s = new Subcategory(intern_s.getID(), intern_s.getName()); }
				
				if (!result_c_exists) { result.add(result_c); }
				if (!result_s_exists) { result_c.addSubcategory(result_s); }
				result_s.addAlbum( (IAlbum)intern_a.clone() ); //clone, because we need the children too, i.e. we copy the images too
			}
			else //album doesn't have a subcategory
			{
				if (!result_c_exists) { result.add(result_c); }
				result_c.addAlbum( (IAlbum)intern_a.clone() ); //clone, because we need the children too, i.e. we copy the images too
			}
			
		}
		
		return result;
	}

	public void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir)
	{
		
    	//this.log.printLogLine("-----------------------------------------------");
    	//this.log.printLogLine(this.getTimeString() + " enqueuing album: " + categoryName + "/" + subcategoryName + "/" + albumName + " ... dir: " + pics_dir);
		this.log.printLogLine(Helper.getTimeString() + " enqueuing album ... dir: " + pics_dir);

    	int uploadCount = 0;
    	int skippedCount = 0;
    	int totalFiles = pics_dir.listFiles().length;
    	int totalMediaFiles = pics_dir.listFiles(Constants.supportedFileTypesFilter).length;
    	int unsupportedCount = totalFiles - totalMediaFiles;
    	
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
        
        //get or create album
        int albumID;
        albumID = this.getAlbumID(categoryID, subCategoryID, albumName);
        if (albumID == 0) //album doesn't exist, so create one
        {
        	albumID = this.connector.createAlbum(categoryID, subCategoryID, albumName);
        	this.addAlbum(categoryID, subCategoryID, albumID, albumName);
        }

        //this.log.printLogLine("categoryID=" + categoryID + ", subcategoryID=" + subCategoryID + ", albumID=" + albumID);
        
        //add album files to the queue
        for (int i=0; i<fileList.length; i++)
        {        	
        	int imageID;
        	imageID = this.getImageID(categoryID, subCategoryID, albumID, fileList[i].getName());
        	if (imageID == 0) //image doesn't exist on smugmug
        	{
                // check if file is smaller than 512 MB and
                if (fileList[i].length() > (Constants.uploadFileSizeLimit))
                {
                	this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " filesize greater than 512 MB is not supported ... skipping");
                	skippedCount++;
                }                
                //check if someone has manually set the ignore tag
                else if ( (new File(fileList[i].getAbsolutePath() + Constants.uploadIgnoreFilePostfix)).exists() )
                {
                	this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " - the ignore tag was set ... skipping");
                	skippedCount++;
                }
                else
                {
    				ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, albumID, fileList[i]);
    				this.transferQueue.add(item);
                    uploadCount++;
                }
        	}
        	else //image already exists on smugmug
        	{
            	String fileMD5 = Helper.computeMD5Hash(fileList[i]);
	        	if (!this.getImage(imageID).getMD5().equals(fileMD5))
	        	{
	        		this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " already exists on smugmug, but has different MD5Sum ... skipping anyway");
	        		skippedCount++;
	        	}
	        	else
	        	{
	        		//this.log.printLogLine("  WARNING: " + fileList[i].getAbsolutePath() + " already exists on smugmug ... skipping");
	        		skippedCount++;
	        	}
        	}
        }        

        this.log.printLogLine("  ... added " + uploadCount + " files to album: " + categoryName + "/" + subcategoryName + "/" + albumName + " (" + skippedCount + " files were skipped, " + unsupportedCount + " had an unsupported file type)");
        

	}

	public void enqueueAlbumForDownload(int albumID, String targetBaseDir)
	{
		this.log.printLogLine(Helper.getTimeString() + " enqueuing album (id:" + albumID + ", target:" + targetBaseDir + ")");
		
		int downloadCount = 0;
		
		String targetDir = targetBaseDir + this.getAlbumDirEnd(albumID);
	
		
		//check target dir
		this.log.printLog("checking dir: " + targetDir + " ... ");
		boolean success = (new File(targetDir)).mkdirs();
	    if (success) { this.log.printLogLine("created"); }
	    else { this.log.printLogLine("ok"); }
	    
	    
		IAlbum album = this.getAlbum(albumID);
		for (IImage image : album.getImageList())
		{
			File imageFile = new File(targetDir + image.getName());
			
			ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), imageFile);
			this.transferQueue.add(item);
			downloadCount++;
		}   
	    
	    this.log.printLogLine("  ... added " + downloadCount + " files (target:" + targetDir + ")");
	}
	
    public void verifyAlbum(int albumID, String targetBaseDir)
    {
		String targetDir = targetBaseDir + this.getAlbumDirEnd(albumID);
    	this.log.printLog(Helper.getTimeString() + " verifying album (id:" + albumID + ", dir:" + targetDir + ") ... ");

		File dir = new File(targetDir);
	    File[] fileList = dir.listFiles(Constants.supportedFileTypesFilter);
	    if (fileList == null)
	    {
	    	/* Either dir does not exist or is not a directory */
	    	this.log.printLogLine("failed");
	      	this.log.printLogLine("ERROR: local album path could not be found");
	      	return;
	    }
	    Arrays.sort(fileList, new Constants.FileComparator()); //sort files, convienence only

	    
	    
        boolean countOK = true;
        String countDelayedOutputString = "";

	    //compare number of files
        IAlbum album = this.getAlbum(albumID);
        Vector<IImage> imageList = album.getImageList();
        if ( fileList.length == imageList.size() )
        {
            //everything seems fine: same number of pictures in SmugMug as in local dir
        }
        else 
        {
        	countOK = false;
        	if ( fileList.length > imageList.size() )
        	{
        		//some files have not been uploaded
        		countDelayedOutputString += "   ERROR: some files have not been uploaded" + "\n";
        		countDelayedOutputString += "   listing local files (" + (fileList.length - imageList.size())  + ") ... " + "\n";
        		for (int i=0; i<fileList.length; i++)
        		{
        			boolean match = false;
        			for (IImage image : imageList)
        			{
        				if (fileList[i].getName().equals(image.getName())) { match = true; }
        			}
        			if (match == false) { countDelayedOutputString += "  " + fileList[i].getAbsolutePath() + "\n"; }
        		}
        		
        	}
        	else //if ( fileList.length < imageList.size() )
        	{
        		//some local files are missing
        		countDelayedOutputString += "   ERROR: some local files are missing" + "\n";
        		countDelayedOutputString += "   listing remote files (" + imageList.size() + ") ... " + "\n";
        		for (IImage image : imageList)
        		{
        			boolean match = false;
        			for (int i=0; i<fileList.length; i++)
        			{
        				if (fileList[i].getName().equals(image.getName())) { match = true; }
        			}
        			if (match == false) { countDelayedOutputString += "  " + image.getName() + "\n"; }
        		}
        	}
          
        	/*
        	delayedOutputString += "listing local files (" + fileList.length + ") ... " + "\n";
        	for (int i=0; i<fileList.length; i++) { delayedOutputString += "  " + fileList[i].getAbsolutePath() + "\n"; }
          
        	delayedOutputString += "listing remote files (" + imageList.size() + ") ... " + "\n";
        	for (IImage image : imageList) { delayedOutputString += "  " + image.getName() + "\n"; }
        	*/
        }        
    	
        
	    // compare albums
        boolean compareOK = true;
        String compareDelayedOutputString = "";
    	for (int i=0; i<fileList.length; i++)
    	{
    		for (IImage image : imageList)
    		{
    			if ( fileList[i].getName().equals(image.getName()) )
    			{
    				//now we have the matching pair, so we check the md5sums
    				String localFileMD5Sum = Helper.computeMD5Hash(fileList[i]);
      			
    				//compare files
			    	//this.log.printLog(this.getTimeString() + "   checking " + fileList[i].getAbsolutePath() + " ... ");
    				compareDelayedOutputString += "   checking " + fileList[i].getAbsolutePath() + " ... ";
					if ( localFileMD5Sum.equals(image.getMD5()) )
					{
						//this.log.printLogLine("ok");
						compareDelayedOutputString += "ok" + "\n";
					}
					else
					{
						//this.log.printLogLine("failed");
						//this.log.printLogLine("   localFileMD5Sum   = " + localFileMD5Sum);
						//this.log.printLogLine("   MD5Sum on SmugMug = " + image.getMD5());

						compareOK = false;
						compareDelayedOutputString += "failed" + "\n";
						compareDelayedOutputString += "      localFileMD5Sum   = " + localFileMD5Sum + "\n";
						compareDelayedOutputString += "      MD5Sum on SmugMug = " + image.getMD5() + "\n";
					}
    			}
      		}
      	}
    	
    	if (countOK && compareOK)
    	{
    		//this.log.printLogLine(this.getTimeString() + " all md5 sums checked ... ok");
    		this.log.printLogLine("ok (all md5 sums checked)");
    	}
    	else
    	{
    		this.log.printLog("failed (see below)\n");
    		if (!countOK)   this.log.printLog( countDelayedOutputString );
    		if (!compareOK) this.log.printLog( compareDelayedOutputString );
    	}
    }

	public void resortCategoryAlbums(int categoryID)
	{
		this.log.printLogLine("resortCategoryAlbums(" + categoryID + ")");
		
		//find category
		ICategory c = this.getCategory(categoryID);

		int index = 0;
		IAlbum[] albumArray = new IAlbum[c.getAlbumList().size()];
		for (IAlbum a : c.getAlbumList())
		{
			albumArray[index] = a;
			index++;
		}
		
		Arrays.sort(albumArray);
		

		//add one pixel image
		int[] imageIDArray = new int[albumArray.length];
		for (int i = 0 ; i < albumArray.length; i++)
		{
			imageIDArray[i] = this.connector.uploadFile(albumArray[i].getID(), new File(Constants.pixelFilename));
		}
		Helper.pause(10000);
		this.connector.relogin();
		//this.pause(30000);
		
		//delete image again
		for (int i = 0 ; i < albumArray.length; i++)
		{
			this.connector.deleteFile(imageIDArray[i]);
		}
	}

	public void resortSubcategoryAlbums(int subcategoryID)
	{
		this.log.printLogLine("resortSubcategoryAlbums(" + subcategoryID + ")");
		
		//find category
		ISubcategory s = this.getSubcategory(subcategoryID);

		int index = 0;
		IAlbum[] albumArray = new IAlbum[s.getAlbumList().size()];
		for (IAlbum a : s.getAlbumList())
		{
			albumArray[index] = a;
			index++;
		}
		
		Arrays.sort(albumArray);
		
		//add one pixel image
		int[] imageIDArray = new int[albumArray.length];
		for (int i = 0 ; i < albumArray.length; i++)
		{
			imageIDArray[i] = this.connector.uploadFile(albumArray[i].getID(), new File(Constants.pixelFilename));
		}
		Helper.pause(10000);
		this.connector.relogin();
		//this.pause(30000);
		
		//delete image again
		for (int i = 0 ; i < albumArray.length; i++)
		{
			this.connector.deleteFile(imageIDArray[i]);
		}
	}
	
	
	public void startProcessingQueue()
	{
		this.transferQueue.startSyncProcessing();
		
		
		this.log.printLog("waiting 30 sec for smugmug to process the images ... ");
		Helper.pause(30000);
		this.log.printLogLine("ok");
		
		//collect Results
		this.connector.relogin(); //probably not nessceary
		Vector<ITransferQueueItem> processedItemList = this.transferQueue.getProcessedItemList();
		for (ITransferQueueItem item : processedItemList)
		{
			this.transferedBytes += item.getResults().getTransferedBytes();
			
			// uploaded images:
			// if item.getAction == upload then add imageid to local data
			if (item.getResults().getAction().equals(TransferQueueItemActionEnum.UPLOAD))
			{
				Hashtable<String, String> imageInfo = this.connector.getImageInfo(item.getResults().getID());
				
				int albumID = Integer.parseInt( imageInfo.get("AlbumID") );
				int imageID = Integer.parseInt( imageInfo.get("ImageID") );
				String imageName = imageInfo.get("ImageName");
			
				//this.log.printLogLine("AlbumID=" + albumID + ", ImageID=" + imageID + ", ImageName=" + imageName);

				if (imageID != 0) { this.addImage(albumID, imageID, imageName); }
			}
		}
		
	}
	
	public long getTransferedBytes() { return this.transferedBytes; }
	
	
	//----------- private ----------
	private Vector<ICategory> getCategoryList()
	{
		return this.categoryList;
	}
	
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
	private int getImageID(int categoryID, int subcategoryID, int albumID, String imageName)
	{
		if (subcategoryID == 0) { return this.getImageID(categoryID, albumID, imageName); }
		
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
									if (i.getName().equals(imageName)) return i.getID();
								}
							}
						}
					}
				}
			}
		}
		
		return 0;
	}
	private int getImageID(int categoryID, int albumID, String imageName)
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
							if (i.getName().equals(imageName)) return i.getID();
						}
					}
				}
			}
		}
		
		return 0;
	}
	
	private ICategory getAlbumCategory(int albumID)
	{
		//find the Category that contains the album, i.e. find the parent category
		
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						return c;
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					return c;
				}
			}
		}
		
		return null;
	}	
	private ISubcategory getAlbumSubcategory(int albumID)
	{
		//find the Subcategory that contains the album, i.e. find the parent subcategory
		
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						return s;
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					return null;
				}
			}
		}
		
		return null;
	}
	
	private ICategory getCategory(int categoryID)
	{
		//find category
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				return c;
			}
		}
		
		return null;
	}
	private ISubcategory getSubcategory(int subcategoryID)
	{
		//find category
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				if (s.getID() == subcategoryID)
				{
					return s;
				}
			}
		}
		
		return null;
	}
	private IAlbum getAlbum(int albumID)
	{
		for (ICategory c : this.getCategoryList())
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID) { return a; }
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID) { return a; }
			}
		}
		
		return null;
	}
	private IImage getImage(int imageID)
	{
		for (ICategory c : this.getCategoryList())
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					for (IImage i : a.getImageList())
					{
						if (i.getID() == imageID) { return i; }
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				for (IImage i : a.getImageList())
				{
					if (i.getID() == imageID) { return i; }
				}
			}
		}
		
		return null;
	}
	
	
	private String getAlbumDirEnd(int albumID)
	{
		//build album dir postfix: search for category, subcategory, and album name
		String resultDir = "";
		
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						resultDir += c.getName() + "/" + s.getName() + "/" + a.getName() + "/";
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					resultDir += c.getName() + "/" + a.getName() + "/";
				}
			}
		}
		
		return resultDir;
	}
	
}