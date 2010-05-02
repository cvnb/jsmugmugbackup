/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.*;
import jSmugmugBackup.model.queue.*;
import jSmugmugBackup.model.smugmugLayer.*;
import jSmugmugBackup.view.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class AccountListingProxy implements IAccountListingProxy
{
    private GlobalConfig config = null;
	private Logger log = null;
	private ISmugmugConnector connector = null;
	private ITransferQueue transferQueue = null;
//	private ILoginView loginMethod = null;
	
	private IRootElement smugmugRoot = null;
	//private Vector<ICategory> categoryList = null;
	
	private long transferedBytes = 0;
	
	public AccountListingProxy()
	{
        this.config = GlobalConfig.getInstance();
		this.log = Logger.getInstance();
        this.transferQueue = TransferQueue.getInstance();
		//this.connector = new SmugmugConnector2G();
        this.connector = new SmugmugConnector3G();
	}
	public Number login(String userEmail, String password)
	{
        return this.connector.login(userEmail, password);
	}
	public void logout()
	{
		this.connector.logout();
	}
	public IRootElement getAccountTree(String categoryName, String subcategoryName, String albumName, String albumKeywords, String albumPassword)
	{
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null)
        {
            this.smugmugRoot = this.connector.getTree(albumPassword);
            if (this.smugmugRoot == null) { return null; }
        }

		IRootElement result = new RootElement("");
		
		//find matching albums
		Vector<IAlbum> albumList = this.getAccountAlbumList(categoryName, subcategoryName, albumName, albumKeywords);
		
		// for all albums in the albumList: put them into a hirarchy
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
			for (ICategory c : result.getCategoryList())
			{
				if (c.getID() == intern_c.getID()) { result_c = c; result_c_exists = true; }
			}
			if (result_c == null) { result_c = new Category(result, intern_c.getID(), intern_c.getName() ); } //create new category empty with the same name, because we don't want to copy subcategories and albums
			
			
			ISubcategory intern_s = this.getAlbumSubcategory(intern_a.getID());
			if ( intern_s != null ) //album has a subcategory
			{
				//check if subcategory already exists in result set, if not create it
				boolean result_s_exists = false;
				ISubcategory result_s = null;
				for (ICategory c : result.getCategoryList())
				{
					for (ISubcategory s : c.getSubcategoryList())
					{
						if (s.getID() == intern_s.getID()) { result_s = s; result_s_exists = true; }
					}
				}
				if ( result_s == null ) { result_s = new Subcategory(result_c, intern_s.getID(), intern_s.getName()); }
				
				if (!result_c_exists) { result.addCategory(result_c); }
				if (!result_s_exists) { result_c.addSubcategory(result_s); }
				result_s.addAlbum( (IAlbum)intern_a.clone(result_s) ); //clone, because we need the children too, i.e. we copy the images too
			}
			else //album doesn't have a subcategory
			{
				if (!result_c_exists) { result.addCategory(result_c); }
				result_c.addAlbum( (IAlbum)intern_a.clone(result_c) ); //clone, because we need the children too, i.e. we copy the images too
			}
			
		}


		return result;
	}
	public Vector<IAlbum> getAccountAlbumList(String categoryName, String subcategoryName, String albumName, String albumKeywords)
	{
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }

        //prepare tags
        Vector<String> albumTags = Helper.getTags(albumKeywords);
        //todo: match with album tags


        //match albums ...

		Vector<IAlbum> selectedAlbums = new Vector<IAlbum>();

		//decend to all album lists of all Subcategories and Categories
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
                                if ( (albumTags == null) || (this.matchTags(albumTags, a.getTags())) )
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
                            if ( (albumTags == null) || (this.matchTags(albumTags, a.getTags())) )
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
		}

		//this.log.printLogLine("matched albums: " + selectedAlbums.size() );

		return selectedAlbums;
	}
	public void enqueueAlbumForUpload(String categoryName, String subcategoryName, String albumName, File pics_dir, String albumKeywords)
	{

        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }

        // convert names to ascii, since smugmug sometimes seems to have problems with non-ascii characters
        String categoryAsciiName = Helper.encodeAsASCII(categoryName);
        String subcategoryAsciiName = Helper.encodeAsASCII(subcategoryName);
        String albumAsciiName = Helper.encodeAsASCII(albumName);

		this.log.printLogLine(Helper.getCurrentTimeString() + " enqueuing album: " + categoryAsciiName + "/" + subcategoryAsciiName + "/" + albumAsciiName + " (" + pics_dir + ")");

        Vector<String> albumTags = Helper.getTags(albumKeywords);

    	int uploadCount = 0;
    	int skippedCount = 0;
    	int totalFiles = pics_dir.listFiles().length;
    	int totalMediaFiles = pics_dir.listFiles(this.config.getConstantSupportedFileTypesFilter()).length;
    	int unsupportedCount = totalFiles - totalMediaFiles;
    	
        File[] fileList = pics_dir.listFiles(this.config.getConstantSupportedFileTypesFilter());
        if (fileList == null) { return; /* Either dir does not exist or is not a directory */ }
        Arrays.sort(fileList, this.config.getConstantFileComparator()); //sort files
        

    	//get or create category
        int categoryID;
        categoryID = this.getCategoryID(categoryAsciiName); //returns -1 for non-existing categorys, because 0 is used by the category named "Other"
        //this.log.printLogLine("DEBUG: categoryName: " + categoryAsciiName + ", categoryID: " + categoryID);
        if (categoryID == -1) //category doesn't exist, so we create one
        {
        	categoryID = this.connector.createCategory(categoryAsciiName); //create on smugmug
        	//this.addCategory(categoryID, categoryName); //add to local structure
        	this.smugmugRoot.addCategory( new Category(this.smugmugRoot, categoryID, categoryAsciiName) );
        }
        
        //get or create subcategory
        int subCategoryID = 0;
        if (subcategoryAsciiName != null)
        {
	        subCategoryID = this.getSubcategoryID(categoryID, subcategoryAsciiName);
	        if (subCategoryID == 0) //subcategory doesn't exist, so we create one
	        {
	        	subCategoryID = this.connector.createSubcategory(categoryID, subcategoryAsciiName); //create on smugmug
	        	this.addSubcategory(categoryID, subCategoryID, subcategoryAsciiName); //add to local structure
	        }
        }
        
        //get or create album
        int albumID;
        albumID = this.getAlbumID(categoryID, subCategoryID, albumAsciiName);
        if (albumID == 0) //album doesn't exist, so create one
        {
        	albumID = this.connector.createAlbum(categoryID, subCategoryID, albumAsciiName, albumTags);
        	this.addAlbum(categoryID, subCategoryID, albumID, "<internal key>", albumAsciiName, albumKeywords, "<internally created>", null);
        }

        //this.log.printLogLine("categoryID=" + categoryID + ", subcategoryID=" + subCategoryID + ", albumID=" + albumID);


        //prepare tags
        Vector<String> tags = null;
//        if (this.config.getPersistentAutoImageKeywords() == true)
//        {
//            tags = new Vector<String>();
//            String[] tagsArray = albumAsciiName.split("[ ,;]");
//
//            //copy array into a Vector
//            for (int i=0; i < tagsArray.length; i++)
//            {
//                if ( (Pattern.matches("\\d{4}-\\d{2}-\\d{2}|\\d{4}-\\d{2}", tagsArray[i]) == false) &&
//                     (tagsArray[i].equals("-") == false) &&
//                     (tagsArray[i].equals("") == false) )
//                {
//                    tags.add(tagsArray[i]);
//                }
//            }
//
//            this.log.printLog("DEBUG: tags: ");
//            for (String tag : tags) { this.log.printLog(tag + ";"); }
//            this.log.printLogLine("");
//        }


        //add album files to the queue
        for (int i=0; i<fileList.length; i++)
        {        	
        	int imageID;
        	//imageID = this.getImageID(categoryID, subCategoryID, albumID, fileList[i].getName());
            // since filename are uploaded with filenames convertey to ascii we have to do the same here in order to find the correct file
            imageID = this.getImageID(categoryID, subCategoryID, albumID, Helper.encodeAsASCII(fileList[i].getName()));
        	if (imageID == 0) //image doesn't exist on smugmug (no need for md5 check, since we have nothing to compare to)
        	{
                // check if file is not empty
                if (fileList[i].length() == 0)
                {
                	this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - file has 0 bytes ... skipping");
                	skippedCount++;
                }                
                // check if file is smaller than 600 MB
                else if (fileList[i].length() > (this.config.getConstantUploadFileSizeLimit()))
                {
                	this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - filesize greater than " + (this.config.getConstantUploadFileSizeLimit() / (1024*1024)) + " MB is not supported ... skipping");
                	skippedCount++;
                }                
                //check if someone has manually set the ignore tag
                else if ( (new File(fileList[i].getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix())).exists() )
                {
                	this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - the ignore tag was set ... skipping");
                	skippedCount++;
                }
                else
                {
    				ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, albumID, null, null, fileList[i], fileList[i].length(), tags, /*null,*/ null);
    				this.transferQueue.add(item);
                    uploadCount++;
                }
        	}
        	else //image already exists on smugmug
        	{
                IImage image = this.getImage(imageID);


                if (this.config.getPersistentCheckMD5Sums() == true) //if md5 checking is enabled in config
                {
                    String fileMD5 = Helper.computeMD5Hash(fileList[i]); //generate md5sum
                    if (!image.getMD5().equals(fileMD5)) //exists already, but has wrong md5
                    {
                        if (Helper.isVideo(image.getName()))
                        {
                            //this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - exists on smugmug, but has different MD5Sum - this is normal for a video ... skipping");
                            skippedCount++;
                        }
                        else
                        {
                            this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - exists on smugmug, but has different MD5Sum - this is unusual ... skipping anyway");
                            skippedCount++;

                            //this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - exists on smugmug, but has different MD5Sum - this is unusual ... uploading again");
                            //ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.UPLOAD, albumID, fileList[i]);
                            //this.transferQueue.add(item);
                            //uploadCount++;
                        }
                    }
                    else //exists already and the md5 is ok --> we can safely skip this upload
                    {
                        //this.log.printLogLine("  WARNING: " + fileList[i].getName() + " already exists on smugmug ... skipping");
                        skippedCount++;
                    }
                }
                else // md5 checking ist disabled in config, check the filesize
                {
                    //this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - exists on smugmug ... skipping");
                    //skippedCount++;

                    if (Helper.isVideo(image.getName()))
                    {
                        //no filesize checking on videos, skipping
                        skippedCount++;
                    }
                    else
                    {
                        //skip anyway, but do an extra check for the filesize
                        skippedCount++;

                        if ( image.getSize() != fileList[i].length() )
                        {
                            this.log.printLogLine("  WARNING: " + fileList[i].getName() + " - filesize does not match (local: " + fileList[i].length() + ", remote: " + image.getSize() + " ... skipping anyway");
                        }
                    }
                }
        	}
        }        

        //reducing the output a little bit: only show this line if we added any files
        if (uploadCount > 0)
        {
            this.log.printLogLine("  ... added " + uploadCount + " files, " + skippedCount + " were skipped, " + unsupportedCount + " had an unsupported file type.");
        }

	}
	public void enqueueAlbumForDownload(int albumID, String albumKey, String albumPassword, String targetBaseDir, /*ResolutionEnum minResolution,*/ ResolutionEnum maxResolution)
	{
        //this.log.printLogLine("DEBUG: enqueueAlbumForDownload(" + albumID + ", " + albumKey + ", " + albumPassword + ", " + targetBaseDir + ")");

        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(albumPassword); }

		this.log.printLogLine(Helper.getCurrentTimeString() + " enqueuing album (id:" + albumID + ", target:" + targetBaseDir + ")");

		int downloadCount = 0;
		int skippedCount = 0;


		String targetDir = targetBaseDir + this.getAlbumDirEnd(albumID);
	
		
		//check target dir
		boolean dirIsNew = (new File(targetDir)).mkdirs();
	    if (dirIsNew) { this.log.printLogLine("  ... created dir: " + targetDir); }

        IAlbum album = this.getAlbum(albumID);

        //this.log.printLogLine("DEBUG: name: " + album.getFullName());
        //for (IImage image : album.getImageList()) { this.log.printLogLine("DEBUG: image name: " + image.getFullName()); }


        //handle cases where the album can not be found the the tree, i.e. a private URL has been given
	    if ( (album == null) && (albumKey != null) ) { album = this.connector.getAlbum(albumID, albumKey, albumPassword); }
		
		for (IImage image : album.getImageList())
		{

            // if it's a video don't use the original name (i.e. .avi), but use .mp4 since all
            // videos are beeing converted by smugmug into mp4 directly after upload ... this
            // should also protect original videos when downloading them into the same folder
            // which they've been uploaded from
            File imageFile = null;
            /*
            if (image.getName().equals("")) //no filename has been assigned
            {
                //TODO: find out if it's a video or an image
                
                imageFile = new File(targetDir + image.getID() + ".jpg");
            }
            else*/
            if (!Helper.isVideo(image.getName())) { imageFile = new File(targetDir + image.getName()); }
            else
            {
                //String videoName = image.getName().substring(0, image.getName().lastIndexOf(".") ) + ".mp4";
                String videoName;
                if (image.getName().endsWith(this.config.getConstantVideoDownloadFilePostfix())) { videoName = image.getName(); }
                else { videoName =  image.getName() + this.config.getConstantVideoDownloadFilePostfix(); }

                imageFile = new File(targetDir + videoName);
            }
            //this.log.printLogLine("DEBUG: file: " + imageFile.getAbsolutePath());

            

            if (imageFile.exists()) //skip file
            {
                //md5 checking doesn't seem to work with downloads either
                if ( (this.config.getPersistentCheckMD5Sums() == true) && (image.getMD5() != null) ) //if md5 checking is enabled in config, and we actually have an md5 (there seems to be no md5 when logging in anonymously)
                {
                    String localFileMD5 = Helper.computeMD5Hash(imageFile); //generate md5sum
                    if (image.getMD5().equals(localFileMD5)) //exists already and md5 looks good
                    {
                        //all ok, skip
                        skippedCount++;
                    }
                    else
                    {
                        //md5 doesn't match, download again
                        this.log.printLogLine("WARNING: image " + image.getName() + " already exists, but has wrong md5 sum ... enqueuing again");
                        ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), image.getKey(), albumPassword, imageFile, image.getSize(), null, /*minResolution,*/ maxResolution);
                        this.transferQueue.add(item);
                        downloadCount++;
                    }
                    
                }
                else // no md5, just compare file size
                {
                    //checking filesize
                    if (image.getSize() == imageFile.length())
                    {
                        // file sizes match, skip
                        skippedCount++;
                    }
                    else
                    {
                        /*
                        //files sizes don't match, download again
                        this.log.printLogLine("WARNING: image " + image.getName() + " exists, but has wrong size (local: " + imageFile.length() + ", remote: " + image.getLargestURLContentSize() + ") ... enqueuing again");
                        ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), imageFile, image.getLargestURLContentSize(), null);
                        this.transferQueue.add(item);
                        downloadCount++;
                        */
                        
                        
                        if (image.getOriginalURL() != null)
                        {
                            //original url is available ... this is unusual
                            //files sizes don't match, download again
                            this.log.printLogLine("WARNING: image " + image.getName() + " exists, but has wrong size (local: " + imageFile.length() + ", remote: " + image.getSize() + ") ... enqueuing again");
                            ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), image.getKey(), albumPassword, imageFile, image.getSize(), null, /*minResolution,*/ maxResolution);
                            this.transferQueue.add(item);
                            downloadCount++;
                        }
                        else
                        {
                            //no original available
                            //files sizes don't match, in most cases this indicates that we couldn't download the original file
                            this.log.printLogLine("WARNING: image " + image.getName() + " exists, but has wrong size (local: " + imageFile.length() + ", remote: " + image.getSize() + ") - since the original url is not available, there's nothing to worry about ... skipping");
                            ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), image.getKey(), albumPassword, imageFile, image.getSize(), null, /*minResolution,*/ maxResolution);
                            skippedCount++;
                        }
                        
                    }                    
                }                
            }
            else //file doesn't exist, download
            {
                ITransferQueueItem item = new TransferQueueItem(TransferQueueItemActionEnum.DOWNLOAD, image.getID(), image.getKey(), albumPassword, imageFile, image.getSize(), null, /*minResolution,*/ maxResolution);
                this.transferQueue.add(item);
                downloadCount++;
            }
		}   
	    
	    this.log.printLogLine("  ... added " + downloadCount + " files to target:" + targetDir + " (" + skippedCount + " were skipped)");
	}
    public void verifyAlbum(int albumID, String targetAlbumDir)
    {
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }

    	//this.log.printLog(Helper.getCurrentTimeString() + " verifying album (id:" + albumID + ", dir:" + targetAlbumDir + ") ... ");
        //this.log.printLog(Helper.getCurrentTimeString() + " verify: " + this.getAlbumDirEnd(albumID) + " ... ");
        this.log.printLog(Helper.getCurrentTimeString() + " verify: " + targetAlbumDir + " ... ");

		File dir = new File(targetAlbumDir);
	    File[] fileList = dir.listFiles(this.config.getConstantSupportedFileTypesFilter());
	    if (fileList == null)
	    {
	    	/* Either dir does not exist or is not a directory */
	    	this.log.printLogLine("failed");
	      	this.log.printLogLine("   ERROR: local album path could not be found");
	      	return;
	    }

	    Arrays.sort(fileList, this.config.getConstantFileComparator()); //sort files, convienence only

	    boolean failed = false;
        IAlbum album = this.getAlbum(albumID);
        Vector<IImage> imageList = album.getImageList();
        
	    // compare albums
        int matchCount = 0; //count the number of matching pairs found
        int filesizeLimitHitCount = 0;
    	for (int i=0; i<fileList.length; i++)
    	{
            //sort out files that exceed the filesize limit
            if (fileList[i].length() > (this.config.getConstantUploadFileSizeLimit()))
            {
                //this.log.printLogLine("   INFO: " + fileList[i].getAbsolutePath() + " ... exceeds filesize limit");
                filesizeLimitHitCount++;
            }
            else // file is smaller than filesize limit
            {
                for (IImage image : imageList)
                {
                    if ( Helper.isVideo(image.getName()) )
                    {
                        // handle videos ...

                        //either the full filename matches or it's the full name plus the download extension
                        if ( //( Helper.encodeAsASCII(fileList[i].getName()).equals(image.getName().substring(0, image.getName().lastIndexOf(".") ) + ".mp4") ) ||
                             ( Helper.encodeAsASCII(fileList[i].getName()).equals(image.getName() + this.config.getConstantVideoDownloadFilePostfix()) ) ||
                             ( Helper.encodeAsASCII(fileList[i].getName()).equals(image.getName()) ) )
                        {
                            //compare files

                            //now we have the matching pair, so we compute the md5sums
                            matchCount++;                            

                            if (this.config.getConstantVerifyMD5ForVideos())
                            {
                                String localFileMD5Sum = Helper.computeMD5Hash(fileList[i]);

                                // checking md5:
                                //   this is either an original video or the video which has already been converted by smugmug (probably uploaded
                                //   and downloaded again) ... md5 sums will most definitively not match
                                // idea: maybe we shouldn't even compute the md5 in this case (this will speed things up a little)
                                if ( localFileMD5Sum.equals(image.getMD5()) )
                                {
                                    // no real need to print that to output, though unusual since md5 verification usually fails on videos
                                    //this.log.printLogLine("   checking " + fileList[i].getAbsolutePath() + " ... ok (unusual, but definitively nothing to worry about)");
                                }
                                else
                                {
                                    if (failed == false)
                                    {
                                        this.log.printLogLine("failed"); //this completes the first line
                                        failed = true;
                                    }
                                    //this.log.printLogLine("   WARNING: " + fileList[i].getAbsolutePath() + " ... md5 failed (reason: videos usually fail)");
                                    this.log.printLogLine("   WARNING: " + fileList[i].getName() + " ... md5 failed (reason: videos usually fail)");
                                    //this.log.printLogLine("      file size (local/remote): " + fileList[i].length() + " / " + image.getSize() );
                                    //this.log.printLogLine("      md5 sum (local/remote)  : " + localFileMD5Sum + " / " + image.getMD5() );
                                }
                            }
                            else
                            {
                                //this.log.printLogLine("   INFO: " + fileList[i].getAbsolutePath() + " ... ok");
                            }
                        }
                    }
                    else if ( Helper.encodeAsASCII(fileList[i].getName()).equals(image.getName()) ) // handle normal images
                    {
                        //now we have the matching pair
                        matchCount++;

    //                    boolean fileCheckResult;
    //                    String localFileMD5Sum = "[not available]";
    //                    if (this.config.getPersistentCheckMD5Sums() == true) //if md5 checking is enabled in config
    //                    {
    //                        //compute the md5sums
    //                        localFileMD5Sum = Helper.computeMD5Hash(fileList[i]);
    //
    //                        //check md5
    //                        if ( localFileMD5Sum.equals(image.getMD5()) ) { fileCheckResult = true; }
    //                        else { fileCheckResult = false; }
    //                    }
    //                    else //check filesize only
    //                    {
    //                        if ( fileList[i].length() == image.getSize() ) { fileCheckResult = true; }
    //                        else { fileCheckResult = false; }
    //                    }
    //
    //
    //                    if (fileCheckResult == true)
    //                    {
    //
    //                    }

                        String localFileMD5Sum = Helper.computeMD5Hash(fileList[i]);

                        //compare files
                        if ( localFileMD5Sum.equals(image.getMD5()) ) //check md5
                        {
                           // no need to print that to output:
                           //this.log.printLogLine("   INFO: " + fileList[i].getAbsolutePath() + " ... ok");
                           //this.log.printLogLine("      orientation             : " + Helper.getOrientationExifMetadata(fileList[i]));
                        }
                        else //md5 check failed
                        {
                            if (failed == false)
                            {
                                this.log.printLogLine("failed"); //this completes the first line
                                failed = true;
                            }

                            int exifOrientation = Helper.getOrientationExifMetadata(fileList[i]);
                            int exifDimensions = Helper.getDimensionExifMetadata(fileList[i]);
                            float filesizeRatio = (float)fileList[i].length() / (float)image.getSize();
                            if ( (exifOrientation > 1) && (filesizeRatio > 0.985) && (filesizeRatio < 1.015) ) //different orientation than "landscape" and file sizes do not differ too much
                            {
                                //this.log.printLogLine("   WARNING: " + fileList[i].getAbsolutePath() + " ... md5 failed (reason: orientation metadata (" + exifOrientation + "))");
                                this.log.printLogLine("   WARNING: " + fileList[i].getName() + " ... md5 failed (reason: orientation metadata (" + exifOrientation + "))");
                            }
                            else if (exifDimensions > 48000000) //image has more than 48 megapixel
                            {
                                //this.log.printLogLine("   WARNING: " + fileList[i].getAbsolutePath() + " ... md5 failed (reason: exceeding 48mp size limitation (" + (exifDimensions / (1024 *1024))  + "mp))");
                                //this.log.printLogLine("   WARNING: " + fileList[i].getAbsolutePath() + " ... md5 failed (reason: exceeding 48mp size limitation (" + (exifDimensions / (1000 *1000))  + "mp))");
                                this.log.printLogLine("   WARNING: " + fileList[i].getName() + " ... md5 failed (reason: exceeding 48mp size limitation (" + (exifDimensions / (1000 *1000))  + "mp))");
                            }
                            else
                            {
                                //this.log.printLogLine("   ERROR: " + fileList[i].getAbsolutePath() + " ... md5 failed");
                                this.log.printLogLine("   ERROR: " + fileList[i].getName() + " ... md5 failed");
                                this.log.printLogLine("      file size (local/remote): " + fileList[i].length() + " / " + image.getSize());
                                this.log.printLogLine("      md5 sum (local/remote)  : " + localFileMD5Sum + " / " + image.getMD5());
                                this.log.printLogLine("      orientation             : " + Helper.getOrientationExifMetadata(fileList[i]));
                                this.log.printLogLine("      pixels                  : " + Helper.getDimensionExifMetadata(fileList[i]));
                            }

                        }
                    }
                }
            }
      	}



        if ( (fileList.length == (matchCount + filesizeLimitHitCount)) && (imageList.size() == (matchCount + filesizeLimitHitCount)) )
        {
            if (failed == false) { this.log.printLogLine("ok"); } // i.e. there were no errors for the whole album
            else { /*NOOP ... the "failed" was already printed before*/ } // i.e. there were errors with md5 verification, but no missing files or so
        }
        else
        {
            if (failed == false) // no error was encountered so far, so we need to print a "failed" message for the album
            {
                this.log.printLogLine("failed");
                failed = true; // not really nesseciary, because "failed" is not used anymore hereafter
            }

            Hashtable<String, Integer> fileMappingTable = new Hashtable<String, Integer>();
            //match files against images on smugmug
            for (int i=0; i<fileList.length; i++)
            {
                int fileMatchCount = 0;
                for (IImage image : imageList)
                {
                    if (fileList[i].getName().equals(image.getName())) { fileMatchCount++; }
                }

                if (fileMatchCount == 0)
                {
                    //check for special cases
                    File ignoreTagFile = new File(fileList[i].getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix());
                    if ( (ignoreTagFile.exists()) && (fileList[i].length() > (this.config.getConstantUploadFileSizeLimit())) ) { fileMatchCount = -11; }
                    else if (ignoreTagFile.exists()) { fileMatchCount = -12; } //check if ignore tag exists
                    else if (fileList[i].length() > (this.config.getConstantUploadFileSizeLimit())) { fileMatchCount = -13; }
                    else { fileMatchCount = -10; } //the value -10 identifies it as a file with no corresponding image on smugmug
                }

                fileMappingTable.put(fileList[i].getAbsolutePath(), fileMatchCount);
            }

            //match images on smugmug against local files
            for (IImage image : imageList)
            {
                int fileMatchCount = 0;
                for (int i=0; i<fileList.length; i++)
                {
                    if (fileList[i].getName().equals(image.getName())) { fileMatchCount++; }
                }
                
                //assumption: local files have distinctive names on disk (should be safe to assume)
                if (fileMatchCount > 1) { this.log.printLogLine("   ERROR: one image matched with multiple local files! This is not supposed to happen, aborting!"); return; }
                
                //the value -20 identifies it as an image on smugmug with no corresponding local file
                if (fileMatchCount == 0) { fileMatchCount = -20; }

                fileMappingTable.put("id=" + image.getID() + ";name=" + image.getName(), fileMatchCount);
            }

            //evaluate matching results
            for (String key : fileMappingTable.keySet())
            {
                if (fileMappingTable.get(key) == 1) { /* NOOP - everything is fine; matched 1:1 */ }
                else if (fileMappingTable.get(key) > 1) { this.log.printLogLine("   WARNING: " + key.substring(key.lastIndexOf("/")+1) + " ... was uploaded multiple (" + fileMappingTable.get(key) + ") times"); }
                else if (fileMappingTable.get(key) == -10) { this.log.printLogLine("   WARNING: " + key.substring(key.lastIndexOf("/")+1) + " ... was not uploaded"); }
                else if (fileMappingTable.get(key) == -11) { this.log.printLogLine("   WARNING: " + key.substring(key.lastIndexOf("/")+1) + " ... was not uploaded (reason: ignore tag and file size limit)"); }
                else if (fileMappingTable.get(key) == -12) { this.log.printLogLine("   WARNING: " + key.substring(key.lastIndexOf("/")+1) + " ... was not uploaded (reason: ignore tag)"); }
                else if (fileMappingTable.get(key) == -13) { this.log.printLogLine("   WARNING: " + key.substring(key.lastIndexOf("/")+1) + " ... was not uploaded (reason: file size limit)"); }
                else if (fileMappingTable.get(key) == -20) { this.log.printLogLine("   WARNING: the image " + key.substring(key.lastIndexOf("/")+1) + " exists on smugmug, but no corresponding file was found"); }
                else
                {
                    this.log.printLogLine("   ERROR: undefined result while matching images with local files");
                    this.log.printLogLine("   ERROR: fileName       = " + key);
                    this.log.printLogLine("   ERROR: fileMatchCount = " + fileMappingTable.get(key));
                }
            }

            /*
            //Debug output
            this.log.printLogLine("   DEBUG: fileList.length  :" + fileList.length);
            this.log.printLogLine("   DEBUG: imageList.size() :" + imageList.size());
            this.log.printLogLine("   DEBUG: matchCount       :" + matchCount);
            if (fileMappingTable.size() != (fileList.length + imageList.size())) { this.log.printLogLine("   DEBUG: incorrect size of fileMappingTable, something is wrong!!!"); }
            this.log.printLogLine("   DEBUG: mapping table ...");
            for (String key : fileMappingTable.keySet())
            {
                if (fileMappingTable.get(key) != 1)
                {
                    this.log.printLogLine("   DEBUG: " + key + " = " + fileMappingTable.get(key));
                }                
            }
            */
        }
    }
    public void sort(String categoryName, String subcategoryName)
    {
        //find matching albums
		Vector<IAlbum> albumList = this.getAccountAlbumList(categoryName, subcategoryName, null, null);

        this.log.printLogLine("The following albums will be sorted by name:");
        for (IAlbum a : albumList) { this.log.printLogLine( "      " + a.getFullName()); }

        //put albums into an array
        int index = 0;
        IAlbum[] albumArray = new IAlbum[albumList.size()];
        for (IAlbum a : albumList)
        {
            albumArray[index] = a;
            index++;
        }

        //sort the array
        Arrays.sort(albumArray);

        this.log.printLogLine("WARNING: sorting should not be interuped while in progress, otherwise there will be nasty leftovers in your albums!");
        this.log.printLog("WARNING: beginning to sort in 30 sec - abort NOW if you need to ... ");
        Helper.pause(30000);
        this.log.printLogLine("ok");

        this.sortAlbums(albumArray);

        //this line is not too useful
		this.log.printLogLine("  ... sorted " + albumArray.length + " albums");
    }
    public void autotag(String categoryName, String subcategoryName, String albumName)
    {
        //find matching albums
		Vector<IAlbum> albumList = this.getAccountAlbumList(categoryName, subcategoryName, albumName, null);

        this.log.printLogLine("Images in the following albums will be tagged:");
        for (IAlbum a : albumList) { this.log.printLogLine( "      " + a.getFullName()); }

        for (IAlbum a : albumList)
        {
            //prepare tags
            Vector<String> autotags = new Vector<String>();
            String[] tagsArray = a.getName().split("[ ,;]");

            //copy array into a Vector
            for (int i=0; i < tagsArray.length; i++)
            {
                if ( (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\.\\d|\\d{4}-\\d{2}-\\d{2}|\\d{4}-\\d{2}|\\d{4}", tagsArray[i]) == false) &&
                     (tagsArray[i].equals("-") == false) &&
                     (tagsArray[i].equals("") == false) )
                {
                    autotags.add(tagsArray[i]);
                }
            }

            this.log.printLog(Helper.getCurrentTimeString() + " tagging album: " + a.getFullName());
            this.log.printLog(" (tags: ");
            for (String tag : autotags) { this.log.printLog(tag + ";"); }
            this.log.printLog(") ... ");

            for (IImage image : a.getImageList())
            {
                //this.log.printLog("DEBUG:    image: " + image.getName());
                //this.log.printLog(" (");
                //if (image.getTags() != null) { for (String itag : image.getTags()) { this.log.printLog(itag + ";"); } }
                //this.log.printLogLine(")");

                //merge autotags and existing tags
                Vector<String> merged_tags = new Vector<String>();
                if (image.getTags() != null) { for (String itag : image.getTags()) { merged_tags.add(itag); } }
                for (String tag : autotags)
                {
                    if (merged_tags.contains(tag) == false) { merged_tags.add(tag); }
                }

//                this.log.printLog("DEBUG:       merged tags  : ");
//                for (String tag : merged_tags) { this.log.printLog(tag + ";"); }
//                this.log.printLogLine("");

                int origImageTags = 0;
                if (image.getTags() != null) { origImageTags = image.getTags().size(); }

                if ( merged_tags.size() > origImageTags )
                {
                    this.connector.setImageKeywords(a.getID(), image.getID(), Helper.getKeywords(merged_tags));
                }

                //update cache
                /*
                //collect Results
                this.log.printLog(Helper.getCurrentTimeString() + " updating local database ... ");
                this.connector.relogin(); //probably not nessceary
                Vector<ITransferQueueItem> processedItemList = this.transferQueue.getProcessedItemList();
                for (ITransferQueueItem item : processedItemList)
                {
                    this.transferedBytes += item.getResults().getTransferedBytes();

                    // uploaded images:
                    // if item.getAction == upload then add imageid to local data
                    if (item.getResults().getAction().equals(TransferQueueItemActionEnum.UPLOAD))
                    {
                        //this.log.printLogLine("getting info for imageID=" + item.getResults().getID());
                        Hashtable<String, String> imageInfo = this.connector.getImageInfo(item.getResults().getID());

                        int albumID = Integer.parseInt( imageInfo.get("AlbumID") );
                        int imageID = Integer.parseInt( imageInfo.get("ImageID") );
                        String imageName = imageInfo.get("ImageName");

                        //this.log.printLogLine("AlbumID=" + albumID + ", ImageID=" + imageID + ", ImageName=" + imageName);

                        if (imageID != 0) { this.addImage(albumID, imageID, imageName); }
                    }
                }
                this.log.printLogLine("ok");
                */
            }
            this.log.printLogLine("ok");
        }


        //this line is not too useful
		this.log.printLogLine(" ... tagged " + albumList.size() + " albums");
    }
    public Vector<IAlbum> statistics(String categoryName, String subcategoryName, String albumName)
    {
        //this.log.printLogLine("DEBUG: Statistics stub (AccountListingProxy)");

        //find matching albums
		Vector<IAlbum> albumList = this.getAccountAlbumList(categoryName, subcategoryName, albumName, null);

        for (IAlbum a : albumList)
        {
            for (IAlbumMonthlyStatistics stats : a.getStatistics())
            {
                if (stats.getBytes() != 0)
                {
                    this.log.printLogLine("DEBUG: " + a.getFullName() + " statistics for " + stats.getMonth() + "/" + stats.getYear() +  ":");
    //                this.log.printLogLine("DEBUG:     ID        :" + a.getStatistics().getAlbumID());
    //                this.log.printLogLine("DEBUG:     Bytes     :" + a.getStatistics().getBytes());
    //                this.log.printLogLine("DEBUG:     Thumb     :" + a.getStatistics().getThumb());
    //                this.log.printLogLine("DEBUG:     Tiny      :" + a.getStatistics().getTiny());
    //                this.log.printLogLine("DEBUG:     Medium    :" + a.getStatistics().getMedium());
    //                this.log.printLogLine("DEBUG:     Large     :" + a.getStatistics().getLarge());
    //                this.log.printLogLine("DEBUG:     XLarge    :" + a.getStatistics().getXLarge());
    //                this.log.printLogLine("DEBUG:     X2Large   :" + a.getStatistics().getX2Large());
    //                this.log.printLogLine("DEBUG:     X3Large   :" + a.getStatistics().getX3Large());
    //                this.log.printLogLine("DEBUG:     Original  :" + a.getStatistics().getOriginal());
    //                this.log.printLogLine("DEBUG:     Video320  :" + a.getStatistics().getVideo320());
    //                this.log.printLogLine("DEBUG:     Video640  :" + a.getStatistics().getVideo640());
    //                this.log.printLogLine("DEBUG:     Video960  :" + a.getStatistics().getVideo960());
    //                this.log.printLogLine("DEBUG:     Video1280 :" + a.getStatistics().getVideo1280());

                }
            }
        }

        return this.getAccountAlbumList(categoryName, subcategoryName, albumName, null);
    }
	public void startSyncProcessingQueue()
	{
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }
        
		// start syncronous processing
		this.transferQueue.startSyncProcessing();
		
		// wait a few secs
		this.log.printLog(Helper.getCurrentTimeString() + " waiting a few secs for smugmug to process the images ... ");
		Helper.pause(this.config.getConstantRetryWait());
		this.log.printLogLine("ok");
		
		//collect Results
		Vector<ITransferQueueItem> processedItemList = this.transferQueue.getProcessedItemList();
		for (ITransferQueueItem item : processedItemList)
		{
			this.transferedBytes += item.getResults().getTransferedBytes();

//			// uploaded images:
//			// if item.getAction == upload then add imageid to local data
//			if (item.getResults().getAction().equals(TransferQueueItemActionEnum.UPLOAD))
//			{
//                if (item.getResults().getID() != 0)
//                {
//                    //this.log.printLogLine("getting info for imageID=" + item.getResults().getID());
//                    Hashtable<String, String> imageInfo = this.connector.getImageInfo(item.getResults().getID());
//
//                    int albumID = Integer.parseInt( imageInfo.get("AlbumID") );
//                    int imageID = Integer.parseInt( imageInfo.get("ImageID") );
//                    String imageName = imageInfo.get("ImageName");
//
//                    //this.log.printLogLine("AlbumID=" + albumID + ", ImageID=" + imageID + ", ImageName=" + imageName);
//
//                    if (imageID != 0) { this.addImage(albumID, imageID, imageName); } //guess this should usually be true, since we already checked before
//                }
//			}
		}

		this.log.printLog(Helper.getCurrentTimeString() + " updating local database ... ");
		this.connector.relogin(); //probably not nessceary

        //discard current root object, and download treedata again (this would be horrobly slow without caching)
        this.smugmugRoot = null;
        this.smugmugRoot = this.connector.getTree(null);
        this.log.printLogLine("ok");
		
	}
    public void startASyncProcessingQueue()
	{
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }

		// start asyncronous processing
		this.transferQueue.startAsyncProcessing();

	}
	public void finishASyncProcessingQueue()
	{
        //initialize Tree is nesseciary
        if (this.smugmugRoot == null) { this.smugmugRoot = this.connector.getTree(null); }

		// wait a few secs
		this.log.printLog(Helper.getCurrentTimeString() + " waiting a few secs for smugmug to process the images ... ");
		Helper.pause(this.config.getConstantRetryWait());
		this.log.printLogLine("ok");

		//collect Results
		Vector<ITransferQueueItem> processedItemList = this.transferQueue.getProcessedItemList();
		for (ITransferQueueItem item : processedItemList)
		{
			this.transferedBytes += item.getResults().getTransferedBytes();

//			// uploaded images:
//			// if item.getAction == upload then add imageid to local data
//			if (item.getResults().getAction().equals(TransferQueueItemActionEnum.UPLOAD))
//			{
//				//this.log.printLogLine("getting info for imageID=" + item.getResults().getID());
//				Hashtable<String, String> imageInfo = this.connector.getImageInfo(item.getResults().getID());
//
//				int albumID = Integer.parseInt( imageInfo.get("AlbumID") );
//				int imageID = Integer.parseInt( imageInfo.get("ImageID") );
//				String imageName = imageInfo.get("ImageName");
//
//				//this.log.printLogLine("AlbumID=" + albumID + ", ImageID=" + imageID + ", ImageName=" + imageName);
//
//				if (imageID != 0) { this.addImage(albumID, imageID, imageName); }
//			}
		}
		
        this.log.printLog(Helper.getCurrentTimeString() + " updating local database ... ");
		this.connector.relogin(); //probably not nessceary

        //discard current root object, and download treedata again (this would be horrobly slow without caching)
        this.smugmugRoot = null;
        this.smugmugRoot = this.connector.getTree(null);
		this.log.printLogLine("ok");

	}
	public long getTransferedBytes() { return this.transferedBytes; }
	
	//----------- private ----------
    private void sortAlbums(IAlbum[] albumArray)
    {

  		//add one pixel image to each album
        Vector<String> tags = new Vector<String>();
        tags.add("jSmugmugBackup_internal");
		int[] imageIDArray = new int[albumArray.length];
		for (int i = 0 ; i < albumArray.length; i++)
		{
			imageIDArray[i] = this.connector.uploadFile(albumArray[i].getID(), new File(this.config.getConstantPixelFilename()), null, tags);
            this.log.printLog("\n");
		}

		this.log.printLog(Helper.getCurrentTimeString() + " waiting a few secs ...");
		Helper.pause(this.config.getConstantRetryWait());
		this.log.printLogLine("ok");

		this.connector.relogin();

		//delete image again
		for (int i = 0 ; i < albumArray.length; i++) { this.connector.deleteFile(imageIDArray[i]); }
    }
    private boolean matchTags(Vector<String> tagList1, Vector<String> tagList2)
    {
        //return true if there is at least one tag that exists in both lists
        if ((tagList1 == null) || (tagList2 == null)) { return false; }

        for(String tag1 : tagList1)
        {
            for (String tag2 : tagList2)
            {
                if (tag1.equals(tag2)) { return true; }
            }
        }

        return false;
    }
//	private Vector<ICategory> getCategoryList()
//	{
//		return this.smugmugRoot.getCategoryList();
//	}
	
//	private void addCategory(int id, String name)
//	{
//		//this.categoryList.add( new Category(this.smugmugRoot, id, name) );
//		this.smugmugRoot.addCategory( new Category(this.smugmugRoot, id, name) );
//	}
	private void addSubcategory(int categoryID, int id, String name)
	{
		for (ICategory c : this.smugmugRoot.getCategoryList())
		{
			if (c.getID() == categoryID)
			{
				c.addSubcategory(new Subcategory(c, id, name) );
				return;
			}
		}
		
		System.out.println("addSubcategory: ERROR!");
	}
	private void addAlbum(int categoryID, int subcategoryID, int id, String key, String name, String albumKeywords, String lastUpdatedString, Vector<IAlbumMonthlyStatistics> albumStats)
	{
		if (subcategoryID == 0) { this.addAlbum(subcategoryID, id, key, name, albumKeywords, lastUpdatedString, albumStats); return; }
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getID() == subcategoryID)
					{
						s.addAlbum( new Album(s, id, key, name, albumKeywords, lastUpdatedString, albumStats) );
						return;
					}
				}
			}
		}
		
		System.out.println("addAlbum: ERROR!");		
	}
	private void addAlbum(int categoryID, int id, String key, String name, String albumKeywords, String lastUpdatedString, Vector<IAlbumMonthlyStatistics> albumStats)
	{
		for (ICategory c : this.smugmugRoot.getCategoryList())
		{
			if (c.getID() == categoryID)
			{
				c.addAlbum( new Album(c, id, key, name, albumKeywords, lastUpdatedString, albumStats) );
				return;
			}
		}
		
		System.out.println("addAlbum: ERROR!");
	}
	private void addImage(int albumID, int id, String name)
	{
		for (ICategory c : this.smugmugRoot.getCategoryList())
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						a.addImage(new Image(a, id, name));
						return;
					}
				}

			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					a.addImage(new Image(a, id, name));
					return;
				}
			}
		}
		
		System.out.println("addImage: ERROR!");
	}

	private int getCategoryID(String categoryName)
	{
		for (ICategory c : this.smugmugRoot.getCategoryList())
		{
			if (c.getName().equals(categoryName)) return c.getID();
		}
		
		return -1; //using "-1" here, because the standard category "Other" is using id=0
	}
	private int getSubcategoryID(int categoryID, String subcategoryName)
	{
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
		
		for (ICategory c : this.smugmugRoot.getCategoryList())
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
