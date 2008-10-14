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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.APIConstants;
import com.kallasoft.smugmug.api.NetworkException;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * Class used to represent the base implementation of a {@link Method} in the
 * kallasoft SmugMug Java API.
 * <p>
 * All custom <em>method</em> implementations should extend this class and
 * provide the additional detail required to support the new method.
 * <p>
 * The {@link #executeImpl(String, String[])} implementation represents the
 * actual implementation of calling the SmugMug JSON API and getting a
 * JSON-formatted reply that it returns to the caller.
 * {@link #executeImpl(String, String[])} is the nuts and bolts of the
 * implementation to {@link Method#execute(String, String[])} but it cannot
 * return a specific {@link AbstractResponse} instance custom for each
 * {@link Method} implementation, so instead it returns the JSON-formatted reply
 * for the caller to parse into an appropriate {@link AbstractResponse} instance
 * and return.
 * <p>
 * The implementation of {@link #executeImpl(String, String[])} is fully
 * optimized according to the <a
 * href="http://jakarta.apache.org/httpcomponents/httpclient-3.x/performance.html">HttpClient
 * Performance Guide</a> to provide the best performance, memory management,
 * error reporting and resource-cleanup. Because of this, implementors are
 * encouraged to extend this class when implementing their own methods to take
 * advantage of it.
 * 
 * @author Riyad Kalla
 */
public abstract class AbstractMethod implements Method {
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractMethod.class);

	private String methodName;

	private String[] arguments;

	/**
	 * Construct a method that represents the given method name which takes the
	 * given arguments.
	 * 
	 * @param methodName
	 *            The name of the SmugMug JSON API method that this
	 *            <em>Method</em> represents.
	 * @param arguments
	 *            The names of the arguments that this method accepts.
	 * 
	 * @throws IllegalArgumentException
	 *             if methodName is <code>null</code> or empty.
	 */
	public AbstractMethod(String methodName, String[] arguments)
			throws IllegalArgumentException {
		if (APIUtils.isEmpty(methodName))
			throw new IllegalArgumentException(
					"methodName cannot be null or empty");

		this.methodName = methodName;
		this.arguments = arguments;

		logger.debug("Created Instance of Method: {}", getMethodName());
	}

	public String getMethodName() {
		return methodName;
	}

	public String[] getArguments() {
		return arguments;
	}

	/**
	 * Used to take the given {@link PostMethod} and setup all the name/value
	 * pairs for it before it is executed.
	 * <p>
	 * Any <code>null</code> values for <code>arguments</code> or
	 * <code>argumentValues</code> cause that pair of name/value to be
	 * ignored. For example, consider the pairs:
	 * <ul>
	 * <li>(1, bob)</li>
	 * <li>(2, mary)</li>
	 * <li>(3, <code>null</code>)</li>
	 * <li>(<code>null</code>, scott)</li>
	 * </ul>. The first 2 name/value pairs will be included in the HTTP POST,
	 * but the 3rd and 4th will be ignored because either their name or value is
	 * <code>null</code>.
	 * <p>
	 * If more arguments exist than values, all the arguments without matching
	 * values will be ignored.
	 * 
	 * @param postMethod
	 *            The {@link PostMethod} that will be setup with all the
	 *            appropriate name/value pairs in order to call the SmugMug
	 *            server.
	 * @param argumentValues
	 *            The values to pass to the API call for each of the arguments
	 *            it takes.
	 * 
	 * @throws IllegalArgumentException
	 *             if postMethod is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if methodName is <code>null</code> or empty.
	 * @throws IllegalArgumentException
	 *             if either arguments or argumentValues is <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if arguments.length &lt; argumentValues.length
	 */
	protected void setupPostParameters(PostMethod postMethod,
			String[] argumentValues) throws IllegalArgumentException {
		String methodName = getMethodName();
		String[] arguments = getArguments();

		if (postMethod == null)
			throw new IllegalArgumentException("postMethod cannot be null");

		if (APIUtils.isEmpty(methodName))
			throw new IllegalArgumentException(
					"methodName cannot be null or empty");

		if (arguments == null || argumentValues == null)
			throw new IllegalArgumentException("Neither arguments ["
					+ arguments + "] or argumentValues [" + argumentValues
					+ "] can be null");

		if (arguments.length < argumentValues.length)
			throw new IllegalArgumentException(
					"arguments.length must be >= argumentValues.length. If arguments.length is larger than argumentValues.length, the extra arguments are ignored.");

		logger
				.debug("\t\tAdding argument name=[method] value=[{}]",
						methodName);

		postMethod.addParameter("method", methodName);

		for (int i = 0; i < arguments.length; i++) {
			String argument = arguments[i];
			String argumentValue = null;

			if (i < argumentValues.length)
				argumentValue = argumentValues[i];
			else {
				/*
				 * We had more arguments than values, so stop building the
				 * string
				 */
				break;
			}

			/* If we have valid name/value pair, add it to the HTTP POST method */
			if (!APIUtils.isEmpty(argument) && !APIUtils.isEmpty(argumentValue)) {
				logger.debug("\t\tAdding argument name=[{}] value=[{}]",
						argument, argumentValue);

				postMethod.addParameter(argument, argumentValue);
			}
		}
	}

	/**
	 * The core implementation of the kallasoft SmugMug Java API that takes the
	 * given URL and argument values, and communicates with the SmugMug JSON API
	 * then returns the JSON-formatted reply to the caller.
	 * <p>
	 * Custom {@link Method} implementors are encouraged to use this method
	 * internally, parsing the JSON reply into the appropriate
	 * {@link AbstractResponse} then returning that to their caller.
	 * <p>
	 * The implementation of this method is fully optimized according to the <a
	 * href="http://jakarta.apache.org/httpcomponents/httpclient-3.x/performance.html">HttpClient
	 * Performance Guide</a> to provide the best performance, memory
	 * management, error reporting and resource-cleanup. Because of this,
	 * implementors are encouraged to extend this class when implementing their
	 * own methods to take advantage of it.
	 * 
	 * @param url
	 *            The URL of the SmugMug API to call.
	 * @param argumentValues
	 *            The values to pass to the API call for each of the arguments
	 *            it takes.
	 * 
	 * @return a JSON-formatted reply from the SmugMug JSON API.
	 * 
	 * @throws IllegalArgumentException
	 *             if url is null or empty.
	 * @throws NetworkException
	 *             if anything other than a HTTP status code of 200 is returned
	 *             from the server. This is not the same as a network exception
	 *             and normally represents a failure on the SmugMug server side.
	 * @throws NetworkException
	 *             if any network-based error occurs while trying to communicate
	 *             with the SmugMug server. This can wrap many different kinds
	 *             of more specific network-based exceptions. The handler can
	 *             call <code>getCause()</code> to retrieve a more specific
	 *             reason for the exception if necessary.
	 */
	protected String executeImpl(String url, String[] argumentValues)
			throws IllegalArgumentException, NetworkException {
		logger.debug("Executing Method {} Using Service URL {}",
				getMethodName(), url);

		if (APIUtils.isEmpty(url))
			throw new IllegalArgumentException("url [" + url
					+ "] cannot be null or empty");

		String response = null;
		PostMethod postMethod = null;

		try {
			/* Setup communication to the server */
			postMethod = new PostMethod(url);

			/* Setup the HTTP POST parameters */
			postMethod.setRequestHeader("User-Agent", APIConstants.USER_AGENT);
			setupPostParameters(postMethod, argumentValues);

			logger.trace("\tExecuting HTTP POST...");

			/* Perform the communication with the server */
			int code = APIConstants.HTTP_CLIENT.executeMethod(postMethod);

			logger.debug("\tReceived HTTP status code {}", Integer
					.valueOf(code));

			/*
			 * Make sure the server responded with a status code of 200 (OK),
			 * otherwise we won't likely have a valid response from it to parse.
			 */
			if (code == HttpStatus.SC_OK) {
				response = IOUtils.toString(postMethod
						.getResponseBodyAsStream(), postMethod
						.getResponseCharSet());

				logger.debug("\tRead response, was {} bytes long", Integer
						.valueOf(response.length()));
			} else {
				String message = "An HTTP status code of ["
						+ code
						+ "] was returned from the server, but a status code of 200 (OK) was expected. Something may be wrong with the SmugMug server at the moment or the network path to the SmugMug server.";
				NetworkException ne = new NetworkException(message);
				logger.error(message, ne);
				throw ne;
			}
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e);
			logger
					.error(
							"An error occured while trying to execute the HTTP Post call",
							re);
			throw re;
		} finally {
			logger.trace("\tAttempting to cleanup network resources...");

			/*
			 * We must release the method no matter what, otherwise the
			 * connection is not released back to the HttpConnectionManager. Do
			 * this as safely as possible.
			 */
			if (postMethod != null) {
				try {
					postMethod.releaseConnection();
					logger.trace("\t\tNetwork resources cleaned up!");
				} catch (Exception e) {
					String message = "Unable to release the connection used by the HttpClient instance. This should not happen.";
					RuntimeException re = new RuntimeException(message, e);
					logger.error(message, re);
					throw re;
				}
			}
		}

		logger.debug("\tReturning JSON response to caller...");
		return response;
	}
}
