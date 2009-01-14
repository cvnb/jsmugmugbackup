/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

public interface ISmugmugObject extends Cloneable, Comparable<ISmugmugObject>
{
	int getID();
	String getName();
    String getFullName();
	
	SmugmugTypeEnum getSmugmugType();
	ISmugmugObject getParent();	
	
	SmugmugObject clone(ISmugmugObject newParent);
}
