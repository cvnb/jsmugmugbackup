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
package com.kallasoft.smugmug.api.json.v1_2_1;

import com.kallasoft.smugmug.api.json.v1_2_1.images.Upload;
import com.kallasoft.smugmug.api.json.v1_2_1.images.UploadFromURL;
import com.kallasoft.smugmug.api.json.v1_2_1.images.UploadHTTPPut;

/*
 * TODO: Audit all the 1.2.1 methods and make sure their call sequence of
 * convenience is logical, for example images.GetEXIF, make sure it calls to the
 * more specific method which eventually calls to String/String[]
 */

/**
 * Class used to define constants that are specific to this version of the JSON
 * API.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 */
public class APIVersionConstants {
	/**
	 * Used to define a String representing the 1.2.1 version of the SmugMug
	 * API.
	 */
	public static final String VERSION = "1.2.1";

	/**
	 * Used to define the SmugMug API 1.2.1 secure (HTTPS) server URL that can
	 * be used to execute any method. The request and response to and from
	 * SmugMug will take place over a secure channel instead of in plain-text.
	 * <p>
	 * It is <strong>highly recommended</strong> that all login methods only
	 * use this URL when communicating with the SmugMug server.
	 */
	public static final String SECURE_SERVER_URL = "https://api.smugmug.com/services/api/json/1.2.1/";

	/**
	 * Used to define the SmugMug API 1.2.1 unsecured (HTTP) server URL that can
	 * be used to execute any method. The request and response to and from
	 * SmugMug will be sent in plain-text.
	 * <p>
	 * It is <strong>highly recommended</strong> that you <strong>do not</strong>
	 * use this URL with any of the login methods, and instead use the
	 * {@link #SECURE_SERVER_URL} if possible.
	 */
	public static final String UNSECURE_SERVER_URL = "http://api.smugmug.com/services/api/json/1.2.1/";

	/**
	 * Used to define the SmugMug API 1.2.1 Binary upload server URL (HTTP).
	 * This URL has to be used with Binary-Only upload methods like
	 * {@link UploadHTTPPut}.
	 * <p>
	 * Attempting to upload to any other URL will result in a no-op essentially
	 * as the picture will not be stored anywhere.
	 */
	public static final String BINARY_UPLOAD_SERVER_URL = "http://upload.smugmug.com/";

	/**
	 * Used to define the SmugMug API 1.2.1 Textual upload server URL (HTTP).
	 * This URL has to be used with Text-Only upload methods like {@link Upload}
	 * and {@link UploadFromURL}.
	 * <p>
	 * Attempting to upload to any other URL will result in a no-op essentially
	 * as the picture will not be stored anywhere.
	 */
	public static final String TEXT_UPLOAD_SERVER_URL = "http://upload.smugmug.com/services/api/json/1.2.1/";
}
