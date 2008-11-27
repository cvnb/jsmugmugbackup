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
	
	public Album(ISmugmugObject parent, int id, String name)
	{
		super(parent, id, name);
		this.imageList = new Vector<IImage>();
	}

	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_ALBUM; }
	
	
	public void addImage(IImage image)
	{
		this.imageList.add(image);
	}
	
	public Vector<IImage> getImageList()
	{
		return this.imageList;
	}





}
