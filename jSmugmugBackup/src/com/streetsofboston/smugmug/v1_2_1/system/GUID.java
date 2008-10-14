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

import java.io.Serializable;

/**
 * This class uniquely identifies any Smugmug Object, regardless of its type.
 * @author Anton Spaans
 *
 */
public class GUID 
	implements Serializable, Comparable<GUID> {

	private static final long serialVersionUID = -1822943470387810245L;

	/**
	 * Represents an object without an ID.
	 */
	public static final GUID NO_GUID = new GUID("", IHasID.NO_ID);

	/**
	 * Creates a GUID for a Smugmug Object of type T, given its integer-id.
	 * @param <T> Album, Category, Image, etc.
	 * @param prototype An instance of an Smugmug Object Prototype.
	 * @param ID The requested integer ID.
	 * @return The proposed GUID for an object of type T
	 */
	public static<T extends ISmugObject<T>> GUID create(ISmugObjectPrototype<T> prototype, int ID) {
		return create(prototype.getClassForPrototype(), ID);
	}
	
	/**
	 * Creates a GUID for a Smugmug Object of type T, given its integer-id.
	 * @param <T> Album, Category, Image, etc.
	 * @param smugObject An instance of an Smugmug Object.
	 * @param ID The requested integer ID.
	 * @return The proposed GUID for an object of type T
	 */
	public static<T extends ISmugObject<?>> GUID create(T smugObject, int ID) {
		return create(smugObject.getClass(), ID);
	}

	/**
	 * Creates a GUID for a Smugmug Object of type T, given its integer-id.
	 * @param <T> Album, Category, Image, etc.
	 * @param smugObject An instance of an Smugmug Object.
	 * @param ID The requested integer ID.
	 * @return The proposed GUID for an object of type T
	 */
	protected static<T extends SmugObjectData<?>> GUID create(SmugObjectData<T> smugObject, int ID) {
		if (smugObject instanceof ISmugObject) {
			ISmugObject<?> smugT = (ISmugObject<?>)smugObject;
			return create(smugT.getClass(), ID);
		}
		return NO_GUID;
	}

	/**
	 * Creates a GUID for a Smugmug Object of type T, given its integer-id.
	 * @param <T> Album, Category, Image, etc.
	 * @param smugClass The class of a Smugmug Object (e.g. Category.class, Album.class)
	 * @param ID The requested integer ID.
	 * @return The proposed GUID for an object of type T
	 */
	public static<T extends ISmugObject<?>> GUID create(Class<T> smugClass, Integer  ID) {
		if (ID == null)
			return null;
		
		final int    tKlassId   = smugClass.hashCode();
		final String tKlassGUID = Integer.toHexString(tKlassId);
		final String itemID     = Integer.toHexString(ID);

		return new GUID(tKlassGUID+"-"+itemID, ID);
	}

	/**
	 * Create a GUID from another GUID.
	 * @param guid The GUID as a String.
	 * @return A new GUID that is equal to guid.
	 */
	public static GUID create(String guid) {
		return new GUID(guid);
	}
	
	/**
	 * Create a GUID from another GUID.
	 * It effectively makes a copy.
	 * @param guid The GUID as a String.
	 * @return A new GUID that is equal to guid.
	 */
	public static GUID create(GUID guid) {
		return new GUID(guid.mGUID, guid.mID);
	}
	
	private final String  mGUID;
	private final Integer mID;
	private String  mKey;
	
	private GUID(String id) {
		mGUID = id; 
		mID   = null;
		mKey  = null;
	}

	private GUID(String id, Integer ID) {
		mGUID = id; 
		mID   = ID;
		mKey  = null;
	}
	
	/**
	 * Set the Smugmug key for the object. (for images and albums)
	 * @param key
	 * @return this GUID.
	 */
	public GUID setKey(String key) {
		mKey = key;
		return this;
	}

	/**
	 * Returns the Smugmug key for this object. (for images and albums)
	 * @return
	 */
	public String getKey() {
		return mKey;
	}
	
	/**
	 * Returns the Integer representation of this guid.
	 * @param guid
	 * @return An integer or null.
	 */
	public static Integer getGUIDInteger(GUID guid) {
		return guid != null ? guid.getIntID() : null;
	}
	
	/**
	 * Returns the String representation of this guid.
	 * @param guid
	 * @return A string or null.
	 */
	public static String getGUIDString(GUID guid) {
		return guid != null ? guid.guid() : null;
	}
	
	/**
	 * Returns the key of this guid.
	 * @param guid
	 * @return A string or null.
	 */
	public static String getGUIDKey(GUID guid) {
		return guid != null ? guid.getKey() : null;
	}

	/**
	 * @return True only if this GUID is a proper guid (a string value that has one or more characters).
	 */
	public boolean hasGUID() {
		return mGUID.length() > 0;
	}
	
	/**
	 * @return The GUID as a String.
	 */
	public String guid() {
		return mGUID;
	}
	
	/**
	 * If the Smugmug Object having this GUID has an integer-id as well, this returns true.
	 * @return True if GUID is backed by an integer value.
	 */
	public boolean hasIntID() {
		return mID != null && mID != IHasID.NO_ID;
	}
	
	/**
	 * If the Smugmug Object having this GUID has an integer-id as well, return the value of this ID.
	 * @return Integer value of this GUID.
	 */
	public int getIntID() {
		return mID != null ? mID.intValue() : IHasID.NO_ID;
	}

	/**
	 * @see #guid()
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mGUID;
	}
	
	/**
	 * Return if the GUIDs are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GUID))
			return false;
		
		return mGUID.equals(((GUID)obj).mGUID);
	}

	/**
	 * Does a string-compare on the string-value of the GUIDs.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(GUID guid) {
		if (guid == null)
			return 1;
		
		return mGUID.compareTo(guid.mGUID);
	}
	
	/**
	 * Returns the hash-code of the GUID's string value.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mGUID.hashCode();
	}
}
