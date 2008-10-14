/*
 * Created on Sep 25, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.data;


import java.util.Vector;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;


public class SubCategoryType implements ISubCategoryType
{
	private GUID guid;
	private String name;
	private Vector<IAlbumType> albumList = null;

	public SubCategoryType(GUID guid, String name)
	{
		this.guid = guid;
		this.name = name;
		this.albumList = new Vector<IAlbumType>();
	}

	public GUID getGUID()
	{
		return this.guid;
	}

	public String getName()
	{
		return this.name;
	}
	
	public void addAlbum(IAlbumType album)
	{
		this.albumList.add(album);
	}
	
	public Vector<IAlbumType> getAlbumList()
	{
		return this.albumList;
	}

	public IAlbumType getAlbum(String name)
	{
		for (IAlbumType album : this.albumList)
		{
			//finds the first subcategory with a matching name
			if ( album.getName().equals(name) ) return album;
		}
		return null;
	}
}
