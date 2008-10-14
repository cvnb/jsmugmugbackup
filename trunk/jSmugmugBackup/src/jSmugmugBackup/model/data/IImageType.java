
package jSmugmugBackup.model.data;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;


public interface IImageType
{
	GUID getGUID();
	String getName();
	String getFullPath();
	String getDescription();
	long getSize();
	String getMD5Sum();
	
	//todo: tags

}
