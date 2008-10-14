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

import java.util.Collection;

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * Prototypes are objects that hold data from which real
 * instances can be created. Objects that implement this interface can
 * create and get (from Smugmug) objects of type T.
 * @param <T> A ISmugObject subclass, like Image, Album, etc.
 * @author Anton Spaans. 
 */
public interface ISmugObjectPrototype<T extends ISmugObject<?>> {
	/**
	 * Creates a new object of type T given the data as returned by 'data()'.
	 * It will add this new smugmug object to the Smugmug repository.<br/>
	 * 
	 * The new object that is created, is always created for the current session-account with
	 * which the caller has logged-in.<br/>
	 * 
	 * 
	 * <code>
	 * Session mySession = Session.login("my@mail.com","passwd");<br/>
	 * Session forOtherUser = mySession.forOtherAccount("peter", null);<br/>
	 * // Return all <b>my</b> albums<br/>
	 * AlbumPrototype apMine   = new AlbumPrototype(<b>mySession</b>);<br/>
	 * AlbumPrototype apPeters = new AlbumPrototype(<b>forOtherUser</b>);<br/>
	 * ...<br/>
	 * ...<br/>
	 * apMine.create()   // Creates album for my@mail.com.<br/>
	 * apPeters.create() // Creates album for my@mail.com as well, not for 'peter'.<br/>
	 * </code>
	 *
	 * @return A new object of type T.
	 * @throws SmugmugException
	 */
	public T create() throws SmugmugException;
	
	/**
	 * Retrieves a smugmug object from Smugmug's repository of type T that has
	 * id 'ID'. If the object can not be found or is not accessible by the
	 * current session-account, an ObjectDoesNotExistException is thrown.<br>
	 * <br>
	 * Depending on which smugmug obect type is fetched from the smugmug repository,
	 * the object is owned by the currently logged-in session-account or by the
	 * account returned by a call to Session.forOtherAccount. I.e. depending on the
	 * Session used when creating an instance of this class:
	 * <ul>
	 * <li><b>Category, SubCategory</b><br>
	 * The object must be owned by the account whose nickname is returned by the
	 * session's getNickName() method. If not, an ObjectNotFoundException is
	 * thrown.</li>
	 * <li><b>Album, Image</b><br>
	 * The object must be owned by the session-account that was created when the
	 * caller logged-in. The session's getNickName() (and optional
	 * getSitePassword() and the object's getObjectPassword()) determine only
	 * the level/amount of information that is held by the returned smugmug object.</li>
	 * <li><b>AlbumTemplate</b><br>
	 * The object must be owned by the session-account that was created when the
	 * caller logged-in. The session's getNickName() (and optional
	 * getSitePassword()) are not used at all.
	 * </ul>
	 * <br/>
	 * 
	 * @param ID
	 *            The ID of the object to retrieve.
	 * @return An object of type T from Smugmug's repository.
	 * @throws SmugmugException
	 */
	public T get(int ID) throws SmugmugException;

	/**
	 * Instead of an integer ID, a GUID is provided.
	 * @see #get(int)
	 * @param ID
	 *            The ID of the object to retrieve.
	 * @return An object of type T from Smugmug's repository.
	 * @throws SmugmugException
	 */
	public T get(GUID ID) throws SmugmugException;
	
	/**
	 * Returns all objects of type T from Smugmug's repository that are accessible by the current
	 * session-account.<br/>
	 * 
	 * If the current session-account's 'NickName' and optional site-password are set, the objects
	 * returned are owned by the user with that nick-name. If the 'NickName' is not set, the session-
	 * account currently logged in is the owner of all the returned smugmug objects.<br/>
	 * 
	 * <code>
	 * Session mySession = Session.login("my@mail.com","passwd");<br/>
	 * Session forOtherUser = mySession.forOtherAccount("peter", null);<br/>
	 * // Return all <b>my</b> albums<br/>
	 * Collection&lt;Album> albs = (new AlbumPrototype(<b>mySession</b>)).getAll();<br/> 
	 * // Return all <b>Peter's</b> albums<br/>
	 * Collection&lt;Album> albs2 = (new AlbumPrototype(<b>forOtherUser</b>)).getAll();<br/> 
	 * </code>
	 * @return All Smugmug objects of type T from Smugmug's repository.
	 * @throws SmugmugException
	 */
	public Collection<T> getAll() throws SmugmugException;
	
	/**
	 * Works like {@link #getAll()} but fetches only the collection of objects
	 * that have a parent-id as specified by the subSetId
	 * @param subSetId The id that indicates the parent-id of the returned objects.
	 * @return
	 * @throws SmugmugException
	 */
	public Collection<T> getAll(GUID subSetId) throws SmugmugException;
	
	/**
	 * Returns all objects of type T from the cache.<br/>
	 * No call to the Smugmug will be made. If you'd like to do that use {@link #getAll()} instead.
	 * 
	 * <code>
	 * Session mySession = Session.login("my@mail.com","passwd");<br/>
	 * // Return all <b>cached</b> albums<br/>
	 * Collection&lt;Album> albs = (new AlbumPrototype(<b>mySession</b>)).getAllFromCache();<br/> 
	 * </code>
	 * @return All Smugmug objects of type T from the cache.
	 * @throws SmugmugException
	 */
	public Collection<T> getAllFromCache() throws SmugmugException;
	
	/**
	 * Works like {@link #getAllFromCache()} but fetches only the collection of objects
	 * that have a parent-id as specified by the subSetId
	 * @param subSetId The id that indicates the parent-id of the returned objects.
	 * @return
	 * @throws SmugmugException
	 */
	public Collection<T> getAllFromCache(GUID subSetId) throws SmugmugException;
	
	/**
	 * @see #create() <br/>
	 * Returns an object's (of type T) prototype data. This data is used to
	 * create new smugmug objects of type T.
	 * @return A Smugmug object.
	 */
	public SmugObjectData<?> data();
	
	/**
	 * Returns the class of the instances that are generated by this prototype.
	 * @return The class.
	 */
	public Class<T> getClassForPrototype();
}
