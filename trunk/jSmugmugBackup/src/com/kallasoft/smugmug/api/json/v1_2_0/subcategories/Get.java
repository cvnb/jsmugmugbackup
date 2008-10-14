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
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method returns a list of SubCategories for the specified user
 * (identified by SessionID or (optional) NickName) in the specified Category
 * (identified by CategoryID).
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.subcategories.get">smugmug.subcategories.get
 *      API Doc</a>
 */
public class Get extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.subcategories.get";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "CategoryID", "NickName",
	 * "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"CategoryID", "NickName", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(Get.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Get() {
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
	public Get(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.subcategories.get method, returning a list of
	 * subcategories for the given user and category.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a list of subcategories for the given
	 *         user and category.
	 */
	public GetResponse execute(String url, String[] argumentValues) {
		return new GetResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.subcategories.get method
	 * to get all the subcategories for the given category.
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
	 * @param categoryID
	 *            The ID of the category whose subcategories will be returned.
	 * 
	 * @return the response that includes a list of subcategories for the given
	 *         user and category.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Integer categoryID) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(categoryID) });
	}

	/**
	 * Convenience method used to execute the smugmug.subcategories.get method
	 * to get all the subcategories for the given category.
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
	 * @param categoryID
	 *            The ID of the category whose subcategories will be returned.
	 * @param nickName
	 *            The nickname of the user who's categories will be returned.
	 * @param sitePassword
	 *            The site password, if one is necessary.
	 * 
	 * @return the response that includes a list of subcategories for the given
	 *         user and category.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Integer categoryID, String nickName, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(categoryID), nickName, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.subcategories.get
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetResponse extends AbstractResponse {
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
		public GetResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				JSONArray subCategoryArray = responseObject
						.getJSONArray("SubCategories");

				for (int i = 0, length = subCategoryArray.length(); i < length; i++)
					subCategoryList.add(new Category(subCategoryArray
							.getJSONObject(i)));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetResponse.class.getName() + "[isError=" + isError()
					+ ", subCategoryListSize=" + getSubCategoryList().size()
					+ "]";
		}

		/**
		 * Used to get a list of the subcategories that belonged to the given
		 * category.
		 * 
		 * @return a list of the subcategories that belonged to the given
		 *         category.
		 */
		public List<Category> getSubCategoryList() {
			return subCategoryList;
		}
	}
}
