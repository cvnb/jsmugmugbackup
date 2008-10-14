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

import com.streetsofboston.smugmug.v1_2_1.Album;
import com.streetsofboston.smugmug.v1_2_1.AlbumPrototype;
import com.streetsofboston.smugmug.v1_2_1.Category;
import com.streetsofboston.smugmug.v1_2_1.CategoryPrototype;
import com.streetsofboston.smugmug.v1_2_1.SubCategory;
import com.streetsofboston.smugmug.v1_2_1.SubCategoryPrototype;
import com.streetsofboston.smugmug.v1_2_1.User;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * This class represents a User account on Smugmug. 
 * It is also called a 'session-account', since it is always tied to a Session.
 * @author Anton Spaans
 */
public class Account {

	public static final String UNKNOWN_NICKNAME = "((unkown))";
	
	private final Session mSession;
	private final int mID;
	private final String mNickName;
	private final String mDisplayName;
	private final String mSitePassword; // used for Session.mOtherAccount.
	
	/**
	 * Creates an anonymous  smugmug-account
	 * @param session Logged-in session.
	 */
	protected Account(Session session) {
		this(session, null, null, 0);
	}
	
	/**
	 * Creates 'another user' smugmug-account
	 * @param session Logged in session.
	 * @param nickName Nickname of the other user-account.
	 * @param sitePassword The site-password for that account.
	 */
	protected Account(Session session, String nickName, String sitePassword) {
		mSession = session;
		mID = 0;
		mNickName = nickName;
		mDisplayName = null;
		mSitePassword = sitePassword;
	}
	
	/**
	 * Creates a logged-in user-account for the given logged in session.
	 * @param session Logged in session.
	 * @param nickName The logged in user's nickname.
	 * @param displayName The logged in user's display name.
	 * @param ID The user-ID of the logged in user.
	 */
	protected Account(Session session, String nickName, String displayName, int ID) {
		mSession = session;
		mID = ID;
		mNickName = nickName;
		mDisplayName = displayName;
		mSitePassword = null;
	}

	/**
	 * This method should not be called if you're not developing/extending this API.
	 * 
	 * This method is called when a session is re-established after a re-login.
	 * @param previousSession Use this to obtain information from the previous (invalid) session.
	 */
	public void sessionReestablished(Session previousSession) {
		// Account oldUserInfo = previousSession.me();
		// copy data from oldUserInfo into 'this' if necessary.
	}

	/**
	 * Call this method to re-login for this Account. 
	 * Note that a new account is returned.
	 * @return A new user account.
	 * @throws SmugmugException
	 */
	public Account reLogin() throws SmugmugException {
		return mSession.reLogin().me();
	}

	/**
	 * Log out of this Account.
	 * @throws SmugmugException
	 */
	public void logout() throws SmugmugException {
		mSession.logout();
	}

	/**
	 * Returns the session associated to this Account.
	 * @return
	 */
	public Session getSession() {
		return mSession;
	}
	
	/**
	 * @return the User ID
	 */
	public int getID() {
		return mID;
	}

	/**
	 * This value can also be used if you are logged in as one person 
	 * (session.getUser()) and want to look at data from another person (other.getNickName()).
	 * @return the Nick name
	 */
	public String getNickName() {
		return mNickName;
	}

	/**
	 * @return the Display Name
	 */
	public String getDisplayName() {
		return mDisplayName;
	}
	
	/**
 	 * @return the Site Password.
	 */
	public String getSitePassword() {
		return mSitePassword;
	}
	
	/**
	 * Returns true if this account is anonymous.
	 * @return true or false
	 */
	public boolean isAnonymous() {
		return mID == 0;
	}
	
	/**
	 * Adds the user's entire hierarchy of categories, subcategories and albums
	 * to the cache.
	 * @throws SmugmugException
	 */
	@SuppressWarnings("unchecked")
	public void getSmugObjectsHierarchy() throws SmugmugException {
		final Collection<? extends SmugObjectData<?>>[] smugObjects = (new User(this)).getSmugObjects();
		
		(new CategoryPrototype(mSession)).postGetAll((Collection<Category>)smugObjects[0], true, GUID.NO_GUID);
		(new SubCategoryPrototype(mSession)).postGetAll((Collection<SubCategory>)smugObjects[1], true, GUID.NO_GUID);
		(new AlbumPrototype(mSession)).postGetAll((Collection<Album>)smugObjects[2], true, GUID.NO_GUID);
	}
	
	public String toString() {
		return "Account { id="+mID+", "+
			"nick-name="+mNickName+", "+
			"display-name="+mDisplayName+" }";
	}
}
