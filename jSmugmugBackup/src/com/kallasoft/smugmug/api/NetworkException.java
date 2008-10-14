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

/**
 * A class used to represent an exception that has occurred due to network
 * connectivity issues. The wrapped exception should give more fine-grained
 * details as to exactly what happened if the handler is interested.
 * <p>
 * This is meant to simplify catching the 6 or so exceptions that can occur if
 * this API is used and encountered network connectivity issues.
 * 
 * @author Riyad Kalla
 */
public class NetworkException extends RuntimeException {
	private static final long serialVersionUID = 630050296217291481L;

	/**
	 * Constructs a new network exception with <code>null</code> as its detail
	 * message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link RuntimeException#initCause(Throwable)}.
	 */
	public NetworkException() {
		super();
	}

	/**
	 * Constructs a new network exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call
	 * to {@link RuntimeException#initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public NetworkException(String message) {
		super(message);
	}

	/**
	 * Constructs a new network exception with the specified detail message and
	 * cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this network exception's detail
	 * message.
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
	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new network exception with the specified cause and a detail
	 * message of: <blockquote>A network error occurred while attempting to
	 * communicate with the SmugMug server. It's possible the problem is local
	 * (no network connection, partial network connection) or the SmugMug server
	 * is not responding.</blockquote>
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public NetworkException(Throwable cause) {
		super(
				"A network error occurred while attempting to communicate with the SmugMug server. It's possible the problem is local (no network connection, partial network connection) or the SmugMug server is not responding.",
				cause);
	}
}
