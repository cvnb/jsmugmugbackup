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
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * This class represents the data held by a Smugmug object of type T.
 * 
 * This class is not meant for public use. It is used by SmugFig API to handle
 * data modifications for Smugmug Objects.
 * 
 * @param <T>
 * @author Anton Spaans.
 */
public abstract class SmugObjectData<T extends SmugObjectData<?>> implements Serializable {

	private static final String quotePattern    = "(\"([^\"]+)\"[\\s,;]*)";
	private static final String commaPattern    = "(([^,;]+)\\s*[,;]?)";
	private static final String spacePattern    = "(([^,;\\s]+)\\s*)";

	private static SimpleDateFormat smSmugmugSDF; 
	private static GregorianCalendar smSmugmugGC;

	private static SimpleDateFormat smUserSDF; 
	private static GregorianCalendar smUserGC;
	
	private static NumberFormat smSmugmugDecFmt;

	static {
		// "2007-07-18 08:12:45" == "YYYY-MM-dd HH:mm:ss"
		try {
		smSmugmugGC  = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"), Locale.US);
		smSmugmugSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		smSmugmugSDF.setCalendar(smSmugmugGC);

		final Locale userLocale = Locale.getDefault(); // read from props/settings
		final TimeZone userTZ   = TimeZone.getDefault(); // read from props/settins
		final String userFMT    = "yyyy-MM-dd HH:mm:ss"; // read from props/settings
		
		smUserGC  = new GregorianCalendar(userTZ, userLocale);
		smUserSDF = new SimpleDateFormat(userFMT, userLocale);
		smUserSDF.setCalendar(smUserGC);
		
		smSmugmugDecFmt = DecimalFormat.getNumberInstance(Locale.US);
		smSmugmugDecFmt.setGroupingUsed(false);
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * Utility functions.
	 */
	
	/**
	 * @param keywords
	 * @return The keyword string for an image as needed by the smugmug repository
	 */
	public static String toKeywordsStringForImage(Collection<String> keywords) {
		if (keywords == null) 
			return null;
		
		if (keywords.size() == 0)
			return "";
		
		StringBuffer strBuf = new StringBuffer();
		boolean first = true;
		for (String keyword : keywords) {
			if (!first) {
				strBuf.append(" ");
			}
			first = false;
			strBuf.append('"');
			strBuf.append(keyword.trim());
			strBuf.append('"');
		}
		return strBuf.toString();
	}
	
	public static String toKeywordsStringForAlbum(Collection<String> keywords) {
		if (keywords == null) 
			return null;
		
		if (keywords.size() == 0)
			return "";
		
		StringBuffer strBuf = new StringBuffer();
		boolean first = true;
		for (String keyword : keywords) {
			if (!first) {
				strBuf.append(", ");
			}
			first = false;
			strBuf.append(keyword.trim());
		}
		return strBuf.toString();
	}

	public static ArrayList<String> toKeywords(String keywords) {
		return quoteParse(keywords);
	}
	
	public static String toStr(IEnumValue<?> value) {
		if (value == null)
			return null;
		
		Object val = value.value();
		if (val instanceof Boolean)
			return toBoolean((Boolean)val);
		if (val instanceof Number)
			return toNumber((Number)val);
		else 
			return val.toString();
	}
	
	public static<T> T toValue(IEnumValue<T> value) {
		if (value == null)
			return null;
		
		return value.value();
	}

	public static String toBoolean(Boolean value) {
		if (value == null)
			return null;
		
		return value.booleanValue() ? "1" : "0";
	}

	public static String toNumber(Number value) {
		if (value == null)
			return null;
		
		return smSmugmugDecFmt.format(value);
	}
	
	public static String toNumber(GUID value) {
		if (value == null || !value.hasIntID())
			return null;
		
		return toNumber(value.getIntID());
	}

	public static Date strToDate(String dateStr, boolean useSmugmugSettings) {
		if (dateStr == null || dateStr.length() == 0)
			return null;
		
		final SimpleDateFormat sdf  = useSmugmugSettings ? smSmugmugSDF : smUserSDF;
		try {
			return sdf.parse(dateStr);
		} 
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String dateToStr(Date date, boolean useSmugmugSettings) {
		if (date == null) 
			return null;
		
		final SimpleDateFormat sdf = useSmugmugSettings ? smSmugmugSDF : smUserSDF;
		return sdf.format(date);
	}
	
	public static String toOtherDate(String dateStr, boolean toSmugmugDate) {
		if (dateStr == null) 
			return null;
		
		final Date date = strToDate(dateStr, !toSmugmugDate);
		return dateToStr(date, toSmugmugDate);
	}
	
	/**
	 * A map of caches: Each type of object can have its own Cache.
	 */
	private static final Map<String, Cache<?>> smCacheMap = new TreeMap<String, Cache<?>>();

	/**
	 * The session used to get/send data from/to the Smugmug repository.
	 */
	private Session mSession;
	
	/**
	 * Smugmug nick-name of the owner of this object.
	 */
	protected String mOwnerNickName;
	
	/**
	 * Optional: It is the object's password.
	 * Some objects, that the session does not own, require passwords.
	 * If not provided, no or limited information to that object is provided. 
	 */
	private String mPassword;
	

	/**
	 * Object's ID.
	 */
	private GUID mID = GUID.NO_GUID;
	
	/**
	 * Object's set of Attributes.
	 */
	private AttributeSet mAttributeSet = new AttributeSet();

	/**
	 * Used to construct a 'new' object of type T.
	 * @param session The working session.
	 */
	protected SmugObjectData(Session session) {
		mSession = session;
		clearID();
	}
	
	/**
	 * Used to construct an existing object of type T with a given ID.
	 * @param session The working session.
	 * @param ID The objects ID (as known in the Smugmug repository).
	 */
	protected SmugObjectData(Session session, int ID) {
		this(session);
		setID(ID);
	}

	/**
	 * Clears the object's id.
	 * @return The old/previous id.
	 */
	protected int clearID() {
		final int id = getID();
		setID(IHasID.NO_ID);
		return id;
	}
	/**
	 * Sets the object's ID.
	 * @param ID ID
	 */
	protected void setID(int ID) {
		mID = GUID.create(this, ID);
	}
	/**
	 * Gets the object's ID.
	 * @return The ID of this objects (as known in the Smugmug repository).
	 */
	public int getID() {
		return mID.getIntID();
	}

	/**
	 * Determines if this object has an ID.
	 * @return True if this object has a proper ID.
	 */
	public boolean hasID() {
		return mID.hasIntID() || mID.getIntID() != IHasID.NO_ID;
	}
	
	/**
	 * @return The GUID of this object.
	 */
	public GUID guid() {
		return mID;
	}

	/**
	 * @return null.
	 */
	public GUID getParentId() {
		return null;
	}
	
	/**
	 * Gets the working session.
	 * @return The working session.
	 */
	public Session getSession() {
		return mSession;
	}
	
	/**
	 * @return the object's password (if any) or null.
	 */
	public String getObjectPassword() {
		return mPassword;
	}
	
	/**
	 * Sets the object's password. 
	 * @param password Object password.
	 */
	public void setObjectPassword(String password) {
		mPassword = password;
	}
	
	/**
	 * Copies the data from smugData into this object.
	 * @param smugData the input data.
	 */
	public void set(SmugObjectData<T> smugData) {
		mAttributeSet.set(smugData.mAttributeSet);
	}

	/**
	 * Returns true if one or more of this object's attributes has changed,
	 * compared to their values in the Smugmug repository.
	 * @return True if this object has changed.
	 */
	public boolean hasChanged() {
		return mAttributeSet.hasChanged();
	}

	/**
	 * Clear all attributes of this object.
	 * This is the same as calling object.setXXXXXX(null) for *each* attribute.
	 */
	public void clear() {
		mAttributeSet.clearAttributes();
	}
	
	/**
	 * Un-does all the changes made to this object. It reverts the object
	 * back to its state when it was retrieved from the Smugmug repository.
	 */
	public void undo() {
		mAttributeSet.reset();
	}
	
	/**
	 * @return True only if this object is owned by the currently logged-in user.
	 */
	public boolean isOwnedByLoggedInUser()  {
		try {
			return mSession.me().getNickName().equals(getOwnerNickName());
		} catch (SmugmugException e) {
			return false;
		}
	}
	
	/**
	 * @return The object owner's nick-name.
	 * @throws SmugmugException
	 */
	public String getOwnerNickName() throws SmugmugException {
		return (mOwnerNickName != null && mOwnerNickName.length()>0) ? mOwnerNickName : (mOwnerNickName = figureOutOwnersNickName());
	}
	protected void setOwnerNickName(String name) { mOwnerNickName = name; }
	protected String figureOutOwnersNickName() throws SmugmugException { return mSession.getNickName(); }
	
	/**
	 * This method should not be called unless you really know what you're doing.
	 * Calling it can interfere with caching and change-management.
	 * 
	 * Copies the values from one object, smugData, into this object.
	 * @param smugData The source-object.
	 */
	public void assign(SmugObjectData<T> smugData) {
		mAttributeSet.assign(smugData.mAttributeSet);
		mOwnerNickName = smugData.mOwnerNickName;
	}

	/**
	 * This method should not be called unless you really know what you're doing.
	 * Calling it can interfere with caching and change-management.
	 * 
	 * This method sync's up all the attributes of this object.
	 */
	public void sync() {
		mAttributeSet.sync();
	}
	
	/**
	 * This method should not be called unless you really know what you're doing.  
	 * Calling this method can interfere with caching objects of type T. 
	 */
	public void addToCache() {
		final Cache<T> cache = getCache(getCacheName());
		cache.add(thisAsT());
	}
	
	
	/**
	 * This method should not be called unless you really know what you're doing.
	 * Use ISmugObjectPrototype< T >.get(ID) instead.
	 *   
	 * Finds the object of type T in the cache that has the given ID
	 * @param ID The known ID of the object.
	 * @return null or the found object.
	 */
	public T findInCache(int ID) {
		final T foundObj = getCache(getCacheName()).get(ID);
		return (foundObj != null && foundObj.getID()==ID) ? foundObj : null;
	}
	
	public String toString() {
		return getClass().getSimpleName()+"={("+mOwnerNickName+") ID="+mID.getIntID()+" "+mAttributeSet.toString()+"}";
	}
	
	/**
	 * Gets all SmugAttributes for this object and bundles them inside a
	 * set called 'mAttributeSet'.
	 */
	protected void bundle() {
		bundle((Class<?>)getClass());
	}
	
	protected void setReturnNullIfNotChanged(boolean set) {
		mAttributeSet.setReturnNullIfNotChanged(set);
	}
	
	/**
	 * Locks this object for any modification from other thread.
	 */
	public void lock() {
		mAttributeSet.lock();
	}

	/**
	 * Unlocks this object from any modification from other threads,
	 * so that other threads can modify this object again. 
	 */
	public void unlock() {
		mAttributeSet.unlock();
	}
	
	/**
	 * Invalidates this object: Its ID will be cleared and it will be removed from the cache.
	 * This object should be called after an unsuccessful operation (e.g. update/getInfo) has occurred
	 * and it is determined/assumed that the object no longer exists in the Smugmug repository (e.g.
	 * someone deleted the object using the Smugmug site itself).
	 * 
	 * The SmugFig API system will do its best to automatically invalidate objects. But sometimes this
	 * doesn't work completely and the user of this object needs to call this methods itself.
	 * Calling invalidate on already invalidated object is OK: This method will do nothing. 
	 */
	public void invalidate() {
		final int ID = getID();
		if (ID == IHasID.NO_ID)
			return;
		
		final Cache<T> cache = getCache(getCacheName());
		final T foundObj = cache.get(ID);
		if (foundObj == this) {
			cache.remove(this);
			clearID();
		}
	}
	
	public boolean equalsSetValues(SmugObjectData<?> obj) {
		if (obj == null)
			return false;
		
		return mAttributeSet.equalsSetValues(obj.mAttributeSet);
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SmugObjectData))
			return false;
		
		final SmugObjectData<?> smugobj = (SmugObjectData<?>)obj;
		return mAttributeSet.equals(smugobj.mAttributeSet);
	}
	
	/**
	 * Returns a unique name that is used for the cache that caches objects of type T.
	 * @return A unique name for the cache.
	 */
	protected abstract String getCacheName();
	
	private void bundle(Class<?> klass) {
		final Field[]  fields  = klass.getDeclaredFields();
		
		for (Field field : fields) {
			if (!SmugAttribute.class.isAssignableFrom(field.getType()))
				continue;
			
			try {
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				final SmugAttribute<?> attr = (SmugAttribute<?>)field.get(this);
				if (attr!=null)
					mAttributeSet.add(attr);
				field.setAccessible(isAccessible);
				
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
		
		Class<?> superClass = klass.getSuperclass();
		if (SmugObjectData.class.isAssignableFrom(superClass))
			bundle(superClass);
	}
	
	/**
	 * This method should not be called unless you really know what you're doing. 
	 * Calling it in-it-self won't hurt. But then using the returned Cache<T> object can
	 * interfere with cache-management.
	 *  
	 * Returns the cache for objects of type T.
	 * @return The object-type's cache.
	 */
	public Cache<T> getCache() {
		return getCache(getCacheName());
	}
	
	private Cache<T> getCache(String cacheName) {
		Cache<?> cache = smCacheMap.get(cacheName);
		if (cache == null) {
			final Cache<T> newCache = new Cache<T>(cacheName);
			smCacheMap.put(cacheName, newCache);
			return newCache;
		}
			
		return getTypeSpecificCache(cache); 
	}

	@SuppressWarnings("unchecked")
	private T thisAsT() {
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * @param cache
	 * @return
	 */
	private Cache<T> getTypeSpecificCache(Cache<?> cache) {
		return (Cache<T>)cache;
	}
	
	/**
	 * 
	 * @author Anton Spaans
	 * This class bundles all the attributes (SmugAttributes) it can find
	 * into this TreeMap. This is done to make change-management a little easier.
	 */
	private class AttributeSet extends TreeMap<String,SmugAttribute<?>> {
		private static final long serialVersionUID = 2170931608263156953L;
	
		private final ReentrantLock mModifyLock = new ReentrantLock();
		
		public void add(SmugAttribute<?> attr) {
			attr.setModifyLockOwner(mModifyLock);
			put(attr.getName(), attr);
		}
		
		public boolean hasChanged() {
			for (SmugAttribute<?> attr : values()) {
				if (attr.hasChanged())
					return true;
			}
			return false;
		}
		
		public void reset() {
			for (SmugAttribute<?> attr : values()) {
				if (attr.hasChanged())
					attr.reset();
			}
		}
		
		public void sync() {
			for (SmugAttribute<?> attr : values()) {
				if (attr.hasChanged())
					attr.sync();
			}
		}
		
		public void clearAttributes() {
			for (SmugAttribute<?> attr : values()) {
				if (attr.hasChanged())
					attr.clear();
			}
		}

		public<U> void set(AttributeSet attrSet) {
			String name;
			SmugAttribute<?> thisAttr, thatAttr;
			for (SmugAttribute<?> attr : attrSet.values()) {
				name = attr.getName();
				
				thisAttr = get(name);
				thatAttr = attr;
				
				
				if (thisAttr != null && thatAttr != null && (thatAttr.get()!=null))
					setSmugAttribute(thisAttr, thatAttr);
			}
		}

		public void assign(AttributeSet attrSet) {
			String name;
			SmugAttribute<?> thisAttr, thatAttr;
			for (SmugAttribute<?> attr : attrSet.values()) {
				name = attr.getName();
				
				thisAttr = get(name);
				thatAttr = attr;
				
				
				if (thisAttr != null && thatAttr != null && (thatAttr.getBackedValue()!=null))
					assignSmugAttribute(thisAttr, thatAttr);
			}
		}

		public String toString() {
			return values().toString();
		}
		
		public void setReturnNullIfNotChanged(boolean set) {
			for (SmugAttribute<?> attr : values()) {
				attr.setReturnNullIfNotChanged(set);
			}
		}
		
		public void lock() {
			mModifyLock.lock();
		}

		public void unlock() {
			mModifyLock.unlock();
		}
		
		public boolean equalsSetValues(SmugObjectData<?>.AttributeSet thatSet) {
			if (thatSet == null)
				return false;

			for (String attrname :keySet()) {
				final SmugAttribute<?> thisValue = get(attrname);
				final SmugAttribute<?> thatValue = thatSet.get(attrname);
				
				if (thisValue.isUndefined())
					continue;
				
				if (!thisValue.equals(thatValue)) {
					; //System.out.println("Unequal values: this = "+thisValue+"; that = "+thatValue);
					// return false;
				}
			}
			return true;
		}
		
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof SmugObjectData<?>.AttributeSet))
				return false;

			final SmugObjectData<?>.AttributeSet setobj = (SmugObjectData<?>.AttributeSet)obj;
			
			for (String attrname :keySet()) {
				final SmugAttribute<?> thisValue = get(attrname);
				final SmugAttribute<?> thatValue = setobj.get(attrname);
				
				if (!thisValue.equals(thatValue)) {
					// System.out.println("Unequal values: this = "+thisValue+"; that = "+thatValue);
					return false;
				}
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		private void setSmugAttribute(SmugAttribute<?> thisAttr, SmugAttribute<?> thatAttr) {
			thisAttr.set((SmugAttribute)thatAttr);
		}
		
		@SuppressWarnings("unchecked")
		private void assignSmugAttribute(SmugAttribute<?> thisAttr, SmugAttribute<?> thatAttr) {
			thisAttr.assign((SmugAttribute)thatAttr);
		}
	}
	
	private static ArrayList<String> quoteParse(String keywordsString) {
		if (keywordsString == null)
			return null;
		
		if (keywordsString.length() == 0)
			return new ArrayList<String>();
		
		final ArrayList<String> retValue = new ArrayList<String>();

		Pattern pat     = Pattern.compile(quotePattern);
		Matcher matcher = pat.matcher(keywordsString);
		
		Set<String> keywords = new TreeSet<String>();
		while (matcher.find()) {
			String matchResult = matcher.group(2);
			if (matchResult.length() > 0)
				keywords.add(matchResult);
		}

		final String remainingNonQuotedWords = matcher.replaceAll("");
		
		if (remainingNonQuotedWords.length() > 0) {
			boolean isSpaceDelimited = 
					remainingNonQuotedWords.indexOf(',')<0 && 
					remainingNonQuotedWords.indexOf(';')<0;

			if (isSpaceDelimited) {
				pat     = Pattern.compile(spacePattern);
				matcher = pat.matcher(remainingNonQuotedWords);
				while (matcher.find()) {
					isSpaceDelimited = true;
					String matchResult = matcher.group(2).trim();
					if (matchResult.length() > 0)
						keywords.add(matchResult);
				}
			}
			else {
				pat     = Pattern.compile(commaPattern);
				matcher = pat.matcher(remainingNonQuotedWords);
				while (matcher.find()) {
					String matchResult = matcher.group(2).trim();
					if (matchResult.length() > 0)
						keywords.add(matchResult);
				}
			}
		}
		
		retValue.addAll(keywords);
		return retValue;
	}
	
//	public static void main(String[] args) {
//		ArrayList<String> list;
//    	list = toKeywords("\"boston\", red       sox, world championship; \"parade\"");
//    	System.out.println(list);
//    	list = toKeywords("anton spaans is mijn naam");
//    	System.out.println(list);
//    	list = toKeywords("qanton spaans, is mijn; naam");
//    	System.out.println(list);
//    	list = toKeywords("\"qanton;,;spaans\",,, \"is    mijn\"; \"naam\" \"toch;zo\"  ");
//    	System.out.println(list);
//    	list = toKeywords("\"qanton spaans\" \"is     mijn\"          \"naam\"");
//    	System.out.println(list);
//    }
}
