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

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * This class should never be called or used directly.
 * It defines a Pool of a number of instances of type T.
 * An instance of this pool manages instances of type T that can be used exclusively by the caller/user.
 *
 * E.g. to create a pool of 3 byte arrays, each array 1024 bytes long:
 * 
 * 		Pool.initPool(new byte[][] { new byte[1024], new byte[1024], new byte[1024] });
 * 
 * Now this Pool holds three byte-buffers (1024 bytes each) that can be used exclusively by
 * the caller. The caller obtains/get a resource (byte-buffer) and must release it after use so
 * that it can be used by another caller.
 *  
 * @param <T>
 * @author Anton Spaans
 */
public class Pool<T> {

	private static final Map<Class<?>, Pool<?>> smPools = new Hashtable<Class<?>, Pool<?>>(); 

	/**
	 * Initializes a pool of type T. The size (number of items of type T) in the pool is determined
	 * by length of 'resources'. 
	 * Note that this pool does not allocate/create resources. The caller of this method must allocate
	 * the resource and assign them to this pool by calling this method.
	 * 
	 * @param <U> Type of resource to be managed by this pool.
	 * @param resources The actual already allocated resources.
	 */
	static public<U> void initPool(U[] resources) {
		smPools.put(resources.getClass().getComponentType(), new Pool<U>(resources));
	}
	
	@SuppressWarnings("unchecked")
	static public<U> Pool<U> getPool(Class<U> type) {
		return (Pool)smPools.get(type);
	}
	
	private final Semaphore mSemaphore;
	
	private final T[]       mResources;
	private final boolean[] mIsInUse;
	
	private Pool(T[] resources) {
		mResources = resources;
		mSemaphore = new Semaphore(mResources.length, true);
		mIsInUse = new boolean[mResources.length];
	}

	/**
	 * Obtain a resource of type T for exclusive use.
	 * @return
	 */
	public T get() {
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			return null;
		}
		return getNextAvailableItem();
	}
	
	/**
	 * Release an obtained resource so that it can be used by some other caller.
	 * @param resource
	 */
	public void release(T resource) {
		if (markAsUnused(resource))
			mSemaphore.release();
	}
	
	private synchronized T getNextAvailableItem() {
		for (int i = 0; i < mResources.length; ++i) {
			if (!mIsInUse[i]) {
				mIsInUse[i] = true;
				return mResources[i];
			}
		}
		return null; // not reached
	}
	
	private synchronized boolean markAsUnused(T item) {
		for (int i = 0; i < mResources.length; ++i) {
			if (item == mResources[i]) {
				if (mIsInUse[i]) {
					mIsInUse[i] = false;
					return true;
				} else
					return false;
			}
		}
		return false;
	}
}
