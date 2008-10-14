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

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.Upload} implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.Upload
 */
public class Upload extends com.kallasoft.smugmug.api.json.v1_2_0.images.Upload {
	private static final Logger logger = LoggerFactory.getLogger(Upload.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Upload() {
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
	public Upload(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	@Override
	public UploadResponse execute(String url, String[] argumentValues) {
		if (!APIVersionConstants.TEXT_UPLOAD_SERVER_URL.equals(url))
			logger
					.warn(
							"url [{}] should normally be equal to the Text-based SmugMug Upload Server URL (defined by APIVersionConstants.TEXT_UPLOAD_SERVER_URL), otherwise the uploaded image won't be processed.",
							url);

		/*
		 * This method has to assume that the String[] being passed in has been
		 * setup exactly as the caller wants it to be, so no additional
		 * pre-processing (e.g. calling one of the methods that calculates MD5,
		 * encoding, etc.) should be performed on the contents of this array.
		 * The arguments should be passed through immediately, as-is, to
		 * super.executeImpl.
		 */
		return new UploadResponse(executeImpl(url, argumentValues));
	}

	@Override
	public UploadResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String fileName, InputStream inputStream) {
		return execute(url, apiKey, sessionID, albumID, fileName, inputStream,
				null, null, null, null, null);
	}

	@Override
	public UploadResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String fileName, InputStream inputStream,
			String caption, String keywords, Double latitude, Double longitude,
			Double altitude) throws IllegalArgumentException, RuntimeException {
		return execute(url, prepareUploadArgumentValues(apiKey,
				sessionID, albumID, fileName, inputStream, caption, keywords,
				latitude, longitude, altitude));
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.Upload.UploadResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.Upload.UploadResponse
	 */
	public class UploadResponse extends
			com.kallasoft.smugmug.api.json.v1_2_0.images.Upload.UploadResponse {
		public UploadResponse(String responseText) {
			super(responseText);
		}
	}
}
