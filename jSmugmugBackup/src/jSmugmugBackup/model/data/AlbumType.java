/*
 * Created on Sep 8, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.data;


import java.util.Vector;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;

public class AlbumType implements IAlbumType
{
	private GUID guid;
	private String name;
	private Vector<IImageType> imageList;
	
	public AlbumType(GUID guid, String name)
	{
		this.guid = guid;
		this.name = name;
		this.imageList = new Vector<IImageType>();
	}

	//@Override
	public GUID getGUID()
	{
		return this.guid;
	}
	
	//@Override
	public String getName()
	{
		return this.name;
	}
	
	//@Override
	public void addImage(IImageType image)
	{
		this.imageList.add(image);
	}

	//@Override
	public Vector<IImageType> getImageList()
	{
		return this.imageList;
	}
}
