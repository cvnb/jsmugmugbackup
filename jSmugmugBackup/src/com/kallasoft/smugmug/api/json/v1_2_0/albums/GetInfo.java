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
 * This method returns detailed information about the album specified by
 * AlbumID. If the album isn't owned by the Session holder and also isn't public
 * or has a password, an "invalid user" error will be returned.
 * <p>
 * Note that many of the fields are protected for the owner's eyes only. Reduced
 * information is provided to a non-owner.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.albums.getInfo">smugmug.albums.getInfo
 *      API Doc</a>
 */
public class GetInfo extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.albums.getInfo";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "AlbumKey", "Password",
	 * "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "AlbumKey", "Password", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(GetInfo.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetInfo() {
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
	public GetInfo(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.albums.getInfo method, returning detailed
	 * album information to the caller.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes the detailed album information.
	 */
	public GetInfoResponse execute(String url, String[] argumentValues) {
		return new GetInfoResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.albums.getInfo method.
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
	 * @param albumID
	 *            The ID of the album whose detailed information will be
	 *            retrieved.
	 * @param albumKey
	 *            The security key for the album.
	 * 
	 * @return the response that includes the detailed album information as
	 *         specified by albumID.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetInfoResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String albumKey) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), albumKey });
	}

	/**
	 * Convenience method used to execute the smugmug.albums.getInfo method.
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
	 * @param albumID
	 *            The ID of the album whose detailed information will be
	 *            retrieved.
	 * @param albumKey
	 *            The security key for the album.
	 * @param password
	 *            The album password, if necessary.
	 * @param sitePassword
	 *            The site password, if necessary.
	 * 
	 * @return the response that includes the detailed album information as
	 *         specified by albumID.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetInfoResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String albumKey, String password,
			String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), albumKey, password, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.albums.getInfo
	 * method call.
	 * <p>
	 * Not all values are available to
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetInfoResponse extends AbstractResponse {
		private Album album;

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
		public GetInfoResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);

				album = new Album(responseObject.getJSONObject("Album"));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetInfoResponse.class.getName() + "[isError=" + isError()
					+ ", album=" + getAlbum() + "]";
		}

		/**
		 * Used to return the album and all it's associated information.
		 * 
		 * @return the album and all it's associated information.
		 */
		public Album getAlbum() {
			return album;
		}
	}
}
