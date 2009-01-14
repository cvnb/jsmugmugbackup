/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

public abstract class SmugmugObject implements ISmugmugObject
{
	private ISmugmugObject parent = null;
	private int id = -1;
	private String name = null;
	
	
	protected SmugmugObject(ISmugmugObject parent, int id, String name)
	{
		this.parent = parent;
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

    public String getFullName()
    {
        String fullName = this.name;

        ISmugmugObject ancestor = this.getParent();
        while (ancestor != null)
        {
            fullName = ancestor.getName() + "." + fullName;
            ancestor = ancestor.getParent();
        }

        return fullName;
    }
	
	public ISmugmugObject getParent()
	{
		return this.parent;
	}

	public abstract SmugmugTypeEnum getSmugmugType();
	

	public int compareTo(ISmugmugObject o)
	{
		return this.name.compareToIgnoreCase( ((SmugmugObject)o).getName() );
	}

	public SmugmugObject clone(ISmugmugObject newParent)
	{
		SmugmugObject myClone = null;
		try
		{
			myClone = (SmugmugObject) super.clone();
		} catch (CloneNotSupportedException e) { e.printStackTrace(); }
		
		//put new parent link
		myClone.parent = newParent;
		
		return myClone;
	}


}
