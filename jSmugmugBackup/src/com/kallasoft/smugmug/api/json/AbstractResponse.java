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
package com.kallasoft.smugmug.api.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * A class that provides the base implementation of a <code>response</code>
 * from the <a href="http://smugmug.jot.com/WikiHome/API">SmugMug JSON API
 * methods</a>.
 * <p>
 * For all of the methods defined in the SmugMug JSON API, all responses share
 * the same fundamental structure of:
 * <ul>
 * <li><em>stat</em> argument that describes the status of the request ("ok"
 * or "fail")</li>
 * <li><em>method</em> argument that contains the name of the method called
 * that was executed</li>
 * <ul>
 * <li>OPTIONAL: <em>code</em> error code of the error that occurred</li>
 * <li>OPTIONAL: <em>message</em> a textual message describing the error that
 * occurred</li>
 * </ul>
 * </ul>
 * This base implementation of a <em>response</em> can take a
 * <code>String</code> representing the JSON response, and parse out all this
 * information, optionally creating the {@link Error} object if an error
 * occurred, otherwise leaving it <code>null</code>.
 * <p>
 * To quickly check if the <code>AbstractResponse</code> you are dealing with
 * represents an error, you can call the {@link #isError()} method.
 * <p>
 * All <code>AbstractResponse</code> implementations in this API extend this
 * base class and provide additional properties and parsing rules depending on
 * the format of the reply for each particular request.
 * 
 * @author Riyad Kalla
 */
public abstract class AbstractResponse {
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractResponse.class);

	private String stat;

	private String method;

	private Error error;

	/**
	 * Construct a response that parses it's values out of the given
	 * <code>responseText</code> that represents the JSON reply.
	 * 
	 * @param responseText
	 *            The text of the JSON reply. If the text is <code>null</code>
	 *            this method simply returns.
	 * 
	 * @throws RuntimeJSONException
	 *             if an error occurs while parsing the JSON response text.
	 */
	public AbstractResponse(String responseText) throws RuntimeJSONException {
		logger.debug("Received JSON response: {}", responseText);

		/* Stop processing if there is no text to parse */
		if (APIUtils.isEmpty(responseText))
			return;

		JSONObject responseObject = null;

		try {
			responseObject = new JSONObject(responseText);

			logger.debug("Parsed JSON response, JSONObject:\n{}",
					responseObject.toString(4));

			/* Parse all mandatory values */
			stat = responseObject.getString("stat");

			Integer errorCode = null;
			String errorMessage = null;

			/*
			 * Now parse optional values (method is optional because it's not
			 * reported back when a failure occurs)
			 */
			if (!responseObject.isNull("method"))
				method = responseObject.getString("method");

			if (!responseObject.isNull("code"))
				errorCode = Integer.valueOf(responseObject.getInt("code"));

			if (!responseObject.isNull("message"))
				errorMessage = responseObject.getString("message");

			/* Only create the Error if we need to */
			if (errorCode != null && errorMessage != null) {
				error = new Error(errorCode, errorMessage);
				logger.debug("\tResponse was an error: {}", error);
			}
		} catch (JSONException e) {
			RuntimeJSONException rje = new RuntimeJSONException(e);
			logger.error("An error occured parsing the JSON response", rje);
			throw rje;
		}
	}

	@Override
	public String toString() {
		return AbstractResponse.class.getName() + "[isError=" + isError()
				+ ", stat=" + getStat() + ", method=" + getMethod()
				+ ", error=" + getError() + "]";
	}

	/**
	 * Used to determine if this response represents an error or not.
	 * 
	 * @return <code>true</code> if an error occurred and this response
	 *         contains it's error code and message, otherwise returns
	 *         <code>false</code>.
	 */
	public boolean isError() {
		return (getError() != null);
	}

	/**
	 * Used to get the "stat" value from the SmugMug JSON API reply.
	 * <p>
	 * This should either be "ok" or "fail".
	 * 
	 * @return the "stat" value from the SmugMug JSON API reply.
	 */
	public String getStat() {
		return stat;
	}

	/**
	 * Used to get the "method" value from the SmugMug JSON API reply.
	 * <p>
	 * This is the method that was called, e.g. "smugmug.login.withPassword".
	 * 
	 * @return the "method" value from the SmugMug JSON API reply.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Used to get the {@link Error} associated with this response if one
	 * occurred, otherwise this will return <code>null</code>.
	 * 
	 * @return the {@link Error} associated with this response if one occurred,
	 *         otherwise this will return <code>null</code>.
	 */
	public Error getError() {
		return error;
	}

	/**
	 * Class used to represent an error from a SmugMug JSON API response.
	 * 
	 * @author Riyad Kalla
	 */
	public class Error {
		private Integer code;

		private String message;

		/**
		 * Construct an error with the given error code and error message.
		 * <p>
		 * Error codes and messages are defined in the <a
		 * href="http://smugmug.jot.com/WikiHome/API">SmugMug API Documentation</a>
		 * for each method.
		 * 
		 * @param code
		 *            The error code for this error.
		 * @param message
		 *            The error message for this error.
		 * 
		 * @throws IllegalArgumentException
		 *             if code is null or &lt; 0 (it must represent a valid
		 *             error code defined by the SmugMug API)
		 * @throws IllegalArgumentException
		 *             if message is <code>null</code> or empty.
		 */
		public Error(Integer code, String message)
				throws IllegalArgumentException {
			if (code == null || code.intValue() < 0)
				throw new IllegalArgumentException(
						"code must be >= 0 and represent a valid error code defined by the SmugMug API");

			if (APIUtils.isEmpty(message))
				throw new IllegalArgumentException(
						"message cannot be null or empty");

			this.code = code;
			this.message = message;
		}

		@Override
		public String toString() {
			return Error.class.getName() + "[code=" + getCode() + ", message="
					+ getMessage() + "]";
		}

		/**
		 * Used to get the error code for this error.
		 * 
		 * @return the error code for this error.
		 */
		public Integer getCode() {
			return code;
		}

		/**
		 * Used to get the message for this error.
		 * 
		 * @return the message for this error.
		 */
		public String getMessage() {
			return message;
		}
	}
}
