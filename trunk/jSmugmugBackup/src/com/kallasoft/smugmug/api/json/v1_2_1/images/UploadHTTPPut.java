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

import com.kallasoft.smugmug.api.NetworkException;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut}
 * implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut
 */
public class UploadHTTPPut extends
		com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut {
	private static final Logger logger = LoggerFactory
			.getLogger(UploadHTTPPut.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public UploadHTTPPut() {
		this("Binary Upload (HTTP PUT)", null);
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
	public UploadHTTPPut(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	@Override
	public AbstractResponse execute(@SuppressWarnings("unused")
	String url, @SuppressWarnings("unused")
	String[] argumentValues) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Please use one of the other execute methods instead. This class implements a Binary-only upload method, it does not make use of the base AbstractMethod.executeImpl(String, String[]) implementation, which is text based. It implements it's own communication logic.");
	}

	@Override
	public UploadHTTPPutResponse execute(String url, String[] httpHeaderValues,
			byte[] imageData) throws IllegalArgumentException,
			NetworkException, RuntimeException {
		if (!APIVersionConstants.BINARY_UPLOAD_SERVER_URL.equals(url))
			logger
					.warn(
							"url [{}] should normally be equal to the Binary SmugMug Upload Server URL (defined by APIVersionConstants.BINARY_UPLOAD_SERVER_URL), otherwise the uploaded image won't be processed.",
							url);

		/*
		 * These values have to be forcibly set here and not only down in
		 * prepareUploadArgumentValues, because this method never makes a call
		 * to prepare the headers, and it's feasible to think that this method
		 * could get called with invalid values set for X-Smug-Version and
		 * X-Smug-ResponseType, so they need to be corrected right here in this
		 * method. We also log a warning to provide feedback to the caller if
		 * that is the case.
		 */

		/* This class is written to support the 1.2.0 JSON API, so set it */
		if (!"1.2.0".equals(httpHeaderValues[3])) {
			logger
					.warn("X-Smug-Version, httpHeaderValues[3], was not set to a value of '1.2.1' which is required when using this method implementation. The correct value has been set, overriding the previous value.");
			httpHeaderValues[3] = "1.2.0";
		}

		/* This can never be something different */
		if (!"JSON".equals(httpHeaderValues[4])) {
			logger
					.warn("X-Smug-ResponseType, httpHeaderValues[4], was not set to a value of 'JSON' which is required when using this method implementation. The correct value has been set, overriding the previous value.");

			httpHeaderValues[4] = "JSON";
		}

		return new UploadHTTPPutResponse(executeUploadImpl(url,
				httpHeaderValues, imageData));
	}

	@Override
	public UploadHTTPPutResponse execute(String url, String sessionID,
			Integer albumID, Integer imageID, String fileName,
			InputStream inputStream) {
		return execute(url, sessionID, albumID, imageID, fileName, inputStream,
				null, null, null, null, null);
	}

	@Override
	public UploadHTTPPutResponse execute(String url, String sessionID,
			Integer albumID, Integer imageID, String fileName,
			InputStream inputStream, String caption, String keywords,
			Double latitude, Double longitude, Double altitude)
			throws IllegalArgumentException, RuntimeException {
		/*
		 * Load the image data here, because we need to pass it to prepare AND
		 * pass it to the upload method.
		 */
		byte[] imageData = APIUtils.readStream(inputStream);

		/* Delegate to the main execute method after preparing the arguments */
		return execute(url, prepareUploadArgumentValues(imageData, sessionID,
				albumID, imageID, fileName, caption, keywords, latitude,
				longitude, altitude), imageData);
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut.UploadHTTPPutResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut.UploadHTTPPutResponse
	 */
	public class UploadHTTPPutResponse
			extends
			com.kallasoft.smugmug.api.json.v1_2_0.images.UploadHTTPPut.UploadHTTPPutResponse {
		public UploadHTTPPutResponse(String responseText) {
			super(responseText);
		}
	}
}
