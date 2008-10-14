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
package com.kallasoft.smugmug.api.json.v1_2_0.images;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.json.util.JSONUtils;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will return all the URLs for the various sizes of the image
 * specified by ImageID.
 * <p>
 * The album must be owned by the session holder, or else be public (if
 * password-protected, a password must be provided) to return results, otherwise
 * an "invalid user" faultCode will result.
 * <p>
 * Additionally, obvious restrictions on Originals and (extra)Larges apply if so
 * set by the owner. They will return as empty strings for those URLs if they're
 * unavailable.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.getURLs">smugmug.images.getURLs
 *      API Doc</a>
 */
public class GetURLs extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.getURLs";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "ImageKey", "TemplateID",
	 * "Password", "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "ImageKey", "TemplateID", "Password", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(GetURLs.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetURLs() {
		this(METHOD_NAME, ARGUMENTS);
	}

	/**
	 * Construct a new method instance that can be executed with the given
	 * arguments.
	 * 
	 * @param methodName
	 *            The name of the SmugMug JSON API method that this
	 *            <em>Method</em> represents.
	 * @param arguments
	 *            The names of the arguments that this method accepts.
	 */
	public GetURLs(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.images.getURLs method, returning a list of
	 * the URLs for the different sizes of the given image.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes a list of the URLs for the different
	 *         sizes of the given image.
	 */
	public GetURLsResponse execute(String url, String[] argumentValues) {
		return new GetURLsResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.getURLs method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param imageID
	 *            The ID of the image whose URLs will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * 
	 * @return the response that includes a list of the URLs for the different
	 *         sizes of the given image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetURLsResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey });
	}

	/**
	 * Convenience method used to execute the smugmug.images.getURLs method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param imageID
	 *            The ID of the image whose URLs will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * @param templateID
	 *            The ID of the template used to build the URLs.
	 * 
	 * @return the response that includes a list of the URLs for the different
	 *         sizes of the given image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetURLsResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey, Integer templateID) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey,
				APIUtils.toString(templateID) });
	}

	/**
	 * Convenience method used to execute the smugmug.images.getURLs method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param imageID
	 *            The ID of the image whose URLs will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * @param templateID
	 *            The ID of the template used to build the URLs.
	 * @param password
	 *            The password to the containing album if necessary.
	 * @param sitePassword
	 *            The site password if necessary.
	 * 
	 * @return the response that includes a list of the URLs for the different
	 *         sizes of the given image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetURLsResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey, Integer templateID,
			String password, String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey,
				APIUtils.toString(templateID), password, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.images.getURLs
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetURLsResponse extends AbstractResponse {
		private Integer id;

		private String albumURL;

		private String tinyURL;

		private String thumbURL;

		private String smallURL;

		private String mediumURL;

		private String largeURL;

		private String xLargeURL;

		private String x2LargeURL;

		private String x3LargeURL;

		private String originalURL;

		private String video320URL;

		private String video640URL;

		private String video960URL;

		private String video1280URL;

		/**
		 * Construct a response by parsing the necessary values out of the JSON
		 * response text.
		 * 
		 * @param responseText
		 *            The JSON-formatted response text that came back from the
		 *            SmugMug API call.
		 * 
		 * @throws RuntimeJSONException
		 *             if an error occurs while parsing the JSON response text.
		 */
		public GetURLsResponse(String responseText) throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);

			/* If there was an error, or the message is empty, do nothing */
			if (isError() || APIUtils.isEmpty(responseText))
				return;

			JSONObject responseObject = null;

			try {
				responseObject = new JSONObject(responseText);
				JSONObject imageObject = responseObject.getJSONObject("Image");

				id = JSONUtils.getIntegerSafely(imageObject, "id");
				albumURL = JSONUtils.getStringSafely(imageObject, "AlbumURL");
				tinyURL = JSONUtils.getStringSafely(imageObject, "TinyURL");
				thumbURL = JSONUtils.getStringSafely(imageObject, "ThumbURL");
				smallURL = JSONUtils.getStringSafely(imageObject, "SmallURL");
				mediumURL = JSONUtils.getStringSafely(imageObject, "MediumURL");
				largeURL = JSONUtils.getStringSafely(imageObject, "LargeURL");
				xLargeURL = JSONUtils.getStringSafely(imageObject, "XLargeURL");
				x2LargeURL = JSONUtils.getStringSafely(imageObject,
						"X2LargeURL");
				x3LargeURL = JSONUtils.getStringSafely(imageObject,
						"X3LargeURL");
				originalURL = JSONUtils.getStringSafely(imageObject,
						"OriginalURL");
				video320URL = JSONUtils.getStringSafely(imageObject,
						"Video320URL");
				video640URL = JSONUtils.getStringSafely(imageObject,
						"Video640URL");
				video960URL = JSONUtils.getStringSafely(imageObject,
						"Video960URL");
				video1280URL = JSONUtils.getStringSafely(imageObject,
						"Video1280URL");
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetURLsResponse.class.getName() + "[isError=" + isError()
					+ ", id=" + getID() + ", albumURL=" + getAlbumURL()
					+ ", tinyURL=" + getTinyURL() + ", thumbURL="
					+ getThumbURL() + ", smallURL=" + getSmallURL()
					+ ", mediumURL=" + getMediumURL() + ", largeURL="
					+ getLargeURL() + ", xLargeURL=" + getXLargeURL()
					+ ", x2LargeURL=" + getX2LargeURL() + ", x3LargeURL="
					+ getX3LargeURL() + ", originalURL=" + getOriginalURL()
					+ ", video320URL=" + getVideo320URL() + ", video640URL="
					+ getVideo640URL() + ", video960URL=" + getVideo960URL()
					+ ", video1280URL=" + getVideo1280URL() + "]";
		}

		/**
		 * Used to get the image ID.
		 * 
		 * @return the image ID.
		 */
		public Integer getID() {
			return id;
		}

		/**
		 * Used to get a URL for the album this image is contained in.
		 * 
		 * @return a URL for the album this image is contained in.
		 */
		public String getAlbumURL() {
			return albumURL;
		}

		/**
		 * Used to get a URL for a tiny size version of the image.
		 * 
		 * @return a URL for a tiny size version of the image.
		 */
		public String getTinyURL() {
			return tinyURL;
		}

		/**
		 * Used to get a URL for a thumbnail size version of the image.
		 * 
		 * @return a URL for a thumbnail size version of the image.
		 */
		public String getThumbURL() {
			return thumbURL;
		}

		/**
		 * Used to get a URL for a small size version of the image.
		 * 
		 * @return a URL for a small size version of the image.
		 */
		public String getSmallURL() {
			return smallURL;
		}

		/**
		 * Used to get a URL for a medium size version of the image.
		 * 
		 * @return a URL for a medium size version of the image.
		 */
		public String getMediumURL() {
			return mediumURL;
		}

		/**
		 * Used to get a URL for a large size version of the image.
		 * 
		 * @return a URL for a large size version of the image.
		 */
		public String getLargeURL() {
			return largeURL;
		}

		/**
		 * Used to get a URL for a x-large size version of the image.
		 * 
		 * @return a URL for a x-large size version of the image.
		 */
		public String getXLargeURL() {
			return xLargeURL;
		}

		/**
		 * Used to get a URL for a xx-large size version of the image.
		 * 
		 * @return a URL for a xx-large size version of the image.
		 */
		public String getX2LargeURL() {
			return x2LargeURL;
		}

		/**
		 * Used to get a URL for a xxx-large size version of the image.
		 * 
		 * @return a URL for a xxx-large size version of the image.
		 */
		public String getX3LargeURL() {
			return x3LargeURL;
		}

		/**
		 * Used to get a URL for the original image.
		 * 
		 * @return a URL for the original image.
		 */
		public String getOriginalURL() {
			return originalURL;
		}

		/**
		 * Used to get a URL for the the 320-pixel wide version of this video.
		 * 
		 * @return a URL for the the 320-pixel wide version of this video.
		 */
		public String getVideo320URL() {
			return video320URL;
		}

		/**
		 * Used to get a URL for the the 640-pixel wide version of this video.
		 * 
		 * @return a URL for the the 640-pixel wide version of this video.
		 */
		public String getVideo640URL() {
			return video640URL;
		}

		/**
		 * Used to get a URL for the the 960-pixel wide version of this video.
		 * 
		 * @return a URL for the the 960-pixel wide version of this video.
		 */
		public String getVideo960URL() {
			return video960URL;
		}

		/**
		 * Used to get a URL for the the 1280-pixel wide version of this video.
		 * 
		 * @return a URL for the the 1280-pixel wide version of this video.
		 */
		public String getVideo1280URL() {
			return video1280URL;
		}
	}
}
