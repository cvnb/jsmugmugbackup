package jSmugmugBackup.model;

import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.view.*;
import jSmugmugBackup.view.login.*;

import java.io.*;
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
    
    public void list(ITransferDialogResult transferDialogResult)
    {
    	this.view.refreshFileListing( this.accListing.getAccountListing(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName()) );
    }

	public void upload(ITransferDialogResult transferDialogResult)
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
    
    public void download(ITransferDialogResult transferDialogResult)
    {
		this.log.printLogLine("preparing to download files to: " + transferDialogResult.getDir());
		
		Vector<IAlbum> selectedAlbums = this.accListing.matchAlbums(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
		if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
		
		for (IAlbum a : selectedAlbums)
		{
			this.accListing.enqueueAlbumForDownload(a.getID(), transferDialogResult.getDir());
		}
    }
    
    public void verify(ITransferDialogResult transferDialogResult)
    {
    	//todo: what about missing local dirs?
    	this.log.printLogLine("preparing to verify files from: " + transferDialogResult.getDir());
    	
		Vector<IAlbum> selectedAlbums = this.accListing.matchAlbums(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
		if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
    	
    	for (IAlbum a : selectedAlbums)
    	{
    		this.accListing.verifyAlbum(a.getID(), transferDialogResult.getDir());
    	}
    }
    
    public void sort(ITransferDialogResult transferDialogResult)
    {
		//try to bring the albums to a correct order - happens if files were uploaded in an wrong order
    	this.log.printLogLine("preparing to sort albums");
    	
    	Vector<ICategory> categoryList = this.accListing.getAccountListing(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
    	//this.log.printLogLine("model: categoryList.size()=" + categoryList.size());
    	
    	for (ICategory c : categoryList)
    	{
    		//this.log.printLogLine("model: c.getName()=" + c.getName());
    		this.accListing.resortCategoryAlbums(c.getID());
    		
    		for (ISubcategory s : c.getSubcategoryList())
    		{
    			//this.log.printLogLine("model: s.getName()=" + s.getName());
    			this.accListing.resortSubcategoryAlbums(s.getID());
    		}
    	}
    }
    
    public void delete(ITransferDialogResult transferDialogResult)
    {
    	this.log.printLogLine("preparing to delete files");
    	this.log.printLogLine("ERROR: not implemented");

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
    	
    	this.quitApplication();
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
    
	private String getExecTimeString(long time)
	{				
		int hours   = (int)(time / 1000 / 60 / 60);
		int minutes = (int)(time / 1000 / 60);
		int seconds = (int)(time / 1000);
		
		return new String(hours + "h" + (minutes-(hours*60)) + "m" + (seconds-(minutes*60)) + "s");
	}
}
