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
package com.kallasoft.smugmug.api.json.v1_2_0.users;

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
import com.kallasoft.smugmug.api.json.entity.Album;
import com.kallasoft.smugmug.api.json.entity.Category;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method returns a complete tree, starting with categories and descending
 * into subcategories (if any) and albums (if any) for the specified user
 * (identified by SessionID or (optional) NickName).
 * <p>
 * This class does support heavy results. If heavy results are not specified,
 * the only album properties returned are <code>id</code> and
 * <code>title</code>, the remaining properties are <code>null</code>.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.users.getTree">smugmug.users.getTree
 *      API Doc</a>
 */
public class GetTree extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.users.getTree";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "NickName", "Heavy", "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"NickName", "Heavy", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(GetTree.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetTree() {
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
	public GetTree(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.users.getTree method, returning a response
	 * containing the tree of categories, subcategories, and albums (in that
	 * order) for the user identified by the <code>SessionID</code> or the
	 * user identified by <code>NickName</code>.
	 * <p>
	 * This method does support <em>heavy</em> results (Heavy=1). If heavy
	 * results are requested, the information returning for {@link Album} will
	 * include values for every argument defined in the class, otherwise just
	 * the {@link Album#getID()} and {@link Album#getTitle()} values are
	 * returned.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response containing the tree of categories, subcategories,
	 *         and albums (in that order) for the user identified by the
	 *         <code>SessionID</code> or the user identified by
	 *         <code>NickName</code>.
	 */
	public GetTreeResponse execute(String url, String[] argumentValues) {
		return new GetTreeResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.users.getTree method.
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
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each album and take longer to process when
	 *            requested.
	 * 
	 * @return the response containing the tree of categories, subcategories,
	 *         and albums (in that order) for the user identified by the
	 *         <code>SessionID</code>.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetTreeResponse execute(String url, String apiKey, String sessionID,
			Boolean heavy) {
		return execute(url, new String[] { apiKey, sessionID, null,
				APIUtils.toString(heavy), null });
	}

	/**
	 * Convenience method used to execute the smugmug.users.getTree method.
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
	 *            A nick name for a user that you wish to load the content-tree
	 *            for. If the content-tree for the given nick name is not
	 *            public, a sitePassword must be provided.
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each album and take longer to process when
	 *            requested.
	 * @param sitePassword
	 *            The password for a given password-protected account identified
	 *            by <code>nickName</code> that you wish to load the
	 *            content-tree for.
	 * 
	 * @return the response containing the tree of categories, subcategories,
	 *         and albums (in that order) for the user identified by the
	 *         <code>SessionID</code> or the user identified by
	 *         <code>NickName</code>.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetTreeResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				APIUtils.toString(heavy), sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.users.getTree method
	 * call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetTreeResponse extends AbstractResponse {
		private List<Category> categoryList = new ArrayList<Category>();

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
		public GetTreeResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				JSONArray categoryArray = responseObject
						.getJSONArray("Categories");

				/*
				 * Process each category entry, which can contain both albums
				 * and subcategories, which themselves can contain albums. All
				 * the logic for this is implemented in the Category class,
				 * parsing the embedded elements.
				 */
				for (int i = 0, length = categoryArray.length(); i < length; i++) {
					categoryList.add(new Category(categoryArray
							.getJSONObject(i)));
				}
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetTreeResponse.class.getName() + "[isError=" + isError()
					+ ", categoryListSize=" + getCategoryList().size() + "]";
		}

		/**
		 * Used to get a list of the categories that represent the root of the
		 * content-tree.
		 * <p>
		 * Under categories are optional subcategories (if they exist) then
		 * albums.
		 * 
		 * @return a list of the categories that represent the root of the
		 *         content-tree.
		 */
		public List<Category> getCategoryList() {
			return categoryList;
		}
	}
}
