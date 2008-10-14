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
package com.streetsofboston.smugmug.v1_2_1.system;

/**
 * Every Smugmug Object has an ID. They need to implement this interface.
 * @author Anton Spaans
 */
public interface IHasID {
	/**
	 * Invalid or 'no' ID. This is the temporary ID for new objects.
	 */
	public static final int NO_ID = -1;
	
	/**
	 * Returns the ID for this Smugmug Object.
	 * @return The object's ID.
	 */
	public int getID();
	
	/**
	 * @return True if the object's ID is a valid id, i.e. getID() != NO_ID
	 */
	public boolean hasID();	
	
	/**
	 * @return The object's GUID.
	 */
	public GUID guid();
}
