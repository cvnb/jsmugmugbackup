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
package com.streetsofboston.smugmug.v1_2_1.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * This is a helper class for objects implementing ISmugObjectPrototype and ISmugObject.
 * Do not call any of its methods directly.
 * @param <T> A type subclassing SmugObjectData.
 * @author Anton Spaans.
 */
public abstract class PrototypeHelper<T extends SmugObjectData<?>> {
	
	/**
	 * This method should not be called directly.
	 * 
	 * Called after an Update or Refresh.
	 * It makes sure that the Smugmug object is in sync with the Smugmug repository.
	 * 
	 * @param <T> Type of the Smugmug object.
	 * @param smugObject The Smugmug object.
	 * @return The updated Smugmug object.
	 */
	static public<T extends SmugObjectData<T>> T postUpdate(T smugObject) {
		smugObject.sync();
		return smugObject;
	}

	/**
	 * This method should not be called directly.
	 * 
	 * Called after a Delete.
	 * It makes sure that the Smugmug object is deleted from the cache and
	 * that its ID is cleared.
	 * 
	 * @param <T> Type of the Smugmug object.
	 * @param smugObject The Smugmug object.
	 */
	static public<T extends SmugObjectData<T>> void postDelete(T smugObject) {
		smugObject.getCache().remove(smugObject);
		smugObject.clearID();
	}
	
	/**
	 * This method should not be called directly.
	 * 
	 * Called after a Refresh.
	 * It makes sure that the Smugmug object is refreshed with data from the 
	 * Smugmug repository.
	 * 
	 * @param smugObject The Smugmug object.
	 * @return The fresh Smugmug object.
	 * @throws SmugmugException
	 */
	public T refresh(T smugObject) throws SmugmugException {
		// Get a fresh category from Smugmug, given the current ID.

		int ID = smugObject.clearID();
		//Just get it again. 'smugObject' object will be updated automatically.
		T object = get(ID);
		object.setID(ID);
		return object;
	}
	
	/**
	 * This method implements the ISmugObjectPrototype.get(ID) for (almost) all
	 * types of Smugmug objects.
	 * It calls 'getAll()' to make sure all the objects of type T are fetched from
	 * the Smugmug repository (or cache) and returns the one with the given ID.
	 * 
	 * @param ID The ID of the Smugmug object (of type T) to retrieve.
	 * @return The desired Smugmug object.
	 * @throws SmugmugException
	 */
	public T get(int ID) throws SmugmugException {
		if (ID == IHasID.NO_ID) 
			throw new SmugmugException("The ID is not specified.");
		
		T findCat = data().findInCache(ID);
		if (findCat != null)
			return findCat;
		
		final Collection<T> all = getAll();
		for (T cat : all) {
			if (cat.getID() == ID) {
				return cat;
			}
		}

		return null;
	}

	public T get(GUID ID) throws SmugmugException {
		return get(ID.getIntID());
	}
	
	/**
	 * This method should not be called directly.
	 * 
	 * Called after a Create.
	 * It make sure that the Smugmug object is in sync with the Smugmug 
	 * repository and adds it to the cache.
	 * 
	 * @param smugObject
	 * @return
	 */
	protected T postCreate(T smugObject) {
		smugObject.sync();
		
		smugObject.setOwnerNickName(data().getSession().me().getNickName());
		
		smugObject.addToCache();
		return smugObject;
	}
	
	/**
	 * This method should not be called directly.
	 * 
	 * Called after a GetAll.
	 * It adds the collection of Smugmug objects to the cache.
	 *  
	 * @param smugObjects
	 * @param assignOwner If true, assign the session-account as the owner of all the objects.
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	// Help: How to get rid of this warning without the SuppressWarnings directive.
	protected Collection<T> postGetAll(Collection<T> smugObjects, boolean assignOwner, GUID parentId) {
		if (assignOwner) {
			final String  ownerName = data().getSession().getNickName(); 
			for (T object : smugObjects) {
				object.setOwnerNickName(ownerName);
			}
		}
		return (Collection<T>)data().getCache().addAll(smugObjects, parentId);
	}
	
	public Collection<T> getAllFromCache() throws SmugmugException {
		return getAllFromCache(GUID.NO_GUID);
	}

	public Collection<T> getAllFromCache(GUID subSetId) throws SmugmugException {
		final Date lastRefreshDate = data().getCache().getLastRefreshTime(subSetId);
		if (lastRefreshDate == null)
			this.getAll(subSetId);
				
		final Collection<T> objs = (Collection<T>)data().getCache().cachedSmugObjects(subSetId);
		final String requestingNickName = data().getSession().getNickName();
		final Collection<T> retList = new ArrayList<T>();

		for (T smugObj : objs) {
			String nickName;
			try {
				nickName = smugObj.getOwnerNickName();
			} 
			catch (SmugmugException e) {
				nickName = null;
			}

			if ((nickName == null || nickName.length() == 0) && 
				(requestingNickName == null || requestingNickName.length() == 0))
				retList.add(smugObj);
			else if (nickName != null && nickName.equals(requestingNickName))
				retList.add(smugObj);
		}
		
		return retList;
	}
	
	/**
	 * This method should not be called directly.
	 * 
	 * Called after a Get/GetAll.
	 * It adds the Smugmug object to the cache.
	 *  
	 * @param smugObject
	 * @return
	 */
	protected Collection<T> postGetAll(T smugObject) {
		final Collection<T> listOfOneImage = new ArrayList<T>();
		listOfOneImage.add(smugObject);
		return postGetAll(listOfOneImage, false, null);
	}
	
	/**
	 * Returns all objects of type T from Smugmug's repository accessible by the current session-account.
	 * @return All objects of type T from Smugmug's repository.
	 * @throws SmugmugException
	 */
	public abstract Collection<T> getAll() throws SmugmugException;
	
	/**
	 * Returns all objects of type T from Smugmug's repository accessible by the current session-account.
	 * @param subSetId The parentId identifying a particular subset of these objects.
	 * @return All objects of type T from Smugmug's repository.
	 * @throws SmugmugException
	 */
	public abstract Collection<T> getAll(GUID subSetId) throws SmugmugException;

	/**
	 * Returns an object's (of type T) prototype data. This data is used to
	 * create new objects (see also create()).
	 * @return The prototype data.
	 */
	public abstract SmugObjectData<T> data();
	
	/**
	 * Returns the classes that are generated by this prototype.
	 * @return The class.
	 */
	public abstract Class<T> getClassForPrototype();
}
