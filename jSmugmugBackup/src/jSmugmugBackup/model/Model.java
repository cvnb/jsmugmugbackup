package jSmugmugBackup.model;

import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.view.*;


import java.io.*;
import java.text.*;
import java.util.*;


public class Model
{
    private GlobalConfig config = null;
	private IAccountListingProxy accListing = null;
    private IView view = null;
    private Logger log = null;
    private long startTime;

    
    /** Constructor */
    public Model()
    {
        this.config = GlobalConfig.getInstance();

    	this.accListing = new AccountListingProxy();
    	this.log = Logger.getInstance();
    	
    	Date date = new Date();
    	this.startTime = date.getTime();
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
    	this.log.printLogLine("finished. (execution time: " + Helper.getDurationTimeString(timeDiff) + ", transfered: " + df.format(transferedMB) + " mb, speed: " + df.format(transferSpeed) + " kb/sec)");
    	System.exit(0);
    }

    public void login(ILoginDialogResult loginDialogResult)
    {
        //this.view.showLoginDialog();

        //String userEmail = this.config.popRtconfigLoginUsername();
		//String password = this.config.popRtconfigLoginPassword();

    	//this.accListing.setLoginMethod(loginMethod);

        

    	//make a maximum of 3 login attempts
    	//this.view.showBusyStart("logging in");

//    	Number userId = null;
//    	for (int i=0; i < 3; i++)
//    	{
//    		userId = this.accListing.login(loginDialogResult.getLoginUsername(), loginDialogResult.getLoginPassword());
//    		if (userId != null) { break; }
//    	}
//    	if (userId == null) { this.quitApplication(); }
    	//this.view.showBusyStop();
    	
    	//this.view.showBusyStart("getting data");
    	//this.accListing.init();
    	//this.view.showBusyStop();


        
        this.accListing.login(loginDialogResult.getLoginUsername(), loginDialogResult.getLoginPassword());


        //this.view.showBusyStart("getting data");
    	//this.accListing.init();
    	//this.view.showBusyStop();
    }
    
    public void list(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

    	this.view.updateFileListing( this.accListing.getAccountListing(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName()) );
    }


	public void upload(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

    	this.log.printLogLine("preparing upload of pics from: " + transferDialogResult.getDir());

		
		String category    = transferDialogResult.getCategoryName();
		String subcategory = transferDialogResult.getSubCategoryName();
		String album       = transferDialogResult.getAlbumName();
		
		if ( (transferDialogResult.getCategoryName()    == null) && 
		     (transferDialogResult.getSubCategoryName() == null) &&
		     (transferDialogResult.getAlbumName()       == null) )
		{
			File rootDir = new File(transferDialogResult.getDir());
			if (rootDir.isDirectory()) //should normally be true
			{
				if (this.containsPics(rootDir))
				{
					//print a warning - we have nowhere to put the images
					this.log.printLogLine("WARNING: the directory " + rootDir + " contains images which will be ignored ... specify a \"--album\" parameter or use the parent directory for the \"--dir\" parameter");
				}
				
				//go on, search for sub-directories
				File[] subDirFileList = rootDir.listFiles(); Arrays.sort(subDirFileList, this.config.getConstantFileComparator());
				for (int i=0; i < subDirFileList.length; i++)
				{
					File subDirFile = subDirFileList[i];
					if (subDirFile.isDirectory())
					{
						if (this.containsPics(subDirFile))
						{
							category = "Other";
							subcategory = null;
							album = subDirFile.getName();								
							this.upload_prepare_albumDir(category, subcategory, album, subDirFile);
						}
						else //search in sub-sub-directories
						{
							File[] subSubDirList = subDirFile.listFiles(); Arrays.sort(subSubDirList, this.config.getConstantFileComparator());
							for (int j=0; j < subSubDirList.length; j++)
							{
								File subSubDirFile = subSubDirList[j];
								if (subSubDirFile.isDirectory())
								{
									if (this.containsPics(subSubDirFile))
									{
										category    = subSubDirFile.getParentFile().getName();
										subcategory = null;
										album       = subSubDirFile.getName();
										this.upload_prepare_albumDir(category, subcategory, album, subSubDirFile);
									}
									else //search in sub-sub-sub-directories
									{
										File[] subSubSubDirList = subSubDirFile.listFiles(); Arrays.sort(subSubSubDirList, this.config.getConstantFileComparator());
										for (int k=0; k < subSubSubDirList.length; k++)
										{
											File subSubSubDirFile = subSubSubDirList[k];
											if (subSubSubDirFile.isDirectory())
											{
												if (this.containsPics(subSubSubDirFile))
												{
													category    = subSubSubDirFile.getParentFile().getParentFile().getName();
													subcategory = subSubSubDirFile.getParentFile().getName();
													album       = subSubSubDirFile.getName();
													this.upload_prepare_albumDir(category, subcategory, album, subSubSubDirFile);
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
				}
	        }
			else
			{
				this.log.printLogLine("expected a directory, not a file (" + rootDir + ")");
			}
		}
		else if ( (transferDialogResult.getCategoryName()    != null) &&
				  (transferDialogResult.getSubCategoryName() == null) &&
				  (transferDialogResult.getAlbumName()       == null) )
		{
		
			File rootDir = new File(transferDialogResult.getDir());
			if (rootDir.isDirectory()) //should normally be true
			{
				if (this.containsPics(rootDir))
				{
					//print a warning - we have nowhere to put the images
					this.log.printLogLine("WARNING: the directory " + rootDir + " contains images which will be ignored ... specify a \"--album\" parameter or use the parent directory for the \"--dir\" parameter");
				}
				
				//go on, search for sub-directories
				File[] subDirFileList = rootDir.listFiles(); Arrays.sort(subDirFileList, this.config.getConstantFileComparator());
				for (int i=0; i < subDirFileList.length; i++)
				{
					File subDirFile = subDirFileList[i];
					if (subDirFile.isDirectory())
					{
						if (this.containsPics(subDirFile))
						{
							//category is defined above
							subcategory = null;
							album = subDirFile.getName();								
							this.upload_prepare_albumDir(category, subcategory, album, subDirFile);
						}
						else //search in sub-sub-directories
						{
							File[] subSubDirList = subDirFile.listFiles(); Arrays.sort(subSubDirList, this.config.getConstantFileComparator());
							for (int j=0; j < subSubDirList.length; j++)
							{
								File subSubDirFile = subSubDirList[j];
								if (subSubDirFile.isDirectory())
								{
									if (this.containsPics(subSubDirFile))
									{
										//category is defined above
										subcategory = subSubDirFile.getParentFile().getName();
										album       = subSubDirFile.getName();
										this.upload_prepare_albumDir(category, subcategory, album, subSubDirFile);
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
			else
			{
				this.log.printLogLine("expected a directory, not a file (" + rootDir + ")");
			}
		}
		else if ( (transferDialogResult.getCategoryName()    != null) &&
				  (transferDialogResult.getSubCategoryName() != null) &&
				  (transferDialogResult.getAlbumName()       == null) )
		{
		
			File rootDir = new File(transferDialogResult.getDir());
			if (rootDir.isDirectory()) //should normally be true
			{
				if (this.containsPics(rootDir))
				{
					//print a warning - we have nowhere to put the images
					this.log.printLogLine("WARNING: the directory " + rootDir + " contains images which will be ignored ... specify a \"--album\" parameter or use the parent directory for the \"--dir\" parameter");
				}
				
				//go on, search for sub-directories
				File[] subDirFileList = rootDir.listFiles(); Arrays.sort(subDirFileList, this.config.getConstantFileComparator());
				for (int i=0; i < subDirFileList.length; i++)
				{
					File subDirFile = subDirFileList[i];
					if (subDirFile.isDirectory())
					{
						if (this.containsPics(subDirFile))
						{
							//category is defined above
							//subcategory is defined above
							album = subDirFile.getName();								
							this.upload_prepare_albumDir(category, subcategory, album, subDirFile);
						}
						else
						{
							//not going any deeper
						}
					}
				}
	        }
			else
			{
				this.log.printLogLine("expected a directory, not a file (" + rootDir + ")");
			}
		}
		else if (transferDialogResult.getAlbumName() != null) //handles all cases where an album name is given
		{			
			if (transferDialogResult.getCategoryName() == null) { category = "Other"; }
			//if subcategory is null or not, doesn't matter
			
			File rootDir = new File(transferDialogResult.getDir());
			if (rootDir.isDirectory()) //should normally be true
			{
				if (this.containsPics(rootDir))
				{
					//category is defined above
					//subcategory is defined above
					//album is defined above
					
					this.upload_prepare_albumDir(category, subcategory, album, rootDir);
				}
	        }
			else
			{
				this.log.printLogLine("expected a directory, not a file (" + rootDir + ")");
			}
		}		
		else
		{
			this.log.printLogLine("ERROR: this case is yet unhandled");
			this.quitApplication();
		}

    }
	
	private void upload_prepare_albumDir(String category, String subcategory, String album, File dir)
	{
//		this.log.printLogLine("DEBUG: enqueuing ...");
//		this.log.printLogLine("DEBUG:      category    : " + category);
//		this.log.printLogLine("DEBUG:      subcategory : " + subcategory);
//		this.log.printLogLine("DEBUG:      album       : " + album);
//		this.log.printLogLine("DEBUG:      dir         : " + dir);
		
		this.accListing.enqueueAlbumForUpload(category, subcategory, album, dir);
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
    	
    	IRootElement smugmugRoot = this.accListing.getAccountListing(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
    	//this.log.printLogLine("model: categoryList.size()=" + categoryList.size());
    	
    	for (ICategory c : smugmugRoot.getCategoryList())
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
    	File[] fileList = dir.listFiles(config.getConstantSupportedFileTypesFilter());
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
    

}
