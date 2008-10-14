package com.kallasoft.smugmug.api.json.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(JSONUtils.class);

	public static Boolean getBooleanSafely(JSONObject jsonObject,
			String propertyName) {
		Object result = getPropertySafely(jsonObject, propertyName);
		return (result == null ? null : (Boolean) result);
	}

	public static Integer getIntegerSafely(JSONObject jsonObject,
			String propertyName) {
		Object result = getPropertySafely(jsonObject, propertyName);
		return (result == null ? null : (Integer) result);
	}

	public static Float getFloatSafely(JSONObject jsonObject,
			String propertyName) {
		Double result = getDoubleSafely(jsonObject, propertyName);
		return (result == null ? null : Float.valueOf((float) result
				.doubleValue()));
	}

	public static Double getDoubleSafely(JSONObject jsonObject,
			String propertyName) {
		Double result = null;

		try {
			if (jsonObject != null && propertyName != null
					&& !jsonObject.isNull(propertyName))
				result = Double.valueOf(jsonObject.getDouble(propertyName));
		} catch (JSONException e) {
			logger
					.error(
							"An error occured while attempting to retrieve property {} from JSONObject {}",
							propertyName, jsonObject);
			e.printStackTrace();
		}

		return result;
	}

	public static String getStringSafely(JSONObject jsonObject,
			String propertyName) {
		Object result = getPropertySafely(jsonObject, propertyName);
		return (result == null ? null : (String) result);
	}

	public static Object getPropertySafely(JSONObject jsonObject,
			String propertyName) {
		Object result = null;

		try {
			if (jsonObject != null && propertyName != null
					&& !jsonObject.isNull(propertyName))
				result = jsonObject.get(propertyName);
		} catch (JSONException e) {
			logger
					.error(
							"An error occured while attempting to retrieve property {} from JSONObject {}",
							propertyName, jsonObject);
			e.printStackTrace();
		}

		return result;
	}
}
