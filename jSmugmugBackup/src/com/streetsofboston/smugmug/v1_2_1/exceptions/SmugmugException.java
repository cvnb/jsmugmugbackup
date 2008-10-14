/* Copyright 2007 StreetsOfBoston (Anton Spaans)
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
package com.streetsofboston.smugmug.v1_2_1.exceptions;

import com.kallasoft.smugmug.api.APIConstants;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;


/**
 * This class is the base class of all SmugmugException.
 * @author Anton Spaans.
 */
public class SmugmugException extends Exception {

	static final long serialVersionUID = 2105661036471338827L;
	
	private final String mAPIMethod;
	private final String mAPIStat;	// Should always be "fail"...
	private final int mCode;
	protected ISmugObject<?> mSmugObject;
	
	/**
	/**
	 * Creates a SmugmugException from a kallasoft-Response.
	 * @param response Kallasoft-Response.
	 * @param smugObject If not null, this object may be modified to handle the exception. E.g. remove it from the cache.
	 * @param <T> An ISmugObject
	 * @return null or an exception if the response indicates an error.
	 */
	public static<T extends ISmugObject<T>> SmugmugException create(AbstractResponse response, T smugObject) {
		if (!response.isError())
			return null;
		
		if (response.getError() == null) {
			return new SmugmugException("unknown error", 
					response.getMethod(),
					response.getStat(),
					APIConstants.UNKNOWN_VALUE
					);
			}
		 
		SmugmugException se = null;
		/*
		 * Handle the error-code and, if necessary, create various subclasses of SmugmugException.
		 */
		switch(response.getError().getCode()) {
		case 1: // invalid logon
			break;
		case 3: // invalid session
			break;
		case 4: // invalid user
			break;
		case 5: // system error
			break;
		case 7: // anonymous logon not permitted
			break;
		case 8: // invalid category
			break;
		case 11: // ancient version
			break;
		case 12: // invalid sort method
			break;
		case 13: // invalid sort order
			break;
		case 15: // empty set.
			return null;
		case 16: // invalid data
			break;
		case 18: // invalid API key
			break;
		default:
			break;
		}

		if (se == null)
			se = new SmugmugException(response);
		
		se.mSmugObject = smugObject;

		return se;
	}
	
	/**
	 * Throws a SmugmugException from a kallasoft-Response.
	 * @param response Kallasoft-Response.
	 * @param smugObject If not null, this object may be modified to handle the exception. E.g. remove it from the cache.
	 * @param <T> An ISmugObject
	 * @throws SmugmugException If the response indicates an error, a SmugmugException is thrown.
	 */
	public static<T extends ISmugObject<T>> void check(AbstractResponse response, T smugObject) throws SmugmugException {
		final SmugmugException se = create(response, smugObject);
		if (se != null)
			throw se;
	}
	
	/**
	 * Throws a SmugmugException from a kallasoft-Response.
	 * @param response Kallasoft-Response.
	 * @throws SmugmugException If the response indicates an error, a SmugmugException is thrown.
	 */
	public static void check(AbstractResponse response) throws SmugmugException {
		X dummy = null;
		final SmugmugException se = create(response, dummy);
		if (se != null)
			throw se;
	}
	private static abstract class X implements ISmugObject<X> {	};
	
	/**
	 * 
	 */
	public SmugmugException() {
		mAPIMethod = null;
		mAPIStat   = "fail";
		mCode      = APIConstants.UNKNOWN_VALUE;
	}

	/**
	 * @param message
	 */
	public SmugmugException(String message) {
		super(message);

		mAPIMethod = null;
		mAPIStat   = "fail";
		mCode      = APIConstants.UNKNOWN_VALUE;
	}

	/**
	 * @param cause
	 */
	public SmugmugException(Throwable cause) {
		super(cause);

		mAPIMethod = null;
		mAPIStat   = "fail";
		mCode      = APIConstants.UNKNOWN_VALUE;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SmugmugException(String message, Throwable cause) {
		super(message, cause);

		mAPIMethod = null;
		mAPIStat   = "fail";
		mCode      = APIConstants.UNKNOWN_VALUE;
	}

	public SmugmugException(AbstractResponse response) {
		this(
			response.getError().getMessage(),
			response.getMethod(),
			response.getStat(),
			response.getError().getCode());
	}

	public SmugmugException(String message, String method, String stat, int code) {
		super(message);
		mAPIMethod = method;
		mAPIStat   = stat;
		mCode      = code;
	}
	
	/**
	 * @return the Smugmug API method name
	 */
	public String getAPIMethod() {
		return mAPIMethod;
	}
	
	/**
	 * @return the Smugmug return status (should always be "fail").
	 */
	public String getAPIStat() {
		return mAPIStat;
	}
	
	/**
	 * @return the Smugmug error code.
	 */
	public int getCode() {
		return mCode;
	}
	
	/**
	 * @return the corresponding smugmug object that caused this exception, if any.
	 */
	public ISmugObject<?> getCorrespondingSmugmugObject() {
		return mSmugObject;
	}
	
}
