/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public class Subcategory extends SmugmugObject implements ISubcategory
{
    private Vector<IAlbum> albumList = null;

    public Subcategory(ICategory parentCategory, int id, String name)
    {
        super(parentCategory, id, name);
        this.albumList = new Vector<IAlbum>();
    }

    public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_SUBCATEGORY; }

    public void addAlbum(IAlbum album)
    {
        this.albumList.add(album);
    }

    public Vector<IAlbum> getAlbumList()
    {
        return this.albumList;
    }

}
