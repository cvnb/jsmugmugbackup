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

import java.io.Serializable;

/**
 * Helper interface for translating enumerated variables into 
 * instances of type T. These instances are values as they are known by the
 * Smugmug repository.
 * 
 * @param <T> Any type to which an 'enum' must be able to be translated.
 * @author Anton Spaans
 */
public interface IEnumValue<T> extends Serializable {
	
	/**
	 * Translates this enum instance into a value of type T as known by the Smugmug repository.
	 * @return The value.
	 */
	public T value();
}