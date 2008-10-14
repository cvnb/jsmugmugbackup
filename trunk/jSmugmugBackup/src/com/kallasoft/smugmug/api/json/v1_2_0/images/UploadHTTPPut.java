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
package com.kallasoft.smugmug.api.json.v1_2_0.images;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.APIConstants;
import com.kallasoft.smugmug.api.NetworkException;
import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.json.util.JSONUtils;
import com.kallasoft.smugmug.api.json.v1_2_0.APIVersionConstants;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will upload a file to the specified album (identified by
 * X-Smug-AlbumID) or replace an existing image (identified by X-Smug-ImageID)
 * with the required parameters identified by the values of
 * {@link #HTTP_HEADERS}.
 * <p>
 * The URL provided to methods in this class should be to the Binary-based
 * SmugMug Upload server ({@link APIVersionConstants#BINARY_UPLOAD_SERVER_URL})
 * or the image will not be processed.
 * <p>
 * This is the preferred method to upload images to SmugMug instead of the
 * text-based methods {@link Upload} and {@link UploadFromURL}.
 * <p>
 * The call-structure of this class is that all the execute convenience methods
 * will eventually delegate to {@link #execute(String, String[], byte[])} (after
 * possibly making use of
 * {@link #prepareUploadArgumentValues(byte[], String, Integer, Integer, String, String, String, Double, Double, Double)}
 * to prepare arguments for them) which itself will setup the HTTP Headers with
 * {@link #setupHTTPHeaders(PutMethod, String[])} and then eventually finish off
 * the request by calling into
 * {@link #executeUploadImpl(String, String[], byte[])} to perform the actual
 * upload to SmugMug.
 * <p>
 * <strong>NOTE</strong>: The values for the X-Smug-ResponseType and
 * X-Smug-Version headers will be forcibly set by all the execute methods in
 * this class, overriding any values that were previously set.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a href="http://smugmug.jot.com/WikiHome/API/Uploading">Binary Uploading
 *      Doc</a>
 */
public class UploadHTTPPut extends AbstractMethod {
	/**
	 * Defines all the HTTP header arguments this method takes.
	 * <p>
	 * The values for X-Smug-ResponseType and X-Smug-Version will automatically
	 * be set (to "JSON" and "1.2.0" respectively) by every execute method;
	 * overriding any values previously set and passed in.
	 * <p>
	 * Additional convenience methods like
	 * {@link #execute(String, String, Integer, Integer, String, InputStream)}
	 * will automatically set more headers such as Content-Length and
	 * Content-MD5 after loading the image. Be sure to check the respective
	 * method's documentation to see which headers are automatically being set
	 * for you.
	 * <p>
	 * Values are: "Content-Length", "Content-MD5", "X-Smug-SessionID",
	 * "X-Smug-Version", "X-Smug-ResponseType", "X-Smug-AlbumID",
	 * "X-Smug-ImageID", "X-Smug-FileName", "X-Smug-Caption", "X-Smug-Keywords",
	 * "X-Smug-Latitude", "X-Smug-Longitude", "X-Smug-Altitude"
	 */
	public static final String[] HTTP_HEADERS = { "Content-Length",
			"Content-MD5", "X-Smug-SessionID", "X-Smug-Version",
			"X-Smug-ResponseType", "X-Smug-AlbumID", "X-Smug-ImageID",
			"X-Smug-FileName", "X-Smug-Caption", "X-Smug-Keywords",
			"X-Smug-Latitude", "X-Smug-Longitude", "X-Smug-Altitude" };

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

	/**
	 * Unimplemented method, throws {@link UnsupportedOperationException}.
	 * <p>
	 * This method is implemented to throw an exception because the underlying
	 * implementation from {@link AbstractMethod} is implemented to do textual
	 * communication only, but this method implements it's own Binary-Only
	 * upload method using HTTP PUT.
	 * <p>
	 * Use any of the other <code>execute</code> methods to perform a binary
	 * upload to SmugMug.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return nothing. This method simply throws an exception.
	 * 
	 * @throws UnsupportedOperationException
	 *             if it is called. Please use one of the other
	 *             <code>execute</code> methods to perform an upload.
	 * 
	 * @see #execute(String, String[], byte[])
	 * @see #execute(String, String, Integer, Integer, String, InputStream)
	 * @see #execute(String, String, Integer, Integer, String, InputStream,
	 *      String, String, Double, Double, Double)
	 */
	public AbstractResponse execute(@SuppressWarnings("unused")
	String url, @SuppressWarnings("unused")
	String[] argumentValues) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Please use one of the other execute methods instead. This class implements a Binary-only upload method and provides alternative execute methods to use. It does not make use of the base AbstractMethod.executeImpl(String, String[]) implementation, which is text based.");
	}

	/**
	 * Used to perform a Binary-only upload using HTTP PUT.
	 * <p>
	 * Only one argument of either "X-Smug-AlbumID" (httpHeaderValues[5]) or
	 * "X-Smug-ImageID" (httpHeaderValues[6]) can be specified. If
	 * "X-Smug-AlbumID" is specified this operation will be a new-image-upload
	 * operation but if "X-Smug-ImageID" is specified the operation will be an
	 * image-replacement operation.
	 * <p>
	 * <strong>NOTE</strong>: This method forcibly sets the X-Smug-ResponseType
	 * header value to "JSON" and the X-Smug-Version header value to "1.2.0".
	 * Sub-classes are encouraged to set their own values, but this class of the
	 * API only supports these values.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param httpHeaderValues
	 *            The HTTP header values that will be sent along with this
	 *            upload. These values represent all the upload arguments to be
	 *            associated with the uploaded image, see {@link #HTTP_HEADERS}.
	 * @param imageData
	 *            A byte array containing the actual image data that will be
	 *            uploaded.
	 * 
	 * @return the response that includes an ID for the image after it's upload
	 *         completes.
	 * 
	 * @throws IllegalArgumentException
	 *             if url is null or empty.
	 * @throws IllegalArgumentException
	 *             if both arguments "X-Smug-AlbumID" (httpHeaderValues[5]) and
	 *             "X-Smug-ImageID" (httpHeaderValues[6]) have values specified
	 *             for the headers. Only 1 value at a time can be specified.
	 * @throws NetworkException
	 *             if anything other than a HTTP status code of 200 (OK) is
	 *             returned from the server. This is not the same as a network
	 *             exception and normally represents a failure on the SmugMug
	 *             server side.
	 * @throws NetworkException
	 *             if any network-based error occurs while trying to communicate
	 *             with the SmugMug server. This can wrap many different kinds
	 *             of more specific network-based exceptions. The handler can
	 *             call <code>getCause()</code> to retrieve a more specific
	 *             reason for the exception if necessary.
	 * @throws RuntimeException
	 *             if the given fileName (httpHeaderValues[7]), represented by
	 *             the X-Smug-FileName header value, cannot be property URL
	 *             encoded before being sent to SmugMug in the upload operation.
	 * 
	 * @see #HTTP_HEADERS
	 */
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
					.warn("X-Smug-Version, httpHeaderValues[3], was not set to a value of '1.2.0' which is required when using this method implementation. The correct value has been set, overriding the previous value.");
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

	/**
	 * Convenience method that delegates to
	 * {@link #execute(String, String, Integer, Integer, String, InputStream, String, String, Double, Double, Double)}.
	 * <p>
	 * Only one argument of either "X-Smug-AlbumID" (httpHeaderValues[5]) or
	 * "X-Smug-ImageID" (httpHeaderValues[6]) can be specified. If
	 * "X-Smug-AlbumID" is specified this operation will be a new-image-upload
	 * operation but if "X-Smug-ImageID" is specified the operation will be an
	 * image-replacement operation.
	 * <p>
	 * <strong>NOTE</strong>: This method forcibly sets the X-Smug-ResponseType
	 * header value to "JSON" and the X-Smug-Version header value to "1.2.0".
	 * Sub-classes are encouraged to set their own values, but this class of the
	 * API only supports these values.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into. Only albumID or
	 *            imageID can be specified, but not both.
	 * @param imageID
	 *            The ID of the image to replace. Only albumID or imageID can be
	 *            specified, but not both.
	 * @param fileName
	 *            The name of the file whose stream will be read for data and
	 *            uploaded.
	 * @param inputStream
	 *            The {@link InputStream} to load the image data from and upload
	 *            to SmugMug.
	 * 
	 * @return the response that includes an ID for the image after it's upload
	 *         completes.
	 * 
	 * @see #execute(String, String[], byte[])
	 * @see #HTTP_HEADERS
	 */
	public UploadHTTPPutResponse execute(String url, String sessionID,
			Integer albumID, Integer imageID, String fileName,
			InputStream inputStream) {
		return execute(url, sessionID, albumID, imageID, fileName, inputStream,
				null, null, null, null, null);
	}

	/**
	 * Convenience method used to take a {@link InputStream} and load the bytes
	 * of the image file before calling
	 * {@link #execute(String, String[], byte[])}.
	 * <p>
	 * Only one argument of either "X-Smug-AlbumID" (httpHeaderValues[5]) or
	 * "X-Smug-ImageID" (httpHeaderValues[6]) can be specified. If
	 * "X-Smug-AlbumID" is specified this operation will be a new-image-upload
	 * operation but if "X-Smug-ImageID" is specified the operation will be an
	 * image-replacement operation.
	 * <p>
	 * This method will automatically set the "Content-Length" and "Content-MD5"
	 * headers after loading the image and computing the values on the fly.
	 * <p>
	 * <strong>NOTE</strong>: This method forcibly sets the X-Smug-ResponseType
	 * header value to "JSON" and the X-Smug-Version header value to "1.2.0".
	 * Sub-classes are encouraged to set their own values, but this class of the
	 * API only supports these values.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into. Only albumID or
	 *            imageID can be specified, but not both.
	 * @param imageID
	 *            The ID of the image to replace. Only albumID or imageID can be
	 *            specified, but not both.
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
	 * @return the response that includes an ID for the image after it's upload
	 *         completes.
	 * 
	 * @throws IllegalArgumentException
	 *             if inputStream is <code>null</code>
	 * @throws RuntimeException
	 *             if the bytes of the image could not be loaded.
	 * 
	 * @see #execute(String, String[], byte[])
	 * @see #HTTP_HEADERS
	 */
	public UploadHTTPPutResponse execute(String url, String sessionID,
			Integer albumID, Integer imageID, String fileName,
			InputStream inputStream, String caption, String keywords,
			Double latitude, Double longitude, Double altitude)
			throws IllegalArgumentException, RuntimeException {
		if (inputStream == null)
			throw new IllegalArgumentException("inputStream cannot be null");

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
	 * Used to prepare all the arguments and placing them in a
	 * <code>String[]</code> that can be passed immediately to
	 * {@link #execute(String, String[], byte[])} and contain all the correct
	 * values.
	 * <p>
	 * This method will take the given <code>imageData</code> and calculate
	 * the MD5 Sum for the given contents as well as set the content length
	 * based on the same array. The caller will load to load the image data
	 * ahead of time before calling this this method to prepare all the argument
	 * values.
	 * <p>
	 * <strong>NOTE</strong>: This method forcibly sets the X-Smug-ResponseType
	 * argument to "JSON" as this is the only response type this entire library
	 * supports.
	 * 
	 * @param imageData
	 *            A <code>byte[]</code> that represents the image data loaded
	 *            from a stream. This method doesn't load the bytes from the
	 *            stream directly because the caller needs access to the
	 *            <code>byte[]</code> as well, so it is the caller's job to
	 *            load it.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to upload the image into.
	 * @param fileName
	 *            The name of the file whose stream will be read for data and
	 *            uploaded.
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
	 * @throws IllegalArgumentException
	 *             if <code>imageData</code> is <code>null</code>.
	 * @throws RuntimeException
	 *             if any of the image operation (calculateMD5Sum) fails as
	 *             defined in {@link APIUtils}. The thrown exception will wrap
	 *             the real exception that describes the failure.
	 */
	protected String[] prepareUploadArgumentValues(byte[] imageData,
			String sessionID, Integer albumID, Integer imageID,
			String fileName, String caption, String keywords, Double latitude,
			Double longitude, Double altitude) throws RuntimeException {
		if (imageData == null)
			throw new IllegalArgumentException("imageData cannot be null");

		String[] httpHeaderValues = new String[HTTP_HEADERS.length];
		String md5Sum = APIUtils.calculateMD5Sum(imageData);

		/* Setup all the header values for the known values so far */
		httpHeaderValues[0] = Integer.toString(imageData.length);
		httpHeaderValues[1] = md5Sum;
		httpHeaderValues[2] = sessionID;

		/*
		 * The version (index 3) is set up in the appropriate execute(String,
		 * String[], byte[]) method.
		 */

		/*
		 * This can never be something different, so we set it here to make it
		 * easier for sub-classes as well as setting it up in the main
		 * execute(String, String[], byte[]) method for good measure
		 */
		httpHeaderValues[4] = "JSON";

		/*
		 * One of these values will be null while the other will be a valid
		 * value (indicating an upload or replace operation respectively).
		 */
		httpHeaderValues[5] = APIUtils.toString(albumID);
		httpHeaderValues[6] = APIUtils.toString(imageID);

		httpHeaderValues[7] = fileName;
		httpHeaderValues[8] = caption;
		httpHeaderValues[9] = keywords;
		httpHeaderValues[10] = APIUtils.toString(latitude);
		httpHeaderValues[11] = APIUtils.toString(longitude);
		httpHeaderValues[12] = APIUtils.toString(altitude);

		return httpHeaderValues;
	}

	/**
	 * Used to take the given {@link PutMethod} and setup all the HTTP header
	 * name/value pairs for it before it is executed.
	 * <p>
	 * This is different from <code>AbstractMethod.setupPostParameters</code>
	 * because it is setting the HTTP header values and not the parameter
	 * values.
	 * <p>
	 * Any <code>null</code> values for <code>httpHeaders</code> or
	 * <code>httpHeaderValues</code> cause that pair of name/value to be
	 * ignored. For example, consider the pairs:
	 * <ul>
	 * <li>(1, bob)</li>
	 * <li>(2, mary)</li>
	 * <li>(3, <code>null</code>)</li>
	 * <li>(<code>null</code>, scott)</li>
	 * </ul>. The first 2 name/value pairs will be included in the HTTP PUT,
	 * but the 3rd and 4th will be ignored because either their name or value is
	 * <code>null</code>.
	 * <p>
	 * If more arguments exist than values, all the arguments without matching
	 * values will be ignored.
	 * 
	 * @param putMethod
	 *            The {@link PutMethod} that will be setup with all the
	 *            appropriate HTTP header name/value pairs in order to call the
	 *            SmugMug server.
	 * @param httpHeaderValues
	 *            The values to pass to the API call for each of the HTTP
	 *            headers.
	 * 
	 * @throws IllegalArgumentException
	 *             if putMethod is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if argumentValues is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if arguments.length &lt; argumentValues.length
	 */
	protected void setupHTTPHeaders(PutMethod putMethod,
			String[] httpHeaderValues) throws IllegalArgumentException {
		if (putMethod == null)
			throw new IllegalArgumentException(
					"putMethod cannot be null when attempting to setup the HTTP headers");

		if (httpHeaderValues == null)
			throw new IllegalArgumentException("httpHeaderValues can be null");

		if (HTTP_HEADERS.length < httpHeaderValues.length)
			throw new IllegalArgumentException(
					"HTTP_HEADERS.length must be >= httpHeaderValues.length. If HTTP_HEADERS.length is larger than httpHeaderValues.length, each header without a value is ignored.");

		for (int i = 0; i < HTTP_HEADERS.length; i++) {
			String httpHeader = HTTP_HEADERS[i];
			String httpHeaderValue = null;

			if (i < httpHeaderValues.length)
				httpHeaderValue = httpHeaderValues[i];
			else {
				/*
				 * We had more arguments than values, so stop building the
				 * string
				 */
				break;
			}

			/* If we have valid name/value pair, add it to the HTTP POST method */
			if (!APIUtils.isEmpty(httpHeader)
					&& !APIUtils.isEmpty(httpHeaderValue)) {
				logger.debug("\t\tAdding HTTP header name=[{}] value=[{}]",
						httpHeader, httpHeaderValue);

				putMethod.addRequestHeader(httpHeader, httpHeaderValue);
			}
		}
	}

	/**
	 * The core implementation used to perform a Binary-only upload using HTTP
	 * PUT.
	 * <p>
	 * Only one argument of either "X-Smug-AlbumID" (httpHeaderValues[5]) or
	 * "X-Smug-ImageID" (httpHeaderValues[6]) can be specified. If
	 * "X-Smug-AlbumID" is specified this operation will be a new-image-upload
	 * operation but if "X-Smug-ImageID" is specified the operation will be an
	 * image-replacement operation.
	 * <p>
	 * The header value X-Smug-ResponseType is set automatically to "JSON",
	 * overwriting any existing value set for it in the
	 * <code>httpHeaderValues</code> argument as this Java API only uses the
	 * SmugMug JSON API to communicate with SmugMug, so this value cannot be
	 * changed.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with. This
	 *            usually must be equal to the the SmugMug Upload URL, otherwise
	 *            the image will not be processed.
	 * @param httpHeaderValues
	 *            The HTTP header values that will be sent along with this
	 *            upload. These values represent all the upload arguments to be
	 *            associated with the uploaded image, see {@link #HTTP_HEADERS}.
	 * @param imageData
	 *            A byte array containing the actual image data that will be
	 *            uploaded.
	 * 
	 * @return a JSON-formatted reply from the SmugMug JSON API.
	 * 
	 * @throws IllegalArgumentException
	 *             if url is null or empty.
	 * @throws IllegalArgumentException
	 *             if both arguments "X-Smug-AlbumID" (httpHeaderValues[5]) and
	 *             "X-Smug-ImageID" (httpHeaderValues[6]) have values specified
	 *             for the headers. Only 1 value at a time can be specified.
	 * @throws NetworkException
	 *             if anything other than a HTTP status code of 200 (OK) is
	 *             returned from the server. This is not the same as a network
	 *             exception and normally represents a failure on the SmugMug
	 *             server side.
	 * @throws NetworkException
	 *             if any network-based error occurs while trying to communicate
	 *             with the SmugMug server. This can wrap many different kinds
	 *             of more specific network-based exceptions. The handler can
	 *             call <code>getCause()</code> to retrieve a more specific
	 *             reason for the exception if necessary.
	 * @throws RuntimeException
	 *             if the given fileName (httpHeaderValues[7]), represented by
	 *             the X-Smug-FileName header value, cannot be property URL
	 *             encoded before being sent to SmugMug in the upload operation.
	 * 
	 * @see #HTTP_HEADERS
	 */
	protected String executeUploadImpl(String url, String[] httpHeaderValues,
			byte[] imageData) throws IllegalArgumentException,
			NetworkException, RuntimeException {
		logger.debug("Executing {} Using Service URL {}", getMethodName(), url);

		if (APIUtils.isEmpty(url))
			throw new IllegalArgumentException("url [" + url
					+ "] cannot be null or empty");

		if (httpHeaderValues == null
				|| httpHeaderValues.length != HTTP_HEADERS.length)
			throw new IllegalArgumentException(
					"httpHeaderValues cannot be null and must be the same size as HTTP_HEADERS array, where each value maps to an argument in the same position from the HTTP_HEADERS array");

		if (httpHeaderValues[5] != null && httpHeaderValues[6] != null)
			throw new IllegalArgumentException(
					"Only 1 value, X-Smug-AlbumID (httpHeaderValues[5] - used to specify an upload album) or X-Smug-ImageID (httpHeaderValues[6] - used to specify an image to replace), can be specified at one time, but both are currently specified.");

		String fileName = httpHeaderValues[7];
		String encodedFileName = null;

		if (APIUtils.isEmpty(fileName))
			throw new IllegalArgumentException(
					"X-Smug-FileName (httpHeaderValues[7]) header value cannot be null or empty, it must be the file name of the image being uploaded (e.g. SmugMug.jpg)");

		try {
			encodedFileName = URIUtil.encode(fileName, null);
		} catch (URIException e) {
			throw new RuntimeException(
					"Unable to URL-encode the given fileName (X-Smug-FileName (httpHeaderValues[7]) header value) ["
							+ fileName
							+ "] in order to use it to perform the image upload operation.");
		}

		String response = null;
		PutMethod putMethod = null;

		try {
			/* SmugMug upload format is (Upload URI)/(File Name) */
			putMethod = new PutMethod(url + encodedFileName);

			/* Setup the HTTP headers */
			putMethod.setRequestHeader("User-Agent", APIConstants.USER_AGENT);
			setupHTTPHeaders(putMethod, httpHeaderValues);

			logger.trace("\tExecuting HTTP PUT...");

			/* Set the body of the PUT to the byte data from the image */
			putMethod.setRequestEntity(new ByteArrayRequestEntity(imageData));

			/* Perform the communication with the server */
			int code = APIConstants.HTTP_CLIENT.executeMethod(putMethod);

			logger.debug("\tReceived HTTP status code {}", Integer
					.valueOf(code));

			/*
			 * Make sure the server responded with a status code of 200 (OK),
			 * otherwise we won't have a valid response from it to parse.
			 */
			if (code == HttpStatus.SC_OK) {
				/* Read the response from the server */
				response = IOUtils.toString(
						putMethod.getResponseBodyAsStream(), putMethod
								.getResponseCharSet());

				logger.debug("\tRead response, was {} bytes long", Integer
						.valueOf(response.length()));
			} else {
				NetworkException ne = new NetworkException(
						"An HTTP status code of ["
								+ code
								+ "] was returned from the server, but a status code of 200 (OK) was expected. Something may be wrong with the SmugMug server at the moment or the network path to the SmugMug server.");
				logger.error("A network error occured", ne);
				throw ne;
			}
		} catch (Exception e) {
			RuntimeException ne = new RuntimeException(e);
			logger
					.error(
							"An error occured while trying to execute the HTTP PUT call",
							ne);
			throw ne;
		} finally {
			logger.trace("\tAttempting to cleanup network resources...");

			/*
			 * We must release the method no matter what, otherwise the
			 * connection is not released back to the HttpConnectionManager. Do
			 * this as safely as possible.
			 */
			if (putMethod != null) {
				try {
					putMethod.releaseConnection();
					logger.trace("\t\tNetwork resources cleaned up!");
				} catch (Exception e) {
					RuntimeException re = new RuntimeException(
							"Unable to release the connection used by the HttpClient instance. This should not happen.",
							e);
					logger
							.error(
									"Unable to release the connection used by the HttpClient instance",
									re);
					throw re;
				}
			}
		}

		logger.trace("\tReturning JSON response to caller...");
		return response;
	}

	/**
	 * Class used to represent the response for a Binary Upload (HTTP PUT)
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class UploadHTTPPutResponse extends AbstractResponse {
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
		public UploadHTTPPutResponse(String responseText)
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
			return UploadHTTPPutResponse.class.getName() + "[isError="
					+ isError() + ", imageID=" + getImageID() + ", imageKey="
					+ getImageKey() + "]";
		}

		/**
		 * Used to get the ID of the image that was uploaded.
		 * 
		 * @return the ID of the image that was uploaded.
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
