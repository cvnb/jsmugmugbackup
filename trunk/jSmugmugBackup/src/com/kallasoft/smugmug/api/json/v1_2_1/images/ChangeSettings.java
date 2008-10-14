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
package com.kallasoft.smugmug.api.json.v1_2_1.images;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings}
 * implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings
 */
public class ChangeSettings extends
		com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings {
	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * In version 1.2.1 support for the Latitude, Longitude and Altitude
	 * arguments was added.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "AlbumID", "Caption",
	 * "Keywords", "Hidden", "Latitude", "Longitude", "Altitude"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "AlbumID", "Caption", "Keywords", "Hidden", "Latitude",
			"Longitude", "Altitude" };

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

	@Override
	public ChangeSettingsResponse execute(String url, String[] argumentValues) {
		return new ChangeSettingsResponse(executeImpl(url, argumentValues));
	}

	@Override
	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, Integer albumID) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), APIUtils.toString(albumID) });
	}

	@Override
	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, String caption, String keywords,
			Boolean hidden) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), null, caption, keywords,
				APIUtils.toString(hidden) });
	}

	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, String caption, String keywords,
			Boolean hidden, Double latitude, Double longitude, Integer altitude) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), null, caption, keywords,
				APIUtils.toString(hidden), APIUtils.toString(latitude),
				APIUtils.toString(longitude), APIUtils.toString(altitude) });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings.ChangeSettingsResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings.ChangeSettingsResponse
	 */
	public class ChangeSettingsResponse
			extends
			com.kallasoft.smugmug.api.json.v1_2_0.images.ChangeSettings.ChangeSettingsResponse {
		public ChangeSettingsResponse(String responseText) {
			super(responseText);
		}
	}
}
