/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

public class SmugmugObject implements ISmugmugObject
{
	private int id = -1;
	private String name = null;

	protected SmugmugObject(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public int getID()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public SmugmugObject clone()
	{
		SmugmugObject myClone = null;
		try
		{
			myClone = (SmugmugObject) super.clone();
		} catch (CloneNotSupportedException e) { e.printStackTrace(); }
		
		return myClone;
	}
}
