/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public class Album extends SmugmugObject implements IAlbum
{
	private Vector<IImage> imageList = null;
	
	public Album(int id, String name)
	{
		super(id, name);
		this.imageList = new Vector<IImage>();
	}

	public void addImage(IImage image)
	{
		this.imageList.add(image);
	}
	
	public Vector<IImage> getImageList()
	{
		return this.imageList;
	}



}
