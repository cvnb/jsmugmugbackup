package jSmugmugBackup.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


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
import com.streetsofboston.smugmug.v1_2_1.system.Session;


/**
 * This is an example that shows how one can create albums and images
 * using the SmugFig API.
 * @author Anton Spaans.
 */
public class ImageExample {
	// TODO Change e-mail and password into valid values.
	public static final String MY_ACCOUNT_EMAIL = "?????";
	public static final String MY_PASSWORD      = "???";
	
	private static Session mSession;
	private static CategoryPrototype mCatPrototype;
	private static SubCategoryPrototype mSCatPrototype;
	
	private static Category mCategory;
	private static SubCategory mSubCategory;
	
	public ImageExample() throws SmugmugException {
		System.out.println("Logging in...");
		mSession = Session.login(MY_ACCOUNT_EMAIL, MY_PASSWORD);
		mCatPrototype = new CategoryPrototype(mSession);
		mSCatPrototype = new SubCategoryPrototype(mSession);
	}
	
	public void init() throws SmugmugException {
		System.out.print("Creating category: ");
		mCatPrototype.data().setName("_Category_"+System.currentTimeMillis());
		mCategory = mCatPrototype.create();
		System.out.println(mCategory);
		mCatPrototype.data().clear();
		
		System.out.print("Creating sub-category: ");
		mSCatPrototype.data().setCategory(mCategory);
		mSCatPrototype.data().setName("_Subcategory_"+System.currentTimeMillis());
		mSubCategory = mSCatPrototype.create();
		System.out.println(mSubCategory);
	}

	public void execImageExamples() throws SmugmugException, IOException {
		final InputStream is1 = getClass().getClassLoader().getResourceAsStream("res/img1.jpg");
		final InputStream is2 = getClass().getClassLoader().getResourceAsStream("res/img2.jpg");
		
		Album album1 = null, album2 = null;
		Image img = null, checkImg = null;
		try {
			AlbumPrototype apt = new AlbumPrototype(mSession);
			Data<Album> data = apt.data();
			data.setAllowComments(false);
			data.setAllowEditsByFamily(false);
			data.setAllowEditsByFriends(false);
			data.setAllowExternalLinks(false);
			data.setCategory(mCategory);
			data.setDescription("Album 1");
			data.setHasCustomAppearance(true);
			data.setHasCustomWatermark(false);
			data.setIsClean(false);
			data.setIsLargestOriginal();
			data.setIsOwnerInfoHidden(false);
			data.setIsPrintable(false);
			data.setIsProtected(false);
			data.setIsSmugSearchable(false);
			data.setIsWorldSearchable(false);
			data.setName("Album title_1_"+System.currentTimeMillis());
			data.setShowEasyShareButton(false);
			data.setShowExif(true);
			data.setShowFilenames(true);
			data.setShowOnHomepage(false);
			data.setSortMethod(SORT_METHOD.Position);
			data.setSortOrder(SORT_ORDER.Asc);
			data.setSubCategory(mSubCategory);
			data.setViewStyleID(VIEW_STYLE.Smugmug);
			album1 = apt.create();
			System.out.println("Created album:\n"+album1+"\n");
			
			data.clear();
			data.setAllowComments(false);
			data.setAllowEditsByFamily(false);
			data.setAllowEditsByFriends(false);
			data.setAllowExternalLinks(false);
			data.setCategory(mCategory);
			data.setDescription("Album 2");
			data.setHasCustomAppearance(false);
			data.setHasCustomWatermark(false);
			data.setIsClean(false);
			data.setIsLargestXLarge();
			data.setIsOwnerInfoHidden(false);
			data.setIsPrintable(false);
			data.setIsProtected(false);
			data.setIsSmugSearchable(false);
			data.setIsWorldSearchable(false);
			data.setName("Album title_2_"+System.currentTimeMillis());
			data.setShowEasyShareButton(false);
			data.setShowExif(false);
			data.setShowFilenames(false);
			data.setShowOnHomepage(false);
			data.setSortMethod(SORT_METHOD.Caption);
			data.setSortOrder(SORT_ORDER.Asc);
			data.setViewStyleID(VIEW_STYLE.Smugmug);
			album2 = apt.create();
			System.out.println("Created album:\n"+album2+"\n");

			ImagePrototype ipt = new ImagePrototype(mSession);

			ipt.data().read(is1);
			is1.close();
			
			ipt.data().setAlbum(album1);
			ipt.data().setAltitude(45);
			ipt.data().setCaption("Image Caption_"+System.currentTimeMillis());
			ipt.data().setFileName("img1.jpg");
			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add("keyword 1"); keywords.add("keyword 2"); 
			data.setKeywords(keywords);
			ipt.data().setKeywords(keywords);
			ipt.data().setLatitude(42.004567);
			ipt.data().setLongitude(20.33420);
			img = ipt.create();
			System.out.println("This is image 1:\n"+img+"\n");
			
			final int id = img.getID();
			img = null;
			
			checkImg = ipt.get(id);
			System.out.println("This is image 1 again:\n"+checkImg+"\n");

			pause(5000); // create() may not have propagated on Smugmug yet. Wait 5 secs.
			checkImg.getInfo();
			System.out.println("This is image 1 again fresh from Smugmug:\n"+checkImg+"\n");

			// Move image to album2 and change the image itself and its caption.
			checkImg.setAlbum(album2);
			checkImg.setCaption("Image Caption_2_"+System.currentTimeMillis());
			checkImg.read(is2);
			is2.close();

			System.out.println("Wait 10 secs... seems to be necessary... don't know why.");
			pause(10000); // 
			System.out.println("Done waiting... update the image now :).");
			checkImg.update();

			pause(5000); // update() may not have propagated on Smugmug yet. Wait 5 secs.
			checkImg.getInfo();
			System.out.println("This is the updated image 1 from Smugmug:\n"+checkImg+"\n");
		}
		finally {
			System.out.println("Deleting image.");
			if (checkImg != null)
				try { checkImg.delete(); } catch(Exception e) { }
			System.out.println("Deleting album1.");
			if (album1 != null)
				try { album1.delete(); } catch(Exception e) { }
			System.out.println("Deleting album2.");
			if (album2 != null)
				try { album2.delete(); } catch(Exception e) { }
		}
	}
	
	public void clean() {
		System.out.println("Deleting category.");
		if (mCategory != null)
			try { mCategory.delete(); } catch(Exception e) { }
			System.out.println("Deleting sub-category.");
		if (mSubCategory != null)
			try { mSubCategory.delete(); } catch(Exception e) { }
			
		System.out.println("Deleting category.");
		try { mSession.logout(); } catch(Exception e) { }
	}

	private void pause(long millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {}
	}
	
	public static void main(String[] args) throws SmugmugException, IOException {
		ImageExample ie = new ImageExample();
		try { 
			ie.init();
			ie.execImageExamples();
		}
		finally {
			ie.clean();
		}
	}
}
