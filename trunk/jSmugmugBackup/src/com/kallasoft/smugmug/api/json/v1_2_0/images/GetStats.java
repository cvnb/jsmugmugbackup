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
import com.kallasoft.smugmug.api.json.entity.ImageTransferStats;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * Gets transfer statistic for the given Image during the given Month. SmugMug
 * often only keeps the current month and the one previous, but you never know. :)
 * <p>
 * A float is provided for Originals because videos often are only partially
 * viewed.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.getStats">smugmug.images.getStats
 *      API Doc</a>
 */
public class GetStats extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.getStats";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "Month"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "Month" };

	private static final Logger logger = LoggerFactory
			.getLogger(GetStats.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetStats() {
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
	public GetStats(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.images.getStats method, returning the
	 * transfer statistics for the given image.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that contains the transfer stats for the given
	 *         image.
	 */
	public GetStatsResponse execute(String url, String[] argumentValues) {
		return new GetStatsResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.getStats method.
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
	 *            The ID of the image that will be moved.
	 * @param month
	 *            The month (1-12) to retrieve the transfer stats for. SmugMug
	 *            normally only keeps stats for the previous 2 months.
	 * 
	 * @return the response that indicates if the operation was successful or
	 *         not.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetStatsResponse execute(String url, String apiKey,
			String sessionID, Integer imageID, Integer month) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), APIUtils.toString(month) });
	}

	/**
	 * Class used to represent the response for the smugmug.images.getStats
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetStatsResponse extends AbstractResponse {
		private ImageTransferStats imageTransferStats;

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
		public GetStatsResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				imageTransferStats = new ImageTransferStats(responseObject
						.getJSONObject("Image"));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetStatsResponse.class.getName() + "[isError=" + isError()
					+ ", imageTransferStats=" + getImageTransferStats() + "]";
		}

		/**
		 * Used to get the transfer statistics for this image.
		 * 
		 * @return the transfer statistics for this image.
		 */
		public ImageTransferStats getImageTransferStats() {
			return imageTransferStats;
		}
	}
}
