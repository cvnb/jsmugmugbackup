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
 * This method establishes a session and logs a user in based on the specified
 * email address and password.
 * <p>
 * NOTE: This is a plain-text password, and thus, should only be sent via the
 * secure server URL.
 * <p>
 * Most of the other methods require a SessionID, which is returned by this
 * method. Store temporarily and re-use it for all other methods which require a
 * SessionID.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.login.withPassword">smugmug.login.withPassword
 *      API Doc</a>
 */
public class WithPassword extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.login.withPassword";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "EmailAddress", "Password"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "EmailAddress",
			"Password" };

	private static final Logger logger = LoggerFactory
			.getLogger(WithPassword.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public WithPassword() {
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
	public WithPassword(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.login.withPassword method, logging the user
	 * in and returning a SessionID that can be used for all other API methods.
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
	public WithPasswordResponse execute(String url, String[] argumentValues) {
		return new WithPasswordResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.login.withPassword method.
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
	 * @param emailAddress
	 *            The email address (login) of the user logging in.
	 * @param password
	 *            The password of the user logging in.
	 * 
	 * @return the response that includes all the information about the logged
	 *         in user.
	 * 
	 * @see #execute(String, String[])
	 */
	public WithPasswordResponse execute(String url, String apiKey,
			String emailAddress, String password) {
		return execute(url, new String[] { apiKey, emailAddress, password });
	}

	/**
	 * Class used to represent the response for the smugmug.login.withPassword
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class WithPasswordResponse extends AbstractResponse {
		private Integer userID;

		private String sessionID;

		private String passwordHash;

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
		public WithPasswordResponse(String responseText)
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

				userID = JSONUtils.getIntegerSafely(userObject, "id");
				nickName = JSONUtils.getStringSafely(userObject, "NickName");
				displayName = JSONUtils.getStringSafely(userObject,
						"DisplayName");

				passwordHash = JSONUtils.getStringSafely(loginObject,
						"PasswordHash");
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
			return WithPasswordResponse.class.getName() + "[isError="
					+ isError() + ", userID=" + getUserID() + ", sessionID="
					+ getSessionID() + ", passwordHash=" + getPasswordHash()
					+ ", nickName=" + getNickName() + ", displayName="
					+ getDisplayName() + ", accountType=" + getAccountType()
					+ ", fileSizeLimit=" + getFileSizeLimit() + "]";
		}

		/**
		 * Used to get the ID of the logged in user.
		 * 
		 * @return the ID of the logged in user.
		 */
		public Integer getUserID() {
			return userID;
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
		 * Used to get the hash of the user's password.
		 * <p>
		 * Using this value can be a slightly more secure method of logging a
		 * user in using {@link WithHash} since the password itself is never
		 * sent.
		 * <p>
		 * For example if the user wanted to store their password client-side in
		 * a config file somewhere for an application, an implementor might
		 * consider storing the password hash itself as the password, then
		 * logging the user back in from that point forward using
		 * {@link WithHash} instead of this method.
		 * 
		 * @return the hash of the user's password.
		 */
		public String getPasswordHash() {
			return passwordHash;
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
