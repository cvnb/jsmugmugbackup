package com.kallasoft.smugmug.api.json.entity;

import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;

/**
 * Class used to define an image and all the transfer statistics that can be
 * reported for it.
 * 
 * @author Riyad Kalla
 */
public class ImageTransferStats {
	private Integer id;

	private Integer bytes;

	private Integer tiny;

	private Integer thumb;

	private Integer small;

	private Integer medium;

	private Integer large;

	private Integer xLarge;

	private Integer x2Large;

	private Integer x3Large;

	private Float original;

	private Float video320;

	private Float video640;

	private Float video960;

	private Float video1280;

	/**
	 * Construct a new image transfer stat object by safely parsing all known
	 * image values out of the given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and ignored and parsing continues.
	 * 
	 * @param imageObject
	 *            The {@link JSONObject} that will be queried for all the image
	 *            transfer stats.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>imageObject</code> is null.
	 */
	public ImageTransferStats(JSONObject imageObject)
			throws IllegalArgumentException {
		if (imageObject == null)
			throw new IllegalArgumentException("imageObject cannot be null");

		id = JSONUtils.getIntegerSafely(imageObject, "id");
		bytes = JSONUtils.getIntegerSafely(imageObject, "Bytes");
		tiny = JSONUtils.getIntegerSafely(imageObject, "Tiny");
		thumb = JSONUtils.getIntegerSafely(imageObject, "Thumb");
		small = JSONUtils.getIntegerSafely(imageObject, "Small");
		medium = JSONUtils.getIntegerSafely(imageObject, "Medium");
		large = JSONUtils.getIntegerSafely(imageObject, "Large");
		xLarge = JSONUtils.getIntegerSafely(imageObject, "XLarge");
		x2Large = JSONUtils.getIntegerSafely(imageObject, "X2Large");
		x3Large = JSONUtils.getIntegerSafely(imageObject, "X3Large");
		original = JSONUtils.getFloatSafely(imageObject, "Original");
		video320 = JSONUtils.getFloatSafely(imageObject, "Video320");
		video640 = JSONUtils.getFloatSafely(imageObject, "Video640");
		video960 = JSONUtils.getFloatSafely(imageObject, "Video960");
		video1280 = JSONUtils.getFloatSafely(imageObject, "Video1280");
	}

	/**
	 * Construct an object with the given image transfer stats.
	 * 
	 * @param id
	 *            The ID of the image.
	 * @param bytes
	 *            The number of bytes transferred for this image.
	 * @param tiny
	 *            The number of times tiny-sized images have been viewed for
	 *            this image.
	 * @param thumb
	 *            The number of times thumb-sized images have been viewed for
	 *            this image.
	 * @param small
	 *            The number of times small-sized images have been viewed for
	 *            this image.
	 * @param medium
	 *            The number of times medium-sized images have been viewed for
	 *            this image.
	 * @param large
	 *            The number of times large-sized images have been viewed for
	 *            this image.
	 * @param xLarge
	 *            The number of times xlarge-sized images have been viewed for
	 *            this image.
	 * @param x2Large
	 *            The number of times xxlarge-sized images have been viewed for
	 *            this image.
	 * @param x3Large
	 *            The number of times xxxlarge-sized images have been viewed for
	 *            this image.
	 * @param original
	 *            The number of times original-sized images have been viewed for
	 *            this image.
	 * @param video320
	 *            The number of times 320-pixel sized movies have been viewed
	 *            for this album.
	 * @param video640
	 *            The number of times 640-pixel sized movies have been viewed
	 *            for this album.
	 * @param video960
	 *            The number of times 960-pixel sized movies have been viewed
	 *            for this album.
	 * @param video1280
	 *            The number of times 1280-pixel sized movies have been viewed
	 *            for this album.
	 */
	public ImageTransferStats(Integer id, Integer bytes, Integer tiny,
			Integer thumb, Integer small, Integer medium, Integer large,
			Integer xLarge, Integer x2Large, Integer x3Large, Float original,
			Float video320, Float video640, Float video960, Float video1280) {
		this.id = id;
		this.bytes = bytes;
		this.tiny = tiny;
		this.thumb = thumb;
		this.small = small;
		this.medium = medium;
		this.large = large;
		this.original = original;
		this.video320 = video320;
		this.video640 = video640;
		this.video960 = video960;
		this.video1280 = video1280;
	}

	@Override
	public String toString() {
		return ImageTransferStats.class.getName() + "[id=" + getID()
				+ ", bytes=" + getBytes() + ", tiny=" + getTiny() + ", thumb="
				+ getThumb() + ", small=" + getSmall() + ", medium="
				+ getMedium() + ", large=" + getLarge() + ", xLarge="
				+ getXLarge() + ", x2Large=" + getX2Large() + ", x3Large="
				+ getX3Large() + ", original=" + getOriginal() + ", video320="
				+ getVideo320() + ", video640=" + getVideo640() + ", video960="
				+ getVideo960() + ", video1280=" + getVideo1280() + "]";
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
	 * Used to get the number of bytes transferred for this image.
	 * 
	 * @return the number of bytes transferred for this image.
	 */
	public Integer getBytes() {
		return bytes;
	}

	/**
	 * Used to get the number of times tiny-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times tiny-sized images have been viewed for this
	 *         image.
	 */
	public Integer getTiny() {
		return tiny;
	}

	/**
	 * Used to get the number of times thumb-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times thumb-sized images have been viewed for this
	 *         image.
	 */
	public Integer getThumb() {
		return thumb;
	}

	/**
	 * Used to get the number of times small-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times small-sized images have been viewed for this
	 *         image.
	 */
	public Integer getSmall() {
		return small;
	}

	/**
	 * Used to get the number of times medium-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times medium-sized images have been viewed for this
	 *         image.
	 */
	public Integer getMedium() {
		return medium;
	}

	/**
	 * Used to get the number of times large-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times large-sized images have been viewed for this
	 *         image.
	 */
	public Integer getLarge() {
		return large;
	}

	/**
	 * Used to get the number of times xlarge-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times xlarge-sized images have been viewed for this
	 *         image.
	 */
	public Integer getXLarge() {
		return xLarge;
	}

	/**
	 * Used to get the number of times xxlarge-sized images have been viewed for
	 * this image.
	 * 
	 * @return the number of times xxlarge-sized images have been viewed for
	 *         this image.
	 */
	public Integer getX2Large() {
		return x2Large;
	}

	/**
	 * Used to get the number of times xxxlarge-sized images have been viewed
	 * for this image.
	 * 
	 * @return the number of times xxxlarge-sized images have been viewed for
	 *         this image.
	 */
	public Integer getX3Large() {
		return x3Large;
	}

	/**
	 * Used to get the number of times original-sized images have been viewed
	 * for this image.
	 * 
	 * @return the number of times original-sized images have been viewed for
	 *         this image.
	 */
	public Float getOriginal() {
		return original;
	}

	/**
	 * Used to get the number of times 320-pixel sized movies have been viewed
	 * for this album.
	 * 
	 * @return the number of times 320-pixel sized movies have been viewed for
	 *         this album.
	 */
	public Float getVideo320() {
		return video320;
	}

	/**
	 * Used to get the number of times 640-pixel sized movies have been viewed
	 * for this album.
	 * 
	 * @return the number of times 640-pixel sized movies have been viewed for
	 *         this album.
	 */
	public Float getVideo640() {
		return video640;
	}

	/**
	 * Used to get the number of times 960-pixel sized movies have been viewed
	 * for this album.
	 * 
	 * @return the number of times 960-pixel sized movies have been viewed for
	 *         this album.
	 */
	public Float getVideo960() {
		return video960;
	}

	/**
	 * Used to get the number of times 1280-pixel sized movies have been viewed
	 * for this album.
	 * 
	 * @return the number of times 1280-pixel sized movies have been viewed for
	 *         this album.
	 */
	public Float getVideo1280() {
		return video1280;
	}
}
