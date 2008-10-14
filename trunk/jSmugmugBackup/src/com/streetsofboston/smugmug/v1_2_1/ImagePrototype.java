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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.codec.digest.DigestUtils;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.images.UploadHTTPPut;
import com.streetsofboston.smugmug.v1_2_1.exceptions.ObjectDoesNotExistException;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.IHasID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype;
import com.streetsofboston.smugmug.v1_2_1.system.Pool;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Use this class to create instances of Image or to retrieve them from the
 * Smugmug repository. For example:<br/>
 * <code>
 * ImagePrototype ipt = new ImagePrototype(session);<br/>
 * InputStream is = getClass().getClassLoader().getResourceAsStream("pic.jpg");<br/>
 * ipt.data().read(is);<br/>
 * ipt.data().setAlbum(someAlbum);<br/>
 * ipt.data().setCaption("Image Caption");<br/>
 * ipt.data().setFileName("testimage.jpg");<br/>
 * Image img = ipt.create();<br/>
 * </code>
 * @author Anton Spaans
 */
public class ImagePrototype 
			extends PrototypeHelper<Image> 
			implements ISmugObjectPrototype<Image> {

	private static final String CACHE = "Image";
	
	private final Data<Image> mData; 

	/**
	 * Constructs a new Image Prototype. 
	 * @param session the current Session.
	 */
	public ImagePrototype(Session session) {
		mData = new Data<Image>(session);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#data()
	 */
	public Data<Image> data() {
		return mData;
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#create()
	 * Creates an image with the given data in the prototype.<br/>
	 * The binary data in the ImageData attribute is used and uploaded as the image
	 * into the Smugmug repository. Note that after calling this method successfully, getImageData 
	 * will return null (to preserve memory).
	 */
	public Image create() throws SmugmugException {
		if (mData.mImageData.isUndefined())
			throw new SmugmugException("The ImageData is not set for "+this);
		
		if (mData.mAlbumID.isUndefined()) 
			throw new SmugmugException("The AlbumID is not set for "+this);

		UploadHTTPPut.UploadHTTPPutResponse resp = mData.doUpload(true);
		
		final Image image = new Image(mData.getSession(), resp.getImageID());
		image.guid().setKey(resp.getImageKey());
		
		// The mImageData is *not* copied into 'image' to preserve memory.
		image.mMD5Sum.set(mData.getMD5Sum());
		image.setCaption(mData.getCaption());
		image.setKeywords(mData.getKeywords());
		image.setAlbumID(mData.getAlbumID());
		image.setFileName(mData.mFileName.get());
		image.setAltitude(mData.mAltitude.get());
		image.setLongitude(mData.mLongitude.get());
		image.setLatitude(mData.mLatitude.get());

		return postCreate(image);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#get(int)
	 */
	public Image get(int ID) throws SmugmugException {
		if (ID == IHasID.NO_ID) 
			throw new SmugmugException("The ID is not specified.");
		
		Image findImg;
		// First see if image is already in the cache.
		findImg = data().findInCache(ID);
		if (findImg != null)
			return findImg;
		
		// Guess not...
		
		// Try to find it in the smugmug repository.
		final Image myImg = new Image(mData.getSession(), ID);
		try {
			myImg.getInfo();
		} catch (ObjectDoesNotExistException odnee) {
			return null;
		}
		postGetAll(myImg);
		return myImg;
	}
	
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll()
	 */
	public Collection<Image> getAll() throws SmugmugException {
		final AlbumPrototype    at = new AlbumPrototype(mData.getSession());
		final Collection<Album> albums = at.getAll();
		
		final Collection<Image> retList = new ArrayList<Image>();
		for (Album album : albums) {
			final Collection<Image> albumImages = getPrivateAll(album.getID(), album.guid().getKey());
			retList.addAll(albumImages);
		}
		return postGetAll(retList, false, GUID.NO_GUID);
	}

	/**
	 * This is the same as {@link #getAll(int)}
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll(java.lang.String)
	 */
	public Collection<Image> getAll(GUID albumId) throws SmugmugException {
		//return getAll(albumId.getIntID());
		return postGetAll(getPrivateAll(albumId.getIntID(), albumId.getKey()), false, albumId);
	}
	
	/**
	 * @see #getAll()
	 * This returns all images within a given album.
	 * @param albumID The ID of the Album.
	 * @return A collection of Images within the given album.
	 * @throws SmugmugException
	 */
	public Collection<Image> getAll(int albumID) throws SmugmugException {
		return getAll(GUID.create(Album.class, albumID));
		//return postGetAll(getPrivateAll(albumID), false, GUID.create(Album.class, albumID));
	}

	/**
	 * @see #getAllFromCache()
	 * This returns all images within a given album.
	 * @param albumID The ID of the Album.
	 * @return A collection of Images within the given album.
	 * @throws SmugmugException
	 */
	public Collection<Image> getAllFromCache(int albumID) throws SmugmugException {
		return getAllFromCache(GUID.create(Album.class, albumID));
	}

	protected static String generateMD5Sum(byte[] imgData) {
		try {
			return DigestUtils.md5Hex(imgData);
		} catch (Exception e) {
			return "";
		}
	}

	private Collection<Image> getPrivateAll(int albumID, String albumKey) throws SmugmugException {
		if (albumID == IHasID.NO_ID) 
			throw new SmugmugException("The AlbumID is not set for "+this);

		Get.GetResponse resp = new Get()
				.execute(APIVersionConstants.SECURE_SERVER_URL, 
							mData.getSession().getAPIKey(), mData.getSession().getSessionID(),
							albumID, albumKey,
							true,
							mData.getObjectPassword(), mData.getSession().getOtherAccount().getSitePassword() 
							);

		SmugmugException.check(resp);

		Collection<com.kallasoft.smugmug.api.json.entity.Image> list = resp.getImageList();
		Collection<Image> retList = new ArrayList<Image>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.Image img : list) {
			final Image myImg = new Image(mData.getSession(), img.getID());
			myImg.setAlbumID(GUID.create(Album.class, albumID).setKey(albumKey));
			myImg.sync();
			retList.add(myImg);
		}

		return retList;
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getClassForPrototype()
	 */
	public Class<Image> getClassForPrototype() {
		return Image.class;
	}

	/** 
	 * Instances of this class hold the prototype-data for creation of Images-s.<br/>
	 * This class also functions as a base-class for Image-s.
	 * @param <T> Image
	 */
	public static class Data<T extends SmugObjectData<?>> extends SmugObjectData<T> {
		static final long serialVersionUID = 3604762668402003176L;

		static protected final Pool<byte[]> smReadWriteBufferPool;
		
		static {
			Pool.initPool(new byte[][]{
					new byte[65536],new byte[65536],new byte[65536],
					new byte[65536],new byte[65536],new byte[65536]
			});
			
			smReadWriteBufferPool = Pool.getPool(byte[].class);
		}
		
		protected Data(Session session) {
			super(session);
			bundle();
		}

		protected Data(Session session, int ID) {
			super(session, ID);
			bundle();
		}
		
		protected String getCacheName() {
			return CACHE;
		}

		/* Actual image data. */
		protected final SmugAttribute<byte[]> mImageData = new SmugAttribute<byte[]>("ImageData");
		
		
		/* Attribute list */
		/* create and change settings */
		protected final SmugAttribute<GUID> mAlbumID = new SmugAttribute<GUID>("AlbumID");
		protected final SmugAttribute<String> mCaption = new SmugAttribute<String>("Caption");
		protected final SmugAttribute<ArrayList<String>> mKeywords = new SmugAttribute<ArrayList<String>>("Keywords");
		
		/* create and read-only (no change-settings/change position). */
		protected final SmugAttribute<String> mMD5Sum = new SmugAttribute<String>("MD5Sum");
		protected final SmugAttribute<Integer> mSize = new SmugAttribute<Integer>("Size");

		/* for creation only and img-replace */
		protected final SmugAttribute<String> mFileName = new SmugAttribute<String>("FileName");
		protected final SmugAttribute<Double> mLongitude = new SmugAttribute<Double>("Longitude");
		protected final SmugAttribute<Double> mLatitude = new SmugAttribute<Double>("Latitude");
		protected final SmugAttribute<Integer> mAltitude = new SmugAttribute<Integer>("Altitude");

		/**
		 * @return the mAlbumID
		 */
		public GUID getAlbumID() {
			return mAlbumID.get();
		}
		
		/**
		 * Returns the album with ID=getAlbumID()
		 * Returns null if this image's AlbumID is unknown.
		 * @return the Album.
		 * @throws SmugmugException
		 */
		public Album getAlbum() throws SmugmugException {
			if (mAlbumID.isUndefined() || !mAlbumID.get().hasGUID())
				return null;
			
			final Album album = (new AlbumPrototype(getSession())).get(getAlbumID());
			return album;
		}

		/**
		 * @param albumID the mAlbumID to set
		 */
		public void setAlbumID(GUID albumID) {
			GUID curGUID = mAlbumID.get();
			if (curGUID != null && curGUID.equals(albumID))
				return;
			
			mAlbumID.set(albumID);
		}

		/**
		 * @param album the Album to set
		 */
		public void setAlbum(Album album) {
			setAlbumID(album != null ? album.guid() : null);
		}
		
		/**
		 * @return the mCaption
		 */
		public String getCaption() {
			return mCaption.get();
		}

		/**
		 * @param caption the mCaption to set
		 */
		public void setCaption(String caption) {
			mCaption.set(caption);
		}

		/**
		 * @return the mKeywords
		 */
		@SuppressWarnings("unchecked")
		public ArrayList<String> getKeywords() {
			ArrayList<String> keyw = mKeywords.get();
			return keyw!= null ? (ArrayList<String>)keyw.clone() : null;
		}

		/**
		 * @param keywords the mKeywords to set
		 */
		@SuppressWarnings("unchecked")
		public void setKeywords(ArrayList<String> keywords) {
			mKeywords.set(keywords != null ? (ArrayList<String>)keywords.clone() : null);
		}

		/**
		 * @param fileName the mFileName to set
		 */
		public void setFileName(String fileName) {
			mFileName.set(fileName);
		}

		/**
		 * @param longitude
		 */
		public void setLongitude(Double longitude) {
			mLongitude.set(longitude);
		}
		/**
		 * @param latitude
		 */
		public void setLatitude(Double latitude) {
			mLatitude.set(latitude);
		}
		/**
		 * @param altitude
		 */
		public void setAltitude(Integer altitude) {
			mAltitude.set(altitude);
		}

	/********* Image Data Attributes ******************/
		/**
		 * This method will read data from the input stream, puts the
		 * resulting binary data into the ImageData attribute and
		 * its MD5-sum in the MD5Sum attribute.
		 * @param is InputStream from which to read.
		 * @throws SmugmugException
		 */
		public void read(InputStream is) throws SmugmugException {
			byte[] imgData = null;  
			try {
				imgData = getByteArrayFromInputStream(is);
			} catch (IOException e) {
				throw new SmugmugException("The Image Data could not be read for "+this, e);
			}
			
			if (imgData == null || imgData.length == 0)
				throw new SmugmugException("The Image Data could not be read for "+this);
			
			setImageData(imgData);
		}
		
		/**
		 * This returns the MD5-sum of the current image-data.
		 * This sum can be used as a unique identifier for the image-data, like
		 * a hash-value.
		 * @return the mMD5Sum
		 */
		public String getMD5Sum() {
			return mMD5Sum.get();
		}

		/**
		 * Returns the size of the raw image data, the number of bytes.
		 * @return the mSize
		 */
		public Integer getSize() {
			return mSize.get();
		}

		/**
		 * Returns the raw image-data as an array of bytes.<br/>
		 * Note that after a successful {@link ImagePrototype#create()} or {@link Image#update()}, this
		 * method will return null. This is done to preserve memory.
		 * @return the raw image-data.
		 */
		public byte[] getImageData() {
			return mImageData.get();
		}
		
		/**
		 * Sets the raw image-data. It updates the MD5Sum and Size as well.
		 * If null, the image-data (and MD5Sum and Size) will be cleared.
		 * Note that after a successful {@link ImagePrototype#create()} or {@link Image#update()}, this
		 * attribute will be null. This is done to preserve memory.
		 * @param imgData The raw image-data.
		 */
		public void setImageData(byte[] imgData) {
			if (imgData == null) {
				mImageData.clear();
				mSize.clear();
				mMD5Sum.clear();
			}
			else { 
				// Not set, but assign! 
				// This makes sure that hasChanged returns false.
				// Only the MD5Sum should be used to see if the image-data has changed.
				mImageData.assign(imgData); 
				
				mSize.set(imgData.length);
				
				final String md5Sum = generateMD5Sum(imgData);
				mMD5Sum.set(md5Sum);
			}
		}
		
		/**
		 * Actually uploads the image-data to the smugmug repository.
		 * @param isNew If set, a new image will be uploaded into its album.
		 * If false, the existing image will be replaced.
		 * @return The kallasoft response.
		 * @throws SmugmugException
		 */
		protected UploadHTTPPut.UploadHTTPPutResponse doUpload(boolean isNew)
				throws SmugmugException {
			UploadHTTPPut.UploadHTTPPutResponse resp = new UploadHTTPPut()
					.execute(APIVersionConstants.BINARY_UPLOAD_SERVER_URL,
							 new String [] {
								toNumber(mImageData.get().length),
								mMD5Sum.get(),
								getSession().getSessionID(),
								"1.2.1", "JSON", 
								isNew ? toNumber(getAlbumID()) : null,
								isNew ? null : toNumber(getID()),
								mFileName.get(),
								isNew ? getCaption() : null,
								isNew ? toKeywordsStringForImage(getKeywords()) : null,
								toNumber(mLatitude.get()),
								toNumber(mLongitude.get()),
								toNumber(mAltitude.get())
							 },
							 mImageData.get());
		
			SmugmugException.check(resp);
			mImageData.clear(); // preserve memory.
			
			return resp;
		}
		
		protected static byte[] getByteArrayFromInputStream(InputStream is) throws IOException {
			byte[] imgData;
			final ByteArrayOutputStream bos  = new ByteArrayOutputStream();
			final BufferedInputStream bufIS  = new BufferedInputStream(is);
			final BufferedOutputStream bufOS = new BufferedOutputStream(bos);
			final byte[] buffer = smReadWriteBufferPool.get();
			try {
				int didRead = bufIS.read(buffer);
				while (didRead > 0) {
					bufOS.write(buffer, 0, didRead); 
					//totalWritten += didRead;

					didRead = bufIS.read(buffer);
				}
			}
			finally {
				smReadWriteBufferPool.release(buffer);
			}
			bufOS.flush();
			imgData = bos.toByteArray();
			return imgData;
		}
	}
}
