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
package com.kallasoft.smugmug.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

/* TODO: Remove the APIKey argument from every method except the login methods */

/**
 * Class used to provide global constants across the entire API.
 * <p>
 * This is defined as a <code>class</code> and not an <code>interface</code>
 * in order to support the static initializer used to init the
 * {@link #HTTP_CLIENT} instance.
 * 
 * @author Riyad Kalla
 */
public class APIConstants {
	/**
	 * Used to store the current version of the API, for use in other portions
	 * of the API like reporting the User-Agent header.
	 */
	public static final String VERSION = "0.5.0";

	/**
	 * Used to define the String used for the User-Agent HTTP header reported by
	 * the API when it connects to SmugMug.
	 */
	public static final String USER_AGENT = "kallasoft API/" + VERSION;

	/**
	 * The singleton reference to the {@link HttpClient} used by the kallasoft
	 * SmugMug Java API to communicate with the SmugMug server. Having one
	 * instance per application is the preferred method.
	 * <p>
	 * This instance of <code>HttpClient</code> is configured specifically for
	 * safe, optimized, multi-threaded use by a static initializer in this
	 * class.
	 * 
	 * @see <a
	 *      href="http://jakarta.apache.org/httpcomponents/httpclient-3.x/performance.html">HttpClient
	 *      Performance Guide</a>
	 */
	public static final HttpClient HTTP_CLIENT = new HttpClient();

	//Quickfix from eclipse
	public static final int UNKNOWN_VALUE = Integer.MIN_VALUE;

	static {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HTTP_CLIENT.setHttpConnectionManager(connectionManager);
	}
}
