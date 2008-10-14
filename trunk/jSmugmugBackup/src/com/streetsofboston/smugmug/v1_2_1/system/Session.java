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

import static com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData.toNumber;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.login.Anonymously;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithHash;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword;
import com.kallasoft.smugmug.api.json.v1_2_1.login.Anonymously.AnonymouslyResponse;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithHash.WithHashResponse;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword.WithPasswordResponse;
import com.kallasoft.smugmug.api.json.v1_2_1.logout.Logout;
import com.kallasoft.smugmug.api.json.v1_2_1.logout.Logout.LogoutResponse;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;

/**
 * This class represents a logged-in Session. It is a pre-requisite for each
 * operation that needs to get or send data from/to Smugmug.
 * 
 * A session is tied to a Session-ID, which can be shared by one or more Sessions.
 * Each session is tied to a user Account.
 * @author Anton Spaans
 */
public class Session {

	/**
	 * 
	 * @author Anton Spaans
	 *
	 * This enumeration defines the 3 types of accounts available from smugmug:
	 * Pro, Power and Basic accounts.
	 */
	public enum ACCOUNT_TYPE {
		Pro,
		Power,
		Basic
	};

	/**
	 * Default SmugFig API key.
	 * Override with your own app's API key if necessary.
	 */
	public static String API_KEY = "EfDnSBoFGKoK2PGgVQEdwksoVw04JLkb";
	
	/**
	 * Smugmug's Session ID.
	 * Session objects can share SessionIDs. If one Session object becomes invalid,
	 * the other Session objects become invalid as well. This is how logged in 
	 * session (Session.me()) and sessions for other user-accounts (Session.forOtherAccount())
	 * share the same session-information.
	 */
	final private SessionID mSessionID;
	
	private String       mHashPassword = null;
	private ACCOUNT_TYPE mAccountType   = null;
	private int          mFilesizeLimit = -1;
	private Account      mMe = null;
	private Account      mOtherAccount = null;
	
	/**
	 * Logs into a Smugmug Account using the account's e-mail and password.
	 * It establishes a working session for this account. This session is returned.
	 * @param eMail Account's e-mail.
	 * @param password Account's password.
	 * @return Working session.
	 * @throws SmugmugException
	 */
	public static Session login(String eMail, String password) throws SmugmugException {
		WithPasswordResponse wpRes = new WithPassword().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				API_KEY, 
				eMail, password });
		
		SmugmugException.check(wpRes);

		final Session session = new Session();
		session.mSessionID.mValue = wpRes.getSessionID();
		session.mHashPassword = wpRes.getPasswordHash();
		session.mAccountType = ACCOUNT_TYPE.valueOf(wpRes.getAccountType());
		session.mFilesizeLimit = wpRes.getFileSizeLimit();
		session.mOtherAccount = new Account(session);

		session.mMe = new Account(session, wpRes.getNickName(), 
				wpRes.getDisplayName(), wpRes.getUserID());
		
		return session;
	}
	
	/**
	 * Logs into a Smugmug Account using the account's User-ID and hashed-password.
	 * It establishes a working session for this account. This session is returned.
	 * 
	 * @param userID Account's User-id
	 * @param hashedPassword Hashed password for this account.
	 * @return Working session.
	 * @throws SmugmugException
	 */
	public static Session loginWithHash(int userID, String hashedPassword) throws SmugmugException {
		WithHashResponse wpRes = new WithHash().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				API_KEY, 
				toNumber(userID), hashedPassword });
		
		SmugmugException.check(wpRes);

		final Session session = new Session();
		session.mSessionID.mValue = wpRes.getSessionID();
		session.mHashPassword = hashedPassword;
		session.mAccountType = ACCOUNT_TYPE.valueOf(wpRes.getAccountType());
		session.mFilesizeLimit = wpRes.getFileSizeLimit();
		session.mOtherAccount = new Account(session);

		session.mMe = new Account(session, wpRes.getNickName(), 
				wpRes.getDisplayName(), userID);
		
		return session;
	}
	
	/**
	 * Logs into Smugmug anonymously and returns a guest's working session.
	 * @return Working session for a guest.
	 * @throws SmugmugException
	 */
	public static Session loginAnonymously() throws SmugmugException {
		AnonymouslyResponse wpRes = new Anonymously().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				API_KEY });
		
		SmugmugException.check(wpRes);

		final Session session = new Session();
		session.mSessionID.mValue = wpRes.getSessionID();
		session.mOtherAccount = new Account(session);
		session.mMe        = new Account(session);
		return session;
	}
	
	private Session() { 
		mSessionID = new SessionID();
	}
	
	private Session(Session session) {
		mSessionID = session.mSessionID;
		mAccountType = session.mAccountType;
		mFilesizeLimit = session.mFilesizeLimit;
		mHashPassword = session.mHashPassword;
		mMe = new Account(this, 
				session.mMe.getNickName(),
				session.mMe.getDisplayName(),
				session.mMe.getID());
		mOtherAccount = null; //new Account(this, session.mMe.getNickName(), null);
	}

	/** 
	 * Determines if current session is anonymous, i.e. whether it was logged in as a guest or not.
	 * @return True only if the session is anonymous. False if logged in with an account and password.
	 */
	public boolean isAnonymous() {
		return mMe.isAnonymous();
	}

	/**
	 * Returns this app's API Key.
	 * @return API key
	 */
	public String getAPIKey() {
		return API_KEY;
	}
	
	/**
	 * Determines if the current session in valid.
	 * Invalid session is established by logging out.
	 * @return True if the current session is invalid (e.g. logged out)
	 */
	public boolean isInvalid() {
		return mSessionID.mValue == null;
	}

	/**
	 * Returns the session's ID (as used by Smugmug).
	 * @return The session-id.
	 */
	public String getSessionID() {
		return mSessionID.mValue;
	}
	
	/**
	 * Sets the appropriate Smugmug cookie for downloading images from smugmug through URLs. 
	 * @param urlConnection The url-connection to the image.
	 */
	public void setCookie(URLConnection urlConnection) {
		urlConnection.setRequestProperty("Cookie", "SMSESS="+getSessionID());
	}
	
	/**
	 * Opens the input stream from the provided URL and makes sure that Smugmug-cookies are set appropriately.
	 * @param url The URL to an image.
	 * @return The input-stream to that image.
	 * @throws IOException
	 */
	public InputStream openInputStream(URL url) throws IOException {
		final URLConnection urlConnection = url.openConnection();
		setCookie(urlConnection);
		return urlConnection.getInputStream();
	}
	
	/**
	 * Returns the user's account type.
	 * @return Account type.
	 */
	public ACCOUNT_TYPE getAccountType() {
		return mAccountType;
	}

	/**
	 * Max limit for the size of files that can be uploaded using this session.
	 * @return Max upload size.
	 */
	public int getFilesizeLimit() {
		return mFilesizeLimit;
	}
	
	/**
	 * The logged in user account.
	 * This will never be 'null'.
	 * @return The logged in user account.
	 */
	public Account me() {
		return mMe;
	}
	
	/**
	 * Call this method if you're interested in data of another user's account.
	 * E.g. you'd like to examine some galleries (Albums) of another user.
	 * 
	 * This method returns a session that is tied to an account representing the other user.
	 * @param nickName The other user's nick-name (as known on Smugmug).
	 * @param sitePassword If necessary, the other user's site-password. 
	 * @return A new working session to obtain data for another user-account.
	 */
	public Session forOtherAccount(String nickName, String sitePassword) {
		final Session newSession = new Session(this);
		newSession.mOtherAccount = new Account(newSession, nickName, sitePassword);
		return newSession;
	}
	
	/**
	 * The account for which data needs to be returned.
	 * E.g. You can be logged in (me() returns the logged in account), but
	 * you could be interested in another user's galleries. The other user
	 * is represented by this method.
	 * 
	 * The method 'forOtherAccount' establishes such a session.
	 * @return The account
	 */
	public Account getOtherAccount() {
		return mOtherAccount;
	}
	
	/**
	 * @return The nick-name that is represented by this session's account.
	 */
	public String getNickName() {
		final String  otherAccountName = getOtherAccount().getNickName(); 
		final boolean isOwnerSession   = (otherAccountName == null || otherAccountName.length() == 0);
		final String  ownerName        = isOwnerSession 
								? me().getNickName()
								: otherAccountName;
		return ownerName;
	}
	
	/**
	 * You can relogin and create a new session. Data from this session is used (user-id 
	 * hashed password) to establish this new session.
	 * 
	 * This method also works if this session is invalid. This method can be used
	 * to establish a new valid session using the current login-credentials.
	 * 
	 * @return New working session.
	 * @throws SmugmugException
	 */
	public Session reLogin() throws SmugmugException {
		Session session;
		if (isAnonymous()) {
			session = loginAnonymously();
		}
		else {
			session = loginWithHash(mMe.getID(), mHashPassword);
		}
		
		session.mOtherAccount = new Account(session, mOtherAccount.getNickName(), mOtherAccount.getSitePassword());

		session.mMe.sessionReestablished(this);
		session.mOtherAccount.sessionReestablished(this);

		return session;
	}
	
	/**
	 * Call this method to log out. The session will become invalid.
	 * @throws SmugmugException
	 */
	public void logout() throws SmugmugException {
		if (isAnonymous() || isInvalid())
			return;
		
		LogoutResponse loRes = new Logout().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				API_KEY, 
				mSessionID.mValue});
		
		SmugmugException.check(loRes);
		
		mSessionID.mValue = null;
	}
	
	public String toString() {
		return "Session { session-id="+mSessionID+", "+
			"me="+mMe+", "+
			"hash-password="+mHashPassword+", "+
			"account-type="+mAccountType+", "+
			"filesize-limit="+mFilesizeLimit+", "+
			"other-user="+mOtherAccount.getNickName()+" }";
	}
	
	private static class SessionID {
		public String mValue;
		public String toString() { return mValue!=null?mValue:"invalidated session"; }
	}
}
