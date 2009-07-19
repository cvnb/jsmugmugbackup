package jSmugmugBackup.model;

import jSmugmugBackup.config.*;
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
    	this.log.printLogLine("finished. (execution time: " + Helper.getDurationTimeString(timeDiff) + ", transfered: " + df.format(transferedMB) + " mb, avg speed: " + df.format(transferSpeed) + " kb/sec)");
    	System.exit(0);
    }

    public void login(ILoginDialogResult loginDialogResult)
    {
        if (loginDialogResult == null) { return; }

        Number loginResult;
        loginResult = this.accListing.login(loginDialogResult.getLoginUsername(), loginDialogResult.getLoginPassword());

        //if login failed, quit
        if (loginResult == null) { this.quitApplication(); }
    }
    
    public void list(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

    	this.view.updateFileListing( this.accListing.getAccountTree(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords()) );
    }

    public void upload(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

    	this.log.printLogLine("preparing upload of pics from: " + transferDialogResult.getDir());


		String category    = transferDialogResult.getCategoryName();
		String subcategory = transferDialogResult.getSubCategoryName();
		String album       = transferDialogResult.getAlbumName();
        String keywords    = transferDialogResult.getAlbumKeywords();

        File rootDir = new File(transferDialogResult.getDir());

        //check if someone has manually set the ignore tag ... this is probably needed only once
        //this.log.printLogLine("checking: " + directory.getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix());
        if ( (new File(rootDir.getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix())).exists() )
        {
            this.log.printLogLine("WARNING: " + rootDir.getAbsolutePath() + " - the ignore tag was set ... skipping this directory");
            return;
        }

		if ((category == null) && (subcategory == null) && (album == null))
		{			
            this.recursiveUploadDirectorySearch(3, rootDir, category, subcategory, album, keywords);
		}
		else if ((category != null) && (subcategory == null) && (album == null))
		{
            this.recursiveUploadDirectorySearch(2, rootDir, category, subcategory, album, keywords);
		}
		else if ((category != null) && (subcategory != null) && (album == null))
		{
            this.recursiveUploadDirectorySearch(1, rootDir, category, subcategory, album, keywords);
		}
		else if (transferDialogResult.getAlbumName() != null) //no recursion needed, handles all cases where an album name is given
		{
			if (transferDialogResult.getCategoryName() == null) { category = "Other"; }
			//if subcategory is null or not, doesn't matter

			if (rootDir.isDirectory()) //should normally be true
			{
				if (this.containsPics(rootDir))
				{
					//category is defined above
					//subcategory is defined above
					//album is defined above

					this.accListing.enqueueAlbumForUpload(category, subcategory, album, rootDir, keywords);
				}
	        }
			else { this.log.printLogLine("ERROR: expected a directory, not a file (" + rootDir + ")"); this.quitApplication(); }
		}
		else
		{
			this.log.printLogLine("ERROR: Model.upload: this case is yet unhandled"); this.quitApplication();
		}
    }
    
    public void download(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

		this.log.printLogLine("preparing to download files to: " + transferDialogResult.getDir());

        IRootElement smugmugRoot = this.accListing.getAccountTree(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords());

        //add all albums, since they have already been filtered above
        Vector<IAlbum> selectedAlbums = new Vector<IAlbum>();
        for (ICategory c : smugmugRoot.getCategoryList())
        {
            for (ISubcategory s : c.getSubcategoryList())
            {
                for (IAlbum a : s.getAlbumList()) { selectedAlbums.add(a); }
            }

            for (IAlbum a : c.getAlbumList()) { selectedAlbums.add(a); }
        }
        if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }

        for (IAlbum a : selectedAlbums)
		{
			this.accListing.enqueueAlbumForDownload(a.getID(), null, transferDialogResult.getDir());
		}

//        this.log.printLogLine("category   : " + transferDialogResult.getCategoryName());
//        this.log.printLogLine("subcategory: " + transferDialogResult.getSubCategoryName());
//		Vector<IAlbum> selectedAlbums = this.accListing.matchAlbums(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
//		if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
//
//		for (IAlbum a : selectedAlbums)
//		{
//			this.accListing.enqueueAlbumForDownload(a.getID(), transferDialogResult.getDir());
//		}
    }

    public void downloadURL(ITransferDialogResult transferDialogResult)
    {
        //this.log.printLogLine("DEBUG: downloadURL stub (Model)");
        //this.log.printLogLine("DEBUG: url=" + transferDialogResult.getURL());

        String url = transferDialogResult.getURL();
        String urlTail = url.substring(url.indexOf("/gallery/") + 9);

        String albumIDString = urlTail.substring(0, urlTail.indexOf("_"));
        int albumID = Integer.parseInt(albumIDString);
        String albumKey = urlTail.substring(urlTail.indexOf("_")+1, urlTail.indexOf("/"));

        //this.log.printLogLine("DEBUG: albumID : " + albumID);
        //this.log.printLogLine("DEBUG: albumKey: " + albumKey);
        this.accListing.enqueueAlbumForDownload(albumID, albumKey, transferDialogResult.getDir());


    }
    
    public void verify(ITransferDialogResult transferDialogResult)
    {
        this.log.printLogLine("preparing to verify files from: " + transferDialogResult.getDir());

//        IRootElement smugmugRoot = this.accListing.getAccountTree(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
//
//        //add all albums, since they have already been filtered above
//        Vector<IAlbum> selectedAlbums = new Vector<IAlbum>();
//        for (ICategory c : smugmugRoot.getCategoryList())
//        {
//            for (ISubcategory s : c.getSubcategoryList())
//            {
//                for (IAlbum a : s.getAlbumList())
//                {
//                    selectedAlbums.add(a);
//                }
//            }
//
//            for (IAlbum a : c.getAlbumList())
//            {
//                selectedAlbums.add(a);
//            }
//        }
        Vector<IAlbum> selectedAlbums = this.accListing.getAccountAlbumList(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords());
        if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }

        
        // compute target base dir
        // convienience: cut off category, subcategory and album dir if they exist
        String targetBaseDir = transferDialogResult.getDir();
        //this.log.printLogLine("targetBaseDir: " + targetBaseDir);

        /*
        //check if a category has been given
        if (transferDialogResult.getCategoryName() != null)
        {
            if (transferDialogResult.getSubCategoryName() != null)
            {
                if (transferDialogResult.getAlbumName() != null)
                {
                    if (targetBaseDir.lastIndexOf(transferDialogResult.getAlbumName()) != -1) { targetBaseDir = targetBaseDir.substring(0, targetBaseDir.lastIndexOf(transferDialogResult.getAlbumName()) ); }
                }

                if (targetBaseDir.lastIndexOf(transferDialogResult.getSubCategoryName()) != -1) { targetBaseDir = targetBaseDir.substring(0, targetBaseDir.lastIndexOf(transferDialogResult.getSubCategoryName()) ); }
            }

            //reduce baseDir by the category name
            if (targetBaseDir.lastIndexOf(transferDialogResult.getCategoryName()) != -1) { targetBaseDir = targetBaseDir.substring(0, targetBaseDir.lastIndexOf(transferDialogResult.getCategoryName()) ); }
        }
        //this.log.printLogLine("targetBaseDir: " + targetBaseDir);
        */

        for (IAlbum a : selectedAlbums)
		{
            String targetAlbumDir = targetBaseDir;

            if (transferDialogResult.getAlbumName() == null)
            {
                if (transferDialogResult.getSubCategoryName() == null)
                {
                    if (transferDialogResult.getCategoryName() == null)
                    {
                        String categoryName = null;
                        if ( a.getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_CATEGORY) ) { categoryName = a.getParent().getName(); }
                        else if ( a.getParent().getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_CATEGORY) ) { categoryName = a.getParent().getParent().getName(); }
                        else { this.log.printLogLine("ERROR: could not find album category!"); return; }
                        targetAlbumDir = targetAlbumDir + categoryName + "/";
                    }

                    String subcategoryName = null;
                    if ( a.getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_SUBCATEGORY) ) { subcategoryName = a.getParent().getName() + "/"; }
                    else if ( a.getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_CATEGORY) ) { subcategoryName = ""; }
                    else { this.log.printLogLine("ERROR: could not find album subcategory!"); return; }
                    targetAlbumDir = targetAlbumDir + subcategoryName;
                }

                //this.log.printLogLine("DEBUG: albumName: " + a.getName());
                String albumName = a.getName();
                targetAlbumDir = targetAlbumDir + albumName + "/";
            }

            this.accListing.verifyAlbum(a.getID(), targetAlbumDir);
        }

//    	//todo: what about missing local dirs?
//    	this.log.printLogLine("preparing to verify files from: " + transferDialogResult.getDir());
//
//		Vector<IAlbum> selectedAlbums = this.accListing.matchAlbums(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
//		if (selectedAlbums.size() == 0) { this.log.printLogLine("no matching album was found on your SmugMug Account"); }
//
//    	for (IAlbum a : selectedAlbums)
//    	{
//    		this.accListing.verifyAlbum(a.getID(), transferDialogResult.getDir());
//    	}
    }
    
    public void sort(ITransferDialogResult transferDialogResult)
    {
		//try to bring the albums to a correct order - happens if files were uploaded in an wrong order
    	//this.log.printLogLine("preparing to sort albums");

        if (transferDialogResult.getAlbumName() != null) { this.log.printLogLine("WARNING: you specified an album name, which will be ignored! We're rearranging albums here, not images within albums!"); }
    	
        this.accListing.sort(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName());
    }

    public void autotag(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }
        this.accListing.autotag(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
    }

    public void statistics(ITransferDialogResult transferDialogResult)
    {
        //this.log.printLogLine("DEBUG: Statistics stub (Model)");

        if (transferDialogResult == null) { return; }
        this.accListing.statistics(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());

        //todo: this.view.showStatistics()
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
    
    public void startSyncProcessingQueue()
    {
    	this.accListing.startSyncProcessingQueue();
    	
    	this.quitApplication();
    }

    public void startASyncProcessingQueue()
    {
    	this.accListing.startASyncProcessingQueue();
    }

    public void finishASyncProcessingQuene()
    {
        this.accListing.finishASyncProcessingQueue();
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
    
//	private void upload_prepare_albumDir(String category, String subcategory, String album, File dir, String keywords)
//	{
////		this.log.printLogLine("DEBUG: enqueuing ...");
////		this.log.printLogLine("DEBUG:      category    : " + category);
////		this.log.printLogLine("DEBUG:      subcategory : " + subcategory);
////		this.log.printLogLine("DEBUG:      album       : " + album);
////		this.log.printLogLine("DEBUG:      dir         : " + dir);
//
//		this.accListing.enqueueAlbumForUpload(category, subcategory, album, dir, keywords);
//	}

    private void recursiveUploadDirectorySearch(int maxRecursionLevel, File directory, String category, String subcategory, String album, String keywords)
    {
        //this.log.printLogLine("DEBUG: Model.recursiveUploadDirectorySearch(" + maxRecursionLevel + ", " + directory + ", " + category + ", " + subcategory + ", " + album + ", " + keywords + ")");

        //check if it's a directory, not a file
        if (!directory.isDirectory()) //should normally be false
        {
            this.log.printLogLine("ERROR: expected a directory, not a file (" + directory + ")");
            this.quitApplication();
        }

        if (this.containsPics(directory))
        {
            //print a warning - we have nowhere to put the images
            this.log.printLogLine("WARNING: the directory " + directory + " contains images which will be ignored ... specify a \"--album\" parameter or use the parent directory for the \"--dir\" parameter");
        }

        File[] directoryList = directory.listFiles();
        Arrays.sort(directoryList, this.config.getConstantFileComparator());

        for (int i=0; i < directoryList.length; i++)
        {
            File subDirectory = directoryList[i];

            // if the file is a directory and there is no ignore tag set
            if ( subDirectory.isDirectory() )
            {
                // if no ignore tag is present, continue processing this directory
                if ( !((new File(subDirectory.getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix())).exists()) )
                {
                    if (this.containsPics(subDirectory))
                    {
                        //this.log.printLogLine("DEBUG: \"" + subDirectory + "\" contains pictures ... info: level=" + maxRecursionLevel + " " + category + "/" + subcategory + "/" + album);
                        if (maxRecursionLevel == 3)
                        {
    //                        if ((category == null) && (subcategory == null) && (album == null)) //should always be true
    //                        {
    //                            category    = "Other";
    //                            subcategory = null;
    //                            album       = subDirectory.getName();
    //                        }
    //                        else
    //                        {
    //                            this.log.printLogLine("ERROR: Model.recursiveUploadDirectorySearch: this case is yet unhandled");
    //                            this.quitApplication();
    //                        }
                            category    = "Other";
                            subcategory = null;
                            album       = subDirectory.getName();

                        }
                        else if (maxRecursionLevel == 2)
                        {
    //                        if ((subcategory == null) && (album == null)) //should always be true
    //                        {
    //                            if (category == null) { category    = subDirectory.getParentFile().getName(); } else { /*NOOP: category is already defined*/ }
    //                            subcategory = null;
    //                            album       = subDirectory.getName();
    //                        }
    //                        else
    //                        {
    //                            this.log.printLogLine("ERROR: Model.recursiveUploadDirectorySearch: this case is yet unhandled");
    //                            this.quitApplication();
    //                        }
                            if (category == null) { category    = subDirectory.getParentFile().getName(); } else { /*NOOP: category is already defined*/ }
                            subcategory = null;
                            album       = subDirectory.getName();

                        }
                        else if (maxRecursionLevel == 1)
                        {
    //                        if (album == null) //should always be true
    //                        {
    //                            //assuming that if there is a subcategory name given, there will be category name too - should have been checked outside this method
    //                            if (category    == null) { category    = subDirectory.getParentFile().getParentFile().getName(); } else { /*NOOP: category is already defined*/ }
    //                            if (subcategory == null) { subcategory = subDirectory.getParentFile().getName(); } else { /*NOOP: subcategory is already defined*/ }
    //                            album       = subDirectory.getName();
    //                        }
    //                        else
    //                        {
    //                            this.log.printLogLine("ERROR: Model.recursiveUploadDirectorySearch: this case is yet unhandled");
    //                            this.quitApplication();
    //                        }
                            //assuming that if there is a subcategory name given, there will be category name too - should have been checked outside this method
                            if (category    == null) { category    = subDirectory.getParentFile().getParentFile().getName(); } else { /*NOOP: category is already defined*/ }
                            if (subcategory == null) { subcategory = subDirectory.getParentFile().getName(); } else { /*NOOP: subcategory is already defined*/ }
                            album       = subDirectory.getName();

                        }
                        else { this.log.printLogLine("ERROR: undefined recursion level"); this.quitApplication(); }

                        this.accListing.enqueueAlbumForUpload(category, subcategory, album, subDirectory, keywords);
                    }
                    else
                    {
                        //recursion
                        if (maxRecursionLevel > 1)
                        {
                            this.recursiveUploadDirectorySearch(maxRecursionLevel-1, subDirectory, category, subcategory, album, keywords);
                        }
                        else
                        {
                            //not going any deeper
                        }

                    }
                }
                else // an ignore tag is present, print warning
                {
                    this.log.printLogLine("WARNING: " + subDirectory.getAbsolutePath() + " - the ignore tag was set ... skipping this directory");
                }

            }
        }

    }

}
