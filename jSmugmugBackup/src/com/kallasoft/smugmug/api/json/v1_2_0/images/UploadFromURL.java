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
import com.kallasoft.smugmug.api.json.util.JSONUtils;
import com.kallasoft.smugmug.api.json.v1_2_0.APIVersionConstants;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will upload a file from a specified URL to the specified Album
 * (identified by AlbumID) with the required parameters.
 * <p>
 * It is recommended that you use the binary upload method {@link UploadHTTPPut}
 * instead as it's a more optimized process and doesn't require that the image
 * be Base64 encoded before uploading it, like this method does.
 * <p>
 * The URL provided to methods in this class should be to the Text-based SmugMug
 * Upload server ({@link APIVersionConstants#TEXT_UPLOAD_SERVER_URL}) or the
 * image will not be processed.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.uploadFromURL">smugmug.images.uploadFromURL
 *      API Doc</a>
 */
public class UploadFromURL extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.uploadFromURL";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "URL", "ByteCount",
	 * "MD5Sum", "Caption", "Keywords", "Latitude", "Longitude", "Altitude"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "URL", "ByteCount", "MD5Sum", "Caption", "Keywords",
			"Latitude", "Longitude", "Altitude" };

	private static final Logger logger = LoggerFactory
			.getLogger(UploadFromURL.class);

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

	/**
	 * Used to execute the smugmug.images.uploadFromURL method, returning the ID
	 * of the uploaded image.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes the ID of the uploaded image.
	 */
	public UploadFromURLResponse execute(String url, String[] argumentValues) {
		if (!APIVersionConstants.TEXT_UPLOAD_SERVER_URL.equals(url))
			logger
					.warn(
							"url [{}] should normally be equal to the Text-based SmugMug Upload Server URL (defined by APIVersionConstants.TEXT_UPLOAD_SERVER_URL), otherwise the uploaded image won't be processed.",
							url);

		return new UploadFromURLResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.uploadFromURL
	 * method.
	 * <p>
	 * This method delegates to
	 * {@link #execute(String, String, String, Integer, String, Integer, String, String, String, Double, Double, Double)}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into.
	 * @param imageURL
	 *            The complete URL to the image to upload (e.g.
	 *            http://site.com/funny.jpg).
	 * 
	 * @return the response that includes the ID of the uploaded image.
	 * 
	 * @see #execute(String, String[])
	 */
	public UploadFromURLResponse execute(String url, String apiKey,
			String sessionID, Integer albumID, String imageURL) {
		return execute(url, apiKey, sessionID, albumID, imageURL, null, null,
				null, null, null, null, null);
	}

	/**
	 * Convenience method used to execute the smugmug.images.uploadFromURL
	 * method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into.
	 * @param imageURL
	 *            The complete URL to the image to upload (e.g.
	 *            http://site.com/funny.jpg).
	 * @param byteCount
	 *            The byte count for the image being uploaded (if known). 
	 * @param md5Sum
	 *            The MD5 Sum of the image being uploaded (if known). Use
	 *            <code>null</code> if value is unknown.
	 * @param caption
	 *            A caption for the image.
	 * @param keywords
	 *            The keywords assigned to the image.
	 * @param latitude
	 *            The latitude used to geocode the image. 
	 * @param longitude
	 *            The longitude used to geocode the image. 
	 * @param altitude
	 *            The altitude (in meters) used to geocode the image. 
	 * 
	 * @return the response that includes the ID of the uploaded image.
	 * 
	 * @see #execute(String, String[])
	 */
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
	 * Class used to represent the response for the smugmug.images.uploadFromURL
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class UploadFromURLResponse extends AbstractResponse {
		private Integer imageID;

		private String imageKey;

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
		public UploadFromURLResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);

				if (!responseObject.isNull("Image")) {
					JSONObject imageObject = responseObject
							.getJSONObject("Image");

					imageID = JSONUtils.getIntegerSafely(imageObject, "id");
					imageKey = JSONUtils.getStringSafely(imageObject, "Key");
				}
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return UploadFromURLResponse.class.getName() + "[isError="
					+ isError() + ", imageID=" + getImageID() + ", imageKey="
					+ getImageKey() + "]";
		}

		/**
		 * Used to get the ID of the uploaded image.
		 * 
		 * @return the ID of the uploaded image.
		 */
		public Integer getImageID() {
			return imageID;
		}

		/**
		 * Used to get the security key of the uploaded image.
		 * 
		 * @return the security key of the uploaded image.
		 */
		public String getImageKey() {
			return imageKey;
		}
	}
}
