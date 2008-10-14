/*
 * Created on Sep 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.data;


import java.util.Vector;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;

public class AccountListing implements IAccountListing
{
	private String nickName = null;
	private Vector<ICategoryType> categoryList = null;
	
	public AccountListing(String nickName)
	{
		this.nickName = nickName;
		this.categoryList = new Vector<ICategoryType>();
	}
	
	public String getNickName()
	{
		return this.nickName;
	}
	
	public Vector<ICategoryType> getCategoryList()
	{
		return this.categoryList;
	}

	public ICategoryType getCategory(String name)
	{
		for (ICategoryType category : this.categoryList)
		{
			//finds the first album with a matching name
			if ( category.getName().equals(name) ) return category;
		}
		return null;
	}
	
	public void addImage(GUID categoryGUID, String categoryName, GUID subCategoryGUID, String subCategoryName, GUID albumGUID, String albumName, GUID imageGUID, String imageName, long imageSize, String imageMD5Sum)
	{

		ICategoryType c = this.findOrCreateCategory(categoryGUID, categoryName);
		ISubCategoryType sc = this.findOrCreateSubCategory(c, subCategoryGUID, subCategoryName);
		IAlbumType a = this.findOrCreateAlbum(c, sc, albumGUID, albumName);
		
		@SuppressWarnings("unused")
		IImageType i = this.findOrCreateImage(a, imageGUID, imageName, imageSize, imageMD5Sum);
		
	}
	
	private ICategoryType findOrCreateCategory(GUID categoryGUID, String categoryName)
	{
		for (ICategoryType c : this.categoryList)
		{
			if (c.getGUID().equals(categoryGUID)) return c;			
		}
		
		//category was not found, so we create one
		ICategoryType c = new CategoryType(categoryGUID, categoryName);
		this.categoryList.add(c);
		return c;		
	}
	
	private ISubCategoryType findOrCreateSubCategory(ICategoryType category, GUID subCategoryGUID, String subCategoryName)
	{
		if (subCategoryGUID == null) return null; //no subCategory present
		
		for (ISubCategoryType sc : category.getSubCategoryList())
		{
			if (sc.getGUID().equals(subCategoryGUID)) return sc;
		}
		
		//no SubCategory found, so create one ...
		ISubCategoryType sc = new SubCategoryType(subCategoryGUID, subCategoryName);
		category.addSubCategory(sc);
		return sc;
	}
	
	private IAlbumType findOrCreateAlbum(ICategoryType category, ISubCategoryType subCategory, GUID albumGUID, String albumName)
	{
		if (subCategory == null)
		{
			for (IAlbumType a : category.getAlbumList())
			{
				if (a.getGUID().equals(albumGUID)) return a;
			}
			
			//album not found, so we create one
			IAlbumType a = new AlbumType(albumGUID, albumName);
			category.addAlbum(a);
			return a;
		}
		else
		{
			for (IAlbumType a : subCategory.getAlbumList())
			{
				if (a.getGUID() == albumGUID) return a;
			}
			
			//album not found, so we create one
			IAlbumType a = new AlbumType(albumGUID, albumName);
			subCategory.addAlbum(a);
			return a;
		}
	}
	
	private IImageType findOrCreateImage(IAlbumType album, GUID imageGUID, String imageName, long imageSize, String imageMD5Sum)
	{
		for (IImageType i : album.getImageList())
		{
			if (i.getGUID().equals(imageGUID))
			{
				//this should usually not happen
				System.out.println("image already exists, something is wrong!!!");
				System.exit(1);
			}
		}
		
		//image is not there, so we create it
		IImageType i = new ImageType(imageGUID, imageName, imageSize, imageMD5Sum);
		album.addImage(i);
		return i;
	}

	public IAlbumType getAlbumByGUID(GUID albumGUID)
	{
		for (ICategoryType c : this.getCategoryList())
		{
			for (ISubCategoryType s : c.getSubCategoryList())
			{
				for (IAlbumType a : s.getAlbumList())
				{
					if (a.getGUID().equals(albumGUID)) { return a; }
				}
			}
			
			for (IAlbumType a : c.getAlbumList())
			{
				if (a.getGUID().equals(albumGUID)) { return a; }
			}
		}
		
		return null;
	}

}
