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
    private String key = null;
    private Vector<String> tags = null;
	private Vector<IImage> imageList = null;
    private String lastUpdatedString = null;
    private Vector<IAlbumMonthlyStatistics> albumStatistics = null; // statistics are written to disk during serialization, but replaced with fresh data after each startup
	
	public Album(ISmugmugObject parent, int id, String name, String key, String keywords, String lastUpdatedString, Vector<IAlbumMonthlyStatistics> albumStatistics)
	{
		super(parent, id, name);
		this.imageList = new Vector<IImage>();

        this.key = key;
        this.tags = Helper.getTags(keywords);
        this.lastUpdatedString = lastUpdatedString;
        this.albumStatistics = albumStatistics;
	}

    // special copy constructor
    // - even when copying (a cached album from disk) we still add fresh statistics
    public Album(ISmugmugObject parent, IAlbum album, Vector<IAlbumMonthlyStatistics> albumMonthlyStatistics)
    {
        this(parent, album.getID(), album.getName(), album.getKey(), Helper.getKeywords( album.getTags() ), album.getLastUpdatedString(), Helper.cloneAlbumMonthlyStatisticsVector(albumMonthlyStatistics) );
        for (IImage i : album.getImageList())
        {
            this.addImage(new Image(album, i));
        }
    }

	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_ALBUM; }

    public String getKey() { return this.key; }
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


    public Vector<IAlbumMonthlyStatistics> getStatistics()
    {
        return this.albumStatistics;
    }



}
