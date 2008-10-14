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
package com.kallasoft.smugmug.api.json.v1_2_0.images;

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
import com.kallasoft.smugmug.api.json.entity.Image;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will fetch all the ImageIDs for the album specified by AlbumID.
 * <p>
 * The album must be owned by the session holder, or else be public (if
 * password-protected, a password must be provided) to return results, otherwise
 * an "invalid user" faultCode will result.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.get">smugmug.images.get
 *      API Doc</a>
 */
public class Get extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.get";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "AlbumKey", "Heavy",
	 * "Password", "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "AlbumKey", "Heavy", "Password", "SitePassword" };

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
	 * Used to execute the smugmug.images.get method, returning a list of image
	 * IDs for the given album.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a list of the image IDs for the given
	 *         album.
	 */
	public GetResponse execute(String url, String[] argumentValues) {
		return new GetResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.get method.
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
	 *            The ID of the album whose image IDs will be returned.
	 * @param albumKey
	 *            The security key for the album.
	 * 
	 * @return the response that includes a list of the image IDs for the given
	 *         album.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String albumKey) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), albumKey });
	}

	/**
	 * Convenience method used to execute the smugmug.images.get method.
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
	 *            The ID of the album whose image IDs will be returned.
	 * @param albumKey
	 *            The security key for the album.
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each image and take longer to process when
	 *            requested.
	 * 
	 * @return the response that includes a list of the image IDs for the given
	 *         album.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String albumKey, Boolean heavy) {
		return execute(url,
				new String[] { apiKey, sessionID, APIUtils.toString(albumID),
						albumKey, APIUtils.toString(heavy) });
	}

	/**
	 * Convenience method used to execute the smugmug.images.get method.
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
	 *            The ID of the album whose image IDs will be returned.
	 * @param albumKey
	 *            The security key for the album.
	 * @param heavy
	 *            A boolean argument (0 or 1) indicating if you would like
	 *            <em>heavy</em> results returned. Heavy results include much
	 *            more detail about each image and take longer to process when
	 *            requested.
	 * @param password
	 *            A password for the album if it is password protected.
	 * @param sitePassword
	 *            A site password for the SmugMug site if it is password
	 *            protected.
	 * 
	 * @return the response that includes a list of the image IDs for the given
	 *         album.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String albumKey, Boolean heavy, String password,
			String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), albumKey, APIUtils.toString(heavy),
				password, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.images.get method
	 * call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetResponse extends AbstractResponse {
		private List<Image> imageList = new ArrayList<Image>();

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

				JSONArray imageArray = responseObject.getJSONArray("Images");

				for (int i = 0, length = imageArray.length(); i < length; i++)
					imageList.add(new Image(imageArray.getJSONObject(i)));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetResponse.class.getName() + "[isError=" + isError()
					+ ", imageListSize=" + getImageList().size() + "]";
		}

		/**
		 * Used to get a list of the images that were returned.
		 * 
		 * @return a list of the images that were returned.
		 */
		public List<Image> getImageList() {
			return imageList;
		}
	}
}
