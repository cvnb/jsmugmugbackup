/*
 * Created on Oct 2, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.abstractionLayer;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.data.*;
import jSmugmugBackup.model.login.*;
import jSmugmugBackup.view.*;

import java.io.*;
import java.text.*;
import java.util.*;

import com.streetsofboston.smugmug.v1_2_1.Album;
import com.streetsofboston.smugmug.v1_2_1.AlbumPrototype;
import com.streetsofboston.smugmug.v1_2_1.Category;
import com.streetsofboston.smugmug.v1_2_1.CategoryPrototype;
import com.streetsofboston.smugmug.v1_2_1.Image;
import com.streetsofboston.smugmug.v1_2_1.ImagePrototype;
import com.streetsofboston.smugmug.v1_2_1.SubCategory;
import com.streetsofboston.smugmug.v1_2_1.SubCategoryPrototype;
import com.streetsofboston.smugmug.v1_2_1.AlbumPrototype.Data;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.SORT_METHOD;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.SORT_ORDER;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.VIEW_STYLE;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;

public class SmugmugConnector implements ISmugMugConnector
{
	private Logger log = null;
	private ISmugmugLogin loginToken = null;
	private long transferedBytes; 

	public SmugmugConnector()
	{
		this.log = Logger.getInstance();
		this.transferedBytes = 0;
	}
	

	public void setLoginToken(ISmugmugLogin loginToken)
	{
		this.loginToken = loginToken;		
	}	

	public void login()
	{		
		if (this.loginToken != null) { this.loginToken.login(); }
	}
	
	public void logout()
	{
		if (this.loginToken != null) { this.loginToken.logout(); }
	}
	
	
	public AccountListing getAccountStructure()
	{
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return null;
		
		String nickName = this.loginToken.getToken().getNickName();
		this.log.printLog(this.getTimeString() + " fetching account data for " + nickName + " (this might take a few minutes or longer, depending on the number of files on your account) ... ");

		AccountListing acc_listing = new AccountListing(nickName);
		
		Collection<Image> image_list = null; 		
		ImagePrototype mImagePrototype = new ImagePrototype(this.loginToken.getToken());
		try	{ image_list = mImagePrototype.getAll(); }
		catch (SmugmugException e) { e.printStackTrace(); }
		
		try
		{
			for (Image i : image_list)
			{
				GUID categoryID    = null;  String categoryName = null;
				GUID subCategoryID = null;  String subCategoryName = null;
				GUID albumID       = null;  String albumName = null;
				GUID imageID       = null;  String imageName = null;
				long imageSize     = 0;     String imageMD5Sum = null;

				
				Image image_info = i.getInfo();
				
				categoryID      = image_info.getAlbum().getCategory().guid();
				categoryName    = image_info.getAlbum().getCategory().getName();
				
				
				if (image_info.getAlbum().getSubCategory() != null)
				{
					subCategoryID   = image_info.getAlbum().getSubCategory().guid();
					subCategoryName = image_info.getAlbum().getSubCategory().getName();
				}
				albumID         = image_info.getAlbum().guid();
				albumName       = image_info.getAlbum().getName();
				imageID         = image_info.guid();
				imageName       = image_info.getFileName();	
				imageSize       = image_info.getSize();
				imageMD5Sum     = image_info.getMD5Sum();
				

				//this.log.printLogLine("got image(id=" + imageID.guid() + ", name=" + imageName + ", md5=" + imageMD5Sum + ")");
			
				acc_listing.addImage(categoryID, categoryName,
			             			 subCategoryID, subCategoryName,
			             			 albumID, albumName,
			             			 imageID, imageName,
			             			 imageSize, imageMD5Sum);
				
			}
			this.log.printLogLine("ok");
		}    			
		catch (SmugmugException e)
		{
			this.log.printLogLine("failed");
			e.printStackTrace();
		}    				

		return acc_listing;
	}

	public GUID getCategoryGUID(String name)
	{
		//this.log.printLogLine("SmugmugConnector.getCategoryID(" + name + ")");
		
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return null;
		this.log.printLog(this.getTimeString() + " fetching CategoryID: " + name + " ... ");

    	CategoryPrototype mCatPrototype;
    	
    	//see if category already exists
    	mCatPrototype = new CategoryPrototype(this.loginToken.getToken());
    	Collection<Category> categoryList = null;
    	try { categoryList = mCatPrototype.getAll(); }
    	catch (SmugmugException e) { e.printStackTrace(); }
    	for (Category c : categoryList)
    	{
    		if (c.getName().equals(name))
    		{
    			this.log.printLogLine("ok");
    			return c.guid();
    		}
    	}
    	
    	
    	//create category
    	Category mCategory = null;
    	mCatPrototype = new CategoryPrototype(this.loginToken.getToken());
    	mCatPrototype.data().setName(name);
		
		try
		{
			mCategory = mCatPrototype.create();
			mCatPrototype.data().clear(); //probably not nesseceary
			
			this.log.printLogLine("created");
	    	
	    	return mCategory.guid();
		}
		catch (SmugmugException e)
		{
			this.log.printLogLine("failed, retrying ...");
			this.log.printLogLine("  ERROR: A SmugmugException occured during getCategoryID!");
			this.log.printLogLine("  ERROR: Message:" + e.getMessage());
			this.log.printLogLine("  ERROR: Message: cause:" + e.getCause().getMessage());
			e.printStackTrace();
		}
			
    	return null;
	}

	public GUID getSubCategoryGUID(GUID categoryGUID, String name)
	{
		//this.log.printLogLine("SmugmugConnector.getSubCategoryID(" + categoryID.guid() + ", " + name + ")");
		
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return null;
		if (name == null) return null;
		this.log.printLog(this.getTimeString() + " fetching SubCategoryID: " + name + " ... ");
		

		SubCategoryPrototype mSCatPrototype;
    	
    	//see if subcategory already exists
    	mSCatPrototype = new SubCategoryPrototype(this.loginToken.getToken());
    	Collection<SubCategory> subCategoryList = null;
    	try { subCategoryList = mSCatPrototype.getAll(); }
    	catch (SmugmugException e) { e.printStackTrace(); }
    	for (SubCategory sc : subCategoryList)
    	{
    		if (sc.getName().equals(name))
    		{
    			this.log.printLogLine("ok");
    			return sc.guid();
    		}
    	}
    	
    	//create SubCategory
    	SubCategory mSubCategory = null;
    	mSCatPrototype = new SubCategoryPrototype(this.loginToken.getToken());
		//mSCatPrototype.data().setCategory(category);
    	mSCatPrototype.data().setCategoryID( categoryGUID );
		mSCatPrototype.data().setName(name);
		
		try
		{
			mSubCategory = mSCatPrototype.create();
			
			this.log.printLogLine("created");
	    	
	    	return mSubCategory.guid();
		}
		catch (SmugmugException e)
		{
			this.log.printLogLine("failed, retrying ...");
			this.log.printLogLine("  ERROR: A SmugmugException occured during getSubCategoryID!");
			this.log.printLogLine("  ERROR: Message:" + e.getMessage());
			this.log.printLogLine("  ERROR: Message: cause:" + e.getCause().getMessage());
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	public GUID getAlbumGUID(GUID categoryGUID, GUID subCategoryGUID, String name, String description)
	{
		//this.log.printLogLine("SmugmugConnector.getAlbumID(" + categoryID.guid() + ", <subCategoryID.guid()>, " + name + ", " + description + ")");
		
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return null;
		this.log.printLog(this.getTimeString() + " fetching AlbumID: " + name + " ... ");
		
		AlbumPrototype mAlbumPrototype;
		
		//experimental: check if album with the same name already exists ...
		//  todo: maybe check if album is empty, then use it ... if it already contains pics, create a new album?
    	mAlbumPrototype = new AlbumPrototype(this.loginToken.getToken());
    	Collection<Album> albumList = null;
    	try { albumList = mAlbumPrototype.getAll(); }
    	catch (SmugmugException e) { e.printStackTrace(); }
    	for (Album a : albumList)
    	{
    		if (a.getName().equals(name))
    		{
    			this.log.printLogLine("ok (experimental: warning, album names are not unique)");
    			return a.guid();
    		}
    	}
		
		
		
    	// create Album
    	Album album = null;
		AlbumPrototype apt = new AlbumPrototype(this.loginToken.getToken());
		Data<Album> data = apt.data();
		data.setAllowComments(false);
		data.setAllowEditsByFamily(false);
		data.setAllowEditsByFriends(false);
		data.setAllowExternalLinks(false);
		//data.setCategory(category);
		data.setCategoryID( categoryGUID );
		data.setDescription(description);
		data.setHasCustomAppearance(true);
		data.setHasCustomWatermark(false);
		data.setIsClean(false);
		data.setIsLargestOriginal();  //check if this really works??
		data.setIsOwnerInfoHidden(false);
		data.setIsPrintable(false);
		data.setIsProtected(false);
		data.setIsSmugSearchable(false);
		data.setIsWorldSearchable(false);
		data.setName(name);
		data.setShowEasyShareButton(false);
		data.setShowExif(true);
		data.setShowFilenames(true);
		data.setShowOnHomepage(false);
		//data.setSortMethod(SORT_METHOD.Position);
		data.setSortMethod(SORT_METHOD.FileName);
		data.setSortOrder(SORT_ORDER.Asc);
		//data.setSubCategory(subCategory);
		data.setSubCategoryID( subCategoryGUID );
		data.setViewStyleID(VIEW_STYLE.Smugmug);
		
		//ArrayList<String> keywords = new ArrayList<String>();
		//keywords.add("keyword 1"); keywords.add("keyword 2"); 
		//data.setKeywords(keywords);
		
		try
		{
			album = apt.create();
			
			//this.printLog("Created album: " + album);
			this.log.printLogLine("created");
	    	
	    	return album.guid(); 
		}
		catch (SmugmugException e)
		{
			this.log.printLogLine("failed, retrying ...");
			this.log.printLogLine("  ERROR: A SmugmugException occured during getSubCategoryID!");
			this.log.printLogLine("  ERROR: Message:" + e.getMessage());
			this.log.printLogLine("  ERROR: Message: cause:" + e.getCause().getMessage());
			e.printStackTrace();
		}
		
		return null;    	   	
	}
	
	public boolean uploadFile(GUID albumGUID, File fileDescriptor)
	{
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return false;
		
		//problem: sessions seem to expire after a certain time
		//         solution 1: force relogin for every file - probably overkill, but should be safe
		//         solution 2: check if session is still valid
		// solution 1 ...
		this.loginToken.reLogin();


		boolean repeat = false;
    	
		//if (file.exists() == false) { this.printLog("      file not found!"); this.quitApplication(); }
		do
		{
	    	this.log.printLog(this.getTimeString() + "   uploading: " + fileDescriptor.getAbsolutePath() + " ... ");
	    	
	    	// check if file is bigger than 512 MB
	    	if ( fileDescriptor.length() > (512*1024*1024) )
	    	{
	    		this.log.printLogLine("failed (filesize greater than 512 MB is not supported)");
	    		return false;
	    	}

	    	try
			{
		    	@SuppressWarnings("unused")
				Image img = null;
				ImagePrototype ipt = new ImagePrototype(loginToken.getToken());
	
				InputStream is = null;
		    	is = new FileInputStream(fileDescriptor);
				ipt.data().read(is);
				is.close();
				is = null; //maybe helps saving a little memory
				
				ipt.data().setAlbumID( albumGUID );			
				ipt.data().setFileName(fileDescriptor.getName());
				
				// don't set image caption
				//ipt.data().setCaption("Image Caption_"+System.currentTimeMillis());
				
				// don't set keywords
				//ArrayList<String> keywords = new ArrayList<String>();
				//keywords.add("keyword 1"); keywords.add("keyword 2"); 
				//ipt.data().setKeywords(keywords);
				
				// don't set geo info
				//ipt.data().setAltitude(45); //??
				//ipt.data().setLatitude(42.004567);
				//ipt.data().setLongitude(20.33420);
				
				long startTime = (new Date()).getTime();
				
				// upload file to SmugMug
				img = ipt.create();
				
				long uploadTime = (new Date()).getTime() - startTime;
				double uploadSpeed = 0.0;
				//avoid division by zero
				if (uploadTime != 0) { uploadSpeed = ((double)fileDescriptor.length() / 1024.0) / ((double)uploadTime / 1000.0); }
				
				// for statistics
				this.transferedBytes += fileDescriptor.length();
				
				DecimalFormat df = new DecimalFormat("0.0");				
				this.log.printLogLine("ok (" + df.format(uploadSpeed) + " kb/sec)");
				return true;
			}
			catch (SmugmugException e)
			{
				this.log.printLogLine("failed");
				if (e.getMessage().equals("wrong format ()"))
				{
					this.log.printLogLine("  ERROR: Message :" + e.getMessage());
					this.log.printLogLine("  ERROR: while uploading " + fileDescriptor.getAbsolutePath());
					this.log.printLogLine("  ERROR: the file format was not recognized by SmugMug");
					this.log.printLogLine("  ERROR: maybe it's neither a picture, nor a video ... or the video is too long?");
					this.log.printLogLine("  ERROR: see: http://www.smugmug.com/homepage/uploadlog.mg");
					this.log.printLogLine("  ERROR: ... continuing with the next file ...");
					repeat = false;
				}
				else if (e.getMessage().contains("wrong format (ByteCount given:"))
				{
					this.log.printLogLine("  ERROR: Message :" + e.getMessage());
					this.log.printLogLine("  ERROR: while uploading " + fileDescriptor.getAbsolutePath());
					this.log.printLogLine("  ERROR: the uploaded file appears to be different than the local file");
					this.log.printLogLine("  ERROR: probably there was an error while transfering the file");
					this.log.printLogLine("  ERROR: see: http://www.smugmug.com/homepage/uploadlog.mg");
					this.log.printLogLine("  ERROR: ... trying again ...");
					repeat = true;
				}
				else
				{
					this.log.printLogLine("failed");
					e.toString();
					e.printStackTrace();
					this.log.printLogLine("A SmugmugException occured during upload, aborting!");
					//this.quitApplication();
					
					repeat = false;
				}				
			}
			catch (FileNotFoundException e)
			{
				this.log.printLogLine("failed");
				e.toString();
				e.printStackTrace();
				this.log.printLogLine("A FileNotFoundException occured during upload, aborting!");
				//this.quitApplication();
				
				repeat = false;
			}
			catch (java.lang.RuntimeException e)
			{				
				this.log.printLogLine("failed, retrying ...");
				this.log.printLogLine("  ERROR: A java.lang.RuntimeException occured during upload!");
				this.log.printLogLine("  ERROR: Message:" + e.getMessage());
				this.log.printLogLine("  ERROR: Message: cause:" + e.getCause().getMessage());
				this.log.printLogLine("  ERROR: waiting a few secs");
				this.pause(Constants.retryWait);
				
				repeat = true;
			}
			catch (IOException e) //maybe repeating on IOException is a little too optimistic
			{				
				this.log.printLogLine("failed, retrying ...");
				this.log.printLogLine("  ERROR: A java.io.IOException occured during upload!");
				this.log.printLogLine("  ERROR: Message :" + e.getMessage());
				this.log.printLogLine("  ERROR: waiting a few secs");
				this.pause(5000);
				
				repeat = true;
			}
//			catch (IOException e)
//			{
//				this.log.printLogLine("failed");
//				e.toString();
//				e.printStackTrace();
//				this.log.printLogLine("A IOException occured during upload, aborting!");
//				//this.quitApplication();
//				
//				repeat = false;
//			}
			catch (Exception e)
			{
				this.log.printLogLine("failed");
				e.toString();
				e.printStackTrace();
				this.log.printLogLine("An Exception occured during upload, aborting!");
				//this.quitApplication();
				
				repeat = false;
			}
		} while (repeat); //infinite loop until repeat becomes false
		
		return false;
	}
	
	public boolean downloadFile(GUID imageGUID, String targetDir)
	{
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return false;
		
		try
		{
			ImagePrototype mImagePrototype = new ImagePrototype(this.loginToken.getToken());
			Image image = mImagePrototype.get( imageGUID );
			Image image_info = image.getInfo();
			String fileName = targetDir + image_info.getFileName();
			
			this.log.printLog(this.getTimeString() + "   downloading: " + fileName + " ... ");
			
			//write Image to disk
			long startTime = (new Date()).getTime();
			FileOutputStream fos = new FileOutputStream(fileName);
			if (image.getFormat().equals("MP4")) // maybe there are other video types too
			{
				int video_width = image.getWidth();
				
				if (video_width == 320) image.write(fos, image.getVideo320URL());
				else if (video_width == 640) image.write(fos, image.getVideo640URL());
				else if (video_width == 960) image.write(fos, image.getVideo960URL());
				else if (video_width == 1280) image.write(fos, image.getVideo1280URL());
				else
				{
					this.log.printLogLine("failed");
					this.log.printLogLine("     ... downloading video didn't go as planned, trying at least the picture ... ");
					image.write(fos, image.getOrginalURL());								
				}
			}
			else // default treatment, it's probably a picture
			{
				image.write(fos, image.getOrginalURL());
			}
			fos.close();
			
			
			long uploadTime = (new Date()).getTime() - startTime;
			double uploadSpeed = 0.0;
			//avoid division by zero
			if (uploadTime != 0) { uploadSpeed = ((double)image.getSize() / 1024.0) / ((double)uploadTime / 1000.0); }

			
			//todo: verify download - check md5sum
		    
			// for statistics
		    this.transferedBytes += image.getSize();
		    
		    DecimalFormat df = new DecimalFormat("0.0");
		    this.log.printLogLine("ok (" + df.format(uploadSpeed) + " kb/sec)");
		    return true;
		}
		catch (SmugmugException e)
		{
			this.log.printLogLine("failed");
			e.printStackTrace();
		}
		catch (FileNotFoundException ex)
		{
			this.log.printLogLine("failed");
			System.out.println("FileNotFoundException : " + ex);
		}
	    catch (IOException ioe)
	    {
	    	this.log.printLogLine("failed");
	    	System.out.println("IOException : " + ioe);
	    }
	    
	    return false;
	}
	
	public boolean verifyFile()
	{
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return false;

		this.log.printLogLine("verify is not yet implemented");
		return false;
	}
	
	public boolean deleteFile(GUID imageGUID)
	{
		// check if we're logged in
		if ( (this.loginToken == null) || ((this.loginToken.getToken() == null)) ) return false;

		this.log.printLogLine("delete file is not yet implemented");
		return false;
	}

	public long getTransferedBytes()
	{
		return this.transferedBytes;
	}
	
	//------------------------------- private ------------------------------
	/*
	private void printLog(String message)
	{
		//todo: find a better solution!!
		System.out.println(message);
	}
	*/
	private String getTimeString()
	{
		Date date = new Date();
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
	}
	
	private void pause(long millisecs)
	{
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {}
	}



}
