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
import com.kallasoft.smugmug.api.json.v1_2_1.categories.Create;
import com.kallasoft.smugmug.api.json.v1_2_1.categories.Get;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Use this class to create instances of Category or to retrieve them from the
 * Smugmug repository. For example :
 * <p>
 * <code>CategoryProtoype cpt = new CategoryPrototype(session); </code><br>
 * <code>cpt.data().setName("New Category"); </code><br>
 * <code>Category cat = cpt.create(); </code>
 * 
 * @author Anton Spaans
 */
public class CategoryPrototype extends PrototypeHelper<Category> implements ISmugObjectPrototype<Category> {

	private static final String CACHE = "Category";
	
	private final Data<Category> mData; 

	/**
	 * Creates a new instance of a Category Prototype.
	 * 
	 * @param session Session-account information.
	 */
	public CategoryPrototype(Session session) {
		mData = new Data<Category>(session);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#create()
	 *  <code>CategoryPrototype cp = new CategoryPrototype(session); </code><br/>
	 *	<code>// Create a new category with name SomeName </code><br/>
	 *	<code>cp.data().setName("SomeName"); </code><br/>
	 *	<code>Category c = cp.<b>create();</b></code><br/>
	 */
	public Category create() throws SmugmugException {
		if (mData.mName.isUndefined()) 
			throw new SmugmugException("The Title is not set for "+mData);

		Create.CreateResponse resp = new Create().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				mData.getName());

		SmugmugException.check(resp);
		
		final Category ret = new Category(mData.getSession(), resp.getCategoryID(), mData.getName()); 
		return postCreate(ret);
	}

	/** @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll() */
	public Collection<Category> getAll() throws SmugmugException {
		Get.GetResponse resp = new Get().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				mData.getSession().getOtherAccount().getNickName(), 
				mData.getSession().getOtherAccount().getSitePassword());

		SmugmugException.check(resp);

		Collection<com.kallasoft.smugmug.api.json.entity.Category> list = resp.getCategoryList();
		Collection<Category> retList = new ArrayList<Category>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.Category cat : list) {
			final Category myCat = new Category(mData.getSession(), cat.getID(), cat.getName());
			myCat.sync();
		
			retList.add(myCat);
		}

		return postGetAll(retList, true, GUID.NO_GUID);
	}

	/**
	 * This is the same as {@link #getAll()}
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll(java.lang.String)
	 */
	public Collection<Category> getAll(GUID notUsed) throws SmugmugException {
		return getAll();
	}
	
	/** 
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll() 
	 *  <code>CategoryPrototype cp = new CategoryPrototype(session); </code><br/>
	 *	<code>// Create a new category with name SomeName </code><br/>
	 *	<code>cp<b>.data()</b>.setName("SomeName"); </code><br/>
	 *	<code>Category c = cp.create();</code><br/>
	 */
	public Data<Category> data() {
		return mData;
	}
	
	/**
	 * 
	 */
	public Class<Category> getClassForPrototype() {
		return Category.class;
	}
	
	/** 
	 * Instances of this class hold the prototype-data for creation of Category-s.<br/>
	 * This class also functions as a base-class for Category-s and SubCategoryPrototype.Data.
	 * @param <T> Category or SubCategory.
	 */
	public static class Data<T extends SmugObjectData<?>> extends SmugObjectData<T> {
		static final long serialVersionUID = 2735651541427397580L;

		protected Data(Session session) {
			super(session);
			bundle();
		}
		
		protected Data(Session session, int ID, String name) {
			super(session, ID);
			mName.set(name);
			bundle();
		}
		
		protected String getCacheName() {
			return CACHE;
		}
		
		/* Attribute list */
		protected final SmugAttribute<String>  mName = new SmugAttribute<String>("Name");
		
		/**
		 * Sets the name/title for the (Sub)Category
		 * 
		 * @param name
		 *            The name
		 */
		public void setName(String name) {
			mName.set(name);
		}
		
		/** @return The (Sub)Category's name. */
		public String getName() {
			return mName.get();
		}
	}
}
