/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Stack;
import java.util.Vector;

public class Image extends SmugmugObject implements IImage
{
	private String caption = null;
	private Vector<String> tags = null;
	private String format = null;
	private int height = 0;
	private int width = 0;
	private long size = 0;
	private String md5 = null;
	private String originalURL = null;
	
	public Image(IAlbum parentAlbum, int id, String name)
	{
		super(parentAlbum, id, name);
	}
	
	public Image(IAlbum parentAlbum, int id, String name, String caption, String keywords, String format, int height, int width, long size, String md5, String originalURL)
	{
		super(parentAlbum, id, name);
		this.caption = caption;

        //handle tags
        if ((keywords == null) || (keywords.equals(""))) { this.tags = null; }
        else
        {
            String[] tagsArray = keywords.split("; ");
            this.tags = new Vector<String>();

            //copy array into a Vector
            for (int i=0; i < tagsArray.length; i++) { this.tags.add(tagsArray[i]); }
        }

        this.format = format;
		this.height = height;
		this.width = width;
		this.size = size;
		this.md5 = md5;
		this.originalURL = originalURL;
	}

	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_IMAGE; }

	
	public String getCaption() { return this.caption; }
	public Vector<String> getTags() { return this.tags; }
	public String getFormat() { return this.format; }
	public int getHeight() { return this.height; }
	public int getWidth() { return this.width; }
	public long getSize() { return this.size; }
	public String getMD5() { return this.md5; }
	public String getOriginalURL() { return this.originalURL; }




}
