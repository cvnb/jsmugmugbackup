/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public interface IImage extends ISmugmugObject
{
    String getKey();
	String getCaption();
	Vector<String> getTags();
	String getFormat();
	int getHeight();
	int getWidth();
	long getSize();
    //int getLargestURLContentSize();
    Double getLatitude();
    Double getLongitude();
    Long getAltitude();
    
	String getMD5();
    String getAlbumURL();
    String getThumbURL();
    String getTinyURL();
    String getSmallURL();
    String getMediumURL();
    String getLargeURL();
    String getXLargeURL();
    String getX2LargeURL();
    String getX3LargeURL();
	String getOriginalURL();
    String getVideo320URL();
    String getVideo640URL();
    String getVideo960URL();
    String getVideo1280URL();
}
