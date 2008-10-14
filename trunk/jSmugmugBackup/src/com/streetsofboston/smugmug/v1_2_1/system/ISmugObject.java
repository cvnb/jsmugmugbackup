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

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * This interface defines all the methods that can be invoked publicly on every 
 * type of Smugmug Object. 
 * 
 * Smugmug objects of type T need to implement this interface. 
 * Classes that implement this interface represent actual objects (of type T)
 * from the Smugmug repository (e.g. Catetory, Album, etc). 
 *
 * @param <T>
 * @author Anton Spaans
 */
public interface ISmugObject<T> extends IHasID {
	
	/**
	 * Obtains detailed information from the repository or cache for this object.
	 * Some objects require a password to obtain (part of) this detailed information (see setObjectPassword).
	 * 
	 * The level of detail is determined by the current session-account. The owner of this
	 * object gets all the information. Any other account may only get part of the object's data.

	 * @return this.
	 * @throws SmugmugException
	 */
	public T getInfo() throws SmugmugException;
	
	/**
	 * If this object has changed, the changes will be sent to the Smugmug repository.
	 * 
	 * Only owners of this object can update the object.
	 * 
	 * @return this.
	 * @throws SmugmugException
	 */
	public T update() throws SmugmugException;
	
	/**
	 * Deletes this object from the Smugmug repository.
	 * 
	 * Only owners of this object can delete the object.
	 * 
	 * @throws SmugmugException
	 */
	public void delete() throws SmugmugException;
	
	/**
	 * Returns true if one or more of this object's attributes has changed,
	 * compared to their known values in the Smugmug repository.
	 * @return True if this object has changed.
	 */
	public boolean hasChanged();

	/**
	 * Clear all attributes of this object.
	 * This is the same as calling object.setXXXXXX(null) for *each* attribute.
	 */
	public void clear();
	
	/**
	 * Un-does all the changes made to this object. It reverts the object
	 * back to its state when it was retrieved from the Smugmug repository.
	 */
	public void undo();

	/**
	 * Gets the working session.
	 * @return The working session.
	 */
	public Session getSession();
	
	/**
	 * @return the object's password (if any) or null.
	 */
	public String getObjectPassword();
	
	/**
	 * Sets the object's password. 
	 * @param password Object password.
	 */
	public void setObjectPassword(String password);
	
	/**
	 * @return True only if this object is owned by the currently logged-in user(=session-account) or
	 * if this object is publicly available from Smugmug itself (e.g. public Categories that can be
	 * used by every user/Account). This means that update and delete don't always work when this method
	 * returns true (e.g. for public Categories).
	 * @throws SmugmugException
	 */
	public boolean isOwnedByLoggedInUser() throws SmugmugException;

	/**
	 * @return The object owner's nick-name.
	 * @throws SmugmugException
	 */
	public String getOwnerNickName() throws SmugmugException;

	/**
	 * Locks this object for any modification from other thread.
	 */
	public void lock();

	/**
	 * Unlocks this object from any modification from other threads,
	 * so that other threads can modify this object again. 
	 */
	public void unlock();

	/**
	 * Invalidates this object: Its ID will be cleared and it will be removed from the cache.
	 * This object should be called after an unsuccessful operation (e.g. update/getInfo/delete) has occurred
	 * and it is determined/assumed that the object no longer exists in the Smugmug repository (e.g.
	 * someone deleted the object using the Smugmug site itself).
	 * 
	 * The SmugFig API system will do its best to automatically invalidate objects. But sometimes this
	 * doesn't work and the user of this object needs to call this methods itself.
	 * Calling invalidate on already invalidated object is OK: This method will do nothing. 
	 */
	public void invalidate();
	
	/**
	 * @return The ID of the parent.
	 */
	public GUID getParentId();
}
