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
package com.kallasoft.smugmug.api.json.v1_2_1.albums;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.albums.Get} implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.albums.Get
 */
public class Get extends com.kallasoft.smugmug.api.json.v1_2_0.albums.Get {
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

	@Override
	public GetResponse execute(String url, String[] argumentValues) {
		return new GetResponse(executeImpl(url, argumentValues));
	}

	@Override
	public GetResponse execute(String url, String apiKey, String sessionID,
			Boolean heavy) {
		return execute(url, apiKey, sessionID, null, heavy, null);
	}

	@Override
	public GetResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword) {
		return execute(url, apiKey, sessionID, nickName, heavy, sitePassword,
				null);
	}

	/**
	 * Convenience method used to execute the smugmug.albums.get method.
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
	 *            A nick name for a user that you wish to load the album list
	 *            for. If the album list for the given nick name is not public,
	 *            a sitePassword must be provided.
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each album and take longer to process when
	 *            requested.
	 * @param sitePassword
	 *            The password for a given password-protected account identified
	 *            by <code>nickName</code>.
	 * @param shareGroupTag
	 *            The tag of the ShareGroup to expose private albums for.
	 * 
	 * @return the response that includes a list of the albums for the user
	 *         identified by the given Session ID or Nick Name.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword,
			String shareGroupTag) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				APIUtils.toString(heavy), sitePassword, shareGroupTag });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.albums.Get.GetResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.albums.Get.GetResponse
	 */
	public class GetResponse extends
			com.kallasoft.smugmug.api.json.v1_2_0.albums.Get.GetResponse {
		public GetResponse(String responseText) {
			super(responseText);
		}
	}
}
