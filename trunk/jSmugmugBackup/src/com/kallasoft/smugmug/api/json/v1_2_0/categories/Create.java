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
package com.kallasoft.smugmug.api.json.v1_2_0.categories;

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
 * This method creates a new Category with the given name.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.categories.create">smugmug.categories.create
 *      API Doc</a>
 */
public class Create extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.categories.create";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "Name"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID", "Name" };

	private static final Logger logger = LoggerFactory.getLogger(Create.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Create() {
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
	public Create(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.categories.create method, creating a new
	 * category.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes the ID of the newly created category.
	 */
	public CreateResponse execute(String url, String[] argumentValues) {
		return new CreateResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.categories.create method
	 * to create a category with the given name.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param name
	 *            The name of the category that will be created.
	 * 
	 * @return the response that includes the ID of the newly created category.
	 * 
	 * @see #execute(String, String[])
	 */
	public CreateResponse execute(String url, String apiKey, String sessionID,
			String name) {
		return execute(url, new String[] { apiKey, sessionID, name });
	}

	/**
	 * Class used to represent the response for the smugmug.categories.create
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class CreateResponse extends AbstractResponse {
		private Integer categoryID;

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
		public CreateResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				categoryID = JSONUtils.getIntegerSafely(responseObject
						.getJSONObject("Category"), "id");
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return CreateResponse.class.getName() + "[isError=" + isError()
					+ ", categoryID=" + getCategoryID() + "]";
		}

		/**
		 * Used to get the ID the newly created category.
		 * 
		 * @return the ID the newly created category.
		 */
		public Integer getCategoryID() {
			return categoryID;
		}
	}
}
