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
package com.kallasoft.smugmug.api.json.v1_2_0.users;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.json.entity.AlbumTransferStats;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * Gets transfer statistics for the logged-in user during the given Month and
 * Year. SmugMug often only keeps the last few months (current + 2 previous).
 * <p>
 * A float is provided for Originals because videos often are only partially
 * viewed.
 * <p>
 * If 'Heavy' is set to '1', an array of images with each image transfer stat
 * (just like smugmug.images.getStats) is provided for each album in addition to
 * the album transfer stats.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.users.getTransferStats">smugmug.users.getTransferStats
 *      API Doc</a>
 */
public class GetTransferStats extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.users.getTransferStats";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "Month", "Year", "Heavy"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID", "Month",
			"Year", "Heavy" };

	private static final Logger logger = LoggerFactory
			.getLogger(GetTransferStats.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetTransferStats() {
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
	public GetTransferStats(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.users.getTransferStats method, returning a
	 * response containing the transfer statistics requested for the particular
	 * user (assuming SmugMug had them on file).
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes all the transfer statistics for the
	 *         user requested (assuming SmugMug had them on file).
	 */
	public GetTransferStatsResponse execute(String url, String[] argumentValues) {
		return new GetTransferStatsResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.users.getTransferStats
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
	 * @param month
	 *            A number (1-12) indicating a month to get transfer statistics
	 *            for.
	 * @param year
	 *            A number (e.g. 2007) indicating the year to get the transfer
	 *            statistics for.
	 * @param heavy
	 *            Set to <code>true</code> to return heavy results, including
	 *            transfer stats for all the images in the album as well,
	 *            otherwise set to <code>false</code>.
	 * 
	 * @return the response that includes all the transfer statistics for the
	 *         given user for the given span of time (assuming SmugMug had it on
	 *         file).
	 * 
	 * @see #execute(String, String[])
	 */
	public GetTransferStatsResponse execute(String url, String apiKey,
			String sessionID, Integer month, Integer year, Boolean heavy) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(month), APIUtils.toString(year),
				APIUtils.toString(heavy) });
	}

	/**
	 * Class used to represent the response for the
	 * smugmug.users.getTransferStats method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetTransferStatsResponse extends AbstractResponse {
		private List<AlbumTransferStats> albumTransferStatsList = new ArrayList<AlbumTransferStats>();

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
		public GetTransferStatsResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				JSONArray albumArray = responseObject.getJSONArray("Albums");

				/*
				 * Process each album, all it's transfer stats and possibly any
				 * images it contains.
				 */
				for (int i = 0, length = albumArray.length(); i < length; i++)
					albumTransferStatsList.add(new AlbumTransferStats(
							albumArray.getJSONObject(i)));
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetTransferStatsResponse.class.getName() + "[isError="
					+ isError() + ", albumTransferStatsListSize="
					+ getAlbumTransferStatsList().size() + "]";
		}

		/**
		 * Used to retrieve the list of {@link AlbumTransferStats}s that
		 * contain transfer statistics for each album.
		 * 
		 * @return the list of {@link AlbumTransferStats}s that contain
		 *         transfer statistics for each album.
		 */
		public List<AlbumTransferStats> getAlbumTransferStatsList() {
			return albumTransferStatsList;
		}
	}
}
