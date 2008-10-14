/* Copyright 2007 StreetsOfBoston (Anton Spaans)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streetsofboston.smugmug.v1_2_1;

import java.util.ArrayList;
import java.util.Collection;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.subcategories.Create;
import com.kallasoft.smugmug.api.json.v1_2_1.subcategories.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.subcategories.GetAll;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Use this class to create instances of SubCategory or to retrieve them from the
 * Smugmug repository. For example :
 * <p>
 * <code>SubCategoryProtoype cpt = new SubCategoryPrototype(session); </code><br>
 * <code>cpt.data().setName("New SubCategory"); </code><br/>
 * <code>cpt.data().setCategory(someCategory);</code><br/>
 * <code>SubCategory scat = cpt.create(); </code>
 * 
 * @author Anton Spaans
 */
public class SubCategoryPrototype extends PrototypeHelper<SubCategory> implements ISmugObjectPrototype<SubCategory> {

	private static final String CACHE = "SubCategory";

	private final Data<SubCategory> mData; 

	/**
	 * Creates a new instance of a SubCategory Prototype.
	 * 
	 * @param session Session-account information.
	 */
	public SubCategoryPrototype(Session session) {
		mData = new Data<SubCategory>(session);
	}
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#create()
	 *  <code>SubCategoryPrototype cp = new SubCategoryPrototype(session); </code><br/>
	 *	<code>// Create a new subcategory with name SomeName </code><br/>
	 *	<code>cp.data().setName("SomeName"); </code><br/>
	 *	<code>SubCategory c = cp.<b>create();</b></code><br/>
	 */
	public SubCategory create() throws SmugmugException {
		if (mData.mName.isUndefined()) 
			throw new SmugmugException("The Title is not set for "+mData);

		if (mData.mCategoryID.isUndefined()) 
			throw new SmugmugException("The CategoryID is not set for "+this);

		Create.CreateResponse resp = new Create().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				mData.getName(), mData.getCategoryID().getIntID());


		SmugmugException.check(resp);
		
		final SubCategory ret = new SubCategory(mData.getSession(), resp.getSubCategoryID(), 
				mData.getCategoryID().getIntID(), mData.getName()); 
		return postCreate(ret);
	}

	/** @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#get(int) */
	public SubCategory get(int ID) throws SmugmugException {
		final GUID catID = mData.mCategoryID.clear();
		final SubCategory scat = super.get(ID);
		mData.mCategoryID.set(catID);
		return scat;
	}
	
	/** 
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll()
	 */
	public Collection<SubCategory> getAll() throws SmugmugException {
		return postGetAll(getAllOfThem(), true, GUID.NO_GUID);
	}

	/**
	 * This is the same as {@link #getAll(int)}
	 * The categoryID is the string-representation of the the integer-value.
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll(java.lang.String)
	 */
	public Collection<SubCategory> getAll(GUID categoryID) throws SmugmugException {
		//return getAll(categoryID.getIntID());
		return postGetAll(getAllInCategory(categoryID.getIntID()), true, categoryID);
	}
	
	/** 
	 *  <code>
	 * SubCategoryPrototype cp = new SubCategoryPrototype(session);<br/>
	 * // Get all sub-categories of 'someCategory'.<br/>
	 * Collection&lt;SubCategory> subs = cp.getAll(someCategory.getID()); <br/>
	 *  </code>
	 * @param categoryID The ID of the parent Category.
	 * @return All SubCategory's for the given Category.
	 * @throws SmugmugException 
	 */
	public Collection<SubCategory> getAll(int categoryID) throws SmugmugException {
		return getAll(GUID.create(Category.class, categoryID));
		//return postGetAll(getAllInCategory(categoryID), true, GUID.create(Category.class, categoryID));
	}

	/** 
	 *  <code>
	 * SubCategoryPrototype cp = new SubCategoryPrototype(session);<br/>
	 * // Get all sub-categories of 'someCategory'.<br/>
	 * Collection&lt;SubCategory> subs = cp.getAll(someCategory.getID()); <br/>
	 *  </code>
	 * @param categoryID The ID of the parent Category.
	 * @return All SubCategory's for the given Category.
	 * @throws SmugmugException
	 */
	public Collection<SubCategory> getAllFromCache(int categoryID) throws SmugmugException {
		return getAllFromCache(GUID.create(Category.class, categoryID));
	}

	/** 
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll() 
	 *  <code>SubCategoryPrototype cp = new SubCategoryPrototype(session); </code><br/>
	 *	<code>// Create a new subcategory with name SomeName </code><br/>
	 *	<code>cp<b>.data()</b>.setName("SomeName"); </code><br/>
	 *	<code>SubCategory c = cp.create();</code><br/>
	 */
	public Data<SubCategory> data() {
		return mData;
	}
	
	public Class<SubCategory> getClassForPrototype() {
		return SubCategory.class;
	}
	
	/** 
	 * Instances of this class hold the prototype-data for creation of SubCategory-s.<br/>
	 * This class also functions as a base-class for SubCategory-s.
	 * @param <T> SubCategory.
	 */
	public static class Data<T extends SmugObjectData<?>> extends CategoryPrototype.Data<T> {
		static final long serialVersionUID = 7794129021846989146L;

		/* Attribute list */
		protected final SmugAttribute<GUID> mCategoryID = new SmugAttribute<GUID>("CategoryID");
		
		protected Data(Session session) {
			super(session);
			bundle();
		}
		
		protected Data(Session session, int ID, int categoryID, String name) {
			super(session, ID, name);
			mCategoryID.set(GUID.create(Category.class, categoryID));
			bundle();
		}

		protected Data(Category category, int ID, String name) {
			this(category.getSession(), ID, category.getID(), name);
		}
	 
		protected String getCacheName() {
			return CACHE;
		}
		
		/**
		 * @param categoryID
		 *            The ID of the SubCategory's parent Category.
		 */
		public void setCategoryID(GUID categoryID) {
			mCategoryID.set(categoryID);
		}
		
		/** @return The ID of the parent Category. */
		public GUID getCategoryID() {
			return mCategoryID.get();
		}
		
		/**
		 * @param category
		 *            The SubCategory's parent Category.
		 */
		public void setCategory(Category category) {
			setCategoryID(category != null ? category.guid() : null);
		}
		
		/**
		 * @return The ID of the parent Category. This may involve a call to the
		 *         Smugmug repository.
		 * @throws SmugmugException 
		 */
		public Category getCategory() throws SmugmugException {
			if (mCategoryID.isUndefined() || !mCategoryID.get().hasGUID())
				return null;
			
			CategoryPrototype catFactory = new CategoryPrototype(getSession());
			return catFactory.get(getCategoryID());
		}
	}

	private Collection<SubCategory> getAllOfThem() throws SmugmugException {
		GetAll.GetAllResponse resp = new GetAll().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				mData.getSession().getOtherAccount().getNickName(), 
				mData.getSession().getOtherAccount().getSitePassword());

		SmugmugException.check(resp);

		Collection<com.kallasoft.smugmug.api.json.entity.Category> list = resp.getSubCategoryList();
		Collection<SubCategory> retList = new ArrayList<SubCategory>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.Category scat : list) {
			final SubCategory mySCat = new SubCategory(mData.getSession(), scat.getID(), scat.getParentCategoryID(), scat.getName());
			mySCat.sync();

			retList.add(mySCat);
		}
		return retList;
	}

	private Collection<SubCategory> getAllInCategory(int ID) throws SmugmugException {
		Get.GetResponse resp = new Get().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				ID,
				mData.getSession().getOtherAccount().getNickName(), 
				mData.getSession().getOtherAccount().getSitePassword());

		SmugmugException.check(resp);

		Collection<com.kallasoft.smugmug.api.json.entity.Category> list = resp.getSubCategoryList();
		Collection<SubCategory> retList = new ArrayList<SubCategory>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.Category scat : list) {
			final SubCategory mySCat = new SubCategory(mData.getSession(), scat.getID(), ID, scat.getName());
			mySCat.sync();

			retList.add(mySCat);
		}
		return retList;
	}
}
