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

/**
 * A class used to convert a {@link JSONException} (checked exception) to a
 * {@link RuntimeException} so it can either be ignored or handled by the caller
 * easily.
 * <p>
 * This allows JSONExceptions to be reported outside the API without requiring
 * implementors to use checked exceptions with every single method.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a href="http://json.org/java/">JSON.org Java Page</a>
 */
public class RuntimeJSONException extends RuntimeException {
	private static final long serialVersionUID = 7500227514521504278L;

	/**
	 * Constructs a new Runtime JSON exception with <code>null</code> as its
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link RuntimeException#initCause(Throwable)}.
	 */
	public RuntimeJSONException() {
		super();
	}

	/**
	 * Constructs a new Runtime JSON exception with the specified detail
	 * message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link RuntimeException#initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public RuntimeJSONException(String message) {
		super(message);
	}

	/**
	 * Constructs a new Runtime JSON exception with the specified detail message
	 * and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this Runtime JSON exception's
	 * detail message.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public RuntimeJSONException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new Runtime JSON exception with the specified cause and a
	 * detail message of: <blockquote>An error occurred while attempting to
	 * parse the JSON object text. It's possible the JSON reply was corrupted in
	 * some manner or that the value requested from it didn't exist.</blockquote>
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public RuntimeJSONException(Throwable cause) {
		this(
				"An error occurred while attempting to parse the JSON object text. It's possible the JSON reply was corrupted (malformed, incomplete, etc.) or that the value requested from it didn't exist.",
				cause);
	}
}
