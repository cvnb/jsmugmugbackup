/*
 * Created on Nov 27, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public interface IRootElement extends ISmugmugObject
{
	void addCategory(ICategory category);
	Vector<ICategory> getCategoryList();

}
