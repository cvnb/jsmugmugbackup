/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.abstractionLayerNG.data;

import java.util.Vector;

public class Subcategory extends SmugmugObject implements ISubcategory
{
	private Vector<IAlbum> albumList = null;
	
	public Subcategory(int id, String name)
	{
		super(id, name);
		this.albumList = new Vector<IAlbum>();
	}


	public void addAlbum(IAlbum album)
	{
		this.albumList.add(album);
	}

	public Vector<IAlbum> getAlbumList()
	{
		return this.albumList;
	}

}
