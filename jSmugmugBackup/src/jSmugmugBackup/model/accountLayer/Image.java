/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import jSmugmugBackup.model.Helper;
import java.io.Serializable;
import java.util.Stack;
import java.util.Vector;

public class Image extends SmugmugObject implements IImage, Serializable
{
    private String key = null;
	private String caption = null;
	private Vector<String> tags = null;
	private String format = null;
	private int height = 0;
	private int width = 0;
	private long size = 0;
    //private int largestURLContentSize = 0;
	private String md5 = null;
    private String mediumURL = null;
    private String largeURL = null;
    private String xLargeURL = null;
    private String x2LargeURL = null;
    private String x3LargteURL = null;
	private String originalURL = null;
	
	public Image(IAlbum parentAlbum, int id, String name)
	{
		super(parentAlbum, id, name);
	}
	
	public Image(IAlbum parentAlbum, int id, String name, String key, String caption, String keywords, String format, int height, int width, long size, /*int largestURLContentSize,*/ String md5,
                 String mediumURL, String largeURL, String xLargeURL, String x2LargeURL, String x3LargeURL, String originalURL)
	{
		super(parentAlbum, id, name);
        this.key = key;
		this.caption = caption;

        //handle tags
        this.tags = Helper.getTags(keywords);

        this.format = format;
		this.height = height;
		this.width = width;
		this.size = size;
        //this.largestURLContentSize = largestURLContentSize;
		this.md5 = md5;
        this.mediumURL = mediumURL;
        this.largeURL = largeURL;
        this.xLargeURL = xLargeURL;
        this.x2LargeURL = x2LargeURL;
        this.x3LargteURL = x3LargeURL;
		this.originalURL = originalURL;
	}

    //special copy constructor
    public Image(IAlbum parentAlbum, IImage image)
    {
        this(parentAlbum, image.getID(), image.getName(), image.getKey(), image.getCaption(), Helper.getKeywords(image.getTags()), image.getFormat(), image.getHeight(), image.getWidth(), image.getSize(), /*image.getLargestURLContentSize(),*/ image.getMD5(),
             image.getMediumURL(), image.getLargeURL(), image.getXLargeURL(), image.getX2LargeURL(), image.getX3LargeURL(), image.getOriginalURL());
    }


	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_IMAGE; }


    public String getKey() { return this.key; }
	public String getCaption() { return this.caption; }
	public Vector<String> getTags() { return this.tags; }
	public String getFormat() { return this.format; }
	public int getHeight() { return this.height; }
	public int getWidth() { return this.width; }
	public long getSize() { return this.size; }
    //public int getLargestURLContentSize() { return this.largestURLContentSize; }
	public String getMD5() { return this.md5; }
    public String getMediumURL() { return this.mediumURL; }
    public String getLargeURL() { return this.largeURL; }
    public String getXLargeURL() { return this.xLargeURL; }
    public String getX2LargeURL() { return this.x2LargeURL; }
    public String getX3LargeURL() { return this.x3LargteURL; }
	public String getOriginalURL() { return this.originalURL; }

}
