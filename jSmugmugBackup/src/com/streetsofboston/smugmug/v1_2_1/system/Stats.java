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
 * Defines the statistics for an object of type T given a year and a month.
 * @author Anton Spaans.
 * @param <T> A type subclassing ISmugObject
 */
public class Stats<T extends ISmugObject<T>> implements Serializable {
	static final long serialVersionUID = 2094176469245507053L;

	private final T   mObject;
	
	private final int mYear;
	private final int mMonth;
	
	private final int mBytes;
	private final int mTiny;
	private final int mThumb;
	private final int mSmall;
	private final int mMedium;
	private final int mLarge;
	private final int mXLarge;
	private final int mX2Large;
	private final int mX3Large;
	private final double mOriginal;
	private final Float mVideo320;
	private final Float mVideo640;
	private final Float mVideo960;
	private final Float mVideo1280;

	public Stats(
			T object,
			int year, int month,
			int bytes,
			int tiny,
			int thumb,
			int small,
			int medium,
			int large, int xlarge, int x2large, int x3large,
			double original,
			Float video320, Float video640, Float video960, Float video1280) {
		mObject = object;
		mYear = year;
		mMonth = month;
		mBytes = bytes;
		mTiny = tiny;
		mThumb = thumb;
		mSmall = small;
		mMedium = medium;
		mLarge = large;
		mXLarge = xlarge;
		mX2Large = x2large;
		mX3Large = x3large;
		mOriginal = original;
		mVideo320 = video320;
		mVideo640 = video640;
		mVideo960 = video960;
		mVideo1280 = video1280;
	}
	
	/**
	 * @return the Smugmug object for which this statistic is valid.
	 */
	public T getSmugObject() {
		return mObject;
	}
	/**
	 * @return The year of the month for which this stat is valid.
	 */
	public int getYear() {
		return mYear;
	}
	/**
	 * 
	 * @return The month for which this stat is valid.
	 */
	public int getMonth() {
		return mMonth;
	}
	/**
	 * @return the number of bytes transferred for the object
	 * (i.e. all images within the album or bytes for this image)
	 */
	public int getBytes() {
		return mBytes;
	}
	/**
	 * @return the number of hits for tiny images.
	 */
	public int getTiny() {
		return mTiny;
	}
	/**
	 * @return the number of hits for thumb-nail images.
	 */
	public int getThumb() {
		return mThumb;
	}
	/**
	 * @return the number of hits for small images.
	 */
	public int getSmall() {
		return mSmall;
	}
	/**
	 * @return the number of hits for medium-sized images.
	 */
	public int getMedium() {
		return mMedium;
	}
	/**
	 * @return the number of hits for large-sized images.
	 */
	public int getLarge() {
		return mLarge;
	}
	/**
	 * @return the number of hits for xlarge-sized images.
	 */
	public int getXLarge() {
		return mXLarge;
	}
	/**
	 * @return the number of hits for xxlarge-sized images.
	 */
	public int getX2Large() {
		return mX2Large;
	}
	/**
	 * @return the number of hits for xxxlarge-sized images.
	 */
	public int getX3Large() {
		return mX3Large;
	}
	/**
	 * @return the number of hits for the original sized images.
	 */
	public double getOriginal() {
		return mOriginal;
	}
	
	public Float getVideo320() {
		return mVideo320;
	}

	public Float getVideo640() {
		return mVideo640;
	}

	public Float getVideo960() {
		return mVideo960;
	}

	public Float getVideo1280() {
		return mVideo1280;
	}
}
