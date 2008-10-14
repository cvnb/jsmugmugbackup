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
package com.kallasoft.smugmug.api.json.v1_2_0.logout;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;

/**
 * This method logs a session out.
 * <p>
 * Subsequent API calls using the logged out SessionID will result in errors.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.logout">smugmug.logout
 *      API Doc</a>
 */
public class Logout extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.logout";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID"
	 * 
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID" };

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Logout() {
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
	public Logout(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.logout method.
	 * <p>
	 * Subsequent API calls using the logged out SessionID will result in
	 * errors.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that occurs from logging the user off.
	 */
	public LogoutResponse execute(String url, String[] argumentValues) {
		return new LogoutResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.logout method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. Either
	 *            secure or unsecured URLs are fine to use for this method.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session
	 *            that will be logged out.
	 * 
	 * @return the response that occurs from logging the user off.
	 * 
	 * @see #execute(String, String[])
	 */
	public LogoutResponse execute(String url, String apiKey, String sessionID) {
		return execute(url, new String[] { apiKey, sessionID });
	}

	/**
	 * Class used to represent the response for the smugmug.logout method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class LogoutResponse extends AbstractResponse {

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
		public LogoutResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);
		}

		@Override
		public String toString() {
			return LogoutResponse.class.getName() + "[isError=" + isError()
					+ "]";
		}
	}
}
