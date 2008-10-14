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
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method establishes a session which isn't logged in.
 * <p>
 * This method can be used to retrieve a SessionID in order to use other method
 * calls that require it without needing credentials to login (e.g. request a
 * list of public albums from a public account. You need the SessionID to
 * perform the query, but don't need credentials for it, so you can use this
 * method).
 * <p>
 * Most of the other methods require a SessionID, which is returned by this
 * method. Store temporarily and re-use it for all other methods which require
 * SessionID.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.login.anonymously">smugmug.login.anonymously
 *      API Doc</a>
 */
public class Anonymously extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.login.anonymously";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Value is: "APIKey"
	 */
	public static final String[] ARGUMENTS = { "APIKey" };

	private static final Logger logger = LoggerFactory
			.getLogger(Anonymously.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Anonymously() {
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
	public Anonymously(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.login.anonymously method, logging the user in
	 * and returning a SessionID that can be used for all other API methods.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. Either
	 *            secure or unsecured URLs are fine to use for this method.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes all the information about the logged
	 *         in user.
	 */
	public AnonymouslyResponse execute(String url, String[] argumentValues) {
		return new AnonymouslyResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.login.anonymously method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. Either
	 *            secure or unsecured URLs are fine to use for this method.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * 
	 * @return the response that includes all the information about the logged
	 *         in user.
	 * 
	 * @see #execute(String, String[])
	 */
	public AnonymouslyResponse execute(String url, String apiKey) {
		return execute(url, new String[] { apiKey });
	}

	/**
	 * Class used to represent the response for the smugmug.login.withPassword
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class AnonymouslyResponse extends AbstractResponse {
		private String sessionID;

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
		public AnonymouslyResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				sessionID = responseObject.getJSONObject("Login")
						.getJSONObject("Session").getString("id");
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return AnonymouslyResponse.class.getName() + "[isError="
					+ isError() + ", sessionID=" + getSessionID() + "]";
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
	}
}
