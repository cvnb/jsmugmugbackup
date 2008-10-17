package jSmugmugBackup.model;

import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.view.*;
import jSmugmugBackup.view.login.*;

import java.io.*;
import java.security.*;
import java.text.*;
import java.util.*;


public class Model
{
	private IAccountListingProxy accListing = null;
    private IView view = null;
    private Logger log = null;
    private long startTime;


    
    /** Constructor */
    public Model()
    {
    	this.accListing = new AccountListingProxy();
    	this.log = Logger.getInstance();
    	
    	Date date = new Date();
    	this.startTime = date.getTime();
    }    

    public void login(ILoginView loginMethod)
    {
    	this.accListing.setLoginMethod(loginMethod);
    	this.accListing.login();
    	this.accListing.init();
    }
    
    public void getFileListing()
    {
    	if (this.view != null)
    	{
    		this.view.refreshFileListing( this.accListing.getCategoryList() );
    	}
    }

    public void uploadPrepare(ITransferDialogResult transferDialogResult)
    {
    	this.log.printLogLine("preparing upload of pics from: " + transferDialogResult.getDir());

		
		String category    = transferDialogResult.getCategoryName();
		String subcategory = transferDialogResult.getSubCategoryName();
		String album       = transferDialogResult.getAlbumName();
		
		if ( (transferDialogResult.getCategoryName() == null) && (transferDialogResult.getSubCategoryName() == null) && (transferDialogResult.getAlbumName() == null) )
		{
			File dir = new File(transferDialogResult.getDir());
			if (dir.isDirectory()) //should normally be true
			{
				if (this.containsPics(dir)) //we're already there
				{
					//album = this.extractAlbumNameFromDir(dir.getAbsolutePath());
					category    = "Other";
					subcategory = null;
					album       = dir.getName();

					//this.uploadPrepareAlbum(category, subcategory, album, dir);
					this.accListing.enqueueAlbumForUpload(category, subcategory, album, dir);
				}
				else //go on, search for sub-directories
				{
					File[] dirList = dir.listFiles();
					Arrays.sort(dirList, new Constants.FileComparator());
					for (int i=0; i < dirList.length; i++)
					{
						File subDir = dirList[i];
						if (subDir.isDirectory())
						{
							if (this.containsPics(subDir))
							{
								category    = subDir.getParentFile().getName();
								subcategory = null;
								album       = subDir.getName();
								
								//this.uploadPrepareAlbum(category, subcategory, album, subDir);
								this.accListing.enqueueAlbumForUpload(category, subcategory, album, subDir);
							}
							else //search in sub-sub-directories
							{
								File[] subDirList = subDir.listFiles();
	    						Arrays.sort(subDirList, new Constants.FileComparator());
								for (int j=0; j < subDirList.length; j++)
								{
									File subSubDir = subDirList[j];
									if (subSubDir.isDirectory())
									{
										if (this.containsPics(subSubDir))
										{
											category    = subSubDir.getParentFile().getParentFile().getName();
											subcategory = subSubDir.getParentFile().getName();
											album       = subSubDir.getName();
											//this.uploadPrepareAlbum(category, subcategory, album, subSubDir);
											this.accListing.enqueueAlbumForUpload(category, subcategory, album, subSubDir);
										}
										else
										{
											//not going any deeper
										}
									}
								}
							}
						}
					}
				}
	        }
			else
			{
				this.log.printLogLine("expected a directory, not a file");
			}
		}
		else if (transferDialogResult.getCategoryName() != null)
		{
			if (transferDialogResult.getAlbumName() == null)
			{
				//determine album name from directory
				album = this.extractAlbumNameFromDir(transferDialogResult.getDir());
			}
			//this.uploadPrepareAlbum(category, subcategory, album, new File(transferDialogResult.getDir()));
			this.accListing.enqueueAlbumForUpload(category, subcategory, album, new File(transferDialogResult.getDir()));
		}
		else
		{
			this.log.printLogLine("this case is yet unhandled");
			this.quitApplication();
		}
    }    
    
    public void downloadPrepare(ITransferDialogResult transferDialogResult)
    {
//    	//check if we are logged in
//    	if ( (this.loginToken != null) && (this.loginToken.getToken() != null) )
//    	{
//    		this.log.printLogLine("preparing to download files to: " + transferDialogResult.getDir());
//    		
//    		AccountListing accListing = this.smugmugConnector.getAccountStructure();
//
//    		//key: GUID; value: album target path
//    		Hashtable<GUID, String> selectedAlbumHashtable = new Hashtable<GUID, String>();
//    		selectedAlbumHashtable = this.matchAlbumsOnSmugmug(accListing, transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
//
//    		/*    		
//    		for (ICategoryType c : accListing.getCategoryList())
//    		{
//    			for (ISubCategoryType s : c.getSubCategoryList())
//    			{
//    				for (IAlbumType a : s.getAlbumList())
//    				{
//    					
//    					if ( (transferDialogResult.getCategoryName() == null) || (c.getName().equals(transferDialogResult.getCategoryName())) )
//    					{
//    						if ( (transferDialogResult.getSubCategoryName() == null) || (s.getName().equals(transferDialogResult.getSubCategoryName())) )
//    						{
//    							if ( (transferDialogResult.getAlbumName() == null) || (a.getName().equals(transferDialogResult.getAlbumName())) )
//    							{
//    								String album_dir;
//    								album_dir = c.getName() + "/" + s.getName() + "/" + a.getName();
//    								
//    								this.log.printLogLine("   selecting album: " + a.getName() + " - " + album_dir);
//    								selectedAlbumHashtable.put(a.getGUID(), album_dir);
//    							}
//    						}
//    					}
//    					
//    				}
//    			}
//    			
//    			for (IAlbumType a : c.getAlbumList())
//    			{
//    				
//    				if (transferDialogResult.getSubCategoryName() == null)
//    				{
//    					if ( (transferDialogResult.getCategoryName() == null) || (c.getName().equals(transferDialogResult.getCategoryName())) )
//    					{
//    						if ( (transferDialogResult.getAlbumName() == null) || (a.getName().equals(transferDialogResult.getAlbumName())) )
//							{
//								String album_dir;
//								album_dir = c.getName() + "/" + a.getName();
//								
//								this.log.printLogLine("   selecting album: " + a.getName() + " - " + album_dir);
//								selectedAlbumHashtable.put(a.getGUID(), album_dir);
//							}
//    					}
//					}
//    				
//    			}
//    		}
//    		*/
//    		
//    		//collect selected albums
//			if (selectedAlbumHashtable.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
//    		
//			for (GUID albumGUID : selectedAlbumHashtable.keySet())
//    		{
//    	    	String targetDir = transferDialogResult.getDir() + selectedAlbumHashtable.get(albumGUID) + "/";    			
//    	    	this.downloadPrepareAlbum(accListing, albumGUID, targetDir);    			
//    		}
//    	}
    }
    
    public void verifyPrepare(ITransferDialogResult transferDialogResult)
    {
//    	//todo: what about missing local dirs?
//    	
//    	//this.log.printLogLine("Model.verifyPrepare() - verifying files ... not implemented yet ...");
//    	//this.log.printLogLine("Model.verifyPrepare() -   category    : " + transferDialogResult.getCategoryName());
//    	//this.log.printLogLine("Model.verifyPrepare() -   subcategory : " + transferDialogResult.getSubCategoryName());
//    	//this.log.printLogLine("Model.verifyPrepare() -   album       : " + transferDialogResult.getAlbumName());
//    	//this.log.printLogLine("Model.verifyPrepare() -   dir         : " + transferDialogResult.getDir());
//
//    	//check if we are logged in
//    	if ( (this.loginToken != null) && (this.loginToken.getToken() != null) )
//    	{
//    		//this.log.printLogLine("preparing to verify files from: " + transferDialogResult.getDir());
//
//    		AccountListing accListing = this.smugmugConnector.getAccountStructure();
//    		
//    		//key: GUID; value: album target path
//    		Hashtable<GUID, String> selectedAlbumHashtable;
//    		selectedAlbumHashtable = this.matchAlbumsOnSmugmug(accListing, transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
//
//    		//collect selected albums
//			if (selectedAlbumHashtable.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
//    		
//			for (GUID albumGUID : selectedAlbumHashtable.keySet())
//    		{
//				String targetDirString = transferDialogResult.getDir() + selectedAlbumHashtable.get(albumGUID) + "/";
//				File targetDir = new File(targetDirString);
//    	    	//this.log.printLogLine("Model.verifyPrepare() -  selected albumGUID: " + albumGUID + " (" + targetDir + ")" );
//    	    	
//    	    	this.verifyAlbum(accListing, albumGUID, targetDir);
//    		}
//    		
//    	}
    }
    
    
    public void deletePrepare(ITransferDialogResult transferDialogResult)
    {
//    	//check if we are logged in
//    	if ( (this.loginToken != null) && (this.loginToken.getToken() != null) )
//    	{
//    		this.log.printLogLine("preparing to delete files");
//
//    		//Vector<IAlbumType> selectedAlbumList = new Vector<IAlbumType>();
//    		//Vector<ISubCategoryType> selectedSubcategoryList = new Vector<ISubCategoryType>();
//    		Vector<ICategoryType> selectedCategoryList = new Vector<ICategoryType>();
//    		
//    		if ( (transferDialogResult.getCategoryName() == null) &&
//    			 (transferDialogResult.getSubCategoryName() == null) &&
//    			 (transferDialogResult.getAlbumName() == null) )
//    		{
//    			this.log.printLogLine("ERROR: the whole account can not be deleted! (for your own safety)");
//    		}
//    		else if ( (transferDialogResult.getCategoryName() != null) &&
//    				  (transferDialogResult.getSubCategoryName() == null) &&
//    			      (transferDialogResult.getAlbumName() == null) )
//    		{
//    			//just the category name is given
//        		AccountListing accListing = this.smugmugConnector.getAccountStructure();
//        		
//        		for (ICategoryType c : accListing.getCategoryList())
//        		{
//        			if ( c.getName().equals(transferDialogResult.getCategoryName()) )
//        			{
//        				selectedCategoryList.add(c);
//        				this.log.printLogLine("   selected category: " + c.getName());
//        				this.smugmugConnector.deleteFile(c.getGUID());
//        			}
//        		}
//    		}
//    		else
//    		{
//    			this.log.printLogLine("ERROR: not implemented yet");
//    		}
//
//    		
//    		this.log.printLogLine("no matching category was found on your SmugMug Account");
//    	}
    }

    
    public void startProcessingQueue()
    {
    	this.accListing.startProcessingQueue();
    }
    
    public void setView(IView view)
    {
    	this.view = view;    	
    }
    
    public void quitApplication()
    {
    	//logout from smugmug
    	//if (this.smugmugConnector != null) { this.smugmugConnector.logout(); }
    	if (this.accListing != null) { this.accListing.logout(); }

    	//statistics
        long totalTransferedBytes = this.accListing.getTransferedBytes();
    	Date date = new Date();
    	long timeDiff = date.getTime() - this.startTime;
    	
    	double transferedMB = (double)totalTransferedBytes / (1024.0 * 1024.0);
    	
		double transferSpeed = 0.0;
		//avoid division by zero
		if (timeDiff != 0) { transferSpeed = ((double)totalTransferedBytes / 1024.0) / ((double)timeDiff / 1000.0); }

		
		DecimalFormat df = new DecimalFormat("0.0");
    	this.log.printLogLine("finished. (execution time: " + this.getExecTimeString(timeDiff) + ", transfered: " + df.format(transferedMB) + " mb, speed: " + df.format(transferSpeed) + " kb/sec)");
    	System.exit(0);
    }
    
    //-------------------------- private ----------------------------------------

    private boolean containsPics(File dir)
    {
    	File[] fileList = dir.listFiles(Constants.supportedFileTypesFilter);
    	for (int i=0; i < fileList.length; i++)
    	{
    		// if the (already filtered) fileList contains Files and not just Directories, these Files must be pictures
    		if (fileList[i].isFile()) return true;
    	}
    	return false;
    }
    
    private String extractAlbumNameFromDir(String dir)
    {
    	String result = dir;
    	
    	//remove tailing slash
    	if (result.endsWith("/")) result = result.substring(0, result.length() - 1);
    	
    	//remove leading root dir
    	result = result.substring( result.lastIndexOf("/") + 1 );
    	
    	this.log.printLogLine("extracted album name \"" + result + "\" from dir: " + dir);
    	
    	return result;
    }
    
//    private Hashtable<GUID, String> matchAlbumsOnSmugmug(AccountListing accListing, String categoryName, String subcategoryName, String albumName)
//    {
//    	Hashtable<GUID, String> selectedAlbumHashtable = new Hashtable<GUID, String>();
//    	
//    	//decend to all album lists of all Subcategories and Categories
//		for (ICategoryType c : accListing.getCategoryList())
//		{
//			for (ISubCategoryType s : c.getSubCategoryList())
//			{
//				for (IAlbumType a : s.getAlbumList())
//				{
//					
//					//here, we should walk over all albums that belong to all subcategories
//					if ( (categoryName == null) || (c.getName().equals(categoryName)) )
//					{
//						if ( (subcategoryName == null) || (s.getName().equals(subcategoryName)) )
//						{
//							if ( (albumName == null) || (a.getName().equals(albumName)) )
//							{
//								String album_dir;
//								album_dir = c.getName() + "/" + s.getName() + "/" + a.getName();
//								
//								//this.log.printLogLine("  matched album: " + a.getName() + " - " + album_dir);
//								this.log.printLogLine("  matched album: " + album_dir);
//								selectedAlbumHashtable.put(a.getGUID(), album_dir);
//							}
//						}
//					}
//					
//				}
//			}
//			
//			for (IAlbumType a : c.getAlbumList())
//			{
//				//here, we walk over all albums which have no subcategory
//				if (subcategoryName == null) //hence, subcategoryName must be null
//				{
//					if ( (categoryName == null) || (c.getName().equals(categoryName)) )
//					{
//						if ( (albumName == null) || (a.getName().equals(albumName)) )
//						{
//							String album_dir;
//							album_dir = c.getName() + "/" + a.getName();
//							
//							this.log.printLogLine("Model.matchAlbumsOnSmugmug() -    selecting album: " + a.getName() + " - " + album_dir);
//							selectedAlbumHashtable.put(a.getGUID(), album_dir);
//						}
//					}
//				}
//
//			}
//		}
//
//		this.log.printLogLine("Model.matchAlbumsOnSmugmug() -    matched albums: " + selectedAlbumHashtable.size() );
//    	
//    	return selectedAlbumHashtable;
//    }
    
//	private void uploadPrepareAlbum(String categoryName, String subcategoryName, String albumName, File pics_dir)
//    {
//    	this.log.printLogLine("-----------------------------------------------");
//    	this.log.printLogLine("preparing album: " + categoryName + "/" + subcategoryName + "/" + albumName + " ... dir: " + pics_dir);
//
//    	int uploadCount = 0;
//        File[] fileList = pics_dir.listFiles(Constants.supportedFileTypesFilter);
//        if (fileList == null) { return; /* Either dir does not exist or is not a directory */ }
//        Arrays.sort(fileList, new Constants.FileComparator()); //sort files
//        
//
//    	//create category, subcategory and album
//        GUID categoryGUID = this.smugmugConnector.getCategoryGUID(categoryName);
//        GUID subCategoryGUID = this.smugmugConnector.getSubCategoryGUID(categoryGUID, subcategoryName);
//        GUID albumGUID = this.smugmugConnector.getAlbumGUID(categoryGUID, subCategoryGUID, albumName, "");
//
//        for (int i=0; i<fileList.length; i++)
//        {
//        	//this.smugmugConnector.uploadFile(albumID, fileList[i]);
//        	
//        	try
//        	{
//				ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, this.loginToken, albumGUID, fileList[i]);
//				this.transferQueue.add(item);
//                uploadCount++;
//			}
//        	catch (TransferQueueException e) { e.printStackTrace(); }
//        }
//
//        this.log.printLogLine("  ... added " + uploadCount + " files to album: " + categoryName + "/" + subcategoryName + "/" + albumName);
//    }
    
//    private void downloadPrepareAlbum(AccountListing accListing, GUID albumGUID, String targetDir)
//    {
//    	this.log.printLogLine("-----------------------------------------------");
//    	this.log.printLogLine("preparing album (target:" + targetDir + ")");
//
//    	int downloadCount = 0;
//
//    	//check target dir
//    	this.log.printLog("checking dir: " + targetDir + " ... ");
//    	boolean success = (new File(targetDir)).mkdirs();
//	    if (success) { this.log.printLogLine("created"); }
//	    else { this.log.printLogLine("ok"); }
//
//    	if (this.loginToken.getToken() != null)
//    	{
//    		IAlbumType album = accListing.getAlbumByGUID(albumGUID);
//    		
//    		for (IImageType image : album.getImageList())
//    		{
//    			//this.smugmugConnector.downloadFile(i.guid(), targetDir);
//    			
//            	try
//            	{
//    				ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, this.loginToken, image.getGUID(), targetDir);
//    				this.transferQueue.add(item);
//                    downloadCount++;
//    			}
//            	catch (TransferQueueException e) { e.printStackTrace(); }
//    		}    		
//    	}
//    	
//        this.log.printLogLine("  ... added " + downloadCount + " files (target:" + targetDir + ")");
//    }
    
//    private void verifyAlbum(AccountListing accListing, GUID albumGUID, File targetDir)
//    {
//    	this.log.printLogLine("-----------------------------------------------");
//    	this.log.printLogLine("verifying album from " + targetDir.getAbsolutePath() + " ...");
//
//        File[] fileList = targetDir.listFiles(Constants.supportedFileTypesFilter);
//        if (fileList == null)
//        {
//        	/* Either dir does not exist or is not a directory */ 
//        	this.log.printLogLine("  exiting unexpected!");
//        	return;
//        }
//        Arrays.sort(fileList, new Constants.FileComparator()); //sort files, convienence only
//
//        IAlbumType album = accListing.getAlbumByGUID(albumGUID);
//        Vector<IImageType> imageList = album.getImageList();
//        
//        if ( fileList.length == imageList.size() )
//        {
//        	//everything seems fine: same number of pictures in SmugMug as in local dir
//        	for (int i=0; i<fileList.length; i++)
//            {
//            	for (IImageType image : imageList)
//            	{
//            		if ( fileList[i].getName().equals(image.getName()) )
//            		{
//            			//now we have the matching pair, so we check the md5sums
//            			String localFileMD5Sum = this.computeMD5Hash(fileList[i]);
//            			
//            			//compare
//				    	this.log.printLog(this.getTimeString() + "   checking " + fileList[i].getAbsolutePath() + " ... ");
//						if ( localFileMD5Sum.equals(image.getMD5Sum()) )
//						{
//							this.log.printLogLine("ok");
//						}
//						else
//						{
//							this.log.printLogLine("failed");
//							this.log.printLogLine("   localFileMD5Sum   = " + localFileMD5Sum);
//							this.log.printLogLine("   MD5Sum on SmugMug = " + image.getMD5Sum());
//						}        				
//            		}
//            	}
//            }            
//        }
//        else 
//        {
//        	if ( fileList.length > imageList.size() )
//        	{
//        		//some files have not been uploaded
//        		this.log.printLogLine("looks like some files have not been uploaded");
//        	}
//        	else //if ( fileList.length < imageList.size() )
//        	{
//            	//some local files are missing
//            	this.log.printLogLine("looks like some local files are missing");
//        	}
//            
//        	this.log.printLogLine("listing local files (" + fileList.length + ") ... ");
//            for (int i=0; i<fileList.length; i++) { this.log.printLogLine("  " + fileList[i].getAbsolutePath() ); }
//            
//            this.log.printLogLine("listing remote files (" + imageList.size() + ") ... ");
//        	for (IImageType image : imageList) { this.log.printLogLine("  " + image.getName() ); }
//        }        
//    }
    
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
    
	private String getExecTimeString(long time)
	{				
		int hours   = (int)(time / 1000 / 60 / 60);
		int minutes = (int)(time / 1000 / 60);
		int seconds = (int)(time / 1000);
		
		return new String(hours + "h" + (minutes-(hours*60)) + "m" + (seconds-(minutes*60)) + "s");
	}
}
