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

import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.streetsofboston.smugmug.v1_2_1.system.IHasID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;

/**
 * This exception is thrown when an operation was attempted on a Smugmug Object
 * that does not or no longer exists in the Smugmug repository.
 * @author Anton Spaans
 */
public class ObjectDoesNotExistException extends SmugmugException {
	static final long serialVersionUID = -3963772675502861885L;

	private int            mID;
	private Class<?>       mClass;
	
	public ObjectDoesNotExistException(int ID, Class<?> klass) {
		super();
		mID = ID;
		mClass = klass;
	}
	
	public ObjectDoesNotExistException(int ID, Class<?> klass, String owner) {
		super();
		mID = ID;
		mClass = klass;
	}

	public<T extends ISmugObject<T>> ObjectDoesNotExistException(T smugObject) {
		super();
		mID = (smugObject != null) ? smugObject.getID() : IHasID.NO_ID;
		mClass = (smugObject != null) ? smugObject.getClass() : null;
		mSmugObject = smugObject;
	}
	
	public<T extends ISmugObject<T>> ObjectDoesNotExistException(AbstractResponse response, T smugObject) {
		super(response);
		mID = (smugObject != null) ? smugObject.getID() : IHasID.NO_ID;
		mClass = (smugObject != null) ? smugObject.getClass() : null;
		mSmugObject = smugObject;
	}
	
	/**
	 * Returns the ID of the Smugmug object.
	 * @return The object's ID.
	 */
	public int getID() {
		return mID;
	}
	
	public String getMessage() {
		StringBuffer strBuf = new StringBuffer();
		if (super.getMessage()!=null) {
			strBuf.append(super.getMessage());
			strBuf.append("\n");
		}
		strBuf.append("Smugmug Object ");
		strBuf.append(mClass!=null? mClass.getSimpleName() : "");
		strBuf.append(" with ID=");
		strBuf.append(mID);
		strBuf.append(" does not exist");
		strBuf.append(".");
			
		return strBuf.toString();
	}
}
