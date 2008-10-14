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
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL}
 * implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL
 */
public class UploadFromURL extends
		com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL {
	/**
	 * Construct a new method instance that can be executed.
	 */
	public UploadFromURL() {
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
	public UploadFromURL(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	@Override
	public UploadFromURLResponse execute(String url, String[] argumentValues) {
		return new UploadFromURLResponse(executeImpl(url, argumentValues));
	}

	@Override
	public UploadFromURLResponse execute(String url, String apiKey,
			String sessionID, Integer albumID, String imageURL) {
		return execute(url, apiKey, sessionID, albumID, imageURL, null, null,
				null, null, null, null, null);
	}

	@Override
	public UploadFromURLResponse execute(String url, String apiKey,
			String sessionID, Integer albumID, String imageURL,
			Integer byteCount, String md5Sum, String caption, String keywords,
			Double latitude, Double longitude, Double altitude) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), imageURL,
				APIUtils.toString(byteCount), md5Sum, caption, keywords,
				APIUtils.toString(latitude), APIUtils.toString(longitude),
				APIUtils.toString(altitude) });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL.UploadFromURLResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL.UploadFromURLResponse
	 */
	public class UploadFromURLResponse
			extends
			com.kallasoft.smugmug.api.json.v1_2_0.images.UploadFromURL.UploadFromURLResponse {
		public UploadFromURLResponse(String responseText) {
			super(responseText);
		}
	}
}
