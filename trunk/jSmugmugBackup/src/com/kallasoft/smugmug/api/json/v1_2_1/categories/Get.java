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
package com.kallasoft.smugmug.api.json.v1_2_1.categories;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.categories.Get} implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.categories.Get
 */
public class Get extends com.kallasoft.smugmug.api.json.v1_2_0.categories.Get {
	/**
	 * Construct a new method instance that can be executed.
	 */
	public Get() {
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
	public Get(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	@Override
	public GetResponse execute(String url, String[] argumentValues) {
		return new GetResponse(executeImpl(url, argumentValues));
	}

	@Override
	public GetResponse execute(String url, String apiKey, String sessionID) {
		return execute(url, apiKey, sessionID, null, null);
	}

	@Override
	public GetResponse execute(String url, String apiKey, String sessionID,
			String nickName, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID, nickName,
				sitePassword });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.categories.Get.GetResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.categories.Get.GetResponse
	 */
	public class GetResponse extends
			com.kallasoft.smugmug.api.json.v1_2_0.categories.Get.GetResponse {
		public GetResponse(String responseText) {
			super(responseText);
		}
	}
}
