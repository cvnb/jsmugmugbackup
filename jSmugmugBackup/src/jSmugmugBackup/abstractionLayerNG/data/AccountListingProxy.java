/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.abstractionLayerNG.data;

import jSmugmugBackup.abstractionLayerNG.ISmugmugConnectorNG;
import jSmugmugBackup.abstractionLayerNG.SmugmugConnectorNG;

import java.util.Vector;

public class AccountListingProxy implements IAccountListingProxy
{
	private ISmugmugConnectorNG connector = null;
	private Vector<ICategory> categoryList = null;
	
	public AccountListingProxy()
	{
		this.connector = new SmugmugConnectorNG(false);
	}
	
	public void login(String userEmail, String password)
	{
		this.connector.login(userEmail, password);
	}
	
	public void logout()
	{
		this.connector.logout();
	}

	public String getNickName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public Vector<ICategory> getCategoryList()
	{
		if (this.categoryList == null)
		{
			this.categoryList = this.connector.getTree();
		}
		
		return this.categoryList;
	}

	//----------- public, but not visible through interface ----------
	public void addCategory(int id, String name)
	{
		this.categoryList.add( new Category(id, name) );
	}
	
	public void addSubcategory(int categoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				c.addSubcategory(new Subcategory(id, name) );
				return;
			}
		}
		
		System.out.println("addSubcategory: ERROR!");
	}

	public void addAlbum(int categoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				c.addAlbum( new Album(id, name) );
				return;
			}
		}
		
		System.out.println("addAlbum: ERROR!");
	}
	
	public void addAlbum(int categoryID, int subcategoryID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			if (c.getID() == categoryID)
			{
				for (ISubcategory s : c.getSubcategoryList())
				{
					if (s.getID() == subcategoryID)
					{
						s.addAlbum( new Album(id, name) );
						return;
					}
				}
			}
		}
		
		System.out.println("addAlbum: ERROR!");		
	}
	
	public void addImage(int albumID, int id, String name)
	{
		for (ICategory c : this.categoryList)
		{
			for (ISubcategory s : c.getSubcategoryList())
			{
				for (IAlbum a : s.getAlbumList())
				{
					if (a.getID() == albumID)
					{
						a.addImage(new Image(id, name));
						return;
					}
				}

			}
			
			for (IAlbum a : c.getAlbumList())
			{
				if (a.getID() == albumID)
				{
					a.addImage(new Image(id, name));
					return;
				}
			}
		}
		
		System.out.println("addImage: ERROR!");
	}

}
