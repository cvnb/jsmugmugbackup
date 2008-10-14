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
 * This method will return details about the image specified by ImageID.
 * <p>
 * The album must be owned by the Session holder, or else be public (if
 * password-protected, a password must be provided) to return results, otherwise
 * an "invalid user" stat code will result. Additionally, some fields are only
 * returned to the album owner.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.getInfo">smugmug.images.getInfo
 *      API Doc</a>
 */
public class GetInfo extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.getInfo";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "ImageKey", "Password",
	 * "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "ImageKey", "Password", "SitePassword" };

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
	 * Used to execute the smugmug.images.getInfo method, returning all the
	 * information about a specific image.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes all the information about a specific
	 *         image.
	 */
	public GetInfoResponse execute(String url, String[] argumentValues) {
		return new GetInfoResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.getInfo method.
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
	 * @param imageID
	 *            The ID of the image whose information will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * 
	 * @return the response that includes all the information about a specific
	 *         image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetInfoResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey });
	}

	/**
	 * Convenience method used to execute the smugmug.images.getInfo method.
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
	 * @param imageID
	 *            The ID of the image whose information will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * @param password
	 *            The password to the containing album if necessary.
	 * @param sitePassword
	 *            The site password if necessary.
	 * 
	 * @return the response that includes all the information about a specific
	 *         image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetInfoResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey, String password,
			String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey, password, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.images.getInfo
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetInfoResponse extends AbstractResponse {
		private Image image;

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

				image = new Image(responseObject.getJSONObject("Image"));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetInfoResponse.class.getName() + "[isError=" + isError()
					+ ", image=" + getImage() + "]";
		}

		/**
		 * Used to return the image and all it's associated information.
		 * 
		 * @return the image and all it's associated information.
		 */
		public Image getImage() {
			return image;
		}
	}
}
