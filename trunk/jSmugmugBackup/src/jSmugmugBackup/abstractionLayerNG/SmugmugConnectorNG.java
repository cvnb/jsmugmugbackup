/*
 * Created on Oct 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.abstractionLayerNG;

import jSmugmugBackup.abstractionLayerNG.data.*;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SmugmugConnectorNG implements ISmugmugConnectorNG
{
	private String login_sessionID = null;
	private Number login_userID = null;
	private String login_nickname = null;
	private String login_passwordHash = null;
	
	public SmugmugConnectorNG(boolean heavy)
	{
		
	}

	public void login(String userEmail, String password)
	{
		this.smugmug_login_login(userEmail, password);
	}

	public void logout()
	{
		this.smugmug_logout_logout();
	}

	public Vector<ICategory> getTree()
	{
		Vector<ICategory> categoryList = new Vector<ICategory>();
		
		JSONObject tree = this.smugmug_users_getTree();
		//this.printJSONObject(tree);
		
		//iterate over categories
		int categoryIndex = 0;
		JSONObject category = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		while (category != null)
		{
			Number categoryID = (Number)this.getJSONValue(category, "id");
			String categoryName = (String)this.getJSONValue(category, "Name");
			System.out.println("categoryIndex=" + categoryIndex + ": id=" + categoryID.intValue() + ", name=" + categoryName);

			//iterate over subcategories
			int subcategoryIndex = 0;
			JSONObject subcategory = (JSONObject)this.getJSONValue(category, "SubCategories[" + subcategoryIndex + "]");
			while (subcategory != null)
			{
				Number subcategoryID = (Number)this.getJSONValue(subcategory, "id");
				String subcategoryName = (String)this.getJSONValue(subcategory, "Name");
				System.out.println("   subcategoryIndex=" + subcategoryIndex + ": id=" + subcategoryID.intValue() + ", name=" + subcategoryName);
				
				//iterate over albums (with subcategories)
				int albumIndex = 0;
				JSONObject album = (JSONObject)this.getJSONValue(subcategory, "Albums[" + albumIndex + "]");
				while (album != null)
				{
					Number albumID = (Number)this.getJSONValue(album, "id");
					String albumName = (String)this.getJSONValue(album, "Title");
					System.out.println("      albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);
					
					//iterate over images
					JSONObject images = (JSONObject)this.smugmug_images_get(albumID.intValue());
					int imageIndex = 0;
					JSONObject image = (JSONObject)this.getJSONValue(images, "Images[" + imageIndex + "]");
					while (image != null)
					{
						Number imageID = (Number)this.getJSONValue(image, "id");
						String imageKey = (String)this.getJSONValue(image, "Key");
						System.out.println("         imageIndex=" + imageIndex + ": id=" + imageID.intValue() + ", key=" + imageKey);
						
						imageIndex++;
						image = (JSONObject)this.getJSONValue(images, "Images[" + imageIndex + "]");
					}				
					
					albumIndex++;
					album = (JSONObject)this.getJSONValue(subcategory, "Albums[" + albumIndex + "]");
				}

				
				subcategoryIndex++;
				subcategory = (JSONObject)this.getJSONValue(category, "SubCategories[" + subcategoryIndex + "]");

			}

			
			//iterate over albums (without subcategories)
			int albumIndex = 0;
			JSONObject album = (JSONObject)this.getJSONValue(category, "Albums[" + albumIndex + "]");
			while (album != null)
			{
				Number albumID = (Number)this.getJSONValue(album, "id");
				String albumName = (String)this.getJSONValue(album, "Title");
				System.out.println("   albumIndex=" + albumIndex + ": id=" + albumID.intValue() + ", name=" + albumName);
				
				//iterate over images
				JSONObject images = (JSONObject)this.smugmug_images_get(albumID.intValue());
				int imageIndex = 0;
				JSONObject image = (JSONObject)this.getJSONValue(images, "Images[" + imageIndex + "]");
				while (image != null)
				{
					Number imageID = (Number)this.getJSONValue(image, "id");
					String imageKey = (String)this.getJSONValue(image, "Key");
					System.out.println("      imageIndex=" + imageIndex + ": id=" + imageID.intValue() + ", key=" + imageKey);
					
					imageIndex++;
					image = (JSONObject)this.getJSONValue(images, "Images[" + imageIndex + "]");
				}	
				
				albumIndex++;
				album = (JSONObject)this.getJSONValue(category, "Albums[" + albumIndex + "]");
			}
			
			
			categoryIndex++;
			category = (JSONObject)this.getJSONValue(tree, "Categories[" + categoryIndex + "]");
		}
		
		return categoryList;
	}

	public void getImages()
	{
		this.smugmug_images_get(6197234);
	}

	public void uploadFile(int albumID, File file) {
		// TODO Auto-generated method stub
		
	}
	
	public void downloadFile(int imageID, File fileName) {
		// TODO Auto-generated method stub
		
	}

	public void verifyFile() {
		// TODO Auto-generated method stub
		
	}
	
	public void deleteFile() {
		// TODO Auto-generated method stub
		
	}
	
	
	//======================== private - smugmug =============================
	private void smugmug_login_login(String userEmail, String password)
	{
		System.out.print("smugmug.login.withPassword ... ");
		
		//build url
		String url = SmugmugConstantsNG.SmugmugServerURL + "?";
		url = url + "method=smugmug.login.withPassword&";
		url = url + "APIKey=EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb&";
		url = url + "EmailAddress=" + userEmail + "&";
		url = url + "Password=" + password + "&";

		
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

        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
        	 (this.getJSONValue(jobj, "method").equals("smugmug.login.withPassword")) )
        {
        	System.out.println("ok");
        	this.login_sessionID    = (String)this.getJSONValue(jobj, "Login.Session.id");
        	this.login_userID       = (Number)this.getJSONValue(jobj, "Login.User.id");
        	this.login_nickname     = (String)this.getJSONValue(jobj, "Login.Session.Nickname");
        	this.login_passwordHash = (String)this.getJSONValue(jobj, "Login.PasswordHash");
        }
        else { System.out.println("failed"); }
	}
	
	private void smugmug_logout_logout()
	{
		System.out.print("smugmug.logout ...");
		
		//build url
		String url = SmugmugConstantsNG.SmugmugServerURL + "?";
		url = url + "method=smugmug.logout&";
		url = url + "SessionID=" + this.login_sessionID + "&";
		
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
        
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.logout")) )
        {        	
           	System.out.println("ok");
           	this.login_sessionID    = null;
           	this.login_userID       = null;
           	this.login_nickname     = null;
           	this.login_passwordHash = null;
        }
        else { System.out.println("failed"); }
	}
	
	private JSONObject smugmug_users_getTree()
	{
		System.out.print("smugmug.users.getTree ... ");
		
		//build url
		String url = SmugmugConstantsNG.SmugmugServerURL + "?";
		url = url + "method=smugmug.users.getTree&";
		url = url + "SessionID=" + this.login_sessionID + "&";
		//url = url + "NickName=" + this.login_nickname + "&"; //optional
		url = url + "Heavy=false&"; //optional
		//url = url + "SitePassword=????&"; //optional
		
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
        //System.out.println(jobj);
        //this.printJSONObject(jobj);
        //System.out.println("album: " + this.getJSONValue(jobj, "Categories[15].Albums[1].Title"));
        
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.users.getTree")) )
        {        	
           	System.out.println("ok");
           	return jobj;
        }
        else { System.out.println("failed"); }
        
        return null;
	}
	
	private JSONObject smugmug_images_get(int albumID)
	{
		System.out.print("smugmug.images.get ... ");
		
		//build url
		String url = SmugmugConstantsNG.SmugmugServerURL + "?";
		url = url + "method=smugmug.images.get&";
		url = url + "SessionID=" + this.login_sessionID + "&";
		url = url + "AlbumID=" + albumID + "&";
		url = url + "Heavy=false&"; //optional
		//url = url + "Password=????&"; //optional
		//url = url + "SitePassword=????&"; //optional
		//url = url + "AlbumKey=" + albumKey + "&"; //seems to be optional, but is not documented
		
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
        //System.out.println(jobj);
        //this.printJSONObject(jobj);
        //System.out.println("album: " + this.getJSONValue(jobj, "Categories[15].Albums[1].Title"));
        
      
        if ( (this.getJSONValue(jobj, "stat").equals("ok")) &&
           	 (this.getJSONValue(jobj, "method").equals("smugmug.images.get")) )
        {        	
           	System.out.println("ok");
           	return jobj;
        }
        else if ( (this.getJSONValue(jobj, "stat").equals("fail")) &&
        		  (this.getJSONValue(jobj, "code").equals(new Long(15))) )
        {
        	System.out.println("empty");
        	return jobj;
        }
        else { System.out.println("failed"); }
        
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


}
