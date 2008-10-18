/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.smugmugLayer;

import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.text.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class SmugmugConnectorNG implements ISmugmugConnectorNG
{
	private Logger log = null;
	
	// hack: there should be a better way to handle multiple instances of
	//       the connector without having to ask for username and password again
	private static String login_sessionID = null;
	private static Number login_userID = null;
	private static String login_nickname = null;
	private static String login_passwordHash = null;
	
	private long transferedBytes = 0;;
	
	
	public SmugmugConnectorNG()
	{
		this.log = Logger.getInstance();
	}

	public void login(String userEmail, String password)
	{
		this.smugmug_login_withPassword(userEmail, password);
	}

	public void relogin()
	{
		this.smugmug_login_withHash();
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

	public Vector<ICategory> getTree()
	{
		this.log.printLog(this.getTimeString() + " downloading account data ... ");
		
		Vector<ICategory> categoryList = new Vector<ICategory>();
		
		JSONObject tree = this.smugmug_users_getTree();
		//this.printJSONObject(tree);
		
		//iterate over categories
		int categoryIndex = 0;
		JSONObject jsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		while (jsonCategory != null)
		{
			Number categoryID = (Number)this.getJSONValue(jsonCategory, "id");
			String categoryName = (String)this.getJSONValue(jsonCategory, "Name");
			//System.out.println("categoryIndex=" + categoryIndex + ": id=" + categoryID.intValue() + ", name=" + categoryName);
			ICategory category = new Category(categoryID.intValue(), categoryName);
			categoryList.add( category );

			
			//iterate over subcategories
			int subcategoryIndex = 0;
			JSONObject jsonSubcategory = (JSONObject)this.getJSONValue(jsonCategory, "SubCategories[" + subcategoryIndex + "]");
			while (jsonSubcategory != null)
			{
				Number subcategoryID = (Number)this.getJSONValue(jsonSubcategory, "id");
				String subcategoryName = (String)this.getJSONValue(jsonSubcategory, "Name");
				//System.out.println("   subcategoryIndex=" + subcategoryIndex + ": id=" + subcategoryID.intValue() + ", name=" + subcategoryName);
				ISubcategory subcategory = new Subcategory(subcategoryID.intValue(), subcategoryName);
				category.addSubcategory(subcategory);
				
				//iterate over albums (with subcategories)
				int albumIndex = 0;
				JSONObject jsonAlbum = (JSONObject)this.getJSONValue(jsonSubcategory, "Albums[" + albumIndex + "]");
				while (jsonAlbum != null)
				{
					Number albumID = (Number)this.getJSONValue(jsonAlbum, "id");
					String albumName = (String)this.getJSONValue(jsonAlbum, "Title");
					//System.out.println("      albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);
					IAlbum album = new Album(albumID.intValue(), albumName);
					subcategory.addAlbum(album);
					
					//iterate over images
					JSONObject jsonImages = (JSONObject)this.smugmug_images_get(albumID.intValue());
					int imageIndex = 0;
					JSONObject jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
					while (jsonImage != null)
					{
						Number imageID          = (Number)this.getJSONValue(jsonImage, "id");
						//String imageKey       = (String)this.getJSONValue(jsonImage, "Key");
						String imageName        = (String)this.getJSONValue(jsonImage, "FileName");
						String imageCaption     = (String)this.getJSONValue(jsonImage, "Caption");
						//String imageKeywords    = (String)this.getJSONValue(jsonImage, "Keywords"); //doesn't work yet, probably a bug in getJSONValue
						String imageFormat      = (String)this.getJSONValue(jsonImage, "Format");
						Number imageHeight      = (Number)this.getJSONValue(jsonImage, "Height");
						Number imageWidth       = (Number)this.getJSONValue(jsonImage, "Width");
						Number imageSize        = (Number)this.getJSONValue(jsonImage, "Size");
						String imageMD5         = (String)this.getJSONValue(jsonImage, "MD5Sum");
						String imageOriginalURL = (String)this.getJSONValue(jsonImage, "OriginalURL");
						//IImage image = new Image(imageID.intValue(), "<image name>"); //todo: get real image name, not just the key
						IImage image = new Image(imageID.intValue(), imageName, imageCaption, "<imageKeywords ... deactivated for the moment>", imageFormat, imageHeight.intValue(), imageWidth.intValue(), imageSize.longValue(), imageMD5, imageOriginalURL);
						album.addImage(image);
						
						imageIndex++;
						jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
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
				Number albumID = (Number)this.getJSONValue(jsonAlbum, "id");
				String albumName = (String)this.getJSONValue(jsonAlbum, "Title");
				//System.out.println("   albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);
				IAlbum album = new Album(albumID.intValue(), albumName);
				category.addAlbum(album);
				
				//iterate over images
				JSONObject jsonImages = (JSONObject)this.smugmug_images_get(albumID.intValue());
				int imageIndex = 0;
				JSONObject jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
				while (jsonImage != null)
				{
					Number imageID          = (Number)this.getJSONValue(jsonImage, "id");
					//String imageKey       = (String)this.getJSONValue(jsonImage, "Key");
					String imageName        = (String)this.getJSONValue(jsonImage, "FileName");
					String imageCaption     = (String)this.getJSONValue(jsonImage, "Caption");
					//String imageKeywords    = (String)this.getJSONValue(jsonImage, "Keywords"); //doesn't work yet, probably a bug in getJSONValue
					String imageFormat      = (String)this.getJSONValue(jsonImage, "Format");
					Number imageHeight      = (Number)this.getJSONValue(jsonImage, "Height");
					Number imageWidth       = (Number)this.getJSONValue(jsonImage, "Width");
					Number imageSize        = (Number)this.getJSONValue(jsonImage, "Size");
					String imageMD5         = (String)this.getJSONValue(jsonImage, "MD5Sum");
					String imageOriginalURL = (String)this.getJSONValue(jsonImage, "OriginalURL");
					//IImage image = new Image(imageID.intValue(), "<image name>"); //todo: get real image name, not just the key
					IImage image = new Image(imageID.intValue(), imageName, imageCaption, "<imageKeywords ... deactivated for the moment>", imageFormat, imageHeight.intValue(), imageWidth.intValue(), imageSize.longValue(), imageMD5, imageOriginalURL);
					album.addImage(image);
					
					imageIndex++;
					jsonImage = (JSONObject)this.getJSONValue(jsonImages, "Images[" + imageIndex + "]");
				}
				
				albumIndex++;
				jsonAlbum = (JSONObject)this.getJSONValue(jsonCategory, "Albums[" + albumIndex + "]");
			}
			
			
			categoryIndex++;
			jsonCategory = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		}
		
		this.log.printLogLine("ok");
		
		return categoryList;
	}

	public void getImages(int albumID)
	{
		JSONObject jobj = this.smugmug_images_get(albumID);
		this.printJSONObject(jobj);
	}
	
	public Hashtable<String, String> getImageInfo(int imageID)
	{
		Hashtable<String, String> result = new Hashtable<String, String>();
		
		JSONObject jobj = this.smugmug_images_getInfo(imageID);
		//this.printJSONObject(jobj);
		
		result.put("AlbumID",   ((Long)this.getJSONValue(jobj, "Image.Album.id")).toString());
		result.put("ImageID",   ((Long)this.getJSONValue(jobj, "Image.id")).toString());
		result.put("ImageName", (String)this.getJSONValue(jobj, "Image.FileName"));
		
		return result;
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

	public int createAlbum(int categoryID, int subCategoryID, String name)
	{
		JSONObject jobj = this.smugmug_albums_create(name, categoryID, subCategoryID);
		//this.printJSONObject(jobj);
		int albumID = ((Number)this.getJSONValue(jobj, "Album.id")).intValue();
		
		return albumID;
	}
	
	public int uploadFile(int albumID, File file)
	{
        // check if file is smaller than 512 MB and
        if (file.length() > (Constants.UploadFileSizeLimit))
        {
        	this.log.printLogLine("  WARNING: " + file.getAbsolutePath() + " filesize greater than 512 MB is not supported ... skipping");
        	return 0;
        }
        
        //check if someone has manually set the ignore tag
        if ( (new File(file.getAbsolutePath() + Constants.UploadIgnoreFilePostfix)).exists() )
        {
        	this.log.printLogLine("  WARNING: " + file.getAbsolutePath() + " - the ignore tag was set ... skipping");
        	return 0;
        }

    	JSONObject jobj = this.smugmug_images_upload(albumID, file);
    	//this.printJSONObject(jobj);
    	Object obj = this.getJSONValue(jobj, "Image.id");
    	if (obj != null) { return ((Number)obj).intValue(); }
    	else return 0;
	}
		
	public void downloadFile(int imageID, File fileName)
	{		
		JSONObject jobj = this.smugmug_images_getURLs(imageID);
		String imageURL = (String)this.getJSONValue(jobj, "Image.OriginalURL");
    	//System.out.println("url = " + imageURL);
		
		this.downloadFile(imageURL, fileName);		
	}
	
	public void downloadFile(String imageURL, File fileName)
	{
		this.log.printLog(this.getTimeString() + " downloading: " + fileName.getAbsolutePath() + " ... ");
		
		
		//write url to file
		try
		{
			URL url	= new URL(imageURL);
			FileOutputStream out = new FileOutputStream(fileName);
			URLConnection conn = url.openConnection();
			InputStream  in = conn.getInputStream();
			
			
			long startTime = (new Date()).getTime();
			
			
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
			
			out.close();
			
			
            long downloadTime = (new Date()).getTime() - startTime;
            double downloadSpeed = 0.0;
            //avoid division by zero
            if (downloadTime != 0) { downloadSpeed = ((double)fileName.length() / 1024.0) / ((double)downloadTime / 1000.0); }
            
            // for statistics
        	this.transferedBytes += fileName.length();
            
            DecimalFormat df = new DecimalFormat("0.0");                            
            this.log.printLogLine("ok (" + df.format(downloadSpeed) + " kb/sec)");
			//this.log.printLogLine("ok");
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e)           { e.printStackTrace(); }
	}

//	public void verifyFile() {
//		// TODO Auto-generated method stub
//		
//	}
	
	public void deleteFile() {
		// TODO Auto-generated method stub
		
	}
	
	public long getTransferedBytes() { return this.transferedBytes; }

	
	//======================== private - smugmug =============================
	
	private JSONObject smugmugJSONRequest(String url)
	{
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url); 

        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = null;
		try { responseBody = httpclient.execute(httpget, responseHandler); }
		catch (ClientProtocolException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
        
        Object obj = JSONValue.parse(responseBody);
        JSONObject jobj = (JSONObject)obj;
        //this.printJSONObject(jobj);
        
        return jobj;
	}
	
	private void smugmug_login_withPassword(String userEmail, String password)
	{
		this.log.printLog(this.getTimeString() + " logging in ... ");
		//this.log.printLog("smugmug.login.withPassword ... ");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=smugmug.login.withPassword&";
		url = url + "APIKey=EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb&";
		url = url + "EmailAddress=" + userEmail + "&";
		url = url + "Password=" + password + "&";

		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);

		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
        	 (this.getJSONValue(jobj, "method").equals("smugmug.login.withPassword")) )
        {
        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
        	SmugmugConnectorNG.login_userID       = (Number)this.getJSONValue(jobj, "Login.User.id");
        	SmugmugConnectorNG.login_nickname     = (String)this.getJSONValue(jobj, "Login.Session.Nickname");
        	SmugmugConnectorNG.login_passwordHash = (String)this.getJSONValue(jobj, "Login.PasswordHash");
        	this.log.printLogLine("ok");
        }
        else { this.log.printLogLine("failed"); }
	}
		
	private void smugmug_login_withHash()
	{
		//this.log.printLog(this.getTimeString() + " relogin ... ");
		
		//this.log.printLog("smugmug.login.withHash ... ");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=smugmug.login.withHash&";
		url = url + "APIKey=EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb&";
		url = url + "UserID=" + SmugmugConnectorNG.login_userID + "&";
		url = url + "PasswordHash=" + SmugmugConnectorNG.login_passwordHash + "&";

		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);

		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
        	 (this.getJSONValue(jobj, "method").equals("smugmug.login.withHash")) )
        {
        	//this.log.printLogLine("ok");
        	SmugmugConnectorNG.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
        }
        else
        {
        	this.log.printLog(this.getTimeString() + " smugmug.login.withHash ... failed");
        	//this.log.printLogLine("failed");
        }
	}
		
	private void smugmug_logout_logout()
	{
		this.log.printLog(this.getTimeString() + " logging out ... ");
		//this.log.printLog("smugmug.logout ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=smugmug.logout&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		
		
		JSONObject jobj = this.smugmugJSONRequest(url);
		
		
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
		
	private JSONObject smugmug_users_getTree()
	{
		//this.log.printLog("smugmug.users.getTree ... ");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=smugmug.users.getTree&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		//url = url + "NickName=" + this.login_nickname + "&"; //optional
		url = url + "Heavy=0&"; //optional
		//url = url + "SitePassword=????&"; //optional
		
		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);
        
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.users.getTree")) )
        {        	
        	//this.log.printLogLine("ok");
           	return jobj;
        }
        else
        {
        	//this.log.printLogLine("failed");
        	this.log.printLogLine(this.getTimeString() + " smugmug.users.getTree ... failed");
        }
        
        return null;
	}
		
	private JSONObject smugmug_images_get(int albumID)
	{
		String methodName = "smugmug.images.get";
		//this.log.printLog("smugmug.images.get ... ");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&";
		url = url + "Heavy=1&"; //optional
		//url = url + "Password=????&"; //optional
		//url = url + "SitePassword=????&"; //optional
		//url = url + "AlbumKey=" + albumKey + "&"; //seems to be optional, but is not documented
		
		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);
      
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.images.get")) )
        {        	
        	//this.log.printLogLine("ok");
           	return jobj;
        }
        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
        		  (this.getJSONValue(jobj, "code").equals(new Long(15))) )
        {
        	//this.log.printLogLine("empty");
        	return jobj;
        }
        else
        {
        	//this.log.printLogLine("failed");
        	this.log.printLogLine(this.getTimeString() + " smugmug.images.get ... failed");
        }
        
        return null;
	}
		
	private JSONObject smugmug_categories_create(String name)
	{
		this.log.printLog(this.getTimeString() + " creating category ... ");
		
		String methodName = "smugmug.categories.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Name=" + name + "&";
				
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "Category.id") + ")");
        	return jobj;
        }
        else { this.log.printLogLine("failed"); }
        
        return null;
    }

	private JSONObject smugmug_subcategories_create(String name, int categoryID)
	{
		this.log.printLog(this.getTimeString() + " creating subcategory ... ");
		
		String methodName = "smugmug.subcategories.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Name=" + name + "&";
		url = url + "CategoryID=" + categoryID + "&";
		
		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (id=" + this.getJSONValue(jobj, "SubCategory.id") + ")");
           	return jobj;
        }
        else { this.log.printLogLine("failed"); }
        
        return null;
	}

	private JSONObject smugmug_albums_create(String title, int categoryID, int subCategoryID)
	{
		this.log.printLog(this.getTimeString() + " creating album ... ");
		
		String methodName = "smugmug.albums.create";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "Title=" + title + "&";
		url = url + "CategoryID=" + categoryID + "&";
		
		//essentials
		//url = url + "AlbumTemplateID=" + 0 + "&"; //integer, optional, default: 0
		url = url + "SubCategoryID=" + subCategoryID + "&"; //integer, optional, default: 0		
		//url = url + "Description=&"; //string, optional
		//url = url + "Keywords=&"; //string, optional
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
		
		
		JSONObject jobj = this.smugmugJSONRequest(url);
		//this.printJSONObject(jobj);
		//this.log.printLogLine("url: " + url);
		
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
        	this.log.printLogLine("ok (" + this.getJSONValue(jobj, "Album.id") + ")");
           	return jobj;
        }
        else { this.log.printLogLine("failed"); }
        
        return null;
	}

	private JSONObject smugmug_images_getURLs(int imageID)
	{
		String methodName = "smugmug.images.getURLs";
		//this.log.printLog(methodName + " ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&"; //integer
		//url = url + "TemplateID=&"; //string, optional (specifies which Style to build the AlbumURL with), default: 3
		//url = url + "Password=&"; //string, optional
		//url = url + "SitePassword=&"; //string, optional
		//url = url + "ImageKey=&"; //string
		
		JSONObject jobj = this.smugmugJSONRequest(url);
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
        	this.log.printLogLine(this.getTimeString() + " " + methodName + " ... failed");
        }
        
        return null;
    }
	
	private JSONObject smugmug_images_getInfo(int imageID)
	{
		String methodName = "smugmug.images.getInfo";
		//this.log.printLog(methodName + " ...");
		
		//build url
		String url = Constants.SmugmugServerURL + "?";
		url = url + "method=" + methodName + "&";
		url = url + "SessionID=" + SmugmugConnectorNG.login_sessionID + "&";
		url = url + "ImageID=" + imageID + "&"; //integer
		//url = url + "Password=&"; //string, optional
		//url = url + "SitePassword=&"; //string, optional
		//url = url + "ImageKey=&"; //string
		
		JSONObject jobj = this.smugmugJSONRequest(url);
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
        	this.log.printLogLine(this.getTimeString() + " " + methodName + " ... failed");
        }
        
        return null;
    }
	
	private JSONObject smugmug_images_upload(int albumID, File fileName)
	{	
		this.log.printLog(this.getTimeString() + " uploading: " + fileName.getAbsolutePath() + " ... ");
		
		String methodName = "smugmug.images.upload";
		//System.out.print(methodName + " ...");
		
		//build url
		String url = "http://upload.smugmug.com/" + fileName.getName();
		
        HttpPut httpPut = new HttpPut(url);
        
        //add header
        //httpPut.addHeader("Content-Length", Long.toString(fileName.length()) );
        httpPut.addHeader("Content-MD5", this.computeMD5Hash(fileName) );
        httpPut.addHeader("X-Smug-SessionID", SmugmugConnectorNG.login_sessionID);
        httpPut.addHeader("X-Smug-Version", Constants.SmugmugAPIVersion);
        httpPut.addHeader("X-Smug-ResponseType", "JSON");
        httpPut.addHeader("X-Smug-AlbumID", Integer.toString(albumID) ); // required for uploading new photos, not for replacing existing ones
        //httpPut.addHeader("X-Smug-ImageID", ""); //required for replacing, not for uploading
        httpPut.addHeader("X-Smug-FileName", fileName.getName()); //optional
        //httpPut.addHeader("X-Smug-Caption", ""); //optional
        //httpPut.addHeader("X-Smug-Keywords", ""); //optional
        //httpPut.addHeader("X-Smug-Latitude", ""); //optional
        //httpPut.addHeader("X-Smug-Longitude", ""); //optional
        //httpPut.addHeader("X-Smug-Altitude", ""); //optional

        
        // see: http://www.iana.org/assignments/media-types/
        HttpEntity entity = new org.apache.http.entity.FileEntity(fileName, "image/jpeg");
        httpPut.setEntity(entity);
        
        long startTime = (new Date()).getTime();

        
        HttpClient httpclient = new DefaultHttpClient();        
        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = null;
		try { responseBody = httpclient.execute(httpPut, responseHandler); }
		catch (ClientProtocolException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
        
        Object obj = JSONValue.parse(responseBody);
        JSONObject jobj = (JSONObject)obj;

        
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
             (this.getJSONValue(jobj, "method").equals(methodName)) )
        {
            long uploadTime = (new Date()).getTime() - startTime;
            double uploadSpeed = 0.0;
            //avoid division by zero
            if (uploadTime != 0) { uploadSpeed = ((double)fileName.length() / 1024.0) / ((double)uploadTime / 1000.0); }
            
            // for statistics
        	this.transferedBytes += fileName.length();
            
            DecimalFormat df = new DecimalFormat("0.0");                            
            this.log.printLogLine("ok (" + df.format(uploadSpeed) + " kb/sec)");
        	//this.log.printLogLine("ok");
        	return jobj;
        }
        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
                  (this.getJSONValue(jobj, "method").equals(methodName)) &&
                  (this.getJSONValue(jobj, "message").equals("wrong format ()")))
        {
        	this.log.printLogLine("failed (wrong format)");
        	return jobj;
        }
        else
        {
        	this.log.printLogLine("failed");
        	this.log.printLogLine("response:");
        	this.log.printLogLine(responseBody);
        	this.printJSONObject(jobj);
        	System.exit(0); //should be removed later ...
        }
        
        return null;
	}
	
	
	//======================== private - helper ==============================
	
	private void printJSONObject(JSONObject jobj)
	{
		System.out.println("jobj=" + jobj);
		
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
				System.out.println(indent + key + ": ");
				this.printJSONObject((JSONObject)value, indent + "   ");
			}
			else if (value instanceof JSONArray)
			{
				System.out.println(indent + key + " (Array)");
				JSONArray array = (JSONArray)value;
				for (int j = 0; j < array.size(); j++)
				{
					Object o = array.get(j);
					this.printJSONObject((JSONObject)o, indent + key + "[" + j + "].");
				}
			}
			else System.out.println(indent + key + ": " + value);
			
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
				if ( identifier.startsWith(key) )
				{
					return this.getJSONValue((JSONObject)value, identifier.substring(identifier.indexOf(".")+1));
				}
			}
		}
		
		return null;
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
