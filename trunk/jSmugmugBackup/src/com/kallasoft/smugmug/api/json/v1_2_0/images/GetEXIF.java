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
 * This method will return camera and photograph details about the image
 * specified by ImageID.
 * <p>
 * The album must be owned by the session holder, or else be public (if
 * password-protected, a password must be provided) to return results, otherwise
 * an "invalid user" faultCode will result.
 * <p>
 * Additionally, the album owner must have specified that EXIF data is allowed.
 * <p>
 * Note that many photos have no EXIF data, so an empty or partially returned
 * result is very normal.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.images.getEXIF">smugmug.images.getEXIF
 *      API Doc</a>
 */
public class GetEXIF extends AbstractMethod {
	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.images.getEXIF";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "ImageID", "ImageKey", "Password",
	 * "SitePassword"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"ImageID", "ImageKey", "Password", "SitePassword" };

	private static final Logger logger = LoggerFactory.getLogger(GetEXIF.class);

	/**
	 * Construct a new method instance that can be executed.
	 */
	public GetEXIF() {
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
	public GetEXIF(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.images.getEXIF method, returning all the EXIF
	 * data from the image.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response that includes all the EXIF data from the image.
	 */
	public GetEXIFResponse execute(String url, String[] argumentValues) {
		return new GetEXIFResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.images.getEXIF method.
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
	 *            The ID of the image whose EXIF data will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * 
	 * @return the response that includes all the EXIF data from the image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetEXIFResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey });
	}

	/**
	 * Convenience method used to execute the smugmug.images.getEXIF method.
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
	 *            The ID of the image whose EXIF data will be returned.
	 * @param imageKey
	 *            The security key for the image.
	 * @param password
	 *            The password to the containing album if necessary.
	 * @param sitePassword
	 *            The site password if necessary.
	 * 
	 * @return the response that includes all the EXIF data from the image.
	 * 
	 * @see #execute(String, String[])
	 */
	public GetEXIFResponse execute(String url, String apiKey, String sessionID,
			Integer imageID, String imageKey, String password,
			String sitePassword) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(imageID), imageKey, password, sitePassword });
	}

	/**
	 * Class used to represent the response for the smugmug.images.getEXIF
	 * method call.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class GetEXIFResponse extends AbstractResponse {
		private Integer id;

		private String dateTime;

		private String dateTimeOriginal;

		private String dateTimeDigitized;

		private String make;

		private String model;

		private String exposureTime;

		private String aperture;

		private Integer iso;

		private String focalLength;

		private Integer focalLengthIn35mmFilm;

		private String ccdWidth;

		private String compressedBitsPerPixel;

		private Integer flash;

		private Integer metering;

		private Integer exposureProgram;

		private String exposureBiasValue;

		private Integer exposureMode;

		private Integer lightSource;

		private Integer whiteBalance;

		private String digitalZoomRatio;

		private Integer contrast;

		private Integer saturation;

		private Integer sharpness;

		private String subjectDistance;

		private Integer subjectDistanceRange;

		private Integer sensingMethod;

		private String colorSpace;

		private String brightness;

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
		public GetEXIFResponse(String responseText) throws RuntimeJSONException {
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
				dateTime = JSONUtils.getStringSafely(imageObject, "DateTime");
				dateTimeOriginal = JSONUtils.getStringSafely(imageObject,
						"DateTimeOriginal");
				dateTimeDigitized = JSONUtils.getStringSafely(imageObject,
						"DateTimeDigitized");
				make = JSONUtils.getStringSafely(imageObject, "Make");
				model = JSONUtils.getStringSafely(imageObject, "Model");
				exposureTime = JSONUtils.getStringSafely(imageObject,
						"ExposureTime");
				aperture = JSONUtils.getStringSafely(imageObject, "Aperture");
				iso = JSONUtils.getIntegerSafely(imageObject, "ISO");
				focalLength = JSONUtils.getStringSafely(imageObject,
						"FocalLength");
				focalLengthIn35mmFilm = JSONUtils.getIntegerSafely(imageObject,
						"FocalLengthIn35mmFilm");
				ccdWidth = JSONUtils.getStringSafely(imageObject, "CCDWidth");
				compressedBitsPerPixel = JSONUtils.getStringSafely(imageObject,
						"CompressedBitsPerPixel");
				flash = JSONUtils.getIntegerSafely(imageObject, "Flash");
				metering = JSONUtils.getIntegerSafely(imageObject, "Metering");
				exposureProgram = JSONUtils.getIntegerSafely(imageObject,
						"ExposureProgram");
				exposureBiasValue = JSONUtils.getStringSafely(imageObject,
						"ExposureBiasValue");
				exposureMode = JSONUtils.getIntegerSafely(imageObject,
						"ExposureMode");
				lightSource = JSONUtils.getIntegerSafely(imageObject,
						"LightSource");
				whiteBalance = JSONUtils.getIntegerSafely(imageObject,
						"WhiteBalance");
				digitalZoomRatio = JSONUtils.getStringSafely(imageObject,
						"DigitalZoomRatio");
				contrast = JSONUtils.getIntegerSafely(imageObject, "Contrast");
				saturation = JSONUtils.getIntegerSafely(imageObject,
						"Saturation");
				sharpness = JSONUtils
						.getIntegerSafely(imageObject, "Sharpness");
				subjectDistance = JSONUtils.getStringSafely(imageObject,
						"SubjectDistance");
				subjectDistanceRange = JSONUtils.getIntegerSafely(imageObject,
						"SubjectDistanceRange");
				sensingMethod = JSONUtils.getIntegerSafely(imageObject,
						"SensingMethod");
				colorSpace = JSONUtils.getStringSafely(imageObject,
						"ColorSpace");
				brightness = JSONUtils.getStringSafely(imageObject,
						"Brightness");
			} catch (JSONException e) {
				RuntimeJSONException rje = new RuntimeJSONException(e);
				logger.error("An error occured parsing the JSON response", rje);
				throw rje;
			}
		}

		@Override
		public String toString() {
			return GetEXIFResponse.class.getName() + "[isError=" + isError()
					+ ", id=" + getID() + ", dateTime=" + getDateTime()
					+ ", dateTimeOriginal=" + getDateTimeOriginal()
					+ ", dateTimeDigitized=" + getDateTimeDigitized()
					+ ", make=" + getMake() + ", model=" + getModel()
					+ ", exposureTime=" + getExposureTime() + ", aperture="
					+ getAperture() + ", iso=" + getISO() + ", focalLength="
					+ getFocalLength() + ", focalLengthIn35mmFilm="
					+ getFocalLengthIn35mmFilm() + ", ccdWidth="
					+ getCCDWidth() + ", compressedBitsPerPixel="
					+ getCompressedBitsPerPixel() + ", flash=" + getFlash()
					+ ", metering=" + getMetering() + ", exposureProgram="
					+ getExposureProgram() + ", exposureBiasValue="
					+ getExposureBiasValue() + ", exposureMode="
					+ getExposureMode() + ", lightSource=" + getLightSource()
					+ ", whiteBalance=" + getWhiteBalance()
					+ ", digitalZoomRatio=" + getDigitalZoomRatio()
					+ ", contrast=" + getContrast() + ", saturation="
					+ getSaturation() + ", sharpness=" + getSharpness()
					+ ", subjectDistance=" + getSubjectDistance()
					+ ", subjectDistanceRange=" + getSubjectDistanceRange()
					+ ", sensingMethod=" + getSensingMethod() + ", colorSpace="
					+ getColorSpace() + ", brightness=" + getBrightness() + "]";
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
		 * Used to get the timestamp from the image's EXIF data.
		 * 
		 * @return the timestamp from the image's EXIF data.
		 */
		public String getDateTime() {
			return dateTime;
		}

		/**
		 * Used to get the original timestamp from the image's EXIF data.
		 * 
		 * @return the original timestamp from the image's EXIF data.
		 */
		public String getDateTimeOriginal() {
			return dateTimeOriginal;
		}

		/**
		 * Used to get the original digitization timestamp from the image's EXIF
		 * data.
		 * 
		 * @return the original digitization timestamp from the image's EXIF
		 *         data.
		 */
		public String getDateTimeDigitized() {
			return dateTimeDigitized;
		}

		/**
		 * Used to get the make of the camera that took the image.
		 * 
		 * @return the make of the camera that took the image.
		 */
		public String getMake() {
			return make;
		}

		/**
		 * Used to get the model of the camera that took the image.
		 * 
		 * @return the model of the camera that took the image.
		 */
		public String getModel() {
			return model;
		}

		/**
		 * Used to get the exposure time that was set when the image was taken.
		 * 
		 * @return the exposure time that was set when the image was taken.
		 */
		public String getExposureTime() {
			return exposureTime;
		}

		/**
		 * Used to get the aperture that was set when the image was taken.
		 * 
		 * @return the aperture that was set when the image was taken.
		 */
		public String getAperture() {
			return aperture;
		}

		/**
		 * Used to get the ISO that was set when the image was taken.
		 * 
		 * @return the ISO that was set when the image was taken.
		 */
		public Integer getISO() {
			return iso;
		}

		/**
		 * Used to get the focal length that was set when the image was taken.
		 * 
		 * @return the focal length that was set when the image was taken.
		 */
		public String getFocalLength() {
			return focalLength;
		}

		/**
		 * Used to get the focal length converted to a 35mm standard.
		 * 
		 * @return the focal length converted to a 35mm standard.
		 */
		public Integer getFocalLengthIn35mmFilm() {
			return focalLengthIn35mmFilm;
		}

		/**
		 * Used to get the CCD width of the camera that took the picture.
		 * 
		 * @return the CCD width of the camera that took the picture.
		 */
		public String getCCDWidth() {
			return ccdWidth;
		}

		/**
		 * Used to get the compressed bits per pixel for the image.
		 * 
		 * @return the compressed bits per pixel for the image.
		 */
		public String getCompressedBitsPerPixel() {
			return compressedBitsPerPixel;
		}

		/**
		 * Used to get the flash setting when the image was taken.
		 * 
		 * @return the flash setting when the image was taken.
		 */
		public Integer getFlash() {
			return flash;
		}

		/**
		 * Used to get the metering used when the image was taken.
		 * 
		 * @return the metering used when the image was taken.
		 */
		public Integer getMetering() {
			return metering;
		}

		/**
		 * Used to get the exposure program used by the camera when the image
		 * was taken.
		 * 
		 * @return the exposure program used by the camera when the image was
		 *         taken.
		 */
		public Integer getExposureProgram() {
			return exposureProgram;
		}

		/**
		 * Used to get the exposure bias value.
		 * 
		 * @return the exposure bias value.
		 */
		public String getExposureBiasValue() {
			return exposureBiasValue;
		}

		/**
		 * Used to get the exposure mode.
		 * 
		 * @return the exposure mode.
		 */
		public Integer getExposureMode() {
			return exposureMode;
		}

		/**
		 * Used to get the light source when the picture was taken.
		 * 
		 * @return the light source when the picture was taken.
		 */
		public Integer getLightSource() {
			return lightSource;
		}

		/**
		 * Used to get the white balance.
		 * 
		 * @return the white balance.
		 */
		public Integer getWhiteBalance() {
			return whiteBalance;
		}

		/**
		 * Used to get the digital zoom ratio for the image.
		 * 
		 * @return the digital zoom ratio for the image.
		 */
		public String getDigitalZoomRatio() {
			return digitalZoomRatio;
		}

		/**
		 * Used to get the contrast of the image.
		 * 
		 * @return the contrast of the image.
		 */
		public Integer getContrast() {
			return contrast;
		}

		/**
		 * Used to get the saturation of the image.
		 * 
		 * @return the saturation of the image.
		 */
		public Integer getSaturation() {
			return saturation;
		}

		/**
		 * Used to get the sharpness.
		 * 
		 * @return the sharpness.
		 */
		public Integer getSharpness() {
			return sharpness;
		}

		/**
		 * Used to get the focused subject distance as reported by the camera.
		 * 
		 * @return the focused subject distance as reported by the camera.
		 */
		public String getSubjectDistance() {
			return subjectDistance;
		}

		/**
		 * Used to get the range of distance for the focused subject.
		 * 
		 * @return the range of distance for the focused subject.
		 */
		public Integer getSubjectDistanceRange() {
			return subjectDistanceRange;
		}

		/**
		 * Used to get the sensing method.
		 * 
		 * @return the sensing method.
		 */
		public Integer getSensingMethod() {
			return sensingMethod;
		}

		/**
		 * Used to get the colorspace for the image.
		 * 
		 * @return the colorspace for the image.
		 */
		public String getColorSpace() {
			return colorSpace;
		}

		/**
		 * Used to get the brightness for the image.
		 * 
		 * @return the brightness for the image.
		 */
		public String getBrightness() {
			return brightness;
		}
	}
}
