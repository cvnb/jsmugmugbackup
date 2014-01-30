/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public interface ISubcategory extends ISmugmugObject
{
    void addAlbum(IAlbum album);
    Vector<IAlbum> getAlbumList();
}
