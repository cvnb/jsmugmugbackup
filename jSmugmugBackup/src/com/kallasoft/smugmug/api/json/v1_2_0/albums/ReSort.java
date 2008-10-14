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
package com.kallasoft.smugmug.api.json.v1_2_0.albums;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will re-sort all the photos inside of the album specified by
 * AlbumID.
 * <p>
 * Note that this is a one-time event, and doesn't apply directly to images
 * added in the future by other means.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.albums.reSort">smugmug.albums.reSort
 *      API Doc</a>
 */
public class ReSort extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.albums.reSort";

	/**
	 * Used to define the "FileName" sort method.
	 */
	public static final String BY_FILE_NAME = "FileName";

	/**
	 * Used to define the "Caption" sort method.
	 */
	public static final String BY_CAPTION = "Caption";

	/**
	 * Used to define the "DateTime" sort method.
	 */
	public static final String BY_DATE_TIME = "DateTime";

	/**
	 * Used to define the Ascending (ASC) sort direction.
	 */
	public static final String DIRECTION_ASCENDING = "ASC";

	/**
	 * Used to define the Ascending (DESC) sort direction.
	 */
	public static final String DIRECTION_DESCENDING = "DESC";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "By", "Direction"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "By", "Direction" };

	/**
	 * Construct a new method instance that can be executed.
	 */
	public ReSort() {
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
	public ReSort(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.albums.reSort method, returning confirmation
	 * that the sort has been executed.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a status of the sort operation.
	 */
	public ReSortResponse execute(String url, String[] argumentValues) {
		return new ReSortResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.albums.reSort method,
	 * returning confirmation that the sort has been executed.
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
	 * @param albumID
	 *            The ID of the album to sort.
	 * @param by
	 *            The method to sort the album by. See BY_XXX constants defined
	 *            in this class.
	 * @param direction
	 *            The direction to sort of the album by. See DIRECTION_XXX
	 *            constants defined in this class.
	 * 
	 * @return the response that includes a status of the sort operation.
	 * 
	 * @see #execute(String, String[])
	 */
	public ReSortResponse execute(String url, String apiKey, String sessionID,
			Integer albumID, String by, String direction) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), by, direction });
	}

	/**
	 * Class used to represent the response for the smugmug.albums.get method
	 * call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class ReSortResponse extends AbstractResponse {
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
		public ReSortResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);
		}

		@Override
		public String toString() {
			return ReSortResponse.class.getName() + "[isError=" + isError()
					+ "]";
		}
	}
}
