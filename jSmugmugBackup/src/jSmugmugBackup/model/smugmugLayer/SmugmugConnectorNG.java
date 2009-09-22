/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import java.util.logging.Level;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import org.json.simple.*;


public class SmugmugConnectorNG implements ISmugmugConnectorNG
{
    private GlobalConfig config = null;
	private Logger log = null;
    
	
	// hack: there should be a better way to handle multiple instances of
	//       the connector without having to ask for username and password again
	private static String login_sessionID = null;
	private static Number login_userID = null;
	private static String login_nickname = null;
	private static String login_passwordHash = null;
    private static SmugmugLocalAlbumCache albumCache = null; //making this static is a bit error prone, but should speed things up

    private Vector<IAlbumMonthlyStatistics> statisticsRuntimeCache = null; //this is a runtime cache, so nothing is stored permanently
	private long transferedBytes = 0;
	
	
	public SmugmugConnectorNG()
	{
        this.config = GlobalConfig.getInstance();
		this.log = Logger.getInstance();
	}

	public Number login(String userEmail, String password)
	{
        this.config.setRtconfigAnonymousLogin( password.equals("anonymous") );
		
        if (this.config.getRtconfigAnonymousLogin())
        {
            // anonymous login
            this.smugmug_login_anonymously(false);
            SmugmugConnectorNG.login_nickname = userEmail;
            return 0;
        }
        else
        {
            // standard login
            return this.smugmug_login_withPassword(userEmail, password);
        }
	}

	public void relogin()
	{
        if (this.config.getRtconfigAnonymousLogin() == false) { this.smugmug_login_withHash(); }
        else { this.smugmug_login_anonymously(true); }
	}

	public void logout()
	{
		if (SmugmugConnectorNG.login_sessionID != null) { this.smugmug_logout_logout(); }
		else
		{
			//just to be sure
        	SmugmugConnectorNG.login_sessionID    = null;
        	SmugmugConnectorNG.login_userID       = null;
        	SmugmugConnectorNG.login_nickname     = null;
        	SmugmugConnectorNG.login_passwordHash = null;
		}
	}

	public IRootElement getTree()
	{
        if (SmugmugConnectorNG.login_sessionID == null) { return null; }
        

		this.log.printLog(Helper.getCurrentTimeString() + " downloading account data (this might take a while) ... ");


        // downloading album statistic data from smugmug ... they will later be merged with other album information



        //SmugmugLocalAlbumCache albumCache = null;
        SmugmugConnectorNG.albumCache = null;
        if (this.config.getPersistentCacheAccountInfo())
        {
            if (this.config.getRtconfigAnonymousLogin() == false)
            {
                SmugmugConnectorNG.albumCache = new SmugmugLocalAlbumCache(SmugmugConnectorNG.login_userID.toString());
            }
            else
            {
                SmugmugConnectorNG.albumCache = new SmugmugLocalAlbumCache(SmugmugConnectorNG.login_nickname);
            }
            SmugmugConnectorNG.albumCache.loadCacheFromDisk();
        }

		IRootElement smugmugRoot = new RootElement(SmugmugConnectorNG.login_nickname);
		
		JSONObject tree = this.smugmug_users_getTree(null);
		//this.printJSONObject(tree);


        //loop over the tree
        //cache: validate if albums in cache are still valid
        //statistics: walk over the tree and count the number of files
        //note: the estimated count seems to be slightly (at least 3) lower than the real count, but is sufficient for an approximation
        TransferStatistics stat = new TransferStatistics();
		int statCategoryIndex = 0;
		JSONObject statJsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + statCategoryIndex + "]");
		while (statJsonCategory != null)
		{
			int statSubcategoryIndex = 0;
			JSONObject statJsonSubcategory = (JSONObject)this.getJSONValue(statJsonCategory, "SubCategories[" + statSubcategoryIndex + "]");
			while (statJsonSubcategory != null)
			{
                int statAlbumIndex = 0;
				JSONObject statJsonAlbum = (JSONObject)this.getJSONValue(statJsonSubcategory, "Albums[" + statAlbumIndex + "]");
				while (statJsonAlbum != null)
				{
                    Number albumID          = (Number)this.getJSONValue(statJsonAlbum, "id");
                    Number albumImageCount  = (Number)this.getJSONValue(statJsonAlbum, "ImageCount");
                    String albumLastUpdated = (String)this.getJSONValue(statJsonAlbum, "LastUpdated");
                    stat.estimatedImageCount += albumImageCount.intValue();
                    stat.estimatedAlbumCount++;

                    if (this.config.getPersistentCacheAccountInfo())
                    {
                        SmugmugConnectorNG.albumCache.validateCachedAlbum(albumID.intValue(), albumImageCount.intValue(), albumLastUpdated);
                    }

                    statAlbumIndex++;
                    statJsonAlbum = (JSONObject)this.getJSONValue(statJsonSubcategory, "Albums[" + statAlbumIndex + "]");
                }
				statSubcategoryIndex++;
				statJsonSubcategory = (JSONObject)this.getJSONValue(statJsonCategory, "SubCategories[" + statSubcategoryIndex + "]");

            }
            
			int _albumIndex = 0;
			JSONObject _jsonAlbum = (JSONObject)this.getJSONValue(statJsonCategory, "Albums[" + _albumIndex + "]");
			while (_jsonAlbum != null)
			{
                Number albumID          = (Number)this.getJSONValue(_jsonAlbum, "id");
                Number albumImageCount  = (Number)this.getJSONValue(_jsonAlbum, "ImageCount");
                String albumLastUpdated = (String)this.getJSONValue(_jsonAlbum, "LastUpdated");
                stat.estimatedImageCount += albumImageCount.intValue();
                stat.estimatedAlbumCount++;

                if (this.config.getPersistentCacheAccountInfo())
                {
                    SmugmugConnectorNG.albumCache.validateCachedAlbum(albumID.intValue(), albumImageCount.intValue(), albumLastUpdated);
                }

                _albumIndex++;
				_jsonAlbum = (JSONObject)this.getJSONValue(statJsonCategory, "Albums[" + _albumIndex + "]");
            }

			statCategoryIndex++;
			statJsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + statCategoryIndex + "]");
        }
        //this.log.printLogLine("totalAlbumCount: " + totalAlbumCount);
        this.log.printLog("(estimatedImageCount: " + stat.estimatedImageCount + ", estimatedAlbumCount: " + stat.estimatedAlbumCount + ") ... "); // too low!!



        //init progress statistics
//        final double statStep = 0.1;
//        double statCurrCompletionStep = statStep;
//        int statImageCount = 0;



        int cacheHits = 0;


		//iterate over categories
		int categoryIndex = 0;
		JSONObject jsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		while (jsonCategory != null)
		{
			Number categoryID = (Number)this.getJSONValue(jsonCategory, "id");
			String categoryName = (String)this.getJSONValue(jsonCategory, "Name");
			//System.out.println("categoryIndex=" + categoryIndex + ": id=" + categoryID.intValue() + ", name=" + categoryName);
			ICategory category = new Category(smugmugRoot, categoryID.intValue(), categoryName);
			smugmugRoot.addCategory(category);

			
			//iterate over subcategories
			int subcategoryIndex = 0;
			JSONObject jsonSubcategory = (JSONObject)this.getJSONValue(jsonCategory, "SubCategories[" + subcategoryIndex + "]");
			while (jsonSubcategory != null)
			{
				Number subcategoryID = (Number)this.getJSONValue(jsonSubcategory, "id");
				String subcategoryName = (String)this.getJSONValue(jsonSubcategory, "Name");
				//System.out.println("   subcategoryIndex=" + subcategoryIndex + ": id=" + subcategoryID.intValue() + ", name=" + subcategoryName);
				ISubcategory subcategory = new Subcategory(category, subcategoryID.intValue(), subcategoryName);
				category.addSubcategory(subcategory);
				
				//iterate over albums (with subcategories)
				int albumIndex = 0;
				JSONObject jsonAlbum = (JSONObject)this.getJSONValue(jsonSubcategory, "Albums[" + albumIndex + "]");
				while (jsonAlbum != null)
				{
					Number albumID       = (Number)this.getJSONValue(jsonAlbum, "id");
					String albumName     = (String)this.getJSONValue(jsonAlbum, "Title");
                    String albumKeywords = (String)this.getJSONValue(jsonAlbum, "Keywords");
                    String albumLastUpdated = (String)this.getJSONValue(jsonAlbum, "LastUpdated");
					//System.out.println("      albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);

                    IAlbum album = null;
                    if (this.config.getPersistentCacheAccountInfo())
                    {
                        if (SmugmugConnectorNG.albumCache.exists(albumID.intValue()))
                        {
                            album = new Album(subcategory, SmugmugConnectorNG.albumCache.getCachedAlbum(albumID.intValue()));
                            subcategory.addAlbum(album);
                            cacheHits++;
                        }
                    }

                    if (album == null)
                    {
                        album = new Album(subcategory, albumID.intValue(), albumName, albumKeywords, albumLastUpdated, this.getAlbumStatistics(albumID.intValue()));
                        subcategory.addAlbum(album);

                        //iterate over images
                        JSONObject jsonImages = (JSONObject)this.smugmug_images_get(album.getID(), null, null, null);
                        this.getTree_iterateImages(jsonImages, album, stat);
//                        JSONObject jsonImages = (JSONObject)this.smugmug_images_get(albumID.intValue());
//                        int imageIndex = 0;
//                        JSONObject jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
//                        while (jsonImage != null)
//                        {
//                            Number imageID            = (Number)this.getJSONValue(jsonImage, "id");
//                            //String imageKey           = (String)this.getJSONValue(jsonImage, "Key");
//                            String imageName          = (String)this.getJSONValue(jsonImage, "FileName");
//                            String imageCaption       = (String)this.getJSONValue(jsonImage, "Caption");
//                            String imageKeywords      = (String)this.getJSONValue(jsonImage, "Keywords");
//                            String imageFormat        = (String)this.getJSONValue(jsonImage, "Format");
//                            Number imageHeight        = (Number)this.getJSONValue(jsonImage, "Height");
//                            Number imageWidth         = (Number)this.getJSONValue(jsonImage, "Width");
//                            Number imageSize          = (Number)this.getJSONValue(jsonImage, "Size");
//                            String imageMD5           = (String)this.getJSONValue(jsonImage, "MD5Sum");
//                            //String imageAlbumURL      = (String)this.getJSONValue(jsonImage, "AlbumURL");
//                            //String imageTinyURL       = (String)this.getJSONValue(jsonImage, "TinyURL");
//                            //String imageThumbURL      = (String)this.getJSONValue(jsonImage, "ThumbURL");
//                            //String imageSmallURL      = (String)this.getJSONValue(jsonImage, "SmallURL");
//                            String imageMediumURL     = (String)this.getJSONValue(jsonImage, "MediumURL");
//                            String imageLargeURL      = (String)this.getJSONValue(jsonImage, "LargeURL");
//                            String imageXLargeURL     = (String)this.getJSONValue(jsonImage, "XLargeURL");
//                            String imageX2LargeURL    = (String)this.getJSONValue(jsonImage, "X2LargeURL");
//                            String imageX3LargeURL    = (String)this.getJSONValue(jsonImage, "X3LargeURL");
//                            String imageOriginalURL   = (String)this.getJSONValue(jsonImage, "OriginalURL");
//                            String imageVideo320URL   = (String)this.getJSONValue(jsonImage, "Video320URL");
//                            String imageVideo640URL   = (String)this.getJSONValue(jsonImage, "Video640URL");
//                            String imageVideo960URL   = (String)this.getJSONValue(jsonImage, "Video960URL");
//                            String imageVideo12800URL = (String)this.getJSONValue(jsonImage, "Video12800URL");
//
//                            //if there is no filename available, take the name from the url
//                            if (imageName == null)
//                            {
//                                String url;
//
//                                //get the largest url available:
//                                if (imageVideo12800URL != null) { url = imageVideo12800URL; }
//                                else if (imageVideo960URL != null) { url = imageVideo960URL; }
//                                else if (imageVideo640URL != null) { url = imageVideo640URL; }
//                                else if (imageVideo320URL != null) { url = imageVideo320URL; }
//                                else if (imageOriginalURL != null) { url = imageOriginalURL; }
//                                else if (imageX3LargeURL != null) { url = imageX3LargeURL; }
//                                else if (imageX2LargeURL != null) { url = imageX2LargeURL; }
//                                else if (imageXLargeURL != null) { url = imageXLargeURL; }
//                                else if (imageLargeURL != null) { url = imageLargeURL; }
//                                else if (imageMediumURL != null) { url = imageMediumURL; }
//                                else { this.printJSONObject(jsonImage); url = null; } // this should never happen and will probably case a null pointer exception later
//
//                                imageName = Helper.extractFilenameFromURL(url);
//                            }
//
//                            IImage image = new Image(album, imageID.intValue(), imageName, imageCaption, imageKeywords, imageFormat, imageHeight.intValue(), imageWidth.intValue(), imageSize.longValue(), imageMD5,
//                                                     imageMediumURL, imageLargeURL, imageXLargeURL, imageX2LargeURL, imageX3LargeURL, imageOriginalURL);
//                            album.addImage(image);
//
//
//                            //progress stats
//                            stat.imageCount++; double currCompletion = (double)stat.imageCount / (double) stat.estimatedImageCount;
//                            if (currCompletion > stat.currCompletionStep) { this.log.printLog( (int)(stat.currCompletionStep*100) + "%..."); stat.currCompletionStep += stat.completionStep; }
//
//                            imageIndex++;
//                            jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
//                        }

                        if (this.config.getPersistentCacheAccountInfo())
                        {
                            SmugmugConnectorNG.albumCache.putAlbum(album);
                            SmugmugConnectorNG.albumCache.saveCacheToDisk(); //this might produce quite some I/O overhead, but allows resuming of account info downloads
                        }
                    }

					albumIndex++;
					jsonAlbum = (JSONObject)this.getJSONValue(jsonSubcategory, "Albums[" + albumIndex + "]");
				}
				
				subcategoryIndex++;
				jsonSubcategory = (JSONObject)this.getJSONValue(jsonCategory, "SubCategories[" + subcategoryIndex + "]");
			}

			
			//iterate over albums (without subcategories)
			int albumIndex = 0;
			JSONObject jsonAlbum = (JSONObject)this.getJSONValue(jsonCategory, "Albums[" + albumIndex + "]");
			while (jsonAlbum != null)
			{
				Number albumID       = (Number)this.getJSONValue(jsonAlbum, "id");
				String albumName     = (String)this.getJSONValue(jsonAlbum, "Title");
                String albumKeywords = (String)this.getJSONValue(jsonAlbum, "Keywords");
                String albumLastUpdated = (String)this.getJSONValue(jsonAlbum, "LastUpdated");
				//System.out.println("   albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);

                IAlbum album = null;
                if (this.config.getPersistentCacheAccountInfo())
                {
                    if (SmugmugConnectorNG.albumCache.exists(albumID.intValue()))
                    {
                        album = new Album(category, SmugmugConnectorNG.albumCache.getCachedAlbum(albumID.intValue()));
                        category.addAlbum(album);
                        cacheHits++;
                    }
                }

                if (album == null)
                {
                    album = new Album(category, albumID.intValue(), albumName, albumKeywords, albumLastUpdated, this.getAlbumStatistics(albumID.intValue()) );
                    category.addAlbum(album);


                    //iterate over images
                    JSONObject jsonImages = (JSONObject)this.smugmug_images_get(album.getID(), null, null, null);
                    this.getTree_iterateImages(jsonImages, album, stat);
//                    JSONObject jsonImages = (JSONObject)this.smugmug_images_get(albumID.intValue());
//                    int imageIndex = 0;
//                    JSONObject jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
//                    while (jsonImage != null)
//                    {
//                        Number imageID            = (Number)this.getJSONValue(jsonImage, "id");
//                        //String imageKey           = (String)this.getJSONValue(jsonImage, "Key");
//                        String imageName          = (String)this.getJSONValue(jsonImage, "FileName");
//                        String imageCaption       = (String)this.getJSONValue(jsonImage, "Caption");
//                        String imageKeywords      = (String)this.getJSONValue(jsonImage, "Keywords");
//                        String imageFormat        = (String)this.getJSONValue(jsonImage, "Format");
//                        Number imageHeight        = (Number)this.getJSONValue(jsonImage, "Height");
//                        Number imageWidth         = (Number)this.getJSONValue(jsonImage, "Width");
//                        Number imageSize          = (Number)this.getJSONValue(jsonImage, "Size");
//                        String imageMD5           = (String)this.getJSONValue(jsonImage, "MD5Sum");
//                        //String imageAlbumURL      = (String)this.getJSONValue(jsonImage, "AlbumURL");
//                        //String imageTinyURL       = (String)this.getJSONValue(jsonImage, "TinyURL");
//                        //String imageThumbURL      = (String)this.getJSONValue(jsonImage, "ThumbURL");
//                        //String imageSmallURL      = (String)this.getJSONValue(jsonImage, "SmallURL");
//                        String imageMediumURL     = (String)this.getJSONValue(jsonImage, "MediumURL");
//                        String imageLargeURL      = (String)this.getJSONValue(jsonImage, "LargeURL");
//                        String imageXLargeURL     = (String)this.getJSONValue(jsonImage, "XLargeURL");
//                        String imageX2LargeURL    = (String)this.getJSONValue(jsonImage, "X2LargeURL");
//                        String imageX3LargeURL    = (String)this.getJSONValue(jsonImage, "X3LargeURL");
//                        String imageOriginalURL   = (String)this.getJSONValue(jsonImage, "OriginalURL");
//                        String imageVideo320URL   = (String)this.getJSONValue(jsonImage, "Video320URL");
//                        String imageVideo640URL   = (String)this.getJSONValue(jsonImage, "Video640URL");
//                        String imageVideo960URL   = (String)this.getJSONValue(jsonImage, "Video960URL");
//                        String imageVideo12800URL = (String)this.getJSONValue(jsonImage, "Video12800URL");
//
//                        //if there is no filename available, take the name from the url
//                        if (imageName == null)
//                        {
//                            String url;
//
//                            //get the largest url available:
//                            if (imageVideo12800URL != null) { url = imageVideo12800URL; }
//                            else if (imageVideo960URL != null) { url = imageVideo960URL; }
//                            else if (imageVideo640URL != null) { url = imageVideo640URL; }
//                            else if (imageVideo320URL != null) { url = imageVideo320URL; }
//                            else if (imageOriginalURL != null) { url = imageOriginalURL; }
//                            else if (imageX3LargeURL != null) { url = imageX3LargeURL; }
//                            else if (imageX2LargeURL != null) { url = imageX2LargeURL; }
//                            else if (imageXLargeURL != null) { url = imageXLargeURL; }
//                            else if (imageLargeURL != null) { url = imageLargeURL; }
//                            else if (imageMediumURL != null) { url = imageMediumURL; }
//                            else { this.printJSONObject(jsonImage); url = null; } // this should never happen and will probably case a null pointer exception later
//
//                            imageName = Helper.extractFilenameFromURL(url);
//                        }
//
//                        IImage image = new Image(album, imageID.intValue(), imageName, imageCaption, imageKeywords, imageFormat, imageHeight.intValue(), imageWidth.intValue(), imageSize.longValue(), imageMD5,
//                                                 imageMediumURL, imageLargeURL, imageXLargeURL, imageX2LargeURL, imageX3LargeURL, imageOriginalURL);
//                        album.addImage(image);
//
//
//                        //progress stats
//                        stat.imageCount++; double currCompletion = (double)stat.imageCount / (double) stat.estimatedImageCount;
//                        if (currCompletion > stat.currCompletionStep) { this.log.printLog( (int)(stat.currCompletionStep*100) + "%..."); stat.currCompletionStep += stat.completionStep; }
//
//                        imageIndex++;
//                        jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
//                    }
                    
                    if (this.config.getPersistentCacheAccountInfo())
                    {
                        SmugmugConnectorNG.albumCache.putAlbum(album);
                        SmugmugConnectorNG.albumCache.saveCacheToDisk(); //this might produce quite some I/O overhead, but allows resuming of account info downloads
                    }
                }
                
				albumIndex++;
				jsonAlbum = (JSONObject)this.getJSONValue(jsonCategory, "Albums[" + albumIndex + "]");
			}			
			
			categoryIndex++;
			jsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		}
		
		

        // statistics only
        int totalAlbumCount = 0;
        int totalImageCount = 0;
        for (ICategory c : smugmugRoot.getCategoryList())
        {
            for (ISubcategory s : c.getSubcategoryList())
            {
                for (IAlbum a : s.getAlbumList())
                {
                    for (IImage i : a.getImageList()) { totalImageCount++; }
                    totalAlbumCount++;
                }
            }
            for (IAlbum a : c.getAlbumList())
            {
                for (IImage i : a.getImageList()) { totalImageCount++; }
                totalAlbumCount++;
            }
        }
        //this.log.printLogLine("checkAlbumCount: " + checkAlbumCount);
        this.log.printLog("(totalImageCount: " + totalImageCount + ", totalAlbumCount: " + totalAlbumCount + ", cacheHits: " + cacheHits + ") ... ");

        if (this.config.getPersistentCacheAccountInfo()) { SmugmugConnectorNG.albumCache.saveCacheToDisk(); }

        this.log.printLogLine("ok");

		return smugmugRoot;
	}

    private void getTree_iterateImages(JSONObject jsonImages, IAlbum album, TransferStatistics stat)
    {        
        int imageIndex = 0;
        JSONObject jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
        //this.printJSONObject(jsonImage);
        while (jsonImage != null)
        {
            Number imageID            = (Number)this.getJSONValue(jsonImage, "id");
            //String imageKey           = (String)this.getJSONValue(jsonImage, "Key");
            String imageName          = (String)this.getJSONValue(jsonImage, "FileName");
            String imageCaption       = (String)this.getJSONValue(jsonImage, "Caption");
            String imageKeywords      = (String)this.getJSONValue(jsonImage, "Keywords");
            String imageFormat        = (String)this.getJSONValue(jsonImage, "Format");
            Number imageHeight        = (Number)this.getJSONValue(jsonImage, "Height");
            Number imageWidth         = (Number)this.getJSONValue(jsonImage, "Width");
            Number imageSize          = (Number)this.getJSONValue(jsonImage, "Size");
            String imageMD5           = (String)this.getJSONValue(jsonImage, "MD5Sum");
            //String imageAlbumURL      = (String)this.getJSONValue(jsonImage, "AlbumURL");
            //String imageTinyURL       = (String)this.getJSONValue(jsonImage, "TinyURL");
            //String imageThumbURL      = (String)this.getJSONValue(jsonImage, "ThumbURL");
            //String imageSmallURL      = (String)this.getJSONValue(jsonImage, "SmallURL");
            String imageMediumURL     = (String)this.getJSONValue(jsonImage, "MediumURL");
            String imageLargeURL      = (String)this.getJSONValue(jsonImage, "LargeURL");
            String imageXLargeURL     = (String)this.getJSONValue(jsonImage, "XLargeURL");
            String imageX2LargeURL    = (String)this.getJSONValue(jsonImage, "X2LargeURL");
            String imageX3LargeURL    = (String)this.getJSONValue(jsonImage, "X3LargeURL");
            String imageOriginalURL   = (String)this.getJSONValue(jsonImage, "OriginalURL");
            String imageVideo320URL   = (String)this.getJSONValue(jsonImage, "Video320URL");
            String imageVideo640URL   = (String)this.getJSONValue(jsonImage, "Video640URL");
            String imageVideo960URL   = (String)this.getJSONValue(jsonImage, "Video960URL");
            String imageVideo12800URL = (String)this.getJSONValue(jsonImage, "Video12800URL");


            //get the largest url available:
            String largestAvailableURL;
            if (imageVideo12800URL != null) { largestAvailableURL = imageVideo12800URL; }
            else if (imageVideo960URL != null) { largestAvailableURL = imageVideo960URL; }
            else if (imageVideo640URL != null) { largestAvailableURL = imageVideo640URL; }
            else if (imageVideo320URL != null) { largestAvailableURL = imageVideo320URL; }
            else if (imageOriginalURL != null) { largestAvailableURL = imageOriginalURL; }
            else if (imageX3LargeURL != null) { largestAvailableURL = imageX3LargeURL; }
            else if (imageX2LargeURL != null) { largestAvailableURL = imageX2LargeURL; }
            else if (imageXLargeURL != null) { largestAvailableURL = imageXLargeURL; }
            else if (imageLargeURL != null) { largestAvailableURL = imageLargeURL; }
            else if (imageMediumURL != null) { largestAvailableURL = imageMediumURL; }
            else { this.printJSONObject(jsonImage); largestAvailableURL = null; } // this should never happen and will probably case a null pointer exception later

            /*
            //get content size for largest available url
            int largestURLContentSize = 0;
            boolean repeat = true;
            while (repeat)
            {
                URL url = null;
                try
                {
                    url = new URL(largestAvailableURL);
                    URLConnection conn = url.openConnection();
                    largestURLContentSize = conn.getContentLength();
                    repeat = false;
                }
                catch (MalformedURLException ex1) { this.log.printLog("caught MalformedURLException (" + ex1.getMessage() + "), retrying ..."); }
                catch (IOException ex2) { this.log.printLog("caught IOException (" + ex2.getMessage() + "), retrying ..."); }
            }
            */


            //if there is no filename available, take the name from the url
            if ((imageName == null) || (imageName.equals(""))) { imageName = Helper.extractFilenameFromURL(largestAvailableURL); }


            IImage image = new Image(album, imageID.intValue(), imageName, imageCaption, imageKeywords, imageFormat, imageHeight.intValue(), imageWidth.intValue(), imageSize.longValue(), /*largestURLContentSize,*/ imageMD5,
                                     imageMediumURL, imageLargeURL, imageXLargeURL, imageX2LargeURL, imageX3LargeURL, imageOriginalURL);
            album.addImage(image);


            //progress stats
            if (stat != null)
            {
                stat.imageCount++; double currCompletion = (double)stat.imageCount / (double) stat.estimatedImageCount;
                if (currCompletion > stat.currCompletionStep) { this.log.printLog( (int)(stat.currCompletionStep*100) + "%..."); stat.currCompletionStep += stat.completionStep; }
            }

            imageIndex++;
            jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
        }
    }

	public IAlbum getAlbum(int albumID, String albumKey)
	{
        //seems to return no valuable info on non-public albums :-(
        //JSONObject jsonAlbum = this.smugmug_albums_getInfo(albumID, null, null, albumKey);
        //this.printJSONObject(jsonAlbum);

		JSONObject jsonImages = this.smugmug_images_get(albumID, null, null, albumKey);
		//this.printJSONObject(jsonImages);


        IAlbum album = new Album(null, albumID, "Album Doe", "", "", new AlbumMonthlyStatistics());
        this.getTree_iterateImages(jsonImages, album, null);

        return album;
	}
	
	public Hashtable<String, String> getImageInfo(int imageID)
	{
        if (imageID == 0) { return null; }

		Hashtable<String, String> result = new Hashtable<String, String>();
		
		JSONObject jobj = this.smugmug_images_getInfo(imageID);
		//this.printJSONObject(jobj);
		
		result.put("AlbumID",   ((Long)this.getJSONValue(jobj, "Image.Album.id")).toString());
		result.put("ImageID",   ((Long)this.getJSONValue(jobj, "Image.id")).toString());
		result.put("ImageName", (String)this.getJSONValue(jobj, "Image.FileName"));
		
		return result;
	}

    public void setImageKeywords(int albumID, int imageID, String keywords)
    {
        JSONObject jobj = this.smugmug_images_changeSettings(imageID, keywords);
        //this.printJSONObject(jobj);

        this.cacheCleanup(albumID);
    }

	public int createCategory(String name)
	{
		JSONObject jobj = this.smugmug_categories_create(name);
		//this.printJSONObject(jobj);
		int categoryID = ((Number)this.getJSONValue(jobj, "Category.id")).intValue();
		
		return categoryID;
	}

	public int createSubcategory(int categoryID, String name)
	{
		JSONObject jobj = this.smugmug_subcategories_create(name, categoryID);
		//this.printJSONObject(jobj);
		int subcategoryID = ((Number)this.getJSONValue(jobj, "SubCategory.id")).intValue();
		
		return subcategoryID;
	}

	public int createAlbum(int categoryID, int subCategoryID, String name, Vector<String> albumTags)
	{
        String albumKeywords = Helper.getKeywords(albumTags);

		JSONObject jobj = this.smugmug_albums_create(name, categoryID, subCategoryID, albumKeywords);
		//this.printJSONObject(jobj);
		int albumID = ((Number)this.getJSONValue(jobj, "Album.id")).intValue();
		
		return albumID;
	}
	
	/*
	public void renameCategory(int categoryID, String newName)
	{
		this.log.printLogLine("renaming Category (id=" + categoryID + ", " + newName + ") ... (stub)");
		
		JSONObject jobj = this.smugmug_categories_rename(categoryID, newName);
	}

	public void renameSubcategory(int subcategoryID, String newName)
	{
		this.log.printLogLine("renaming Subcategory (id=" + subcategoryID + ", " + newName + ") ... (stub)");
		
		JSONObject jobj = this.smugmug_subcategories_rename(subcategoryID, newName);
	}

	public void renameAlbum(int albumID, String newName)
	{
		this.log.printLogLine("renaming Album (id=" + albumID + ", newName=" + newName + ") ...");
		
		JSONObject jobj = this.smugmug_albums_changeSettings_title(albumID, newName);
	}
	
	public void setAlbumPosition(int albumID, int newPosition)
	{
		this.log.printLogLine("setting Album position (id=" + albumID + ", newPosition=" + newPosition + ") ...");
		
		//JSONObject albumInfo = this.smugmug_albums_getInfo(albumID);
		//this.printJSONObject(albumInfo);
		
		JSONObject jobj = this.smugmug_albums_changeSettings_position(albumID, newPosition);
	}
	*/
	
	public int uploadFile(int albumID, File file, String caption, Vector<String> tags)
	{
		//this.log.printLog("uploading ... ");

        //prepare tags
        String keywords = Helper.getKeywords(tags);


    	JSONObject jobj = this.smugmug_images_upload(albumID, file, caption, keywords);
    	
        this.cacheCleanup(albumID);

        //this.printJSONObject(jobj);
    	Object obj = this.getJSONValue(jobj, "Image.id");
    	if (obj != null) { return ((Number)obj).intValue(); }
    	else return 0;
	}
		
	public void downloadFile(int imageID, File fileName/*, long expectedFilesize*/)
	{
    	//JSONObject jobj = this.smugmug_images_getURLs(imageID); //retrieves just the urls
		JSONObject jobj = this.smugmug_images_getInfo(imageID); // get image_info, including url
		//this.printJSONObject(jobj);
		
//		String imageFormat = (String)this.getJSONValue(jobj, "Image.Format");
//		String imageURL = null;
//
//        if (imageFormat == null)
//        {
//            //no format specified, guessing this is a picture
//            imageURL = (String)this.getJSONValue(jobj, "Image.OriginalURL");
//            this.printJSONObject(jobj);
//            this.log.printLog(" ... no format specified, guessing it's a picture ...");
//        }
//        else if (imageFormat.equals("MP4")) // maybe there are other video types too
//        {
//            int video_width = ((Number)this.getJSONValue(jobj, "Image.Width")).intValue();
//
//            //download always the largest resolution available
//            if (video_width == 320)       { imageURL = (String)this.getJSONValue(jobj, "Image.Video320URL"); }
//            else if (video_width == 640)  { imageURL = (String)this.getJSONValue(jobj, "Image.Video640URL"); }
//            else if (video_width == 960)  { imageURL = (String)this.getJSONValue(jobj, "Image.Video960URL"); }
//            else if (video_width == 1280) { imageURL = (String)this.getJSONValue(jobj, "Image.Video1280URL"); }
//            else { this.log.printLogLine("failed (could not retrieve video url))"); }
//        }
//        else
//        {
//        	//this is most likely a normal picture
//        	imageURL = (String)this.getJSONValue(jobj, "Image.OriginalURL");
//        }
		
        String imageURL = null;
        
        //attempt to get the largest image/video url available
        if (this.getJSONValue(jobj, "Image.Video1280URL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.Video1280URL"); }
        else if (this.getJSONValue(jobj, "Image.Video960URL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.Video960URL"); }
        else if (this.getJSONValue(jobj, "Image.Video640URL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.Video640URL"); }
        else if (this.getJSONValue(jobj, "Image.Video320URL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.Video320URL"); }
        else if (this.getJSONValue(jobj, "Image.OriginalURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.OriginalURL"); }
        else if (this.getJSONValue(jobj, "Image.X3LargeURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.X3LargeURL"); }
        else if (this.getJSONValue(jobj, "Image.X2LargeURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.X2LargeURL"); }
        else if (this.getJSONValue(jobj, "Image.XLargeURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.XLargeURL"); }
        else if (this.getJSONValue(jobj, "Image.LargeURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.LargeURL"); }
        else if (this.getJSONValue(jobj, "Image.MediumURL") != null) { imageURL = (String)this.getJSONValue(jobj, "Image.MediumURL"); }
        else
        {
            this.printJSONObject(jobj);
            this.log.printLogLine("ERROR: no URL found!");
            System.exit(1);
        }

        //long expectedFilesize = (Long)this.getJSONValue(jobj, "Image.Size");

		this.downloadFile(imageURL, fileName/*, expectedFilesize*/);
	}
	
	public void downloadFile(String imageURL, File fileName/*, long expectedFilesize*/)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " downloading: " + fileName.getAbsolutePath() + " ... ");

        //temporary download destination, renaming afterwards
        File tempFileName = new File(fileName.getParentFile().getAbsolutePath() + "/" + this.config.getConstantTempDownloadFilename());

		//write url to file
		try
		{
            long startTime = (new Date()).getTime(); // this might belong inside the loop ...


//            int contentFilesize = 0;
//            //int retryCount = 0;
//            do //loop until the downloaded file has the correct filesize
//            {
//                URL url	= new URL(imageURL);
//                FileOutputStream out = new FileOutputStream(tempFileName, false);
//                URLConnection conn = url.openConnection();
//                contentFilesize = conn.getContentLength();
//                InputStream  in = conn.getInputStream();
//
//
//                byte[] buffer = new byte[65536]; //write data in 64kb chunks
//                int numRead;
//                long numWritten = 0;
//                while ((numRead = in.read(buffer)) != -1)
//                {
//                    out.write(buffer, 0, numRead);
//                    numWritten += numRead;
//                }
//
//                out.close();
//
//
//                //retryCount++;
//                //if (retryCount > 3) { this.log.printLog("giving up (under development) ... "); } else
//                if (tempFileName.length() != contentFilesize) { this.log.printLog("incomplete file detected (size: " + tempFileName.length() + ", expected: " + contentFilesize + "), retrying ... "); }
//            } while ( (tempFileName.length() != contentFilesize) /*&& (retryCount <= 3)*/ );



            
            int contentFilesize = 0;
            do //loop until the downloaded file has the correct filesize
            {
                //temporary: get the file size "old school"
                URL url	= new URL(imageURL);
                URLConnection conn = url.openConnection();
                contentFilesize = conn.getContentLength();

                // alternate download ... todo: exceptions, repeating
                String responseBody = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(imageURL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                responseBody = httpclient.execute(httpget, responseHandler);
                FileOutputStream out = new FileOutputStream(tempFileName, false);
                out.write(responseBody.getBytes("ISO-8859-1")); //the fixed encoding might cause problems
                //this.log.printLog("DEBUG: length: " + tempFileName.length() + ", alternative length: " + responseBody.length() + ", contentFilesize: " + contentFilesize);
                if (tempFileName.length() != contentFilesize) { this.log.printLog("incomplete file detected (size: " + tempFileName.length() + ", expected: " + contentFilesize + "), retrying ... "); }
            } while (tempFileName.length() != contentFilesize);



            //rename file
            boolean renameResult;
            renameResult = tempFileName.renameTo(fileName);
            if (!renameResult) { this.log.printLogLine("ERROR: renaming temporary file to " + fileName + " failed - this might be a platform specific issue"); System.exit(1); }

			
            long downloadTime = (new Date()).getTime() - startTime;
            double downloadSpeed = 0.0;
            //avoid division by zero
            if (downloadTime != 0) { downloadSpeed = ((double)fileName.length() / 1024.0) / ((double)downloadTime / 1000.0); }
            
            // for statistics
        	this.transferedBytes += fileName.length();
            double filesizeMB = ((double)fileName.length() / (1024.0 * 1024.0));
            
            //debug
            //this.log.printLog("... fileName.length=" + fileName.length() + " ...");

            DecimalFormat df = new DecimalFormat("0.0");            
            this.log.printLog("ok (" + df.format(filesizeMB) + "mb@" + df.format(downloadSpeed) + "kb/s)");
			//this.log.printLogLine("ok");
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e)           { e.printStackTrace(); }
        catch (Exception e)             { e.printStackTrace(); } //not really nesseciary
	}

//	public void verifyFile() {
//		// TODO Auto-generated method stub
//		
//	}
	
	public void deleteFile(int imageID)
	{
		JSONObject jobj = this.smugmug_images_delete(imageID);		
	}
	
	public long getTransferedBytes() { return this.transferedBytes; }



	
	//======================== private - smugmug =============================
	
	private JSONObject smugmugJSONRequest(HttpRequestBase httpRequest)
	{
        String responseBody = null;
		
        //User-Agent String to be identified by Smugmug ...
        httpRequest.addHeader("User-Agent", this.config.getConstantSmugmugUserAgentString());
        
		//repeat until success ... pretty agressive
		// - maybe there should be a relogin in the loop???
		boolean repeat = false;
		do
		{
	        HttpClient httpclient = new DefaultHttpClient();        
	
	        // Create a response handler
	        ResponseHandler<String> responseHandler = new BasicResponseHandler();


	        try
			{
				responseBody = httpclient.execute(httpRequest, responseHandler);
				repeat = false; //no exception has been caught up to here, so we can exit the loop
			}
			catch (ClientProtocolException e)
			{
            	this.log.printLog(Helper.getCurrentTimeString() + " caught ClientProtocolException (message:" + e.getMessage() + "), retrying ... ");
            	Helper.pause(this.config.getConstantRetryWait());
                repeat = true;
			}
			catch (FileNotFoundException e)
			{
				this.log.printLog("caught FileNotFoundException ... ");
				repeat = false;
			}
            catch (java.net.SocketException e)
            {
            	this.log.printLog(Helper.getCurrentTimeString() + " caught java.net.SocketException (message:" + e.getMessage() + "), retrying ... ");
            	Helper.pause(this.config.getConstantRetryWait());
                repeat = true;


                //special: this routine should identify cases where there was an exception thrown, but the video has been successfully uploaded anyway
                //         ... this is probably due the the time smugmug needs to process a video properly
                //first: check if this was an upload request - all other requests don't have any but the standard headers
                if (httpRequest.containsHeader("X-Smug-SessionID"))
                {
                    //this.log.printLog("special case (in development) ...");

                    //if (e.getMessage().equals("Broken pipe"))

                    //now check if it's a video
                    boolean isVideo = false;
                    String fileName = httpRequest.getHeaders("X-Smug-FileName")[0].getValue();
                    int albumID = Integer.parseInt(httpRequest.getHeaders("X-Smug-AlbumID")[0].getValue());
                    //this.log.printLog("(" + fileName + ", " + albumID + ")");
                    for (String fileEnding : this.config.getConstantSupportedFileTypes_Videos())
                    {
                        if (fileName.toLowerCase().endsWith(fileEnding)) { isVideo = true; }
                    }

                    if (isVideo)
                    {
                        this.log.printLog("special socket exception with video: waiting 5min and checking if the video is already there ... ");
                        //this.log.printLog("special (waiting 5min) ... ");
                        Helper.pause(300 * 1000); // 300s = 20min
                        JSONObject jobj_imageList = this.smugmug_images_get(albumID, null, null, null);
                        //this.printJSONObject(jobj_imageList);

                        //iterate over images - trying to find out if the image is already listed on smugmug
                        int imageIndex = 0;
                        JSONObject jsonImage = (JSONObject)this.getJSONValue(jobj_imageList, "Images[" + imageIndex + "]");
                        while (jsonImage != null)
                        {
                            String imageName        = (String)this.getJSONValue(jsonImage, "FileName");

                            if (imageName.equals(fileName))
                            {
                                this.log.printLog("success: we've found the video, no need to upload it again! ...");
                                //this.printJSONObject(jsonImage);
                                repeat = false; // not really nesseciary, cause we're returning early
                                JSONObject jobj = new JSONObject(); // construct a fake JSON reply
                                jobj.put("stat", "fail");
                                jobj.put("method", "smugmug.images.upload");
                                jobj.put("message", "jSmugmugBackup internal (video seems to having been uploaded)");
                                jobj.put("Image.id", (Long)this.getJSONValue(jsonImage, "id"));
                                return jobj;
                            }

                            imageIndex++;
                            jsonImage = (JSONObject)this.getJSONValue(jobj_imageList, "Images[" + imageIndex + "]");
                        }

                        //if (repeat) { this.log.printLog("end of special (the video wasn't found :-( ) ... "); }
                        this.log.printLog("nothing found :-(, retrying ...");
                    }
                }
            }
            catch (IOException e) //maybe repeating on IOException is a little too optimistic
            {
            	this.log.printLog(Helper.getCurrentTimeString() + " caught IOException (message:" + e.getMessage() + "), retrying ... ");
            	Helper.pause(this.config.getConstantRetryWait());
                repeat = true;
            }
			catch (java.lang.RuntimeException e)
            {
            	this.log.printLog(Helper.getCurrentTimeString() + " caught java.lang.RuntimeException (message:" + e.getMessage() + "), retrying ... ");
            	Helper.pause(this.config.getConstantRetryWait());
                repeat = true;
            }
            catch (Exception e)
            {
            	this.log.printLog(Helper.getCurrentTimeString() + " caught Exception (message:" + e.getMessage() + ") ... ");
                e.printStackTrace();
            	repeat = false;
            }
		} while (repeat); //infinite loop until repeat becomes false

		
        Object obj = JSONValue.parse(responseBody);
        JSONObject jobj = (JSONObject)obj;
        //this.printJSONObject(jobj);
        
        //temporary:
        if (jobj == null)
        {
        	this.log.printLogLine("ERROR: jobj == null ... this is unusual");
        	this.log.printLogLine(responseBody);
        }
        
        return jobj;
	}
	
	private Number smugmug_login_withPassword(String userEmail, String password)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " logging in ... ");
		//this.log.printLog("smugmug.login.withPassword ... ");
		
        String methodName = "smugmug.login.withPassword";

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "APIKey=" + this.config.getConstantSmugmugAPIKey() + "&";
		url = url + "EmailAddress=" + userEmail + "&";
		url = url + "Password=" + password + "&";

		do
		{	
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);
	
			
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	        	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
	        	SmugmugConnectorNG.login_userID       = (Number)this.getJSONValue(jobj, "Login.User.id");
	        	SmugmugConnectorNG.login_nickname     = (String)this.getJSONValue(jobj, "Login.User.NickName");
	        	SmugmugConnectorNG.login_passwordHash = (String)this.getJSONValue(jobj, "Login.PasswordHash");
	        	this.log.printLogLine("ok");
	        	//return true;
	        	return SmugmugConnectorNG.login_userID;
	        }
            else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
		       	 (this.getJSONValue(jobj, "message").equals("invalid login")) )
	        {
	        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
	        	SmugmugConnectorNG.login_userID       = (Number)this.getJSONValue(jobj, "Login.User.id");
	        	SmugmugConnectorNG.login_nickname     = (String)this.getJSONValue(jobj, "Login.Session.NickName");
	        	SmugmugConnectorNG.login_passwordHash = (String)this.getJSONValue(jobj, "Login.PasswordHash");
	        	this.log.printLogLine("failed");
	        	
	        	//this is not the optimal solution
	        	//System.exit(0);
	        	
	        	return null;
	        }
            else if (this.getJSONValue(jobj, "stat").equals("fail"))
            {
                // generic "fail" handling ... everything except the "invalid login" which is handled above
                this.log.printLog("login failed (code " + this.getJSONValue(jobj, "code") + ": " + this.getJSONValue(jobj, "message") + "), retrying ... ");

                //maybe we should quit here??

                Helper.pause(this.config.getConstantRetryWait());
            }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("retrying ... ");
	        	this.printJSONObject(jobj); //temporary

                Helper.pause(this.config.getConstantRetryWait());
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...

	}
		
	private void smugmug_login_withHash()
	{
		//this.log.printLog(this.getTimeString() + " relogin ... ");
		
		//this.log.printLog("smugmug.login.withHash ... ");
		
        String methodName = "smugmug.login.withHash";

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "APIKey=" + this.config.getConstantSmugmugAPIKey() + "&";
		url = url + "UserID=" + SmugmugConnectorNG.login_userID + "&";
		url = url + "PasswordHash=" + SmugmugConnectorNG.login_passwordHash + "&";

		
		do
		{	
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);
	
			
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	        	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	//this.log.printLogLine("ok");
	        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
	        	return;
	        }
            else if (this.getJSONValue(jobj, "stat").equals("fail"))
            {
                // generic "fail" handling ... everything except the "invalid login" which is handled above
                this.log.printLog("relogin failed (code " + this.getJSONValue(jobj, "code") + ": " + this.getJSONValue(jobj, "message") + "), retrying ... ");
                Helper.pause(this.config.getConstantRetryWait());

                //maybe we should quit here??
            }
            else
	        {
	        	//this.log.printLog(this.getTimeString() + " smugmug.login.withHash ... failed");
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("relogin failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
                Helper.pause(this.config.getConstantRetryWait());
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
	}

	private void smugmug_login_anonymously(boolean beQuiet)
	{
        if (!beQuiet) { this.log.printLog(Helper.getCurrentTimeString() + " logging in anonymously ... "); }

		//this.log.printLog("smugmug.login.withHash ... ");

        String methodName = "smugmug.login.anonymously";

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "APIKey=" + this.config.getConstantSmugmugAPIKey() + "&";



		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);


	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	        	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	if (!beQuiet) { this.log.printLogLine("ok"); }
	        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
	        	return;
	        }
	        else
	        {
	        	this.log.printLog("anonymous login failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
	}


	private void smugmug_logout_logout()
	{
		this.log.printLog(Helper.getCurrentTimeString() + " logging out ... ");
		//this.log.printLog("smugmug.logout ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=smugmug.logout&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.logout")) )
        {        	
        	SmugmugConnectorNG.login_sessionID    = null;
        	SmugmugConnectorNG.login_userID       = null;
        	SmugmugConnectorNG.login_nickname     = null;
        	SmugmugConnectorNG.login_passwordHash = null;
        	this.log.printLogLine("ok");
        }
        else { this.log.printLogLine("failed"); }
	}
		
	private JSONObject smugmug_users_getTree(String sitePassword)
	{
		//this.log.printLog("smugmug.users.getTree ... ");
		
        String methodName = "smugmug.users.getTree";

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "NickName=" + SmugmugConnectorNG.login_nickname + "&"; //optional
		url = url + "Heavy=1&"; //optional, the extra info might be useful at a later time
		if (sitePassword != null) { url = url + "SitePassword=" + sitePassword + "&"; } //optional
		
		do
		{		
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);
	        
			
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	           	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {        	
	        	//this.log.printLogLine("ok");
                //this.printJSONObject(jobj);
	           	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("smugmug.users.getTree failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
        
        //return null;
	}

	private JSONObject smugmug_users_getTransferStats(int month, int year)
	{
		//this.log.printLog("smugmug.users.getTransferStats(" + month + ", " + year + ") ... ");
        this.log.printLog("(loading statistics for " + month + "/" + year + " ... ");

        String methodName = "smugmug.users.getTransferStats";

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Month=" + month + "&";
        url = url + "Year=" + month + "&";
		url = url + "Heavy=0&"; //optional, Heavy=1 doesn't seem to work


		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);


	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	           	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	this.log.printLog("ok) ... ");
                //this.printJSONObject(jobj);
	           	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("smugmug.users.getTransferStats failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...

        //return null;
	}
		
	private JSONObject smugmug_categories_create(String name)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " creating category ... ");
		
		String methodName = "smugmug.categories.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Name=" + Helper.encodeForURL(name) + "&";
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "Category.id") + ")");
        	return jobj;
        }
        else if ( (this.getJSONValue(jobj, "stat").equals("failed")) &&
                  (this.getJSONValue(jobj, "code").equals(new Long(16))) )
        {
            //category already exists
        	this.log.printLogLine("failed (" + this.getJSONValue(jobj, "message") + ")");
        	return null;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
    }

	private JSONObject smugmug_categories_rename(int categoryID, String newName)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " renaming category ... ");
		
		String methodName = "smugmug.categories.rename";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "CategoryID=" + categoryID + "&";
		url = url + "Name=" + Helper.encodeForURL(newName) + "&";
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "Category.id") + ")");
        	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
    }

	
	private JSONObject smugmug_subcategories_create(String name, int categoryID)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " creating subcategory ... ");
		
		String methodName = "smugmug.subcategories.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Name=" + Helper.encodeForURL(name) + "&";
		url = url + "CategoryID=" + categoryID + "&";
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	//this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "SubCategory.id") + ")");
        	this.log.printLogLine("ok");
        	//this.printJSONObject(jobj);
           	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
	}
	
	private JSONObject smugmug_subcategories_rename(int subcategoryID, String newName)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " renaming subcategory ... ");
		
		String methodName = "smugmug.subcategories.rename";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "SubCategoryID=" + subcategoryID + "&";
		url = url + "Name=" + Helper.encodeForURL(newName) + "&";
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	//this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "SubCategory.id") + ")");
        	this.log.printLogLine("ok");
        	this.printJSONObject(jobj);
        	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
    }

	private JSONObject smugmug_albums_create(String title, int categoryID, int subCategoryID, String albumKeywords)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " creating album ... ");
		
		String methodName = "smugmug.albums.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Title=" + Helper.encodeForURL(title) + "&";
		url = url + "CategoryID=" + categoryID + "&";
		
		//essentials
		//url = url + "AlbumTemplateID=" + 0 + "&"; //integer, optional, default: 0
		url = url + "SubCategoryID=" + subCategoryID + "&"; //integer, optional, default: 0
		//url = url + "Description=&"; //string, optional
		//url = url + "Keywords=&"; //string, optional
        if (albumKeywords != null) { url = url + "Keywords=" + Helper.encodeForURL(albumKeywords) + "&"; }//string, optional
		//url = url + "Geography=&"; //boolean, optional, default: 1
		//url = url + "HighlightID=&"; //integer, optional
		//url = url + "Position=&"; //integer, optional
		
		//look&feel
		//url = url + "Header=&"; //boolean, optional (power & pro only), default: 0
		//url = url + "Clean=&"; //boolean, optional, default: 0
		//url = url + "EXIF=&"; //boolean, optional, default: 1
		url = url + "Filenames=1&"; //boolean, optional, default: 0
		url = url + "SquareThumbs=0&"; //boolean, optional, default: 1
		//url = url + "TemplateID=&"; //integer, optional, default: 0 (viewer choice)
		url = url + "SortMethod=FileName&"; //string, optional, default: position
		url = url + "SortDirection=0&"; //boolean, optional, 0 --> ascending, 1 --> decending
		
		//security&privacy
		//url = url + "Password=&"; //string, optional
		//url = url + "PasswordHint=&"; //string, optional
		url = url + "Public=0&"; //boolean, optional, default: 1
		url = url + "WorldSearchable=0&"; //boolean, optional, default: 1
		url = url + "SmugSearchable=0&"; //boolean, optional, default: 1
		url = url + "External=0&"; //boolean, optional, default: 1
		//url = url + "Protected=&"; //boolean, optional(power&pro only), default: 0
		//url = url + "Watermarking=&"; //boolean, optional (pro only), default: 0
		//url = url + "WatermarkID=&"; //integer, optional (pro only), default: 0
		//url = url + "HideOwner=&"; //boolean, optional, default: 0
		//url = url + "Larges=&"; //boolean, optional (pro only), default: 1
		//url = url + "XLarges=&"; //boolean, optional (pro only), default: 1
		//url = url + "X2Larges=&"; //boolean, optional, default: 1
		//url = url + "X3Larges=&"; //boolean, optional, default: 1
		//url = url + "Originals=&"; //boolean, optional, default: 1
		
		//social
		//url = url + "CanRank=&"; //boolean, optional, default: 1
		//url = url + "FriendEdit=&"; //boolean, optional, default: 0
		//url = url + "FamilyEdit=&"; //boolean, optional, default: 0
		//url = url + "Comments=&"; //boolean, optional, default: 1
		url = url + "Share=0&"; //boolean, optional, default: 1
		
		// printing&sales
		//url = url + "Printable=&"; //boolean, optional, default: 1
		//url = url + "DefaultColor=&"; //boolean, optional (pro only), default: 0
		//url = url + "ProofDays=&"; //integer, optional (pro only), default: 0
		//url = url + "Backprinting=&"; //string, optional (pro only)
		
		// photo sharpening
		//url = url + "UnsharpAmount=&"; //float, optional (power&pro only), default: 0.200
		//url = url + "UnsharpRadius=&"; //float, optional (power&pro only), default: 1.000
		//url = url + "UnsharpThreshold=&"; //float, optional (power&pro only), default: 0.050
		//url = url + "UnsharpSigma=&"; //float, optional (power&pro only), default: 1.000
		
		// community
		//url = url + "CommunityID=&"; //integer, optional, default: 0
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (" + this.getJSONValue(jobj, "Album.id") + ")");
           	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
	}

	private JSONObject smugmug_albums_getInfo(int albumID, String password, String sitePassword, String albumKey)
	{
		String methodName = "smugmug.albums.getInfo";
		//this.log.printLog(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&"; //integer
		if (password != null)     { url = url + "Password=" + password + "&"; } //string, optional
		if (sitePassword != null) { url = url + "SitePassword=" + sitePassword + "&"; } //string, optional
		if (albumKey != null)     { url = url + "AlbumKey=" + albumKey + "&"; } //string, seems to be optional, but is not documented


		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);
			
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	             (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	//this.log.printLogLine("ok");
	        	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	//this.log.printLogLine(this.getTimeString() + " " + methodName + " ... failed");
	        	this.log.printLog(methodName + "retrying ... ");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
        
        //return null;
    }


	private JSONObject smugmug_albums_changeSettings_title(int albumID, String newTitle)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " changing album settings (title) ... ");
		
		String methodName = "smugmug.albums.changeSettings";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&";
		
		

		
		//essentials
		url = url + "Title=" + Helper.encodeForURL(newTitle) + "&";
		//url = url + "CategoryID=" + categoryID + "&";
		//url = url + "SubCategoryID=" + subCategoryID + "&"; //integer, optional, default: 0		
		//url = url + "Description=&"; //string, optional
		//url = url + "Keywords=&"; //string, optional
		//url = url + "AlbumTemplateID=" + 0 + "&"; //integer, optional, default: 0
		//url = url + "Geography=&"; //boolean, optional, default: 1
		//url = url + "HighlightID=&"; //integer, optional
		//url = url + "Position=&"; //integer, optional
		
		//look&feel
		//url = url + "Header=&"; //boolean, optional (power & pro only), default: 0
		//url = url + "Clean=&"; //boolean, optional, default: 0
		//url = url + "EXIF=&"; //boolean, optional, default: 1
		//url = url + "Filenames=1&"; //boolean, optional, default: 0
		//url = url + "SquareThumbs=0&"; //boolean, optional, default: 1
		//url = url + "TemplateID=&"; //integer, optional, default: 0 (viewer choice)
		//url = url + "SortMethod=FileName&"; //string, optional, default: position
		//url = url + "SortDirection=0&"; //boolean, optional, 0 --> ascending, 1 --> decending
		
		//security&privacy
		//url = url + "Password=&"; //string, optional
		//url = url + "PasswordHint=&"; //string, optional
		//url = url + "Public=0&"; //boolean, optional, default: 1
		//url = url + "WorldSearchable=0&"; //boolean, optional, default: 1
		//url = url + "SmugSearchable=0&"; //boolean, optional, default: 1
		//url = url + "External=&"; //boolean, optional, default: 1
		//url = url + "Protected=&"; //boolean, optional(power&pro only), default: 0
		//url = url + "Watermarking=&"; //boolean, optional (pro only), default: 0
		//url = url + "WatermarkID=&"; //integer, optional (pro only), default: 0
		//url = url + "HideOwner=&"; //boolean, optional, default: 0
		//url = url + "Larges=&"; //boolean, optional (pro only), default: 1
		//url = url + "XLarges=&"; //boolean, optional (pro only), default: 1
		//url = url + "X2Larges=&"; //boolean, optional, default: 1
		//url = url + "X3Larges=&"; //boolean, optional, default: 1
		//url = url + "Originals=&"; //boolean, optional, default: 1
		
		//social
		//url = url + "CanRank=&"; //boolean, optional, default: 1
		//url = url + "FriendEdit=&"; //boolean, optional, default: 0
		//url = url + "FamilyEdit=&"; //boolean, optional, default: 0
		//url = url + "Comments=&"; //boolean, optional, default: 1
		//url = url + "Share=&"; //boolean, optional, default: 1
		
		// printing&sales
		//url = url + "Printable=&"; //boolean, optional, default: 1
		//url = url + "DefaultColor=&"; //boolean, optional (pro only), default: 0
		//url = url + "ProofDays=&"; //integer, optional (pro only), default: 0
		//url = url + "Backprinting=&"; //string, optional (pro only)
		
		// photo sharpening
		//url = url + "UnsharpAmount=&"; //float, optional (power&pro only), default: 0.200
		//url = url + "UnsharpRadius=&"; //float, optional (power&pro only), default: 1.000
		//url = url + "UnsharpThreshold=&"; //float, optional (power&pro only), default: 0.050
		//url = url + "UnsharpSigma=&"; //float, optional (power&pro only), default: 1.000
		
		// community
		//url = url + "CommunityID=&"; //integer, optional, default: 0
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	//this.log.printLogLine("ok (" + this.getJSONValue(jobj, "Album.id") + ")");
        	this.log.printLogLine("ok");
        	//this.printJSONObject(jobj);
        	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
	}

	private JSONObject smugmug_albums_changeSettings_position(int albumID, int newPosition)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " changing album settings (id=" + albumID + ", newPosition=" + newPosition + ") ... ");
		
		String methodName = "smugmug.albums.changeSettings";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&";
		
		

		
		//essentials
		//url = url + "Title=" + this.encodeForURL(newTitle) + "&";
		//url = url + "CategoryID=" + categoryID + "&";
		//url = url + "SubCategoryID=" + subCategoryID + "&"; //integer, optional, default: 0		
		//url = url + "Description=&"; //string, optional
		//url = url + "Keywords=&"; //string, optional
		//url = url + "AlbumTemplateID=" + 0 + "&"; //integer, optional, default: 0
		//url = url + "Geography=&"; //boolean, optional, default: 1
		//url = url + "HighlightID=&"; //integer, optional
		url = url + "Position=" + newPosition + "&"; //integer, optional
		
		//look&feel
		//url = url + "Header=&"; //boolean, optional (power & pro only), default: 0
		//url = url + "Clean=&"; //boolean, optional, default: 0
		//url = url + "EXIF=&"; //boolean, optional, default: 1
		//url = url + "Filenames=1&"; //boolean, optional, default: 0
		//url = url + "SquareThumbs=0&"; //boolean, optional, default: 1
		//url = url + "TemplateID=&"; //integer, optional, default: 0 (viewer choice)
		//url = url + "SortMethod=FileName&"; //string, optional, default: position
		//url = url + "SortDirection=0&"; //boolean, optional, 0 --> ascending, 1 --> decending
		
		//security&privacy
		//url = url + "Password=&"; //string, optional
		//url = url + "PasswordHint=&"; //string, optional
		//url = url + "Public=0&"; //boolean, optional, default: 1
		//url = url + "WorldSearchable=0&"; //boolean, optional, default: 1
		//url = url + "SmugSearchable=0&"; //boolean, optional, default: 1
		//url = url + "External=&"; //boolean, optional, default: 1
		//url = url + "Protected=&"; //boolean, optional(power&pro only), default: 0
		//url = url + "Watermarking=&"; //boolean, optional (pro only), default: 0
		//url = url + "WatermarkID=&"; //integer, optional (pro only), default: 0
		//url = url + "HideOwner=&"; //boolean, optional, default: 0
		//url = url + "Larges=&"; //boolean, optional (pro only), default: 1
		//url = url + "XLarges=&"; //boolean, optional (pro only), default: 1
		//url = url + "X2Larges=&"; //boolean, optional, default: 1
		//url = url + "X3Larges=&"; //boolean, optional, default: 1
		//url = url + "Originals=&"; //boolean, optional, default: 1
		
		//social
		//url = url + "CanRank=&"; //boolean, optional, default: 1
		//url = url + "FriendEdit=&"; //boolean, optional, default: 0
		//url = url + "FamilyEdit=&"; //boolean, optional, default: 0
		//url = url + "Comments=&"; //boolean, optional, default: 1
		//url = url + "Share=&"; //boolean, optional, default: 1
		
		// printing&sales
		//url = url + "Printable=&"; //boolean, optional, default: 1
		//url = url + "DefaultColor=&"; //boolean, optional (pro only), default: 0
		//url = url + "ProofDays=&"; //integer, optional (pro only), default: 0
		//url = url + "Backprinting=&"; //string, optional (pro only)
		
		// photo sharpening
		//url = url + "UnsharpAmount=&"; //float, optional (power&pro only), default: 0.200
		//url = url + "UnsharpRadius=&"; //float, optional (power&pro only), default: 1.000
		//url = url + "UnsharpThreshold=&"; //float, optional (power&pro only), default: 0.050
		//url = url + "UnsharpSigma=&"; //float, optional (power&pro only), default: 1.000
		
		// community
		//url = url + "CommunityID=&"; //integer, optional, default: 0
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		this.log.printLogLine("url: " + url);
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	//this.log.printLogLine("ok (" + this.getJSONValue(jobj, "Album.id") + ")");
        	this.log.printLogLine("ok");
        	//this.printJSONObject(jobj);
        	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.printJSONObject(jobj);
        }
        
        return null;
	}



	private JSONObject smugmug_images_get(int albumID, String password, String sitePassword, String albumKey)
	{
		String methodName = "smugmug.images.get";
		//this.log.printLog("smugmug.images.get ... ");

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&";
		url = url + "Heavy=1&"; //optional
		if (password != null)     { url = url + "Password=" + password + "&"; } //optional
		if (sitePassword != null) { url = url + "SitePassword=" + sitePassword + "&"; } //optional
		if (albumKey != null)     { url = url + "AlbumKey=" + albumKey + "&"; } //seems to be optional, but is not documented


		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);

	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	           	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	//this.log.printLogLine("ok");
	           	return jobj;
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	        		  (this.getJSONValue(jobj, "code").equals(new Long(15))) )
	        {
                //no images found
	        	//this.log.printLogLine("empty");
	        	return jobj;
	        }
            else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	        		  (this.getJSONValue(jobj, "code").equals(new Long(4))) )
	        {
                //invalid user ... probably a missing site password in conjunction with anonymous login ... ignoring this
	        	//this.log.printLogLine("invalid user");
	        	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("smugmug.images.get failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...

        //return null;
	}

	private JSONObject smugmug_images_changeSettings(int imageID, String keywords)
	{
        //this.log.printLog(Helper.getCurrentTimeString() + " changing image settings: id=" + imageID + " keywords=" + keywords + " ... ");

		String methodName = "smugmug.images.changeSettings";
		//this.log.printLog("smugmug.images.changeSettings ... ");

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&";


        //url = url + "AlbumID=????&"; //optional
		//url = url + "Caption=????&"; //optional
		url = url + "Keywords=" + Helper.encodeForURL(keywords) + "&"; //optional
		//url = url + "Hidden=????&"; //optional

        //this.log.printLogLine("DEBUG: changing keywords url: " + url);

		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);

	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	           	 (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	//this.log.printLogLine("ok");
                this.log.printLog(".");
	           	return jobj;
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	        		  (this.getJSONValue(jobj, "code").equals(new Long(5))) )
	        {
	        	this.log.printLogLine("nothing changed");
                //this.log.printLog("_");
	        	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	this.log.printLog("smugmug.images.changeSettings failed, retrying ...");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...

        //return null;
	}

	private JSONObject smugmug_images_delete(int imageID)
	{
		this.log.printLog(Helper.getCurrentTimeString() + " deleting (imageID=" + imageID + ") ... ");

		String methodName = "smugmug.images.delete";
		//this.log.printLog("smugmug.images.get ... ");

		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&";


		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);


        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok");
           	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
           	this.printJSONObject(jobj);
        }

        return null;
	}
	
	private JSONObject smugmug_images_getURLs(int imageID)
	{
		String methodName = "smugmug.images.getURLs";
		//this.log.printLog(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&"; //integer
		//url = url + "TemplateID=&"; //string, optional (specifies which Style to build the AlbumURL with), default: 3
		//url = url + "Password=&"; //string, optional
		//url = url + "SitePassword=&"; //string, optional
		//url = url + "ImageKey=&"; //string
		
		HttpGet httpget = new HttpGet(url);
		JSONObject jobj = this.smugmugJSONRequest(httpget);
		//this.printJSONObject(jobj);
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	//this.log.printLogLine("ok");
        	return jobj;
        }
        else
        {
        	//this.log.printLogLine("failed");
        	this.log.printLogLine(Helper.getCurrentTimeString() + " " + methodName + " ... failed");
        }
        
        return null;
    }
	
	private JSONObject smugmug_images_getInfo(int imageID)
	{
		String methodName = "smugmug.images.getInfo";
		//this.log.printLog(methodName + " ...");
		
		//build url
		String url = this.config.getConstantSmugmugServerURL() + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&"; //integer
		//url = url + "Password=&"; //string, optional
		//url = url + "SitePassword=&"; //string, optional
		//url = url + "ImageKey=&"; //string
		
		do
		{
			HttpGet httpget = new HttpGet(url);
			JSONObject jobj = this.smugmugJSONRequest(httpget);
			//this.printJSONObject(jobj);
			
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	             (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	        	//this.log.printLogLine("ok");
	        	return jobj;
	        }
	        else
	        {
	        	//this.log.printLogLine("failed");
	        	//this.log.printLogLine(this.getTimeString() + " " + methodName + " ... failed");
	        	this.log.printLog(methodName + "retrying ... ");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
        
        //return null;
    }
	
	private JSONObject smugmug_images_upload(int albumID, File fileName, String caption, String keywords)
	{
        if (keywords == null)
        {
            this.log.printLog(Helper.getCurrentTimeString() + " upload: " + fileName.getAbsolutePath() + " ... ");
        }
        else
        {
            this.log.printLog(Helper.getCurrentTimeString() + " upload: " + fileName.getAbsolutePath() + " (" + keywords + ") ... ");
        }



		String methodName = "smugmug.images.upload";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = "http://upload.smugmug.com/" + Helper.encodeForURL(fileName.getName());

		do
		{	
	        HttpPut httpPut = new HttpPut(url);

	        //add header
	        //httpPut.addHeader("Content-Length", Long.toString(fileName.length()) );
	        httpPut.addHeader("Content-MD5", Helper.computeMD5Hash(fileName) );
	        httpPut.addHeader("X-Smug-SessionID", SmugmugConnectorNG.login_sessionID);
	        httpPut.addHeader("X-Smug-Version", this.config.getConstantSmugmugAPIVersion());
	        httpPut.addHeader("X-Smug-ResponseType", "JSON");
	        httpPut.addHeader("X-Smug-AlbumID", Integer.toString(albumID) ); // required for uploading new photos, not for replacing existing ones
	        //httpPut.addHeader("X-Smug-ImageID", ""); //required for replacing, not for uploading
	        
            //httpPut.addHeader("X-Smug-FileName", fileName.getName()); //optional
            //if (caption != null)  { httpPut.addHeader("X-Smug-Caption", Helper.encodeForURL(caption)); } //optional
	        //if (keywords != null) { httpPut.addHeader("X-Smug-Keywords", Helper.encodeForURL(keywords)); } //optional
            // only ASCII characters are allowed in headers, so we convert eventually non conform characters
            httpPut.addHeader("X-Smug-FileName", Helper.encodeAsASCII(fileName.getName())); //optional, seems like smugmug prefers this parameter over the url name
            if (caption != null)  { httpPut.addHeader("X-Smug-Caption", caption); } //optional
	        if (keywords != null) { httpPut.addHeader("X-Smug-Keywords", keywords); } //optional

            //httpPut.addHeader("X-Smug-Latitude", ""); //optional
	        //httpPut.addHeader("X-Smug-Longitude", ""); //optional
	        //httpPut.addHeader("X-Smug-Altitude", ""); //optional
	
	        
	        // see: http://www.iana.org/assignments/media-types/
	        // ... maybe this causes the upload-problems with videos???
	        HttpEntity entity = new org.apache.http.entity.FileEntity(fileName, "image/jpeg");
	        httpPut.setEntity(entity);
	        
	        long startTime = (new Date()).getTime();
	
	        
			JSONObject jobj = this.smugmugJSONRequest(httpPut);
	        
	        
	        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
	             (this.getJSONValue(jobj, "method").equals(methodName)) )
	        {
	            long uploadTime = (new Date()).getTime() - startTime;
	            double uploadSpeed = 0.0;
	            //avoid division by zero
	            if (uploadTime != 0) { uploadSpeed = ((double)fileName.length() / 1024.0) / ((double)uploadTime / 1000.0); }
	            
	            // for statistics
	        	this.transferedBytes += fileName.length();
	            double filesizeMB = ((double)fileName.length() / (1024.0 * 1024.0));
	        	
	            DecimalFormat df = new DecimalFormat("0.0");                            
	            this.log.printLog("ok (" + df.format(filesizeMB) + "mb@" + df.format(uploadSpeed) + "kb/s)");
	        	//this.log.printLogLine("ok");
	        	return jobj;
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	                  (this.getJSONValue(jobj, "method").equals(methodName)) &&
	                  (this.getJSONValue(jobj, "message").equals("wrong format ()")))
	        {
	        	this.log.printLogLine("failed (wrong format)");
	            this.log.printLogLine("  ERROR: the file format was not recognized by SmugMug");
	            this.log.printLogLine("  ERROR: maybe it's neither a picture, nor a video ... or the video is too long?");
	            this.log.printLogLine("  ERROR: see: http://www.smugmug.com/homepage/uploadlog.mg");
	            
	            //todo: maybe set ignore tag here ... and print info to console
	
	        	return jobj;
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	                  (this.getJSONValue(jobj, "method").equals(methodName)) &&
	                  (((String)this.getJSONValue(jobj, "message")).startsWith("wrong format (ByteCount given:") ))
	        {
	        	//this.log.printLogLine("failed (wrong bytecount)");
	        	//this.log.printLogLine("  ERROR: the uploaded file appears to be different than the local file");
	        	//this.log.printLogLine("  ERROR: probably there was an error while transfering the file");
	        	//this.log.printLogLine("  ERROR: see: http://www.smugmug.com/homepage/uploadlog.mg");
	        
	        	//return jobj;

                /*
                 * special treatment for videos is not nesseciary here, since we've recieved a valid JSON response - if
                 * the video would be uploaded we would have gotten another response
                //check if it's a video
                boolean isVideo = false;
                for (String fileEnding : this.config.getConstantSupportedFileTypes_Videos())
                {
                    if (fileName.getName().toLowerCase().endsWith(fileEnding)) { isVideo = true; }
                }

                if (isVideo)
                {
                    this.log.printLog("special (truncated video): checking if the video is already there ... ");
                    Helper.pause(this.config.getConstantRetryWait() * 6);
                    JSONObject jobj_imageList = this.smugmug_images_get(albumID);
                    this.printJSONObject(jobj_imageList);
                }
                */


            	this.log.printLog(Helper.getCurrentTimeString() + " retrying (file was truncated) ... ");
                Helper.pause(this.config.getConstantRetryWait());
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	                  (this.getJSONValue(jobj, "method").equals(methodName)) &&
	                  (((String)this.getJSONValue(jobj, "message")).startsWith("system error (invalid album id)") ))
	        {
	          	this.log.printLog(Helper.getCurrentTimeString() + " retrying (invalid album id) ... ");
	          	Helper.pause(this.config.getConstantRetryWait());

	          	//note: this error seems not to go away, even through repetition ... maybe a relogin helps???
	        }
	        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
	                  (this.getJSONValue(jobj, "method").equals(methodName)) &&
	                  (this.getJSONValue(jobj, "message").equals("jSmugmugBackup internal (video seems to having been uploaded)") ))
	        {
                long uploadTime = (new Date()).getTime() - startTime;
	            double uploadSpeed = 0.0;
	            //avoid division by zero
	            if (uploadTime != 0) { uploadSpeed = ((double)fileName.length() / 1024.0) / ((double)uploadTime / 1000.0); }

	            // for statistics
	        	this.transferedBytes += fileName.length();
	            double filesizeMB = ((double)fileName.length() / (1024.0 * 1024.0));

	            DecimalFormat df = new DecimalFormat("0.0");
	            this.log.printLog("recovered from exception - ok (" + df.format(filesizeMB) + "mb@" + df.format(uploadSpeed) + "kb/s)");
	        	//this.log.printLogLine("ok");
	        	return jobj;
	        }
	        else
	        {
	        	
	        	this.log.printLog(Helper.getCurrentTimeString() + " retrying (wrong bytecount???) ... ");
	        	this.printJSONObject(jobj); //temporary
	        }
		} while (true); //hopefully, this will have an end ... sooner or later ...
        
        //return null;
	}
	
	

	//======================== private - helper ==============================
	
	private void printJSONObject(JSONObject jobj)
	{
		this.log.printLogLine("DEBUG: printing JSONObject ...");
		this.log.printLogLine("DEBUG: jobj=" + jobj);
		
		this.printJSONObject(jobj, "");
	}

	private void printJSONObject(JSONObject jobj, String indent)
	{
		for (int i = 0; i < jobj.keySet().size(); i++)
		{
			String key = (String) jobj.keySet().toArray()[i];
			Object value = jobj.get(jobj.keySet().toArray()[i]);
			
			if (value instanceof JSONObject)
			{
				this.log.printLogLine("DEBUG:" + indent + key + ": ");
				this.printJSONObject((JSONObject)value, indent + "   ");
			}
			else if (value instanceof JSONArray)
			{
				this.log.printLogLine("DEBUG:" + indent + key + " (Array)");
				JSONArray array = (JSONArray)value;
				for (int j = 0; j < array.size(); j++)
				{
					Object o = array.get(j);
					this.printJSONObject((JSONObject)o, indent + key + "[" + j + "].");
				}
			}
			else this.log.printLogLine("DEBUG:" + indent + key + ": " + value);
			
			//System.out.println(indent + jobj.get(jobj.keySet().toArray()[i]));
			
		}
	}
	
	private Object getJSONValue(JSONObject jobj, String identifier)
	{
		//System.out.println("getJSONValue(jobj, " + identifier + ")");
		
		for (int i = 0; i < jobj.keySet().size(); i++)
		{
			String key = (String) jobj.keySet().toArray()[i];
			Object value = jobj.get(jobj.keySet().toArray()[i]);
			
			if ( identifier.equals(key) ) { return value;  }
			else if (value instanceof JSONArray)
			{
				if ( (identifier.startsWith(key + "[")) && (identifier.contains(".")) )
				{
					//extract index
					int index = Integer.parseInt( identifier.substring(key.length()+1, identifier.indexOf("]")) );
					//System.out.println("identifier=" + identifier);
					//System.out.println("key=" + key);
					//System.out.println("index=" +  identifier.substring(key.length()+1, identifier.indexOf("]")));
					//System.out.println("new identifier=" + identifier.substring(identifier.indexOf(".")+1));
					JSONArray array = (JSONArray)value;
					if ( (index >= 0) && (index < array.size()) ) return this.getJSONValue((JSONObject)array.get(index), identifier.substring(identifier.indexOf(".")+1));
					else return null;
				}
				else if (identifier.startsWith(key + "["))					
				{
					//stop recursing here, since there is not "."
					//ugly hack, maybe ther's a better solution ...
					int index = Integer.parseInt( identifier.substring(key.length()+1, identifier.indexOf("]")) );
					JSONArray array = (JSONArray)value;
					if ( (index >= 0) && (index < array.size()) ) return array.get(index);
					else return null;
				}
			}
			else
			{
				if ( identifier.startsWith(key + ".") )
				{
                    //this.log.printLogLine("key: " + key);
                    //this.log.printLogLine("value: " + value);
                    //this.log.printLogLine("identifier: " + identifier);
					return this.getJSONValue((JSONObject)value, identifier.substring(identifier.indexOf(".")+1));
				}
			}
		}
		
		return null;
	}


    private synchronized void cacheCleanup(int albumID)
    {
        if (this.config.getPersistentCacheAccountInfo())
        {
            /*
            SmugmugLocalAlbumCache albumCache = null;

            //hack: we don't have to consider anonymous login here (hence another cache filename), because
            //      anonymous login does not allow changing anything in the account, and thus there no
            //      need for a cache cleanup
            albumCache = new SmugmugLocalAlbumCache(login_userID.toString());
            albumCache.loadCacheFromDisk();
            */

            //checking if cache has already been initialized
            if (SmugmugConnectorNG.albumCache == null) { this.log.printLogLine("ERROR: album cache has not been initialized"); System.exit(1); }

            if (SmugmugConnectorNG.albumCache.exists(albumID))
            {
                SmugmugConnectorNG.albumCache.removeAlbum(albumID);
                //this.log.printLogLine(Helper.getCurrentTimeString() + " removed album (id=" + albumID + ") from cache");

                SmugmugConnectorNG.albumCache.saveCacheToDisk();
            }
            
        }
    }

    private IAlbumMonthlyStatistics getAlbumStatistics(int albumID)
    {
        //only download statistics once
        if (this.statisticsRuntimeCache == null)
        {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            this.statisticsRuntimeCache = this.getStatistics(month, year);
        }

        for (IAlbumMonthlyStatistics stat : this.statisticsRuntimeCache)
        {
            if (stat.getAlbumID() == albumID)
            {
                //this.log.printLogLine("DEBUG: SmugmugConnectorNG.getAlbumStatistics(" + albumID + "): returning statistics!");
                return stat;
            }
        }

        //this.log.printLogLine("DEBUG: SmugmugConnectorNG.getAlbumStatistics(" + albumID + "): returning empty statistics");
        return new AlbumMonthlyStatistics(); //return empty statistics ... not the best solution, but should work for the moment
    }


    private Vector<IAlbumMonthlyStatistics> getStatistics(int month, int year)
    {
        //this.log.printLogLine("DEBUG: Statistics stub (SmugmugConnector)");

        Vector<IAlbumMonthlyStatistics> result = new Vector<IAlbumMonthlyStatistics>();

        JSONObject jsonAlbumStatisticsArray = this.smugmug_users_getTransferStats(month, year);
        //this.printJSONObject(jsonAlbumStatisticsArray);

		int albumIndex = 0;
		JSONObject jsonAlbumStatistics = (JSONObject)this.getJSONValue(jsonAlbumStatisticsArray, "Albums[" + albumIndex + "]");
		while (jsonAlbumStatistics != null)
		{
            //Integers
            Number albumID   = (Number)this.getJSONValue(jsonAlbumStatistics, "id");
            Number bytes     = (Number)this.getJSONValue(jsonAlbumStatistics, "Bytes");
            Number thumb     = (Number)this.getJSONValue(jsonAlbumStatistics, "Thumb");
            Number tiny      = (Number)this.getJSONValue(jsonAlbumStatistics, "Tiny");
            Number medium    = (Number)this.getJSONValue(jsonAlbumStatistics, "Medium");
            Number large     = (Number)this.getJSONValue(jsonAlbumStatistics, "Large");
            Number xLarge    = (Number)this.getJSONValue(jsonAlbumStatistics, "XLarge");
            Number x2Large   = (Number)this.getJSONValue(jsonAlbumStatistics, "X2Large");
            Number x3Large   = (Number)this.getJSONValue(jsonAlbumStatistics, "X3Large");

            //Floats
            Number original   = (Number)this.getJSONValue(jsonAlbumStatistics, "Original");
            Number video320   = (Number)this.getJSONValue(jsonAlbumStatistics, "Video320");
            Number video640   = (Number)this.getJSONValue(jsonAlbumStatistics, "Video640");
            Number video960   = (Number)this.getJSONValue(jsonAlbumStatistics, "Video960");
            Number video1280  = (Number)this.getJSONValue(jsonAlbumStatistics, "Video1280");

            IAlbumMonthlyStatistics albumStat = new AlbumMonthlyStatistics(month, year, albumID.intValue(), bytes.intValue(), thumb.intValue(), tiny.intValue(), medium.intValue(),
                                                             large.intValue(), xLarge.intValue(), x2Large.intValue(), x3Large.intValue(),
                                                             original.floatValue(), video320.floatValue(), video640.floatValue(), video960.floatValue(), video1280.floatValue());

            result.add(albumStat);

            
            albumIndex++;
            jsonAlbumStatistics = (JSONObject)this.getJSONValue(jsonAlbumStatisticsArray, "Albums[" + albumIndex + "]");
        }



        return result;
        /*
            smugmug.users.getTransferStats

            Gets the transfer statistics for the logged-in user during the given Month and Year. SmugMug only keeps the last few months of traffic on file, so requesting farther back then 2 months may not return valid results.

            A float is provided for Original and video sizes because it's possible to watch only a portion of a video.

            If Heavy is set to "1", transfer statistics for each image in each album will be returned as well.
            Arguments

                * string SessionID
                * integer Month
                * integer Year
                * boolean Heavy (optional)

            Result

                standard response

                * array Albums
                      o struct Album
                            + integer id
                            + integer Bytes
                            + integer Tiny
                            + integer Thumb
                            + integer Small
                            + integer Medium
                            + integer Large
                            + integer XLarge
                            + integer X2Large
                            + integer X3Large
                            + float Original
                            + float Video320
                            + float Video640
                            + float Video960
                            + float Video1280

                heavy response

                * array Albums
                      o struct Album
                            + integer id
                            + integer Bytes
                            + integer Tiny
                            + integer Thumb
                            + integer Small
                            + integer Medium
                            + integer Large
                            + integer XLarge
                            + integer X2Large
                            + integer X3Large
                            + float Original
                            + float Video320
                            + float Video640
                            + float Video960
                            + float Video1280
                                  # array Images
                                        * struct Image
                                              o integer id
                                              o integer Bytes
                                              o integer Tiny
                                              o integer Thumb
                                              o integer Small
                                              o integer Medium
                                              o integer Large
                                              o integer XLarge
                                              o integer X2Large
                                              o integer X3Large
                                              o float Original

            Fault Codes

                * 4 - "invalid user (message)"
         */

    }


}
