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
package com.kallasoft.smugmug.api.json.v1_2_0.albums;

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
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method returns a list of albums for a given user (either via SessionID
 * or (optional) NickName). If the session is logged in as the requested user, a
 * complete list of albums is returned. Otherwise, only the public albums are
 * returned.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.albums.get">smugmug.albums.get
 *      API Doc</a>
 */
public class Get extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.albums.get";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "NickName", "Heavy", "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"NickName", "Heavy", "SitePassword" };

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
	 * Used to execute the smugmug.albums.get method, returning a list of albums
	 * for the user identified by the Session ID or the given Nick Name.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a list of the albums for the user
	 *         identified by the given Session ID or Nick Name.
	 */
	public GetResponse execute(String url, String[] argumentValues) {
		return new GetResponse(executeImpl(url, argumentValues));
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
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each album and take longer to process when
	 *            requested.
	 * 
	 * @return the response that includes a list of the albums for the user
	 *         identified by the given Session ID.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Boolean heavy) {
		return execute(url, new String[] { apiKey, sessionID, null,
				APIUtils.toString(heavy) });
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
	 * 
	 * @return the response that includes a list of the albums for the user
	 *         identified by the given Session ID or Nick Name.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			String nickName, Boolean heavy, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				APIUtils.toString(heavy), sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.albums.get method
	 * call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetResponse extends AbstractResponse {
		private List<Album> albumList = new ArrayList<Album>();

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
				JSONArray albumArray = responseObject.getJSONArray("Albums");

				for (int i = 0, length = albumArray.length(); i < length; i++)
					albumList.add(new Album(albumArray.getJSONObject(i)));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetResponse.class.getName() + "[isError=" + isError()
					+ ", albumListSize=" + albumList.size() + "]";
		}

		/**
		 * Used to get a list of the albums that were returned.
		 * 
		 * @return a list of the albums that were returned.
		 */
		public List<Album> getAlbumList() {
			return albumList;
		}
	}
}
