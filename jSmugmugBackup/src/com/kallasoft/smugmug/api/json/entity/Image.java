package com.kallasoft.smugmug.api.json.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;

/**
 * Class used to define an image and all the properties that go along with it.
 * 
 * @author Riyad Kalla
 */
public class Image {
	private Integer id;

	private String imageKey;

	private String fileName;

	private String caption;

	private String keywords;

	private Integer position;

	private String date;

	private String format;

	private Integer serial;

	private String watermark;

	private Double latitude;

	private Double longitude;

	private Double altitude;

	private Boolean hidden;

	private Integer size;

	private Integer width;

	private Integer height;

	private String md5sum;

	private String lastUpdated;

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

	private Album album;

	/**
	 * Construct a new image by safely parsing all known image values out of the
	 * given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and ignored and parsing continues.
	 * 
	 * @param imageObject
	 *            The {@link JSONObject} that will be queried for all the known
	 *            properties for this image.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>imageObject</code> is null.
	 * 
	 * @throws JSONException
	 *             if any JSONObject-related exception occurs.
	 */
	public Image(JSONObject imageObject) throws IllegalArgumentException,
			JSONException {
		if (imageObject == null)
			throw new IllegalArgumentException("imageObject cannot be null");

		id = JSONUtils.getIntegerSafely(imageObject, "id");
		imageKey = JSONUtils.getStringSafely(imageObject, "Key");
		fileName = JSONUtils.getStringSafely(imageObject, "FileName");
		caption = JSONUtils.getStringSafely(imageObject, "Caption");
		keywords = JSONUtils.getStringSafely(imageObject, "Keywords");
		position = JSONUtils.getIntegerSafely(imageObject, "Position");
		date = JSONUtils.getStringSafely(imageObject, "Date");
		format = JSONUtils.getStringSafely(imageObject, "Format");
		serial = JSONUtils.getIntegerSafely(imageObject, "Serial");
		watermark = JSONUtils.getStringSafely(imageObject, "Watermark");
		latitude = JSONUtils.getDoubleSafely(imageObject, "Latitude");
		longitude = JSONUtils.getDoubleSafely(imageObject, "Longitude");
		altitude = JSONUtils.getDoubleSafely(imageObject, "Altitude");
		hidden = JSONUtils.getBooleanSafely(imageObject, "Hidden");
		size = JSONUtils.getIntegerSafely(imageObject, "Size");
		width = JSONUtils.getIntegerSafely(imageObject, "Width");
		height = JSONUtils.getIntegerSafely(imageObject, "Height");
		md5sum = JSONUtils.getStringSafely(imageObject, "MD5Sum");
		lastUpdated = JSONUtils.getStringSafely(imageObject, "LastUpdated");
		albumURL = JSONUtils.getStringSafely(imageObject, "AlbumURL");
		tinyURL = JSONUtils.getStringSafely(imageObject, "TinyURL");
		thumbURL = JSONUtils.getStringSafely(imageObject, "ThumbURL");
		smallURL = JSONUtils.getStringSafely(imageObject, "SmallURL");
		mediumURL = JSONUtils.getStringSafely(imageObject, "MediumURL");
		largeURL = JSONUtils.getStringSafely(imageObject, "LargeURL");
		xLargeURL = JSONUtils.getStringSafely(imageObject, "XLargeURL");
		x2LargeURL = JSONUtils.getStringSafely(imageObject, "X2LargeURL");
		x3LargeURL = JSONUtils.getStringSafely(imageObject, "X3LargeURL");
		originalURL = JSONUtils.getStringSafely(imageObject, "OriginalURL");
		video320URL = JSONUtils.getStringSafely(imageObject, "Video320URL");
		video640URL = JSONUtils.getStringSafely(imageObject, "Video640URL");
		video960URL = JSONUtils.getStringSafely(imageObject, "Video960URL");
		video1280URL = JSONUtils.getStringSafely(imageObject, "Video1280URL");

		if (!imageObject.isNull("Album"))
			album = new Album(imageObject.getJSONObject("Album"));
	}

	/**
	 * Construct a new image with all the given property values.
	 * 
	 * @param id
	 *            The ID of the image.
	 * @param imageKey
	 *            The security key for the image.
	 * @param fileName
	 *            The original file name of the image.
	 * @param caption
	 *            The caption for the image.
	 * @param keywords
	 *            Keywords assigned to the image.
	 * @param position
	 *            The position of the image in the album.
	 * @param date
	 *            The timestamp from the image.
	 * @param format
	 *            The file format of the image.
	 * @param serial
	 *            The "version" of the image as edited by SmugMug. Each time an
	 *            image is edited it's "version" is incremented and is
	 *            represented by the serial value.
	 * @param watermark
	 *            The watermark to place on the image.
	 * @param latitude
	 *            The latitude associated with this image (for geo-tagging
	 *            purposes).
	 * @param longitude
	 *            The longitude associated with this image (for geo-tagging
	 *            purposes).
	 * @param altitude
	 *            The altitude associated with this image (for geo-tagging
	 *            purposes).
	 * @param hidden
	 *            A flag indicating if the image is hidden or not.
	 * @param size
	 *            The size, in bytes, of the image.
	 * @param width
	 *            The width of the image;
	 * @param height
	 *            The height of the image;
	 * @param md5sum
	 *            The MD5 Sum of the image.
	 * @param lastUpdated
	 *            The timestamp of the last updated date for the image.
	 * @param albumURL
	 *            A URL for the album this image is contained in.
	 * @param tinyURL
	 *            A URL for a tiny size version of the image.
	 * @param thumbURL
	 *            A URL for a thumbnail size version of the image.
	 * @param smallURL
	 *            A URL for a small size version of the image.
	 * @param mediumURL
	 *            A URL for a medium size version of the image.
	 * @param largeURL
	 *            A URL for a large size version of the image.
	 * @param xLargeURL
	 *            A URL for a x-large size version of the image.
	 * @param x2LargeURL
	 *            A URL for a xx-large size version of the image.
	 * @param x3LargeURL
	 *            A URL for a xxx-large size version of the image.
	 * @param originalURL
	 *            A URL for the original image.
	 * @param video320URL
	 *            A URL for the 320-pixel wide version of this video.
	 * @param video640URL
	 *            A URL for the 640-pixel wide version of this video.
	 * @param video960URL
	 *            A URL for the 960-pixel wide version of this video.
	 * @param video1280URL
	 *            A URL for the 1280-pixel wide version of this video.
	 * @param album
	 *            The {@link Album} that this image is contained in.
	 */
	public Image(Integer id, String imageKey, String fileName, String caption,
			String keywords, Integer position, String date, String format,
			Integer serial, String watermark, Double latitude,
			Double longitude, Double altitude, Boolean hidden, Integer size,
			Integer width, Integer height, String md5sum, String lastUpdated,
			String albumURL, String tinyURL, String thumbURL, String smallURL,
			String mediumURL, String largeURL, String xLargeURL,
			String x2LargeURL, String x3LargeURL, String originalURL,
			String video320URL, String video640URL, String video960URL,
			String video1280URL, Album album) {
		this.id = id;
		this.imageKey = imageKey;
		this.fileName = fileName;
		this.caption = caption;
		this.keywords = keywords;
		this.position = position;
		this.date = date;
		this.format = format;
		this.serial = serial;
		this.watermark = watermark;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.hidden = hidden;
		this.size = size;
		this.width = width;
		this.height = height;
		this.md5sum = md5sum;
		this.lastUpdated = lastUpdated;
		this.albumURL = albumURL;
		this.tinyURL = tinyURL;
		this.thumbURL = thumbURL;
		this.smallURL = smallURL;
		this.mediumURL = mediumURL;
		this.largeURL = largeURL;
		this.xLargeURL = xLargeURL;
		this.x2LargeURL = x2LargeURL;
		this.x3LargeURL = x3LargeURL;
		this.originalURL = originalURL;
		this.video320URL = video320URL;
		this.video640URL = video640URL;
		this.video960URL = video960URL;
		this.video1280URL = video1280URL;
		this.album = album;
	}

	@Override
	public String toString() {
		return Image.class.getName() + "[id=" + getID() + ", imageKey="
				+ getImageKey() + ", fileName=" + getFileName() + ", caption="
				+ getCaption() + ", keywords=" + getKeywords() + ", position="
				+ getPosition() + ", date=" + getDate() + ", format="
				+ getFormat() + ", serial=" + getSerial() + ", watermark="
				+ getWatermark() + ", latitude=" + getLatitude()
				+ ", longitude=" + getLongitude() + ", altitude="
				+ getAltitude() + ", hidden=" + isHidden() + ", size="
				+ getSize() + ", width=" + getWidth() + ", height="
				+ getHeight() + ", MD5Sum=" + getMD5Sum() + ", lastUpdated="
				+ getLastUpdated() + ", albumURL=" + getAlbumURL()
				+ ", tinyURL=" + getTinyURL() + ", thumbURL=" + getThumbURL()
				+ ", smallURL=" + getSmallURL() + ", mediumURL="
				+ getMediumURL() + ", largeURL=" + getLargeURL()
				+ ", xLargeURL=" + getXLargeURL() + ", x2LargeURL="
				+ getX2LargeURL() + ", x3LargeURL=" + getX3LargeURL()
				+ ", originalURL=" + getOriginalURL() + ", video320URL="
				+ getVideo320URL() + ", video640URL=" + getVideo640URL()
				+ ", video960URL=" + getVideo960URL() + ", video1280URL="
				+ getVideo1280URL() + ", album=" + getAlbum() + "]";
	}

	/**
	 * Used to get the ID of the image.
	 * 
	 * @return the ID of the image.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Used to get the security key for the image.
	 * 
	 * @return the security key for the image.
	 */
	public String getImageKey() {
		return imageKey;
	}

	/**
	 * Used to get the original file name of the image.
	 * 
	 * @return the original file name of the image.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Used to get the caption for the image.
	 * 
	 * @return the caption for the image.
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Used to get the keywords assigned to the image.
	 * 
	 * @return the keywords assigned to the image.
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * Used to get the position of the image in the album.
	 * 
	 * @return the position of the image in the album.
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Used to get the timestamp from the image.
	 * 
	 * @return the timestamp from the image.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Used to get the file format of the image.
	 * 
	 * @return the file format of the image.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Used to get the "version" of the image as edited by SmugMug. Each time an
	 * image is edited it's "version" is incremented and is represented by the
	 * serial value.
	 * 
	 * @return the "version" of the image as edited by SmugMug.
	 */
	public Integer getSerial() {
		return serial;
	}

	/**
	 * Used to get the watermark to place on the image.
	 * 
	 * @return the watermark to place on the image.
	 */
	public String getWatermark() {
		return watermark;
	}

	/**
	 * Used to get the latitude associated with this image (for geo-tagging
	 * purposes).
	 * 
	 * @return the latitude associated with this image (for geo-tagging
	 *         purposes).
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Used to get the longitude associated with this image (for geo-tagging
	 * purposes).
	 * 
	 * @return the longitude associated with this image (for geo-tagging
	 *         purposes).
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Used to get the altitude associated with this image (for geo-tagging
	 * purposes).
	 * 
	 * @return the altitude associated with this image (for geo-tagging
	 *         purposes).
	 */
	public Double getAltitude() {
		return altitude;
	}

	/**
	 * Used to determine if the image is hidden.
	 * 
	 * @return <code>true</code> if the image is hidden, otherwise returns
	 *         <code>false</code>.
	 */
	public Boolean isHidden() {
		return hidden;
	}

	/**
	 * Used to get the size, in bytes, of the image.
	 * 
	 * @return the size, in bytes, of the image.
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * Used to get the width of the image.
	 * 
	 * @return the width of the image.
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * Used to get the height of the image.
	 * 
	 * @return the height of the image.
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * Used to get the MD5 Sum of the image.
	 * 
	 * @return the MD5 Sum of the image.
	 */
	public String getMD5Sum() {
		return md5sum;
	}

	/**
	 * Used to get the timestamp of the last updated date for the image.
	 * 
	 * @return the timestamp of the last updated date for the image.
	 */
	public String getLastUpdated() {
		return lastUpdated;
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

	/**
	 * Used to get the {@link Album} that this image is contained in.
	 * 
	 * @return the {@link Album} that this image is contained in.
	 */
	public Album getAlbum() {
		return album;
	}

	//cadam - quickfix
	public Integer getAlbumID() {
		
		return album.getID();
	}
}
