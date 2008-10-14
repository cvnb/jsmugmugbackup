/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.abstractionLayerNG.data;

import java.util.Vector;

public interface IAccountListingProxy
{
	void login(String userEmail, String password);
	void logout();
	
	String getNickName();
	
	Vector<ICategory> getCategoryList();

}
