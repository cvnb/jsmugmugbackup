package jSmugmugBackup.model;

import jSmugmugBackup.config.*;
import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.view.*;


import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;


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
    	this.log.printLogLine(LogLevelEnum.Message, 0, "finished. (execution time: " + Helper.getDurationTimeString(timeDiff) + ", transfered: " + df.format(transferedMB) + " mb, avg speed: " + df.format(transferSpeed) + " kb/sec)");
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

    	this.view.updateFileListing( this.accListing.getAccountTree(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords(), transferDialogResult.getAlbumPassword()) );
    }
    public void upload(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

    	//this.log.printLogLine("preparing upload of pics from: " + transferDialogResult.getDir());


		String category    = transferDialogResult.getCategoryName();
		String subcategory = transferDialogResult.getSubCategoryName();
		String album       = transferDialogResult.getAlbumName();
        String keywords    = transferDialogResult.getAlbumKeywords();

        File rootDir = new File(transferDialogResult.getDir());

        //check if someone has manually set the ignore tag ... this is probably needed only once
        //this.log.printLogLine("checking: " + directory.getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix());
        if ( (new File(rootDir.getAbsolutePath() + this.config.getConstantUploadIgnoreFilePostfix())).exists() )
        {
            this.log.printLogLine(LogLevelEnum.Warning, 0, "WARNING: " + rootDir.getAbsolutePath() + " - the ignore tag was set ... skipping this directory");
            return;
        }

        //this.log.printLogLine("DEBUG: " + category + "/" + subcategory + "/" + album);
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
			else { this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: expected a directory, not a file (" + rootDir + ")"); this.quitApplication(); }
		}
		else
		{
			this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: Model.upload: this case is yet unhandled"); this.quitApplication();
		}
    }
    public void download(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }

		//this.log.printLogLine("preparing to download files to: " + transferDialogResult.getDir());

        IRootElement smugmugRoot = this.accListing.getAccountTree(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords(), transferDialogResult.getAlbumPassword());

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
        if (selectedAlbums.size() == 0) { this.log.printLogLine(LogLevelEnum.Message, 0, "no matching album was found on your SmugMug Account"); }

        for (IAlbum a : selectedAlbums)
		{
			this.accListing.enqueueAlbumForDownload(a.getID(), null, transferDialogResult.getAlbumPassword(), transferDialogResult.getDir(), /*transferDialogResult.getMinResolution(),*/ transferDialogResult.getMaxResolution());
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
        int albumID;
        String albumKey;
        if (url.contains("/gallery/"))  // old style url's
        {
            String urlTail = url.substring(url.indexOf("/gallery/") + 9);
            String albumIDString = urlTail.substring(0, urlTail.indexOf("_"));
            albumID = Integer.parseInt(albumIDString);
            albumKey = urlTail.substring(urlTail.indexOf("_")+1, urlTail.indexOf("/"));
        }
        else
        {
            //remove slash at the end
            if ( url.charAt(url.length()-1) == '/' ) { url = url.substring(0, url.length()-2); }
            String urlTail = url.substring(url.lastIndexOf("/") + 1);
            String albumIDString = urlTail.substring(0, urlTail.indexOf("_"));
            albumID = Integer.parseInt(albumIDString);
            albumKey = urlTail.substring(urlTail.indexOf("_")+1, urlTail.indexOf("#"));
        }


        //this.log.printLogLine("DEBUG: albumID : " + albumID);
        //this.log.printLogLine("DEBUG: albumKey: " + albumKey);
        this.accListing.enqueueAlbumForDownload(albumID, albumKey, transferDialogResult.getAlbumPassword(), transferDialogResult.getDir(), /*transferDialogResult.getMinResolution(),*/ transferDialogResult.getMaxResolution());


    }
    public void verify(ITransferDialogResult transferDialogResult)
    {
        if (this.config.getConstantVerifyMD5ForVideos() == false) { this.log.printLogLine(LogLevelEnum.Warning, 0, "md5 sums for videos will not be checked, since they usually fail anyway"); }

        //this.log.printLogLine("preparing to verify files from: " + transferDialogResult.getDir());

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
        if (selectedAlbums.size() == 0) { this.log.printLogLine(LogLevelEnum.Message, 0, "no matching album was found on your SmugMug Account"); }

        
        // compute target base dir
        // convienience: cut off category, subcategory and album dir if they exist
        String targetBaseDir = transferDialogResult.getDir();
        //this.log.printLogLine(LogLevelEnum.Message, "targetBaseDir: " + targetBaseDir);

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
                        else { this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: could not find album category!"); return; }
                        targetAlbumDir = targetAlbumDir + categoryName + "/";
                    }

                    String subcategoryName = null;
                    if ( a.getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_SUBCATEGORY) ) { subcategoryName = a.getParent().getName() + "/"; }
                    else if ( a.getParent().getSmugmugType().equals(SmugmugTypeEnum.SMUGMUG_CATEGORY) ) { subcategoryName = ""; }
                    else { this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: could not find album subcategory!"); return; }
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

        if (transferDialogResult.getAlbumName() != null) { this.log.printLogLine(LogLevelEnum.Warning, 0, "WARNING: you specified an album name, which will be ignored! We're rearranging albums here, not images within albums!"); }
    	
        this.accListing.sort(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName());
    }
    public void autotag(ITransferDialogResult transferDialogResult)
    {
        if (transferDialogResult == null) { return; }
        this.accListing.autotag(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());
    }
    public void statistics(ITransferDialogResult transferDialogResult)
    {
        this.log.printLogLine(LogLevelEnum.Info, 0, "INFO: albums with zero transferd bytes will be omitted");


        if (transferDialogResult == null) { return; }
        Vector<IAlbum> albumList = this.accListing.statistics(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName());

        this.view.showStatistics(albumList);
    }
    public void osmlayer(ITransferDialogResult transferDialogResult)
    {
        Vector<IAlbum> albumList = this.accListing.getAccountAlbumList(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords());

        String dir = transferDialogResult.getDir();
        if (dir == null) { dir = "./"; }
        

        Hashtable<String, Vector<IImage>> contentGeotagsHashtable = new Hashtable<String, Vector<IImage>>();
        //this.log.printLogLine("DEBUG:----------------- geotags.txt -------------------------------------");
        //this.log.printLogLine("DEBUG:lat	lon	title	description	icon	iconSize	iconOffset");
        for (IAlbum a : albumList)
        {
            for (IImage i : a.getImageList())
            {
                if ( !((i.getLongitude() == null) && (i.getLatitude() == null) && (i.getAltitude() == null)) )
                {
                    //this.log.printLogLine("DEBUG:   " + a.getFullName() + "." + i.getName());
                    //this.log.printLogLine("DEBUG:      latitude : " + i.getLatitude());
                    //this.log.printLogLine("DEBUG:      longitude: " + i.getLongitude());
                    //this.log.printLogLine("DEBUG:      altitude : " + i.getAltitude());
                    //this.log.printLogLine("DEBUG:" + i.getLatitude() + "\t" +
                    //                                 i.getLongitude() + "\t" +
                    //                                 i.getName() + "\t" +
                    //                                 "geotags exported from smugmug using jSmugmugBackup<br><img src=\"" + i.getSmallURL() + "\" />" + "\t" +
                    //                                 "icon.png" + "\t" +
                    //                                 "24,24" + "\t" +
                    //                                 "0,-24");

                    //contentGeotagsTxt += i.getLatitude() + "\t" +
                    //                     i.getLongitude() + "\t" +
                    //                     i.getName() + "\t" +
                    //                     //"geotags exported from smugmug using jSmugmugBackup<br><img src=\"" + i.getSmallURL() + "\" />" + "\t" +
                    //                     //"<img src=\"" + i.getSmallURL() + "\" />" + "\t" +
                    //                     "<a href=" + i.getLargeURL() + " target=\"_blank\"><img src=\"" + i.getTinyURL() + "\" /></a><br><h6>[exported using jSmugmugBackup]</h6>" + "\t" +
                    //                     "icon.png" + "\t" +
                    //                     "24,24" + "\t" +
                    //                     "0,-24" + "\n";

                    //// check if coordinates already exist in table ... if yes, change them slightly
                    //String tableKey = i.getLatitude() + i.getLongitude();
                    //Vector<String> geotag = new Vector<String>();
                    //if ( !contentGeotagsHashtable.containsKey(tableKey) )
                    //{
                    //    geotag.add(i.getLatitude());
                    //    geotag.add(i.getLongitude());
                    //}
                    //else
                    //{
                    //    String latitude;
                    //    String longitude;
                    //    String newTableKey;
                    //    int count = 1;
                    //    do
                    //    {
                    //        //modifying coordinates slightly
                    //        long latitudeFraction = Long.parseLong( i.getLatitude().substring(i.getLatitude().lastIndexOf(".") + 1) );
                    //        latitudeFraction = latitudeFraction + count * 1111;
                    //        latitude = i.getLatitude().substring(0, i.getLatitude().lastIndexOf(".") + 1) + Long.toString(latitudeFraction);
                    //
                    //        long longitudeFraction = Long.parseLong( i.getLongitude().substring(i.getLongitude().lastIndexOf(".") + 1) );
                    //        longitudeFraction = longitudeFraction - count * 1111;
                    //        longitude = i.getLongitude().substring(0, i.getLongitude().lastIndexOf(".") + 1) + Long.toString(longitudeFraction);
                    //
                    //        count++;
                    //        newTableKey = latitude + longitude;
                    //    } while ( contentGeotagsHashtable.containsKey(newTableKey) );
                    //
                    //    geotag.add(latitude);
                    //    geotag.add(longitude);
                    //
                    //    this.log.printLogLine("DEBUG:      modLatitude : " + latitude);
                    //    this.log.printLogLine("DEBUG:      modLongitude: " + longitude);
                    //}
                    //geotag.add(i.getName());
                    //geotag.add(i.getLargeURL());
                    //geotag.add(i.getTinyURL());
                    //contentGeotagsHashtable.put(tableKey, geotag);

                    String locationKey = i.getLatitude().toString() + i.getLongitude().toString();
                    if ( !contentGeotagsHashtable.containsKey(locationKey) )
                    {
                        Vector<IImage> imageVector = new Vector<IImage>();
                        imageVector.add(i);
                        contentGeotagsHashtable.put(locationKey, imageVector);
                    }
                    else
                    {
                        Vector<IImage> imageVector = contentGeotagsHashtable.get(locationKey);
                        imageVector.add(i);
                    }

                }                
            }
        }
        //this.log.printLogLine("DEBUG:-------------------------------------------------------------------");

        String contentGeotagsTxt = "lat	lon	title	description	icon	iconSize	iconOffset\n";
        for (Vector<IImage> geotag : contentGeotagsHashtable.values())
        {
            contentGeotagsTxt += geotag.get(0).getLatitude() + "\t";
            contentGeotagsTxt += geotag.get(0).getLongitude() + "\t";
            
            contentGeotagsTxt += geotag.get(0).getName();
            int n = 1;
            while (n < geotag.size())
            {
                contentGeotagsTxt += " and " + geotag.get(n).getName();
                n++;
            }
            contentGeotagsTxt += "\t";

            int m = 0;
            while (m < geotag.size())
            {
                contentGeotagsTxt += "<a href=" + geotag.get(m).getLargeURL() + " target=\"_blank\"><img src=\"" + geotag.get(m).getTinyURL() + "\" /></a><br><h6>[exported using jSmugmugBackup]</h6>";
                m++;
            }
            contentGeotagsTxt += "\t";
            
            contentGeotagsTxt += "icon.png" + "\t";
            contentGeotagsTxt += "24,24" + "\t";
            contentGeotagsTxt += "0,-24" + "\n";
        }


        //write files
        try
        {
            FileWriter fstream_html = new FileWriter(dir + "index.html"); // Create file
            BufferedWriter out_html = new BufferedWriter(fstream_html);
            out_html.write(this.config.getConstantOSMbasicHtml());
            out_html.close(); //Close the output stream

            FileWriter fstream_geotags = new FileWriter(dir + "geotags.txt"); // Create file
            BufferedWriter out_geotags = new BufferedWriter(fstream_geotags);
            out_geotags.write(contentGeotagsTxt);
            out_geotags.close(); //Close the output stream
        }
        catch (Exception e)//Catch exception if any
        {
          System.err.println("Error: " + e.getMessage());
        }

        //copy icon file
        if (!Helper.copyFile(this.config.getConstantOsmIconFilename(), dir + "icon.png"))
        {
            this.log.printLogLine(LogLevelEnum.Warning, 0, "WARNING: the file " + this.config.getConstantOsmIconFilename() + " could not be copied to it's destination. You might have to adjust the generated geotags.txt manually." );
        }

        this.log.printLogLine(LogLevelEnum.Info, 0, "INFO: finished creating a layer for OpenStreetMap. The results can be found in " + dir);

    }
    public void kmllayer(ITransferDialogResult transferDialogResult)
    {
        Vector<IAlbum> albumList = this.accListing.getAccountAlbumList(transferDialogResult.getCategoryName(), transferDialogResult.getSubCategoryName(), transferDialogResult.getAlbumName(), transferDialogResult.getAlbumKeywords());

        ResolutionEnum maxResolution = transferDialogResult.getMaxResolution();

        String dir = transferDialogResult.getDir();
        if (dir == null) { dir = "./"; }


        Hashtable<String, Vector<IImage>> contentGeotagsHashtable = new Hashtable<String, Vector<IImage>>();
        for (IAlbum a : albumList)
        {
            for (IImage i : a.getImageList())
            {
                if ( !((i.getLongitude() == null) && (i.getLatitude() == null) && (i.getAltitude() == null)) )
                {
                    String locationKey = i.getLatitude().toString() + i.getLongitude().toString();
                    if ( !contentGeotagsHashtable.containsKey(locationKey) )
                    {
                        Vector<IImage> imageVector = new Vector<IImage>();
                        imageVector.add(i);
                        contentGeotagsHashtable.put(locationKey, imageVector);
                    }
                    else
                    {
                        Vector<IImage> imageVector = contentGeotagsHashtable.get(locationKey);
                        
                        boolean md5Exists = false;
                        for (IImage img : imageVector)
                        {
                            if (img.getMD5().equals(i.getMD5())) { md5Exists = true; }
                        }
                        
                        if (!md5Exists) { imageVector.add(i); }
                    }
                }
            }
        }


        try
        {            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //Create instance of DocumentBuilderFactory
            DocumentBuilder docBuilder = factory.newDocumentBuilder(); //Get the DocumentBuilder
            Document doc = docBuilder.newDocument(); //Create blank DOM Document

            Comment comment = doc.createComment("created by jSmugmugBackup"); //create a comment
            doc.appendChild(comment);
            
            Element rootElement = doc.createElement("kml"); //create the root element
            rootElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
            doc.appendChild(rootElement); //all it to the xml tree

            Element documentElement = doc.createElement("Document");
            rootElement.appendChild(documentElement);

//            Element screenOverlayElement = doc.createElement("ScreenOverlay");
//            Element overlayNameElement = doc.createElement("name");
//            overlayNameElement.setTextContent("SoulCube.net");
//            screenOverlayElement.appendChild(overlayNameElement);
//            Element overlayDescriptionElement = doc.createElement("description");
//            CDATASection overlayDescriptionCDATA = doc.createCDATASection("<p><a href=\"http://code.google.com/p/jsmugmugbackup/\">jSmugmugBackup</a></p>");
//            screenOverlayElement.appendChild(overlayDescriptionElement);
//            Element overlayIconElement = doc.createElement("Icon");
//            Element overlayHrefElement = doc.createElement("href");
//            overlayHrefElement.setTextContent("http://");
//            overlayIconElement.appendChild(overlayHrefElement);
//            screenOverlayElement.appendChild(overlayIconElement);
//            Element overlayOverlayXYElement = doc.createElement("overlayXY");
//            overlayOverlayXYElement.setAttribute("x", "0");
//            overlayOverlayXYElement.setAttribute("y", "0");
//            overlayOverlayXYElement.setAttribute("xunits", "fraction");
//            overlayOverlayXYElement.setAttribute("yunits", "fraction");
//            screenOverlayElement.appendChild(overlayOverlayXYElement);
//            Element overlayScreenXYElement = doc.createElement("screenXY");
//            overlayScreenXYElement.setAttribute("x", "0");
//            overlayScreenXYElement.setAttribute("y", "0");
//            overlayScreenXYElement.setAttribute("xunits", "fraction");
//            overlayScreenXYElement.setAttribute("yunits", "fraction");
//            screenOverlayElement.appendChild(overlayScreenXYElement);
//            Element overlaySizeElement = doc.createElement("size");
//            overlaySizeElement.setAttribute("x", "0");
//            overlaySizeElement.setAttribute("y", "0");
//            overlaySizeElement.setAttribute("xunits", "fraction");
//            overlaySizeElement.setAttribute("yunits", "fraction");
//            screenOverlayElement.appendChild(overlaySizeElement);
//            documentElement.appendChild(screenOverlayElement);


            Element styleElement = doc.createElement("Style");
            styleElement.setAttribute("id", "photoStyle");
            Element iconStyleElement = doc.createElement("IconStyle");
            Element imageIconElement = doc.createElement("Icon");
            Element imageHrefElement = doc.createElement("href");
            //imageHrefElement.setTextContent("http://maps.google.com/mapfiles/kml/paddle/ylw-blank_maps.png");
            imageHrefElement.setTextContent("http://maps.google.com/mapfiles/kml/paddle/pink-blank_maps.png");
            imageIconElement.appendChild(imageHrefElement);
            iconStyleElement.appendChild(imageIconElement);
            styleElement.appendChild(iconStyleElement);
            documentElement.appendChild(styleElement);



            for (Vector<IImage> geotag : contentGeotagsHashtable.values())
            {
                String placemarkCoordinates = geotag.get(0).getLongitude() + "," + geotag.get(0).getLatitude() + "," + geotag.get(0).getAltitude();

                String placemarkName = "";
                for (IImage image : geotag)
                {
                    if (placemarkName.length() != 0) { placemarkName += ", "; }
                    placemarkName += image.getName();
                }


                String placemarkHtmlDescription = "";
                //placemarkHtmlDescription += placemarkName + "<br/>";
                placemarkHtmlDescription += "Longitude : " + geotag.get(0).getLongitude() + "<br/>";
                placemarkHtmlDescription += "Latitude  : " + geotag.get(0).getLatitude() + "<br/>";
                placemarkHtmlDescription += "Altitude  : " + geotag.get(0).getAltitude() + "m<br/>";
                placemarkHtmlDescription += "<br/>";
                for (IImage image : geotag)
                {
                    String downloadURL = Helper.getDownloadURL(image, maxResolution);
                    placemarkHtmlDescription += "Category    : " + image.getParent().getParent().getParent().getName() + "<br/>";
                    placemarkHtmlDescription += "Subcategory : " + image.getParent().getParent().getName() + "<br/>";
                    placemarkHtmlDescription += "Album       : " + image.getParent().getName() + "<br/>";
                    placemarkHtmlDescription += "<a href=\"" + downloadURL + "\"><img src=\"" + image.getMediumURL() + "\" border=\"0\" alt=\"" + image.getName() + "\" /></a><br/>";
                }

                Element placemarkElement = doc.createElement("Placemark");

                Element imageNameElement = doc.createElement("name");
                imageNameElement.setTextContent(placemarkName);
                placemarkElement.appendChild(imageNameElement);

                Element imageDescriptionElement = doc.createElement("description");
                CDATASection imageDescriptionCdataElement = doc.createCDATASection(placemarkHtmlDescription);
                imageDescriptionElement.appendChild(imageDescriptionCdataElement);
                placemarkElement.appendChild(imageDescriptionElement);

                Element imageStyleUrl = doc.createElement("styleUrl");
                imageStyleUrl.setTextContent("#photoStyle");
                placemarkElement.appendChild(imageStyleUrl);


                Element imagePointElement = doc.createElement("Point");
                Element imageCoordinatesElement = doc.createElement("coordinates");
                imageCoordinatesElement.setTextContent(placemarkCoordinates);
                imagePointElement.appendChild(imageCoordinatesElement);
                placemarkElement.appendChild(imagePointElement);

                documentElement.appendChild(placemarkElement);
            }


            /*
             * TODO: - description of position,altitude, time, date and album name, see generated kmz
             * */




            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Source src = new DOMSource(doc);
            Result dest = new StreamResult(dir + "jsb.kml");
            aTransformer.transform(src, dest);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void delete(ITransferDialogResult transferDialogResult)
    {
    	this.log.printLogLine(LogLevelEnum.Message, 0, "preparing to delete files");
    	this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: not implemented");

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
    	
    	this.log.printLogLine(LogLevelEnum.Message, 0, "extracted album name \"" + result + "\" from dir: " + dir);
    	
    	return result;
    }
    private void recursiveUploadDirectorySearch(int maxRecursionLevel, File directory, String category, String subcategory, String album, String keywords)
    {
        //this.log.printLogLine("DEBUG: Model.recursiveUploadDirectorySearch(" + maxRecursionLevel + ", " + directory + ", " + category + ", " + subcategory + ", " + album + ", " + keywords + ")");

        //check if it's a directory, not a file
        if (!directory.isDirectory()) //should normally be false
        {
            this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: expected a directory, not a file (" + directory + ")");
            this.quitApplication();
        }

        if (this.containsPics(directory))
        {
            //print a warning - we have nowhere to put the images
            this.log.printLogLine(LogLevelEnum.Warning, 0, "WARNING: the directory " + directory + " contains images which will be ignored ... specify a \"--album\" parameter or use the parent directory for the \"--dir\" parameter");
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
                            //if (category == null) { category    = subDirectory.getParentFile().getName(); } else { /*NOOP: category is already defined*/ }
                            category    = subDirectory.getParentFile().getName();
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
                        else { this.log.printLogLine(LogLevelEnum.Error, 0, "ERROR: undefined recursion level"); this.quitApplication(); }

                        //this.log.printLogLine("DEBUG: enqueuing: " + subDirectory + "(" + category + "/" + subcategory + "/" + album + ")");
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
                    this.log.printLogLine(LogLevelEnum.Warning, 0, "WARNING: " + subDirectory.getAbsolutePath() + " - the ignore tag was set ... skipping this directory");
                }

            }
        }

    }

}
