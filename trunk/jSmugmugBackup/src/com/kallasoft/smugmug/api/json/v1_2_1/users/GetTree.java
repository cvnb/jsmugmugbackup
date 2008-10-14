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
package com.kallasoft.smugmug.api.json.v1_2_1.users;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree} implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree
 */
public class GetTree extends
		com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree {
	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "NickName", "Heavy", "SitePassword",
	 * "ShareGroup"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"NickName", "Heavy", "SitePassword", "ShareGroup" };

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

	@Override
	public GetTreeResponse execute(String url, String[] argumentValues) {
		return new GetTreeResponse(executeImpl(url, argumentValues));
	}

	@Override
	public GetTreeResponse execute(String url, String apiKey, String sessionID,
			Boolean heavy) {
		return execute(url, apiKey, sessionID, null, heavy, null);
	}

	@Override
	public GetTreeResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword) {
		return execute(url, apiKey, sessionID, nickName, heavy, sitePassword,
				null);
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
	 * @param shareGroupTag
	 *            The tag of the ShareGroup to expose private albums for.
	 * 
	 * @return the response containing the tree of categories, subcategories,
	 *         and albums (in that order) for the user identified by the
	 *         <code>SessionID</code> or the user identified by
	 *         <code>NickName</code>.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetTreeResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword,
			String shareGroupTag) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				APIUtils.toString(heavy), sitePassword, shareGroupTag });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree.GetTreeResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree.GetTreeResponse
	 */
	public class GetTreeResponse extends
			com.kallasoft.smugmug.api.json.v1_2_0.users.GetTree.GetTreeResponse {
		public GetTreeResponse(String responseText) {
			super(responseText);
		}
	}
}
