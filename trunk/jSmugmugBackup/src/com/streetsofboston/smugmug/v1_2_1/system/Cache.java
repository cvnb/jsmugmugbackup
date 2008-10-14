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
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a Cache that holds instances of Smugmug
 * objects of type T. 
 *  
 * Smugmug objects are identified in the cache by their ID. Since an object's
 * ID can change (object.clearID() for example), the object's key into the cache can
 * be different that its ID: mCacheMap.get(id).getID() != id. If this is the case, then
 * this is a sign for the cache that the object is out-of-sync/invalid/deleted.
 * 
 * This class should never be created/used directly. Instances of this Cache are used
 * by the SmugFig API. Calling methods of this class may interfere  adversely with SmugFig API. 
 * 
 * @param <T> A class that is subclass of SmugObjectData
 * @author Anton Spaans
 */
public class Cache<T extends SmugObjectData<?>> {
	
	private final Map<Integer,T> mCacheMap;
	private final String mName;
	
	private final Map<GUID,Date> mRefreshMap = new TreeMap<GUID,Date>();
	
	/**
	 * Creates an unbounded cache.
	 * @param name The name of the cache.
	 */
	protected Cache(String name) {
		this(name, 0, -1);
	}
	
	/**
	 * Creates a cache that has a minimum and maximum size.
	 * TODO minSize and maxSize are ignored for now.
	 * @param name The name of the cache.
	 * @param minSize minimum size of cache.
	 * @param maxSize maximum size of cache.s
	 */
	protected Cache(String name, int minSize, int maxSize) {
		mCacheMap = new TreeMap<Integer,T>();
		mName     = name;
	}
	
	/**
	 * Add Smugmug object of type T to this cache.
	 * If smugObject is new, it is added to the cached.
	 * If smugObject is already in cache, the cache's object will be updated
	 * with the values from smugObject. The smugObject itself will not be added.
	 * 
	 * @param smugObject
	 * @return The smugmug object added or the smugmug object from the cache that was updated.
	 */
	public T add(T smugObject) {
		if (smugObject == null)
			return null;

		final int srcID = smugObject.getID();
		if (srcID == IHasID.NO_ID)
			return null;
		
		final T foundObj = get(srcID);
		if (foundObj == null) {
			mCacheMap.put(smugObject.getID(), smugObject);
			return smugObject;
		}
		else {
			foundObj.assign(smugObject);
			if (foundObj.getID() != srcID)
				foundObj.setID(srcID);
			return foundObj;
		}
	}
	
	/**
	 * Call add(T smugObject) for each item in the smugObjects collection.
	 * @param smugObjects Collection of smugObjects.
	 * @return The smugObjects or their representations that are already in the cache. 
	 */
	public Collection<T> addAll(Collection<T> smugObjects) {
		if (smugObjects == null) 
			return null;
		
		if (smugObjects.size() == 0) {
			return new ArrayList<T>();
		}
		
		Collection<T> retCol = new ArrayList<T>();
		for(T smugObject : smugObjects) {
			final T ret = add(smugObject);
			if (ret != null) {
				retCol.add(ret);
			}
		}

		return retCol; 
	}

	/**
	 * Call add(T smugObject) for each item in the smugObjects collection.
	 * @param smugObjects Collection of smugObjects.
	 * @param parentId The objects' parentId, used for caching purposes.
	 * @return The smugObjects or their representations that are already in the cache. 
	 */
	public Collection<T> addAll(Collection<T> smugObjects, GUID parentId) {
		final Collection<T> retCol = addAll(smugObjects);
		
		if (parentId != null)
			mRefreshMap.put(parentId, new Date());
		
		return retCol; 
	}

	/**
	 * Determines if an object with a given ID is in the cache.
	 * @param ID Object ID.
	 * @return True if object is in the cache.
	 */
	public boolean has(int ID) {
		if (ID == IHasID.NO_ID)
			return false;
		
		return mCacheMap.containsKey(ID);
	}

	/**
	 * Determines if an object with a given ID is in the cache.
	 * @param ID Object ID.
	 * @return True if object is in the cache.
	 */
	public boolean has(Integer ID) {
		if (ID == null || ID.intValue() == IHasID.NO_ID)
			return false;

		return mCacheMap.containsKey(ID);
	}

	/**
	 * Determines if an object is in the cache.
	 * @param smugObject Smugmug object.
	 * @return True if object is in the cache.
	 */
	public boolean has(T smugObject) {
		if (smugObject == null)
			return false;

		return mCacheMap.containsValue(smugObject);
	}

	/**
	 * Returns the Smugmug object of type T with a given ID from the cache.
	 * @param ID The object's ID.
	 * @return Smugmug object or null when not found.
	 */
	public T get(int ID) {
		if (ID == IHasID.NO_ID)
			return null;
		
		return mCacheMap.get(ID);
	}

	/**
	 * Returns the Smugmug object of type T with a given ID from the cache.
	 * @param ID The object's ID.
	 * @return Smugmug object or null when not found.
	 */
	public T get(Integer ID) {
		if (ID == null || ID.intValue() == IHasID.NO_ID)
			return null;

		return mCacheMap.get(ID);
	}
	
	/**
	 * Removes Smugmug object with the given ID from the cache.
	 * @param ID The object's ID.
	 * @return The object that was removed or null if no object was removed.
	 */
	public T remove(int ID) {
		if (ID == IHasID.NO_ID)
			return null;

		return mCacheMap.remove(ID);
	}
	
	/**
	 * Removes a Smugmug object from the cache.
	 * @param <U> An Image or Album, etc.
	 * @param smugObject The object.
	 * @return The object that was removed or null if no object was removed.
	 */
	public<U extends SmugObjectData<?>> T remove(U smugObject) {
		if (smugObject == null)
			return null;
		
		return remove(smugObject.getID());
	}

	public T test(T smugObject) { return null; }
	
	public Collection<Integer> cachedIDs() {
		return mCacheMap.keySet();
	}
	
	public Collection<T> cachedSmugObjects() {
		return mCacheMap.values();
	}

	public Collection<T> cachedSmugObjects(GUID parentId) {
		if (parentId == null || !parentId.hasGUID())
			return cachedSmugObjects();
		else {
			final Collection<T> allValues = cachedSmugObjects();
			final Collection<T> retList = new ArrayList<T>();
			for (T object : allValues) {
				if (parentId.equals(object.getParentId()))
					retList.add(object);
			}
			
			return retList;
		}
	}
	
	public Date getLastRefreshTime(GUID parentId) {
		if (parentId == null)
			parentId = GUID.NO_GUID;
		
		Date ret = null;
		if (parentId.hasGUID()) {
			ret = mRefreshMap.get(GUID.NO_GUID);
		}
		if (ret == null)
			ret = mRefreshMap.get(parentId);
		
		return ret;
	}

	public String getName() {
		return mName;
	}
	
	public int hashCode() {
		return mName.hashCode();
	}
}
