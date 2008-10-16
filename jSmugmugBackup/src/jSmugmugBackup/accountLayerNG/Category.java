/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.accountLayerNG;

import java.util.Vector;

public class Category extends SmugmugObject implements ICategory
{
	private Vector<IAlbum> albumList = null;
	private Vector<ISubcategory> subcategoryList = null;


	public Category(int id, String name)
	{
		super(id, name);
		this.albumList = new Vector<IAlbum>();
		this.subcategoryList = new Vector<ISubcategory>();		
	}
	
	public void addAlbum(IAlbum album)
	{
		this.albumList.add(album);		
	}
	
	public Vector<IAlbum> getAlbumList()
	{
		return this.albumList;
	}
	
	public void addSubcategory(ISubcategory subcategory)
	{
		this.subcategoryList.add(subcategory);
	}

	public Vector<ISubcategory> getSubcategoryList() {
		return this.subcategoryList;
	}




}
