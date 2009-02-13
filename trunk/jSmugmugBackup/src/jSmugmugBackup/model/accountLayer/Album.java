/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.model.Helper;
import java.io.Serializable;
import java.util.Vector;

public class Album extends SmugmugObject implements IAlbum, Serializable
{
    private Vector<String> tags = null;
	private Vector<IImage> imageList = null;
    private String lastUpdatedString = null;
	
	public Album(ISmugmugObject parent, int id, String name, String keywords, String lastUpdatedString)
	{
		super(parent, id, name);
		this.imageList = new Vector<IImage>();

        this.tags = Helper.getTags(keywords);
        this.lastUpdatedString = lastUpdatedString;
	}

    // special copy constructor
    public Album(ISmugmugObject parent, IAlbum album)
    {
        this(parent, album.getID(), album.getName(), Helper.getKeywords( album.getTags() ), album.getLastUpdatedString());
        for (IImage i : album.getImageList())
        {
            this.addImage(new Image(album, i));
        }
    }

	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_ALBUM; }

    public Vector<String> getTags() { return this.tags; }
	
	public void addImage(IImage image)
	{
		this.imageList.add(image);
	}
	
	public Vector<IImage> getImageList()
	{
		return this.imageList;
	}

    public int getImageCount()
    {
        return this.imageList.size();
    }

    public String getLastUpdatedString()
    {
        return this.lastUpdatedString;
    }





}