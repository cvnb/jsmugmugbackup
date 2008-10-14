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
package com.streetsofboston.smugmug.v1_2_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.images.ChangePosition;
import com.kallasoft.smugmug.api.json.v1_2_1.images.ChangeSettings;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Delete;
import com.kallasoft.smugmug.api.json.v1_2_1.images.GetEXIF;
import com.kallasoft.smugmug.api.json.v1_2_1.images.GetInfo;
import com.kallasoft.smugmug.api.json.v1_2_1.images.GetStats;
import com.kallasoft.smugmug.api.json.v1_2_1.images.GetURLs;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.VIEW_STYLE;
import com.streetsofboston.smugmug.v1_2_1.exceptions.ObjectDoesNotExistException;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.IHasID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.Stats;

/**
 * Instances of this class represent Images on Smugmug. 
 * @author Anton Spaans
 */
public class Image 
			extends ImagePrototype.Data<Image> 
			implements ISmugObject<Image> {
	static final long serialVersionUID = 8690338834953484516L;

	public enum IMAGE_SIZE {
		Tiny,
		Thumb,
		Small,
		Medium,
		Large,
		XLarge,
		X2Large,
		X3Large,
		Original,
		Custom
	}
	
	private Stats<Image> mStats;
	
	protected final SmugAttribute<Boolean> mIsHidden = new SmugAttribute<Boolean>("IsHidden");
	
	/* read-only and change position */
	protected final SmugAttribute<Integer> mPosition = new SmugAttribute<Integer>("Position");
	
	/* read-only */
	protected final SmugAttribute<Integer> mSerial = new SmugAttribute<Integer>("Serial");
	protected final SmugAttribute<Integer> mWidth = new SmugAttribute<Integer>("Width");
	protected final SmugAttribute<Integer> mHeight = new SmugAttribute<Integer>("Height");
	protected final SmugAttribute<Date> mLastUpdated = new SmugAttribute<Date>("LastUpdated");

	protected final SmugAttribute<String> mWatermark = new SmugAttribute<String>("Watermark");
	protected final SmugAttribute<String> mFormat = new SmugAttribute<String>("Format");
	protected final SmugAttribute<Date> mDate = new SmugAttribute<Date>("Date");

	protected final SmugAttribute<URL> mAlbumURL = new SmugAttribute<URL>("AlbumURL");
	protected final SmugAttribute<URL> mTinyURL = new SmugAttribute<URL>("TinyURL");
	protected final SmugAttribute<URL> mThumbURL = new SmugAttribute<URL>("ThumbURL");
	protected final SmugAttribute<URL> mSmallURL = new SmugAttribute<URL>("SmallURL");
	protected final SmugAttribute<URL> mMediumURL = new SmugAttribute<URL>("MediumURL");
	protected final SmugAttribute<URL> mLargeURL = new SmugAttribute<URL>("LargeURL");
	protected final SmugAttribute<URL> mXLargeURL = new SmugAttribute<URL>("XLargeURL");
	protected final SmugAttribute<URL> mX2LargeURL = new SmugAttribute<URL>("X2LargeURL");
	protected final SmugAttribute<URL> mX3LargeURL = new SmugAttribute<URL>("X3LargeURL");
	protected final SmugAttribute<URL> mOriginalURL = new SmugAttribute<URL>("OriginalURL");

	protected final SmugAttribute<URL> mVideo320URL = new SmugAttribute<URL>("Video320URL");
	protected final SmugAttribute<URL> mVideo640URL = new SmugAttribute<URL>("Video640URL");
	protected final SmugAttribute<URL> mVideo960URL = new SmugAttribute<URL>("Video960URL");
	protected final SmugAttribute<URL> mVideo1280URL = new SmugAttribute<URL>("Video1280URL");
	
	protected Image(Session session, int ID) {
		super(session, ID);
		bundle();
	}
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#delete()
	 */
	public void delete() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);

		Delete.DeleteResponse resp = new Delete().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID());

		SmugmugException.check(resp, this);
		
		PrototypeHelper.postDelete(this);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#getInfo()
	 */
	public Image getInfo() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);
			
		GetInfo.GetInfoResponse resp1 = new GetInfo().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID(), guid().getKey(), 
				getObjectPassword(), getSession().getOtherAccount().getSitePassword());

		SmugmugException.check(resp1, this);
		if (resp1.getImage().getID()!=getID())
			throw new ObjectDoesNotExistException(getID(), Image.class);

		com.kallasoft.smugmug.api.json.entity.Image resp = resp1.getImage();
		lock();
		try {
			clear();
			
			setAlbumID(GUID.create(Album.class, resp.getAlbumID()));
			setCaption(resp.getCaption());
			mDate.set(strToDate(resp.getDate(), true));
			setFileName(resp.getFileName());
			mFormat.set(resp.getFormat());
			mHeight.set(resp.getHeight());
			setKeywords(toKeywords(resp.getKeywords()));
			mLastUpdated.set(strToDate(resp.getLastUpdated(), true));
			mMD5Sum.set(resp.getMD5Sum());
			setPosition(resp.getPosition());
			mSerial.set(resp.getSerial());
			mSize.set(resp.getSize());
			mWatermark.set(resp.getWatermark());
			mWidth.set(resp.getWidth());
			try {
				mAlbumURL.set(new URL(resp.getAlbumURL()));
				mOriginalURL.set(getURL(resp.getOriginalURL()));
				mX3LargeURL.set(getURL(resp.getX3LargeURL()));
				mX2LargeURL.set(getURL(resp.getX2LargeURL()));
				mXLargeURL.set(getURL(resp.getXLargeURL()));
				mLargeURL.set(getURL(resp.getLargeURL()));
				mMediumURL.set(new URL(resp.getMediumURL()));
				mSmallURL.set(new URL(resp.getSmallURL()));
				mThumbURL.set(new URL(resp.getThumbURL()));
				mTinyURL.set(new URL(resp.getTinyURL()));

				mVideo320URL.set(getURL(resp.getVideo320URL()));
				mVideo640URL.set(getURL(resp.getVideo640URL()));
				mVideo960URL.set(getURL(resp.getVideo960URL()));
				mVideo1280URL.set(getURL(resp.getVideo1280URL()));
			} catch (MalformedURLException e) {
				throw new SmugmugException(e);
			}
			return PrototypeHelper.postUpdate(this);
		} finally {
			unlock();
		}
	}
	private static URL getURL(String url) {
		try {
			return (url!=null&&url.length()>0) ? new URL(url) : null;
		}
		catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Retrieves information, from the Smugmug repository, about the URLs of 
	 * this image only. All this object's attributes ending in 'URL' are changed
	 * after calling this method (e.g. AlbumURL, OriginalURL, MediumURL, etc.). 
	 * @param viewStyle Used to create a proper AlbumURL.
	 * @return This object.
	 * @throws SmugmugException
	 */
	public Image getInfoURLsOnly(VIEW_STYLE viewStyle) throws SmugmugException {
		if (viewStyle!=null) {
			switch (viewStyle) {
			case Smugmug:
			case Traditional:
			case AllThumbs:
			case SlideShow:
			case Journal:
				break;
			default:
				throw new SmugmugException("View Style "+viewStyle+" not accepted.");
			}
		}
		
		final Integer templateID = viewStyle != null 
				? viewStyle.value()
				: null;

		GetURLs.GetURLsResponse resp = new GetURLs().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID(), guid().getKey(), templateID,
				getObjectPassword(), getSession().getOtherAccount().getSitePassword() );

		SmugmugException.check(resp, this);
		
		lock();
		try {
			clear();
			
			try {
				mAlbumURL.set(new URL(resp.getAlbumURL()));
				mOriginalURL.set(getURL(resp.getOriginalURL()));
				mX3LargeURL.set(getURL(resp.getX3LargeURL()));
				mX2LargeURL.set(getURL(resp.getX2LargeURL()));
				mXLargeURL.set(getURL(resp.getXLargeURL()));
				mLargeURL.set(getURL(resp.getLargeURL()));
				mMediumURL.set(new URL(resp.getMediumURL()));
				mSmallURL.set(new URL(resp.getSmallURL()));
				mThumbURL.set(new URL(resp.getThumbURL()));
				mTinyURL.set(new URL(resp.getTinyURL()));
			} catch (MalformedURLException e) {
				throw new SmugmugException(e);
			}
			return PrototypeHelper.postUpdate(this);
		} finally {
			unlock();
		}
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#update() <br/>
	 * This method may reposition the image within the album (Position has changed),
	 * may update the raw image data (MD5Sum has changed), move the image to another
	 * album (AlbumID or Album has changed) and/or change the caption/keywords of this image.
	 */
	public Image update() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);
		
		boolean bImgDataChanged = false;
		boolean bRePos = false;
		boolean bChangeSettings = false;
		String[] updateVals = null;

		lock();
		try {
			boolean bMove = false;
			bRePos = false;
			
			if (mMD5Sum.hasChanged() && !mImageData.isUndefined() && mImageData.get().length > 0) {
				bImgDataChanged = true;
			}
			else {
				// ImageData is cleared after a successful update.
				// Even if the update was a no-op, because nothing changed.
				mImageData.clear();
			}
			
			if (mAlbumID.hasChanged()) {
				bMove = true;
				bChangeSettings = true;
			}
			
			if (mCaption.hasChanged() || mKeywords.hasChanged()) {
				bChangeSettings = true;
			}
			
			if (bMove) {
				mPosition.clear();
			}
			
			if (mPosition.hasChanged()) {
				bRePos = true;
			}
			
			if (bChangeSettings) {
				setReturnNullIfNotChanged(true);
				updateVals = new String[] { 
						getSession().getAPIKey(), getSession().getSessionID(), 
						toNumber(getID()), toNumber(getAlbumID()), 
						getCaption(), toKeywordsStringForImage(getKeywords()),
						toBoolean(getIsHidden())};
				setReturnNullIfNotChanged(false);
			}
		} finally {
			unlock();
		}
		
		if (bImgDataChanged) {
			doUpload(false);
		}
		
		if (bChangeSettings) {
			ChangeSettings.ChangeSettingsResponse resp = new ChangeSettings()
					.execute(APIVersionConstants.SECURE_SERVER_URL, updateVals);

			SmugmugException.check(resp, this);
		}

		if (bRePos) {
			ChangePosition.ChangePositionResponse resp = new ChangePosition().execute(
					APIVersionConstants.SECURE_SERVER_URL,
					getSession().getAPIKey(), getSession().getSessionID(), 
					getID(), getPosition() );

			SmugmugException.check(resp, this);
		}

		return PrototypeHelper.postUpdate(this);
	}

	/**
	 * Returns the image statistics on Smugmug.
	 * @param month Month of statistics.
	 * @return The statistics for this image.
	 * @throws SmugmugException 
	 */
	public Stats<Image> getStatistics(int month) throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);

		GetStats.GetStatsResponse resp = new GetStats().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID(),
				month );

		SmugmugException.check(resp, this);
		
		mStats = new Stats<Image>(this, -1, month,
				resp.getImageTransferStats().getBytes(),
				resp.getImageTransferStats().getTiny(),
				resp.getImageTransferStats().getThumb(),
				resp.getImageTransferStats().getSmall(),
				resp.getImageTransferStats().getMedium(),
				resp.getImageTransferStats().getLarge(), resp.getImageTransferStats().getXLarge(), 
				resp.getImageTransferStats().getX2Large(), resp.getImageTransferStats().getX3Large(),
				resp.getImageTransferStats().getOriginal(),
				resp.getImageTransferStats().getVideo320(), resp.getImageTransferStats().getVideo640(),
				resp.getImageTransferStats().getVideo960(), resp.getImageTransferStats().getVideo1280());
		
		return mStats;
	}

	/**
	 * Get the statistics that were obtained from Smugmug most recently.
	 * Return null if no stats were obtained yet. Call getStatistics(month) to obtain them.
	 * @return The image's stats.
	 */
	public Stats<Image> getStatistics() {
		return mStats;
	}
	
	protected void setStatistics(Stats<Image> stats) {
		mStats = stats;
	}
	
	/**
	 * @return The filename of this image.
	 */
	public String getFileName() {
		return mFileName.get();
	}
	/**
	 * @return True if image is hidden from view for everyone but the owner.
	 */
	public Boolean getIsHidden() {
		return mIsHidden.get();
	}
	
	/**
	 * Set it to true if image can be visible only to the owner.
	 * @param hidden True or false.
	 */
	public void setIsHidden(Boolean hidden) {
		mIsHidden.set(hidden);
	}
	
	/**
	 * @return the mPosition
	 */
	public Integer getPosition() {
		return mPosition.get();
	}

	/**
	 * @param position the mPosition to set
	 */
	public void setPosition(Integer position) {
		mPosition.set(position);
	}

	/**
	 * @return the mSerial
	 */
	public Integer getSerial() {
		return mSerial.get();
	}

//	/**
//	 * @param serial the mSerial to set
//	 */
//	public void setSerial(Integer serial) {
//		mSerial.set(serial);
//	}

	/**
	 * @return the mWidth
	 */
	public Integer getWidth() {
		return mWidth.get();
	}

//	/**
//	 * @param width the mWidth to set
//	 */
//	public void setWidth(Integer width) {
//		mWidth.set(width);
//	}

	/**
	 * @return the mHeight
	 */
	public Integer getHeight() {
		return mHeight.get();
	}

//	/**
//	 * @param height the mHeight to set
//	 */
//	public void setHeight(Integer height) {
//		mHeight.set(height);
//	}

	/**
	 * @return the mLastUpdated
	 */
	public Date getLastUpdated() {
		return mLastUpdated.get();
	}

//	/**
//	 * @param lastUpdated the mLastUpdated to set
//	 */
//	public void setLastUpdated(Date lastUpdated) {
//		mLastUpdated.set(lastUpdated);
//	}

	/**
	 * @return the mWatermark
	 */
	public String getWatermark() {
		return mWatermark.get();
	}

//	/**
//	 * @param watermark the mWatermark to set
//	 */
//	public void setWatermark(String watermark) {
//		mWatermark.set(watermark);
//	}

	/**
	 * @return the mFormat
	 */
	public String getFormat() {
		return mFormat.get();
	}

//	/**
//	 * @param format the mFormat to set
//	 */
//	public void setFormat(String format) {
//		mFormat.set(format);
//	}

	/**
	 * @return the mDate
	 */
	public Date getDate() {
		return mDate.get();
	}

//	/**
//	 * @param date the mDate to set
//	 */
//	public void setDate(Date date) {
//		mDate.set(date);
//	}

	/**
	 * @return the mAlbumURL
	 */
	public URL getAlbumURL() {
		return mAlbumURL.get();
	}

//	/**
//	 * @param albumURL the mAlbumURL to set
//	 */
//	public void setAlbumURL(URL albumURL) {
//		mAlbumURL.set(albumURL);
//	}

	/**
	 * @return the mTinyURL
	 */
	public URL getTinyURL() {
		return mTinyURL.get();
	}

//	/**
//	 * @param tinyURL the mTinyURL to set
//	 */
//	public void setTinyURL(URL tinyURL) {
//		mTinyURL.set(tinyURL);
//	}

	/**
	 * @return the mThumbURL
	 */
	public URL getThumbURL() {
		return mThumbURL.get();
	}

//	/**
//	 * @param thumbURL the mThumbURL to set
//	 */
//	public void setThumbURL(URL thumbURL) {
//		mThumbURL.set(thumbURL);
//	}

	/**
	 * @return the mSmallURL
	 */
	public URL getSmallURL() {
		return mSmallURL.get();
	}

//	/**
//	 * @param smallURL the mSmallURL to set
//	 */
//	public void setSmallURL(URL smallURL) {
//		mSmallURL.set(smallURL);
//	}

	/**
	 * @return the mMediumURL
	 */
	public URL getMediumURL() {
		return mMediumURL.get();
	}

//	/**
//	 * @param mediumURL the mMediumURL to set
//	 */
//	public void setMediumURL(URL mediumURL) {
//		mMediumURL.set(mediumURL);
//	}

	/**
	 * @return the mLargeURL
	 */
	public URL getLargeURL() {
		return mLargeURL.get();
	}

//	/**
//	 * @param largeURL the mLargeURL to set
//	 */
//	public void setLargeURL(URL largeURL) {
//		mLargeURL.set(largeURL);
//	}

	/**
	 * @return the mXLargeURL
	 */
	public URL getXLargeURL() {
		return mXLargeURL.get();
	}

//	/**
//	 * @param largeURL the mXLargeURL to set
//	 */
//	public void setXLargeURL(URL largeURL) {
//		mXLargeURL.set(largeURL);
//	}

	/**
	 * @return the mX2LargeURL
	 */
	public URL getX2LargeURL() {
		return mX2LargeURL.get();
	}

//	/**
//	 * @param largeURL the mX2LargeURL to set
//	 */
//	public void setX2LargeURL(URL largeURL) {
//		mX2LargeURL.set(largeURL);
//	}

	/**
	 * @return the mX3LargeURL
	 */
	public URL getX3LargeURL() {
		return mX3LargeURL.get();
	}

//	/**
//	 * @param largeURL the mX3LargeURL to set
//	 */
//	public void setX3LargeURL(URL largeURL) {
//		mX3LargeURL.set(largeURL);
//	}

	/**
	 * @return the mOrginalURL
	 */
	public URL getOrginalURL() {
		return mOriginalURL.get();
	}

	/**
	 * @return the mVideo320URL
	 */
	public URL getVideo320URL() {
		return mVideo320URL.get();
	}
	
	/**
	 * @return the mVideo640URL
	 */
	public URL getVideo640URL() {
		return mVideo640URL.get();
	}
	
	/**
	 * @return the mVideo960URL
	 */
	public URL getVideo960URL() {
		return mVideo960URL.get();
	}
	
	/**
	 * @return the mVideo1280URL
	 */
	public URL getVideo1280URL() {
		return mVideo1280URL.get();
	}
	
	/**
	 * @param size
	 * @return The URL of the image on Smugmug given the specified image-size.
	 */
	public URL getURL(IMAGE_SIZE size) {
		if (size == null)
			return null;

		switch(size) {
		case Original:
			return getOrginalURL();
		case X3Large:
			return getX3LargeURL();
		case X2Large:
			return getX2LargeURL();
		case XLarge:
			return getXLargeURL();
		case Large:
			return getLargeURL();
		case Medium:
			return getMediumURL();
		case Small:
			return getSmallURL();
		case Thumb:
			return getThumbURL();
		case Tiny:
			return getTinyURL();
		case Custom:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * @return The size of the largest (allowed) version of the image.
	 * @throws SmugmugException
	 */
	public IMAGE_SIZE getLargestSize() throws SmugmugException {
		if (getID() == IHasID.NO_ID)
			return null;
		
		final Album album = getAlbum();
		if (album == null)
			return null;
		
		return album.getLargestImageSize();
	}

	/**
	 * @return The URL of the largest (allowed) version of the image.
	 * @throws SmugmugException
	 */
	public URL getLargestSizeURL() throws SmugmugException {
		final IMAGE_SIZE size = getLargestSize();
		
		return getURL(size);
	}

	/**
	 * Writes the image-data of this image to the specified output-stream.
	 * If the ImageData attribute is a byte-array of more than 0 bytes, this data
	 * will be written. If ImageData is not set, the image-data is fetched from the
	 * image's URL given the image-size (@see {@link #getURL(String)}). 
	 * @param os The target output-stream.
	 * @param size Determines the 'size' of the image-URL. If set to null, the {@link #getLargestSizeURL()} is used instead.
	 * @return The raw image data.
	 * @throws SmugmugException
	 */
	public byte[] write(OutputStream os, IMAGE_SIZE size) throws SmugmugException {
		if (os != null && !mImageData.isUndefined() && mImageData.get().length>0) {
			try {
				os.write(mImageData.get());
				os.flush();
				return mImageData.get();
			} catch (IOException e) {
				throw new SmugmugException("The Image Data could not be written for "+this, e);
			}
		}
		
		final URL url = size == null ? getLargestSizeURL() : getURL(size);
		try {
			return write(os, url);
		}
		catch (SmugmugException e ) {
			throw new SmugmugException(e.getMessage()+" for "+this, e.getCause());
		}
	}

	public byte[] write(OutputStream os, URL url) throws SmugmugException {
		try {
			final InputStream is = getSession().openInputStream(url);
			final byte[] bytes = getByteArrayFromInputStream(is);
			try { is.close(); } catch (IOException e2) { }

			if (os != null) {
				os.write(bytes);
				os.flush();
			}
			return bytes;
		} catch (IOException e) {
			throw new SmugmugException("The Image Data could not be written", e);
		}
	}
	
	//	/**
//	 * @param orginalURL the mOrginalURL to set
//	 */
//	public void setOrginalURL(URL orginalURL) {
//		mOrginalURL.set(orginalURL);
//	}
	
	/**
	 * @return the EXIF data for this image.
	 * @throws SmugmugException
	 */
	public GetEXIF.GetEXIFResponse getEXIF() throws SmugmugException {
		final GetEXIF.GetEXIFResponse resp = new GetEXIF().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				getSession().getAPIKey(), getSession().getSessionID(), 
				toNumber(getID()), 
				getObjectPassword(), getSession().getOtherAccount().getSitePassword() 
		});

		SmugmugException.check(resp, this);
		
		return resp;
	}

	protected String figureOutOwnersNickName() throws SmugmugException {
		return getAlbum().getOwnerNickName();
	}

	/**
	 * @return The ID of the parent.
	 */
	public GUID getParentId() {
		return mAlbumID.isUndefined() ? null : mAlbumID.get();
	}
}
