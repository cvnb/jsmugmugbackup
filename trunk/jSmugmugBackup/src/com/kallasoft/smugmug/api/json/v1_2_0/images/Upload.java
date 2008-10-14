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

import java.io.InputStream;

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
 * This method will upload a file to the specified Album (identified by AlbumID)
 * with the required parameters.
 * <p>
 * It is recommended that you use the binary upload method {@link UploadHTTPPut}
 * instead as it's a more optimized process and doesn't require that the image
 * be Base64 encoded before uploading it, like this method does.
 * <p>
 * The URL provided to methods in this class should be to the Text-based SmugMug
 * Upload server ({@link APIVersionConstants#TEXT_UPLOAD_SERVER_URL}) or the
 * image will not be processed.
 * <p>
 * The inter-relationships between the methods in this class are as follows:
 * <ul>
 * <li>{@link #execute(String, String[])} passes it's values directly through,
 * as-is, to the super executeImpl method, wrapping the result in an appropriate
 * response.</li>
 * <li>{@link #execute(String, String, String, Integer, String, InputStream)}
 * simply passes it's values, as-is, straight through to
 * {@link #execute(String, String, String, Integer, String, InputStream, String, String, Double, Double, Double)},
 * including <code>null</code> for values that are unknown.</li>
 * <li>{@link #execute(String, String, String, Integer, String, InputStream, String, String, Double, Double, Double)}
 * utilizes
 * {@link #prepareUploadArgumentValues(String, String, Integer, String, InputStream, String, String, Double, Double, Double)}
 * to take all the arguments and pre-process them and then assign them,
 * in-order, into a <code>String[]</code> that is immediately passed off to
 * {@link #execute(String, String[])} for execution.</li>
 * </ul>
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.upload">smugmug.images.upload
 *      API Doc</a>
 */
public class Upload extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.upload";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "FileName", "Data",
	 * "ByteCount", "MD5Sum", "Caption", "Keywords", "Latitude", "Longitude",
	 * "Altitude"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "FileName", "Data", "ByteCount", "MD5Sum", "Caption",
			"Keywords", "Latitude", "Longitude", "Altitude" };

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

	/**
	 * Used to execute the smugmug.images.upload method, returning the ID of the
	 * uploaded image.
	 * <p>
	 * <strong>NOTE</strong>: This method calls directly through to
	 * AbstractMethod#executeImpl, it does not call into any other method to do
	 * pre-processing of it's values.
	 * <p>
	 * For methods that offer pre-processing conveniences, please look at the
	 * other <code>execute(...)</code> methods defined in this class and check
	 * the associated Javadoc with each method to see what it does.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes the ID of the uploaded image.
	 * 
	 * @see #execute(String, String, String, Integer, String, InputStream)
	 * @see #execute(String, String, String, Integer, String, InputStream,
	 *      String, String, Double, Double, Double)
	 */
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

	/**
	 * Convenience method used to execute the smugmug.images.upload method.
	 * <p>
	 * This method delegates to
	 * {@link #execute(String, String, String, Integer, String, InputStream, String, String, Double, Double, Double)}.
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
	 * @param fileName
	 *            The name of the file whose stream will be read for data and
	 *            uploaded.
	 * @param inputStream
	 *            The {@link InputStream} to load and upload to SmugMug.
	 * 
	 * @return the response that includes the ID of the uploaded image.
	 * 
	 * @see #execute(String, String[])
	 * @see #execute(String, String, String, Integer, String, InputStream,
	 *      String, String, Double, Double, Double)
	 */
	public UploadResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String fileName, InputStream inputStream) {
		return execute(url, apiKey, sessionID, albumID, fileName, inputStream,
				null, null, null, null, null);
	}

	/**
	 * Convenience method used to execute the smugmug.images.upload method.
	 * <p>
	 * This method will perform the following tasks automatically:
	 * <ol>
	 * <li>Load the bytes of the image from the stream, keeping track of the
	 * image's size</li>
	 * <li>Calculate the MD5 Sum of the image</li>
	 * <li>Base64-encode the image data using a highly optimized Base64
	 * encoding algorithm</li>
	 * <li>Then delegate the call to {@link #execute(String, String[])},
	 * passing all the argument values included in the method call along with
	 * Data, ByteCount and MD5Sum.</li>
	 * </ol>
	 * Because of steps 1 and 3, this method will require memory slightly more
	 * than twice the size of the image being loaded. Once to load the image
	 * data, then again to load the Base64-encoded version of it.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * <p>
	 * <strong>NOTE</strong>: This method uses
	 * {@link #prepareUploadArgumentValues(String, String, Integer, String, InputStream, String, String, Double, Double, Double)}
	 * to do the actual preparation of the image data before delegating to
	 * {@link #execute(String, String[])}.
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
	 * @param fileName
	 *            The name of the file whose stream will be read for data and
	 *            uploaded.
	 * @param inputStream
	 *            The {@link InputStream} to load and upload to SmugMug.
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
	 * @see #prepareUploadArgumentValues(String, String, Integer, String,
	 *      InputStream, String, String, Double, Double, Double)
	 */
	public UploadResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String fileName, InputStream inputStream,
			String caption, String keywords, Double latitude, Double longitude,
			Double altitude) {
		return execute(url, prepareUploadArgumentValues(apiKey, sessionID,
				albumID, fileName, inputStream, caption, keywords, latitude,
				longitude, altitude));
	}

	/**
	 * Convenience method used by this class and can be used by any sub-class to
	 * prepare the given arguments by pre-processing the image data and then
	 * placing all the resulting values into a <code>String[]</code> that can
	 * be then passed directly to {@link #execute(String, String[])} to perform
	 * the upload.
	 * <p>
	 * This method will perform the following operations automatically:
	 * <ol>
	 * <li>Read the image data from the given stream using
	 * <code>APIUtils.readStream</code></li>
	 * <li>Base64-encode the read-in image data using
	 * <code>APIUtils.base64Encode</code></li>
	 * <li>Calculate the MD5 Sum of the image data that was read in using
	 * <code>APIUtils.calculateMD5Sum</code></li>
	 * </ol>
	 * 
	 * Placing all the resulting values, and the rest of the passed in values,
	 * in-order, into a <code>String[]</code> and returning it.
	 * 
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into.
	 * @param fileName
	 *            The name of the file whose stream will be read for data and
	 *            uploaded.
	 * @param inputStream
	 *            The {@link InputStream} to load and upload to SmugMug.
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
	 * @return a <code>String[]</code> that represents all the fully prepared
	 *         argument values that can now be passed directly to the
	 *         {@link #execute(String, String[])} method.
	 * 
	 * @throws RuntimeException
	 *             if any of the image operations (readStream, base64Encode or
	 *             calculateMD5Sum) fail as defined in {@link APIUtils}. The
	 *             thrown exception will wrap the real exception that describes
	 *             the failure.
	 */
	protected String[] prepareUploadArgumentValues(String apiKey,
			String sessionID, Integer albumID, String fileName,
			InputStream inputStream, String caption, String keywords,
			Double latitude, Double longitude, Double altitude)
			throws RuntimeException {
		if (inputStream == null)
			throw new IllegalArgumentException("inputStream cannot be null");

		/*
		 * Perform all the image data processing steps. Any of these can throw
		 * an exception that will kick back out to the caller to catch.
		 */
		byte[] imageData = APIUtils.readStream(inputStream);
		String encodedData = APIUtils.base64Encode(imageData);
		String md5Sum = APIUtils.calculateMD5Sum(imageData);

		String[] argumentValues = new String[12];

		/* Now fill out the argument values array */
		argumentValues[0] = apiKey;
		argumentValues[1] = sessionID;
		argumentValues[2] = APIUtils.toString(albumID);
		argumentValues[3] = fileName;
		argumentValues[4] = encodedData;
		argumentValues[5] = Integer.toString(imageData.length);
		argumentValues[6] = md5Sum;
		argumentValues[7] = caption;
		argumentValues[8] = keywords;
		argumentValues[9] = APIUtils.toString(latitude);
		argumentValues[10] = APIUtils.toString(longitude);
		argumentValues[11] = APIUtils.toString(altitude);

		return argumentValues;
	}

	/**
	 * Class used to represent the response for the smugmug.images.upload method
	 * call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class UploadResponse extends AbstractResponse {
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
		public UploadResponse(String responseText) throws RuntimeJSONException {
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
			return UploadResponse.class.getName() + "[isError=" + isError()
					+ ", imageID=" + getImageID() + ", imageKey="
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
