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
package com.kallasoft.smugmug.api.json.v1_2_0.subcategories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.json.entity.Category;
import com.kallasoft.smugmug.api.json.v1_2_0.categories.Get;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method returns a list of SubCategories for the specified user
 * (identified by SessionID or (optional) NickName).
 * <p>
 * Unlike {@link Get}, this returns a complete list, rather than one just for a
 * given Category.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.subcategories.getAll">smugmug.subcategories.getAll
 *      API Doc</a>
 */
public class GetAll extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.subcategories.getAll";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "NickName", "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"NickName", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(GetAll.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetAll() {
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
	public GetAll(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.subcategories.getAll method, returning a list
	 * of all the subcategories that this user has.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a list of all the subcategories this
	 *         user has.
	 */
	public GetAllResponse execute(String url, String[] argumentValues) {
		return new GetAllResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.subcategories.getAll
	 * method to get all the subcategories for this user.
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
	 * 
	 * @return the response that includes a list of all the subcategories this
	 *         user has.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetAllResponse execute(String url, String apiKey, String sessionID) {
		return execute(url, new String[] { apiKey, sessionID });
	}

	/**
	 * Convenience method used to execute the smugmug.subcategories.getAll
	 * method to get all the subcategories for the given user.
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
	 * @param nickName
	 *            The nickname of the user who's categories will be returned.
	 * @param sitePassword
	 *            The site password, if one is necessary.
	 * 
	 * @return the response that includes a list of all the subcategories this
	 *         user has.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetAllResponse execute(String url, String apiKey, String sessionID,
			String nickName, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.subcategories.getAll
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetAllResponse extends AbstractResponse {
		private List<Category> subCategoryList = new ArrayList<Category>();

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
		public GetAllResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				JSONArray categoryArray = responseObject
						.getJSONArray("SubCategories");

				for (int i = 0, length = categoryArray.length(); i < length; i++)
					subCategoryList.add(new Category(categoryArray
							.getJSONObject(i)));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetAllResponse.class.getName() + "[isError=" + isError()
					+ ", subCategoryListSize=" + getSubCategoryList().size()
					+ "]";
		}

		/**
		 * Used to get a list of all the subcategories.
		 * 
		 * @return a list of all the subcategories.
		 */
		public List<Category> getSubCategoryList() {
			return subCategoryList;
		}
	}
}
