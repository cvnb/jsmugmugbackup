package com.kallasoft.smugmug.api.json.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;

/**
 * Class used to define an album and all the transfer statistics that can be
 * reported for it.
 * 
 * @author Riyad Kalla
 */
public class AlbumTransferStats {
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

	private List<ImageTransferStats> imageTransferStatsList = new ArrayList<ImageTransferStats>();

	/**
	 * Construct a new album transfer stat object by safely parsing all known
	 * album values (and any contained image transfer stat objects if available)
	 * out of the given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and ignored and parsing continues.
	 * 
	 * @param albumObject
	 *            The {@link JSONObject} that will be queried for all the album
	 *            transfer stats.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>albumObject</code> is null.
	 * 
	 * @throws JSONException
	 *             if any JSONObject-related exception occurs.
	 */
	public AlbumTransferStats(JSONObject albumObject)
			throws IllegalArgumentException, JSONException {
		if (albumObject == null)
			throw new IllegalArgumentException("albumObject cannot be null");

		/* Parse all the values */
		id = JSONUtils.getIntegerSafely(albumObject, "id");
		bytes = JSONUtils.getIntegerSafely(albumObject, "Bytes");
		tiny = JSONUtils.getIntegerSafely(albumObject, "Tiny");
		thumb = JSONUtils.getIntegerSafely(albumObject, "Thumb");
		small = JSONUtils.getIntegerSafely(albumObject, "Small");
		medium = JSONUtils.getIntegerSafely(albumObject, "Medium");
		large = JSONUtils.getIntegerSafely(albumObject, "Large");
		xLarge = JSONUtils.getIntegerSafely(albumObject, "XLarge");
		x2Large = JSONUtils.getIntegerSafely(albumObject, "X2Large");
		x3Large = JSONUtils.getIntegerSafely(albumObject, "X3Large");
		original = JSONUtils.getFloatSafely(albumObject, "Original");
		video320 = JSONUtils.getFloatSafely(albumObject, "Video320");
		video640 = JSONUtils.getFloatSafely(albumObject, "Video640");
		video960 = JSONUtils.getFloatSafely(albumObject, "Video960");
		video1280 = JSONUtils.getFloatSafely(albumObject, "Video1280");

		/* Now process the images in this album if there are any */
		if (!albumObject.isNull("Images")) {
			JSONArray imageArray = albumObject.getJSONArray("Images");

			for (int j = 0, jLength = imageArray.length(); j < jLength; j++)
				imageTransferStatsList.add(new ImageTransferStats(imageArray
						.getJSONObject(j)));
		}
	}

	/**
	 * Construct an object with the given album transfer stats.
	 * 
	 * @param id
	 *            The ID of the album.
	 * @param bytes
	 *            The number of bytes transferred for this album.
	 * @param tiny
	 *            The number of times tiny-sized images have been viewed for
	 *            this album.
	 * @param thumb
	 *            The number of times thumb-sized images have been viewed for
	 *            this album.
	 * @param small
	 *            The number of times small-sized images have been viewed for
	 *            this album.
	 * @param medium
	 *            The number of times medium-sized images have been viewed for
	 *            this album.
	 * @param large
	 *            The number of times large-sized images have been viewed for
	 *            this album.
	 * @param xLarge
	 *            The number of times xlarge-sized images have been viewed for
	 *            this album.
	 * @param x2Large
	 *            The number of times xxlarge-sized images have been viewed for
	 *            this album.
	 * @param x3Large
	 *            The number of times xxxlarge-sized images have been viewed for
	 *            this album.
	 * @param original
	 *            The number of times original-sized images have been viewed for
	 *            this album.
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
	 * @param imageTransferStatsList
	 *            A list of image transfer stat objects that represent the
	 *            images in this album, if available.
	 */
	public AlbumTransferStats(Integer id, Integer bytes, Integer tiny,
			Integer thumb, Integer small, Integer medium, Integer large,
			Integer xLarge, Integer x2Large, Integer x3Large, Float original,
			Float video320, Float video640, Float video960, Float video1280,
			List<ImageTransferStats> imageTransferStatsList) {
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
		this.imageTransferStatsList = imageTransferStatsList;
	}

	@Override
	public String toString() {
		return Album.class.getName() + "[id=" + getID() + ", bytes="
				+ getBytes() + ", tiny=" + getTiny() + ", thumb=" + getThumb()
				+ ", small=" + getSmall() + ", medium=" + getMedium()
				+ ", large=" + getLarge() + ", xLarge=" + getXLarge()
				+ ", x2Large=" + getX2Large() + ", x3Large=" + getX3Large()
				+ ", original=" + getOriginal() + ", video320=" + getVideo320()
				+ ", video640=" + getVideo640() + ", video960=" + getVideo960()
				+ ", video1280=" + getVideo1280()
				+ ", imageTransferStatsListSize="
				+ getImageTransferStatsList().size() + "]";
	}

	/**
	 * Used to get the album ID.
	 * 
	 * @return the album ID.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Used to get the number of bytes transferred for this album.
	 * 
	 * @return the number of bytes transferred for this album.
	 */
	public Integer getBytes() {
		return bytes;
	}

	/**
	 * Used to get the number of times tiny-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times tiny-sized images have been viewed for this
	 *         album.
	 */
	public Integer getTiny() {
		return tiny;
	}

	/**
	 * Used to get the number of times thumb-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times thumb-sized images have been viewed for this
	 *         album.
	 */
	public Integer getThumb() {
		return thumb;
	}

	/**
	 * Used to get the number of times small-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times small-sized images have been viewed for this
	 *         album.
	 */
	public Integer getSmall() {
		return small;
	}

	/**
	 * Used to get the number of times medium-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times medium-sized images have been viewed for this
	 *         album.
	 */
	public Integer getMedium() {
		return medium;
	}

	/**
	 * Used to get the number of times large-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times large-sized images have been viewed for this
	 *         album.
	 */
	public Integer getLarge() {
		return large;
	}

	/**
	 * Used to get the number of times xlarge-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times xlarge-sized images have been viewed for this
	 *         album.
	 */
	public Integer getXLarge() {
		return xLarge;
	}

	/**
	 * Used to get the number of times xxlarge-sized images have been viewed for
	 * this album.
	 * 
	 * @return the number of times xxlarge-sized images have been viewed for
	 *         this album.
	 */
	public Integer getX2Large() {
		return x2Large;
	}

	/**
	 * Used to get the number of times xxxlarge-sized images have been viewed
	 * for this album.
	 * 
	 * @return the number of times xxxlarge-sized images have been viewed for
	 *         this album.
	 */
	public Integer getX3Large() {
		return x3Large;
	}

	/**
	 * Used to get the number of times original-sized images have been viewed
	 * for this album.
	 * 
	 * @return the number of times original-sized images have been viewed for
	 *         this album.
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

	/**
	 * Used to get a list of {@link ImageTransferStats}s that belong to this
	 * album that contain individual transfer statistics for each image.
	 * 
	 * @return a list of {@link ImageTransferStats}s that belong to this album
	 *         that contain individual transfer statistics for each image.
	 */
	public List<ImageTransferStats> getImageTransferStatsList() {
		return imageTransferStatsList;
	}
}
