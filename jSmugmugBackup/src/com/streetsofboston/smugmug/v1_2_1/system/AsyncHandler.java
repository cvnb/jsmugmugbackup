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

import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.streetsofboston.smugmug.v1_2_1.Image;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * Use this class to execute ISmugObject or ISmugObjectPrototype methods asynchronously.
 * Most methods take the ISmugObject or ISmugObjectPrototype instance and an optional AsyncHandler.IReady listener.
 * 
 * When the asynchronous methods has finished doing its work, the specified IReady instance is called to
 * notify that the operation has finished or has failed (the 'SmugmugException se' parameter is set to a non-
 * null value).
 * @author Anton Spaans
 * 
 */
public abstract class AsyncHandler {
	
	private static final ExecutorService EXECUTORS;
	
	static { 
		// TODO Read pool size from config/property file.
		final int poolSize = 6;
		EXECUTORS = Executors.newFixedThreadPool(poolSize);
	}

	/**
	 * Class that want to listen when a Smugmug operation has finished need to implement this
	 * interface. It will be called when the operation has succeeded or failed. 
	 * @author Anton Spaans.
	 *
	 * @param <T>
	 * @param <A>
	 */
	public interface IReady<T, A> {
		/**
		 * This method is called when the Smugmug operation has succeeded (se is null) or when it 
		 * has failed (se is not null).
		 * @param subSetId TODO
		 * @param smugObject The ISmugObject or ISmugObjectPrototype instance on which the operation took place.
		 * @param returnValue Return value of the Smugmug operation.
		 * @param se If not null, an error occurred.
		 * @param ID An optional GUID. It is set if it was set in the original request as well (as subSetId).
		 */
		public void isReady(GUID subSetId, T smugObject, A returnValue, SmugmugException se);
	}
	
	/**
	 * Login asynchronously.
	 * @param userName The user's name.
	 * @param password The password associated with the user-name.
	 * @param readyListener The listener, being notified when the login succeeded or failed.
	 * @return A cancel-able task.
	 */
	public static 
	Future<?> login(final String userName, final String password, final IReady<String,Session> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Session session = Session.login(userName, password);
					//session.me().getSmugObjectsHierarchy();
					if (readyListener != null) 
						readyListener.isReady(null, userName, session, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, userName, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Execute teh provided action as soon as possible in the future.
	 * @param doAction The action to be performed.
	 * @return A cancel-able task to be run asap in the future.
	 */
	public static Future<?> doFutureAction(final Runnable doAction) {
		final FutureTask<?> ft = new FutureTask<Object>(doAction, null);
		EXECUTORS.execute(ft);
		return ft;
	}
	
	/**
	 * Re-login asynchronously.
	 * @param session The session that needs to be re-established.
	 * @param password The password associated with the user-name.
	 * @param readyListener The listener, being notified when the login succeeded or failed.
	 * @return A cancel-able task.
	 */
	public static 
	Future<?> reLogin(final Session session, final IReady<String,Session> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Session newSession = session.reLogin();
					//session.me().getSmugObjectsHierarchy();
					if (readyListener != null) 
						readyListener.isReady(null, session.getNickName(), newSession, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, session.getNickName(), null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Re-login asynchronously.
	 * @param session The session that needs to be re-established.
	 * @param password The password associated with the user-name.
	 * @param readyListener The listener, being notified when the login succeeded or failed.
	 * @return A cancel-able task.
	 */
	public static 
	Future<?> logout(final Session session, final IReady<String,Session> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					session.logout();
					if (readyListener != null) 
						readyListener.isReady(null, session.getNickName(), session, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, session.getNickName(), null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Delete smugObject asynchronously. If referenceObject is specified, call its isReady method. 
	 * @param <T> A ISmugObject type. Album, Category, etc.
	 * @param <A> Any type of object.
	 * @param smugObject An ISmugObject instance. Can not be null.
	 * @param readyListener The listener, being notified when the delete has ended.
	 * @return A cancel-able task.
	 */
	public static<T extends ISmugObject<?>> 
	Future<?> delete(final T smugObject, final IReady<T,T> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					smugObject.delete();
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, null, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Get information from the smugObject asynchronously. If referenceObject is specified, call its isReady method. 
	 * @param <T> A ISmugObject type. Album, Category, etc.
	 * @param smugObject An ISmugObject instance. Can not be null.
	 * @param readyListener The listener, being notified when the getInfo has ended.
	 * @return A cancel-able task.
	 */
	public static<T extends ISmugObject<?>> 
	Future<?> getInfo(final T smugObject, final IReady<T,T> readyListener)  {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final T ret = (T)smugObject.getInfo();
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Update smugObject asynchronously. If referenceObject is specified, call its isReady method. 
	 * @param <T> A ISmugObject type. Album, Category, etc.
	 * @param smugObject An ISmugObject instance. Can not be null.
	 * @param readyListener The listener, being notified when the update has ended.
	 * @return A cancel-able task.
	 */
	public static<T extends ISmugObject<?>> 
	Future<?> update(final T smugObject, final IReady<T,T> readyListener)  {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final T ret = (T)smugObject.update();
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugObject, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Create an instance of type U
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param smugPrototype The ISmugObjectPrototype object used to create the new ISmugObject.
	 * @param readyListener The listener, notified when the create has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<?>, T extends ISmugObjectPrototype<?>> 
	Future<?> create(final T smugPrototype, final IReady<T,U> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final U ret = (U)smugPrototype.create();
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Get an instance of type U from the Smugmug repository.
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param ID The ISmugObject's ID.
	 * @param smugPrototype The ISmugObjectPrototype object used to get the ISmugObject.
	 * @param readyListener The listener, notified when the get has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<?>, T extends ISmugObjectPrototype<?>> 
	Future<?> get(final int ID, final T smugPrototype, final IReady<T,U> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final U ret = (U)smugPrototype.get(ID);
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Get all instances of type U from the Smugmug repository.
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param smugPrototype The ISmugObjectPrototype object used to get all the ISmugObjects.
	 * @param readyListener The listener, notified when the get has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<T>, T extends ISmugObjectPrototype<U>> 
	Future<?> getAll(final T smugPrototype, final IReady<T,Collection<U>> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Collection<U> ret = smugPrototype.getAll();
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Get all instances of type U from the Smugmug repository.
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param subSetId The ISmugObject's ID.
	 * @param smugPrototype The ISmugObjectPrototype object used to get all the ISmugObjects.
	 * @param readyListener The listener, notified when the get has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<?>, T extends ISmugObjectPrototype<U>> 
	Future<?> getAll(final GUID subSetId, final T smugPrototype, final IReady<T,Collection<U>> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Collection<U> ret = smugPrototype.getAll(subSetId);
					if (readyListener != null) 
						readyListener.isReady(subSetId, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(subSetId, smugPrototype, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}

	/**
	 * Get all instances of type U from the Smugmug cache.
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param smugPrototype The ISmugObjectPrototype object used to get all the ISmugObjects.
	 * @param readyListener The listener, notified when the get has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<?>, T extends ISmugObjectPrototype<U>> 
	Future<?> getAllFromCache(final T smugPrototype, final IReady<T,Collection<U>> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Collection<U> ret = (Collection<U>)smugPrototype.getAllFromCache();
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(null, smugPrototype, null, se);
				}
			}
		};

		return doFutureAction(doAction);
	}

	/**
	 * Get all instances of type U from the Smugmug cache.
	 * @param <U> An ISmugObject type, e.g. Category, Album, etc.
	 * @param <T> An ISmugObjectPrototype type, e.g. CategoryPrototype, AlbumPrototype, etc.
	 * @param subSetId The ISmugObject's ID.
	 * @param smugPrototype The ISmugObjectPrototype object used to get all the ISmugObjects.
	 * @param readyListener The listener, notified when the get has ended.
	 * @return A cancel-able task.
	 */
	public static<U extends ISmugObject<?>, T extends ISmugObjectPrototype<U>> 
	Future<?> getAllFromCache(final GUID subSetId, final T smugPrototype, final IReady<T,Collection<U>> readyListener) {
		final Runnable doAction = new Runnable() {
			public void run() {
				try {
					final Collection<U> ret = smugPrototype.getAllFromCache(subSetId);
					if (readyListener != null) 
						readyListener.isReady(subSetId, smugPrototype, ret, null);
				} catch (SmugmugException se) {
					if (readyListener != null) 
						readyListener.isReady(subSetId, smugPrototype, null, se);
				}
			}
		};
		
		return doFutureAction(doAction);
	}
	
	public static Future<?> loadImageFromURL(final URL url, final Image smugImage, final ImageObserver imageObserver) {
		final Runnable doAction = new Runnable() {
			public void run() {
				java.awt.Image image = null;
				try {
					final byte[] rawData = smugImage.write(null, url);
					if (rawData != null && rawData.length > 0) 
						image = Toolkit.getDefaultToolkit().createImage(rawData, 0, rawData.length);
				} catch (SmugmugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					try {
						imageObserver.imageUpdate(image, 
						           image!=null ? MediaTracker.COMPLETE : MediaTracker.ERRORED, 
						           0, 0, 
						           -1, -1);
					} catch (Exception e) {}
				}
			}
		};
		
		return doFutureAction(doAction);
	}
}
