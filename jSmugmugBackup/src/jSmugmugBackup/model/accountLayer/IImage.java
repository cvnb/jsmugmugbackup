/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

public interface IImage extends ISmugmugObject
{
	String getCaption();
	String getKeywords();
	String getFormat();
	int getHeight();
	int getWidth();
	long getSize();
	String getMD5();
	String getOriginalURL();
}
