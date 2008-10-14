/*
 * Created on Sep 8, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.data;


import java.util.Vector;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;


public interface ICategoryType
{
	GUID getGUID();
	String getName();
	
	void addSubCategory(ISubCategoryType subcategory);
	Vector<ISubCategoryType> getSubCategoryList();	
	ISubCategoryType getSubCategory(String name);
	
	void addAlbum(IAlbumType album);	
	Vector<IAlbumType> getAlbumList();
	IAlbumType getAlbum(String name);
}
