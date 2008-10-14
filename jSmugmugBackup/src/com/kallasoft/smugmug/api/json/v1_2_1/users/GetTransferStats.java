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
package com.kallasoft.smugmug.api.json.v1_2_1.users;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats}
 * implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats
 */
public class GetTransferStats extends
		com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats {
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

	@Override
	public GetTransferStatsResponse execute(String url, String[] argumentValues) {
		return new GetTransferStatsResponse(executeImpl(url, argumentValues));
	}

	@Override
	public GetTransferStatsResponse execute(String url, String apiKey,
			String sessionID, Integer month, Integer year, Boolean heavy) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(month), APIUtils.toString(year),
				APIUtils.toString(heavy) });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats.GetTransferStatsResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats.GetTransferStatsResponse
	 */
	public class GetTransferStatsResponse
			extends
			com.kallasoft.smugmug.api.json.v1_2_0.users.GetTransferStats.GetTransferStatsResponse {
		public GetTransferStatsResponse(String responseText) {
			super(responseText);
		}
	}
}
