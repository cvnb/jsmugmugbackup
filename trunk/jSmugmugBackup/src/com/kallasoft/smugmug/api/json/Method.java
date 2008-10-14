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

/**
 * Interface used to define a method in the kallasoft SmugMug Java API.
 * <p>
 * A method typically represents one of the <a
 * href="http://smugmug.jot.com/WikiHome/API">SmugMug JSON API methods</a> but
 * implementors are free to build on top of the <code>Method</code>
 * implementation support to build any functionality they would like.
 * <p>
 * The SmugMug JSON API uses the standard HTTP protocol to communicate,
 * therefore all values passed to the kallasoft SmugMug Java API must be
 * converted to a <code>String</code> representation as the last step before
 * it can be included in the HTTP POST to the SmugMug server. Because of this,
 * the base implementation of {@link #execute(String, String[])} in all methods
 * of the kallasoft SmugMug Java API have been normalized to accept a
 * <code>String[]</code> of values, in order, for each value included in the
 * request identified by the {@link #getArguments()} method.
 * <p>
 * While the kallasoft SmugMug Java API attempts to provide convenience methods
 * for most all methods to make using them easier, implements are free to add
 * more convenience methods that can take other values and ultimately handle
 * converting them to <code>String</code>s before making the call to
 * {@link #execute(String, String[])}.
 * 
 * @author Riyad Kalla
 */
public interface Method {
	/**
	 * Used to get the name of the SmugMug JSON API method that this
	 * <code>Method</code> represents and will call when executed. For
	 * example, <code>smugmug.login.withPassword</code>.
	 * 
	 * @return the name of the SmugMug JSON API method that this method
	 *         represents and will call when executed.
	 */
	public String getMethodName();

	/**
	 * Used to get a list of the argument names for this method. For example,
	 * <code>APIKey, SessionID and AlbumID</code>.
	 * 
	 * @return a list of the argument names for this method.
	 */
	public String[] getArguments();

	/**
	 * Used to execute a <code>Method</code> by calling the given
	 * <code>url</code> and passing the given <code>argumentValues</code>
	 * for each of the arguments this method defines (identified by
	 * {@link #getArguments()})
	 * <p>
	 * While implementations of <code>Method</code> may provide additional
	 * wrapper methods that make it easier to use the API, this method defines
	 * the central entry point into the kallasoft SmugMug Java API where the
	 * communication with the SmugMug server is initiated and result parsing
	 * begun.
	 * 
	 * @param url
	 *            The URL of the SmugMug API to call.
	 * @param argumentValues
	 *            The values to pass to the API call for each of the arguments
	 *            it takes.
	 * 
	 * @return a {@link AbstractResponse} implementation that represents the
	 *         JSON-formatted reply from the SmugMug JSON API call.
	 */
	public AbstractResponse execute(String url, String[] argumentValues);
}
