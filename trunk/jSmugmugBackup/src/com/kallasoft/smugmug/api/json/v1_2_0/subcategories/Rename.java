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
package com.kallasoft.smugmug.api.json.v1_2_0.subcategories;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method renames the specified SubCategory with new Name.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.subcategories.rename">smugmug.subcategories.rename
 *      API Doc</a>
 */
public class Rename extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.subcategories.rename";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "SubCategoryID", "Name"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"SubCategoryID", "Name" };

	/**
	 * Construct a new method instance that can be executed.
	 */
	public Rename() {
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
	public Rename(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.subcategories.rename method, renaming the
	 * given subcategory with the given name.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes the status of the operation.
	 */
	public RenameResponse execute(String url, String[] argumentValues) {
		return new RenameResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.subcategories.rename
	 * method, renaming the given subcategory with the given name.
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
	 * @param subCategoryID
	 *            The ID of the subcategory that will be deleted.
	 * @param name
	 *            The new name for the subcategory.
	 * 
	 * @return the response that includes the status of the operation.
	 * 
	 * @see #execute(String, String[])
	 */
	public RenameResponse execute(String url, String apiKey, String sessionID,
			Integer subCategoryID, String name) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(subCategoryID), name });
	}

	/**
	 * Class used to represent the response for the smugmug.categories.rename
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class RenameResponse extends AbstractResponse {
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
		public RenameResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);
		}

		@Override
		public String toString() {
			return RenameResponse.class.getName() + "[isError=" + isError()
					+ "]";
		}
	}
}
