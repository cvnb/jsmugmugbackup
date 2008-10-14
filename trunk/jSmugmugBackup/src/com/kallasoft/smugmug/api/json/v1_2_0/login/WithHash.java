/* Copyright 2007 kallasoft
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
package com.kallasoft.smugmug.api.json.v1_2_0.login;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.json.util.JSONUtils;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method establishes a session and logs in with a stored hash, rather than
 * the real password. Slightly more secure than logging in with a plain-text
 * password, it's still recommended that this step occur via SSL (using the
 * secure server URL) whenever possible.
 * <p>
 * For implementors working on a client-side application, storing the hash of
 * the password after using the {@link WithPassword} method, then using the has
 * in conjunction with this method every time after that to log a user in might
 * provide a slightly more secure method of user authentication then worrying
 * about storing the user's password in a file somewhere on the hard drive.
 * <p>
 * Using this method requires that you know the user's password hash and their
 * user ID. Both these values can be retrieved by logging the user in at least
 * once using {@link WithPassword} and checking the response for the values.
 * After that those values can be stored and reused later.
 * <p>
 * Most of the other methods require a SessionID, which is returned by this
 * method. Store temporarily and re-use it for all other methods which require
 * SessionID.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.login.withHash">smugmug.login.withHash
 *      API Doc</a>
 */
public class WithHash extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.login.withHash";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "UserID", "PasswordHash"
	 * 
	 */
	public static final String[] ARGUMENTS = { "APIKey", "UserID",
			"PasswordHash" };

	private static final Logger logger = LoggerFactory
			.getLogger(WithHash.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public WithHash() {
		this(METHOD_NAME, ARGUMENTS);
	}

	/**
	 * Construct a new method instance that can be executed with the given
	 * arguments.
	 * 
	 * @param methodName
	 *            The name of the SmugMug JSON API method that this
	 *            <em>Method</em> represents.
	 * @param arguments
	 *            The names of the arguments that this method accepts.
	 */
	public WithHash(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.login.withHash method, logging the user in
	 * and returning a SessionID that can be used for all other API methods.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. Callers are
	 *            encouraged to always use the secure server URL but are not
	 *            restricted from using an alternative URL.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes all the information about the logged
	 *         in user.
	 */
	public WithHashResponse execute(String url, String[] argumentValues) {
		return new WithHashResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.login.withHash method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. Callers are
	 *            encouraged to always use the secure server URL but are not
	 *            restricted from using an alternative URL.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param userID
	 *            The ID of the user that wishes to login.
	 * @param passwordHash
	 *            The hash of the user's password.
	 * 
	 * @return the response that includes all the information about the logged
	 *         in user.
	 * 
	 * @see #execute(String, String[])
	 */
	public WithHashResponse execute(String url, String apiKey, Integer userID,
			String passwordHash) {
		return execute(url, new String[] { apiKey, APIUtils.toString(userID),
				passwordHash });
	}

	/**
	 * Class used to represent the response for the smugmug.login.withHash
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class WithHashResponse extends AbstractResponse {
		private String sessionID;

		private String nickName;

		private String displayName;

		private String accountType;

		private Integer fileSizeLimit;

		/**
		 * Construct a response by parsing the necessary values out of the JSON
		 * response text.
		 * 
		 * @param responseText
		 *            The JSON-formatted response text that came back from the
		 *            SmugMug API call.
		 * 
		 * @throws RuntimeJSONException
		 *             if an error occurs while parsing the JSON response text.
		 */
		public WithHashResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);

				JSONObject loginObject = responseObject.getJSONObject("Login");
				JSONObject userObject = loginObject.getJSONObject("User");

				nickName = JSONUtils.getStringSafely(userObject, "NickName");
				displayName = JSONUtils.getStringSafely(userObject,
						"DisplayName");

				accountType = JSONUtils.getStringSafely(loginObject,
						"AccountType");
				fileSizeLimit = JSONUtils.getIntegerSafely(loginObject,
						"FileSizeLimit");
				sessionID = loginObject.getJSONObject("Session")
						.getString("id");
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return WithHashResponse.class.getName() + "[isError=" + isError()
					+ ", sessionID=" + getSessionID() + ", nickName="
					+ getNickName() + ", displayName=" + getDisplayName()
					+ ", accountType=" + getAccountType() + ", fileSizeLimit="
					+ getFileSizeLimit() + "]";
		}

		/**
		 * Used to get the Session ID assigned to this logged in user by
		 * SmugMug.
		 * 
		 * @return the Session ID assigned to this logged in user by SmugMug.
		 */
		public String getSessionID() {
			return sessionID;
		}

		/**
		 * Used to get the nick name of the logged in user.
		 * 
		 * @return the nick name of the logged in user.
		 */
		public String getNickName() {
			return nickName;
		}

		/**
		 * Used to get the display name of the logged in user.
		 * 
		 * @return the display name of the logged in user.
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * Used to get the account type (e.g. Pro) of the logged in user.
		 * 
		 * @return the account type (e.g. Pro) of the logged in user.
		 */
		public String getAccountType() {
			return accountType;
		}

		/**
		 * Used to get the upload file size limit for the logged in user in
		 * bytes.
		 * 
		 * @return the upload file size limit for the logged in user in bytes.
		 */
		public Integer getFileSizeLimit() {
			return fileSizeLimit;
		}
	}
}
