///*
// * Created on Sep 8, 2008
// *
// * TODO To change the template for this generated file go to
// * Window - Preferences - Java - Code Generation - Code and Comments
// */
//package jSmugmugBackup.model.data;
//
//
//import java.util.Vector;
//
//import com.streetsofboston.smugmug.v1_2_1.system.GUID;
//
//
//public class CategoryType implements ICategoryType
//{
//	private GUID guid;
//	private String name;
//	private Vector<ISubCategoryType> subCategoryList = null;
//	private Vector<IAlbumType> albumList = null;
//	
//	public CategoryType(GUID guid, String name)
//	{
//		this.guid = guid;		
//		this.name = name;
//		this.subCategoryList = new Vector<ISubCategoryType>();
//		this.albumList = new Vector<IAlbumType>();
//	}
//
//	public GUID getGUID()
//	{
//		return this.guid;
//	}
//	
//	public String getName()
//	{
//		return this.name;
//	}
//
//	public void addSubCategory(ISubCategoryType subcategory)
//	{
//		this.subCategoryList.add(subcategory);
//	}
//
//	public Vector<ISubCategoryType> getSubCategoryList()
//	{
//		return this.subCategoryList;
//	}
//	
//	public ISubCategoryType getSubCategory(String name)
//	{
//		for (ISubCategoryType subcategory : this.subCategoryList)
//		{
//			//finds the first album with a matching name
//			if ( subcategory.getName().equals(name) ) return subcategory;
//		}
//		return null;
//	}
//	
//	public void addAlbum(IAlbumType album)
//	{
//		this.albumList.add(album);
//	}
//
//	public Vector<IAlbumType> getAlbumList()
//	{
//		return this.albumList;
//	}
//
//	public IAlbumType getAlbum(String name)
//	{
//		for (IAlbumType album : this.albumList)
//		{
//			//finds the first album with a matching name
//			if ( album.getName().equals(name) ) return album;
//		}
//		return null;
//	}
//
//
//
//}
