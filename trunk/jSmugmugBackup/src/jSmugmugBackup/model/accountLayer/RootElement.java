/*
 * Created on Nov 27, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.accountLayer;

import java.util.Vector;

public class RootElement extends SmugmugObject implements IRootElement
{
    private String nickname = null;
	private Vector<ICategory> categoryList = null;

	public RootElement(String name)
	{
		super(null, 0, name);

        this.nickname = name;
		this.categoryList = new Vector<ICategory>();
	}

	public SmugmugTypeEnum getSmugmugType() { return SmugmugTypeEnum.SMUGMUG_ROOT; }

    public String getNickname()
    {
        return this.nickname;
    }

    public void addCategory(ICategory category)
	{
		this.categoryList.add(category);
	}
	
	public Vector<ICategory> getCategoryList()
	{
		return this.categoryList;
	}




}
