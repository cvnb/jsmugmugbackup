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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * This class represents attributes for Smugmug objects.
 * Each attribute holds two values, each of type T (T can be Integer, String, Boolean, etc).
 * One value is the backed-value, the other is the new-value.
 * The backed-value is the value of the object's attribute as it is known from the Smugmug repository.
 * The new-value is the value as set/changed by the user of the object.
 * @param <T>
 * @author Anton Spaans
 */
public class SmugAttribute<T> implements Serializable {
	static final long serialVersionUID = 1457880568807664636L;
	
	private transient boolean mReturnNullIfNotChanged = false;
	
	private Lock mModifyLock = NON_LOCK;
	
	/**
	 * Name of the attribute.
	 */
	private final String mName;
	/**
	 * The attribute's backed-value. 
	 */
	private T mBackedValue;
	/**
	 * The attribute's new-value.
	 */
	private T mNewValue;

	/**
	 * Sets the attribute's name.
	 * @param name
	 */
	public SmugAttribute(String name) {
		mName = name;
	}

	/**
	 * @return The name of the attribute.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Returns true if the new-value is different than the backed-value.
	 * In short, hasChanged() will return true if the user has set this attribute
	 * to a new value.
	 * @return True if changed.
	 */
	public boolean hasChanged() {
		return mNewValue == null 
			? false
			: !mNewValue.equals(mBackedValue);
	}
	
	/**
	 * Determines if attribute has a null/undefined value.
	 * This happens if the new-value is null and the backed-value is null.
	 * @return True if the attribute is undefined.
	 */
	public boolean isUndefined() {
		return mNewValue == null && mBackedValue == null;
	}
	
	/**
	 * Returns the attribute's value.
	 * If the attribute has changed by the user, the user's value is returned (new-value).
	 * If the attribute has not changed by the user, the backed-value is returned.
	 * @return The attribute's value.
	 */
	public T get() {
		if (mReturnNullIfNotChanged && !hasChanged())
			return null;
		
		return mNewValue != null ? mNewValue : mBackedValue;
	}

	/**
	 * Sets the attribute to a new value (represented by the new-value).
	 * @param value The new value.
	 */
	public void set(T value) {
		mModifyLock.lock(); try { mNewValue = value; } finally { mModifyLock.unlock(); }
	}
	
	/**
	 * Sets the attribute to a new value (represented by the new-value).
	 * @param value The new value.
	 */
	public void set(SmugAttribute<T> value) {
		mModifyLock.lock(); try { mNewValue = value.mNewValue; } finally { mModifyLock.unlock(); }
	}

	/**
	 * Sets both the new and backed-value to null. It makes the 
	 * attribute 'undefined'. When an undefined attribute is sent to Smugmug,
	 * its value will not be used (setting to 'null' is not allowed) and ignored
	 * by the request in which this attribute is used.
	 * @return This object. 
	 */
	public T clear() {
		mModifyLock.lock(); 
		try {
			T oldValue = get();
			mNewValue = mBackedValue = null; 
			return oldValue;
		} finally { mModifyLock.unlock(); }
	}
	
	/**
	 * Returns the attribute's backed-value.
	 * @return The backed-value.
	 */
	public T getBackedValue() {
		return mBackedValue;
	}
	
	/**
	 * Reverts the attributes value back to the backed-value.
	 * It does so by simply setting mNewValue to null.
	 * E.g: 
	 * 	attr.get()==x; attr.set(y); attr.get()==y; attr.undo(); attr.get()==x;
	 * where x is the backed-value, y the new-value.
	 */
	public void reset() {
		mModifyLock.lock(); try { mNewValue = null; } finally { mModifyLock.unlock(); }
	}
	
	/**
	 * Do NOT call this method if you're not sure what you're doing. It may 
	 * affect the change-management and caching mechanisms.
	 * 
	 * Assigns a new value to the backed-value of this attribute.
	 * @param value new value for this attribute.
	 */
	public void assign(T value) {
		mModifyLock.lock(); try { mBackedValue = value; } finally { mModifyLock.unlock(); }
	}
	
	/**
	 * Do NOT call this method if you're not sure what you're doing. It may 
	 * affect the change-management and caching mechanisms.
	 * 
	 * Assigns a new value to the backed-value of this attribute.
	 * @param value new value for this attribute.
	 */
	public void assign(SmugAttribute<T> value) {
		mModifyLock.lock(); try { mBackedValue = value.mBackedValue; } finally { mModifyLock.unlock(); }
	}

	/**
	 * Do NOT call this method if you're not sure what you're doing. It may 
	 * affect the change-management and caching mechanisms.
	 *
	 * This method changes the backed-value to have the same value as the new-value.
	 * This method is called after object-creations and updates.
	 */
	public void sync() {
		mModifyLock.lock(); 
		try { 
			if (mNewValue!=null) {
				mBackedValue = mNewValue;
				mNewValue    = null;
			}
		} finally { mModifyLock.unlock(); }
	}

	//@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SmugAttribute))
			return false;
		
		SmugAttribute<?> otherObj = (SmugAttribute<?>)obj;
		if (!mName.equals(otherObj.mName))
			return false;
		else 
			return isEqual(otherObj.get());
	}
	
	public int hashCode() {
		return mName.hashCode();
	}
	
	public String toString() {
		return getName()+
			(isUndefined() 
				? ""
				: "="+get()+
					(hasChanged() 
						? "("+getBackedValue()+")"
						: ""
					)
			);
	}
	
	protected boolean isEqual(Object otherValue) {
		final T thisValue = get();
		if (thisValue == null && otherValue == null)
			return true;
		else if (thisValue == null)
			return false;
		else if (otherValue == null)
			return false;
		else
			return thisValue.equals(otherValue);
	}
	
	protected void setReturnNullIfNotChanged(boolean set) {
		mModifyLock.lock(); try { mReturnNullIfNotChanged = set; } finally { mModifyLock.unlock(); }
	}
	
	protected void setModifyLockOwner(Lock lock) {
		if (lock == null) {
			mModifyLock = NON_LOCK;
		}
		mModifyLock = lock;
	}
	
	private static class NonLock implements Lock {
		public void lock() {}
		public void lockInterruptibly() throws InterruptedException {}
		public Condition newCondition() {return null;}
		public boolean tryLock() {return false;}
		public boolean tryLock(long time, TimeUnit unit) {return false;}
		public void unlock() {}
	}
	private static final Lock NON_LOCK = new NonLock(); 
}
