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

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method updates specific settings for a given image specified by ImageID.
 * <p>
 * If any of the parameters are missing or are empty strings "", they're ignored
 * and the current settings are preserved, otherwise they're updated with the
 * new settings.
 * <p>
 * This method can be used to update captions, keywords, or to move an image
 * from one album to another.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.changeSettings">smugmug.images.changeSettings
 *      API Doc</a>
 */
public class ChangeSettings extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.changeSettings";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "AlbumID", "Caption",
	 * "Keywords", "Hidden"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "AlbumID", "Caption", "Keywords", "Hidden" };

	/**
	 * Construct a new method instance that can be executed.
	 */
	public ChangeSettings() {
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
	public ChangeSettings(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.images.changeSettings method.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that indicates if the operation was successful or
	 *         not.
	 */
	public ChangeSettingsResponse execute(String url, String[] argumentValues) {
		return new ChangeSettingsResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.changeSettings
	 * method.
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
	 *            The ID of the image who will have the change operation
	 *            executed on.
	 * @param albumID
	 *            The ID of the album that this image will be moved to.
	 * 
	 * @return the response that indicates if the operation was successful or
	 *         not.
	 * 
	 * @see #execute(String, String[])
	 */
	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, Integer albumID) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), APIUtils.toString(albumID) });
	}

	/**
	 * Convenience method used to execute the smugmug.images.changeSettings
	 * method.
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
	 *            The ID of the image who will have the change operation
	 *            executed on.
	 * @param caption
	 *            A caption to assign to the given image.
	 * @param keywords
	 *            The keywords to assign to the given image.
	 * @param hidden
	 *            Set to <code>true</code> to hide the image or
	 *            <code>false</code> to leave it unhidden. Default is
	 *            <code>false</code>.
	 * 
	 * @return the response that indicates if the operation was successful or
	 *         not.
	 * 
	 * @see #execute(String, String[])
	 */
	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, String caption, String keywords,
			Boolean hidden) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), null, caption, keywords,
				APIUtils.toString(hidden) });
	}

	/**
	 * Class used to represent the response for the
	 * smugmug.images.changeSettings method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class ChangeSettingsResponse extends AbstractResponse {
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
		public ChangeSettingsResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);
		}

		@Override
		public String toString() {
			return ChangeSettingsResponse.class.getName() + "[isError="
					+ isError() + "]";
		}
	}
}
