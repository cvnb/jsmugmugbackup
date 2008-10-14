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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albumtemplates.Get;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.PRINT_COLOR;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.SORT_METHOD;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.SORT_ORDER;
import com.streetsofboston.smugmug.v1_2_1.AlbumTemplatePrototype.Data.VIEW_STYLE;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.IEnumValue;
import com.streetsofboston.smugmug.v1_2_1.system.IHasID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Use this class to create instances of AlbumCategory or to retrieve them from the
 * Smugmug repository.
 * @author Anton Spaans
 */
public class AlbumTemplatePrototype extends PrototypeHelper<AlbumTemplate> implements ISmugObjectPrototype<AlbumTemplate> {

	private static final String CACHE = "AlbumTemplate";
	
	private final Data<AlbumTemplate> mData; 

	/**
	 * Creates a new instance of a AlbumTemplate Prototype.
	 * 
	 * @param session Session-account information.
	 */
	public AlbumTemplatePrototype(Session session) {
		mData = new Data<AlbumTemplate>(session);
	}
	
	/**
	 * @throws SmugmugException. This method is not (yet) implemented.
	 */
	public AlbumTemplate create() throws SmugmugException {
		throw new SmugmugException(new NoSuchMethodException(getClass().getName()+".create"));
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#getAll()
	 */
	public Collection<AlbumTemplate> getAll() throws SmugmugException {
		Get.GetResponse gresp = new Get().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID());

		SmugmugException.check(gresp);

		Collection<com.kallasoft.smugmug.api.json.entity.AlbumTemplate> list = gresp.getAlbumTemplateList();
		Collection<AlbumTemplate> retList = new ArrayList<AlbumTemplate>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.AlbumTemplate resp : list) {
			final AlbumTemplate temp = new AlbumTemplate(mData.getSession(), resp.getID(), resp.getAlbumTemplateName());
			
			temp.setBackPrintString(resp.getBackprinting());
			temp.setCommunityID(resp.getCommunityID());
			temp.setPassword(resp.getPassword());
			temp.setPasswordHint(resp.getPasswordHint());
			temp.setNumberOfProofDays(resp.getProofDays());
			temp.setSortMethod(SORT_METHOD.valueOf(resp.getSortMethod()));
			temp.setViewStyleID(VIEW_STYLE.get(resp.getTemplateID()));
			temp.setWatermarkID(resp.getWatermarkID());
			temp.setAllowRankPhotos(resp.canRank());
			temp.setIsClean(resp.isClean());
			temp.setAllowComments(resp.isComments());
			temp.setPrintColor(PRINT_COLOR.get(resp.isDefaultColor()));
			temp.setShowExif(resp.isExif());
			temp.setAllowExternalLinks(resp.isExternal());
			temp.setAllowEditsByFamily(resp.isFamilyEdit());
			temp.setShowFilenames(resp.isFilenames());
			temp.setAllowEditsByFriends(resp.isFriendEdit());
			temp.setShowGeography(resp.isGeography());
			temp.setHasCustomAppearance(resp.isHeader());
			temp.setIsOwnerInfoHidden(resp.isHideOwner());
			temp.mIsLargestLarge.set(resp.isLarges());
			temp.mIsLargestOriginal.set(resp.isOriginals());
			temp.setIsPrintable(resp.isPrintable());
			temp.setIsProtected(resp.isProtected());
			temp.setShowOnHomepage(resp.isPublic());
			temp.setShowEasyShareButton(resp.isShare());
			temp.setIsSmugSearchable(resp.isSmugSearchable());
			temp.setSortOrder(SORT_ORDER.get(resp.getSortDirection()));
			temp.setHasCustomWatermark(resp.isWatermarking());
			temp.setIsWorldSearchable(resp.isWorldSearchable());

			temp.mIsLargestXLarge.set(resp.isXLarges());
			temp.mIsLargestX2Large.set(resp.isX2Larges());
			temp.mIsLargestX3Large.set(resp.isX3Larges());
			
			temp.setUnsharpAmount(resp.getUnsharpAmount());
			temp.setUnsharpRadius(resp.getUnsharpRadius());
			temp.setUnsharpSigma(resp.getUnsharpSigma());
			temp.setUnsharpThreshold(resp.getUnsharpThreshold());
			
			temp.setLargestImageSize(temp.getLargestImageSize());
			temp.sync();
			
			retList.add(temp);
		}
		return postGetAll(retList, false, GUID.NO_GUID);
	}

	/**
	 * This is the same as {@link #getAll()}
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll(java.lang.String)
	 */
	public Collection<AlbumTemplate> getAll(GUID notUsed) throws SmugmugException {
		return getAll();
	}
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#data()
	 */
	public Data<AlbumTemplate> data() {
		return mData;
	}
	
	public Class<AlbumTemplate> getClassForPrototype() {
		return AlbumTemplate.class;
	}
	
	/** 
	 * Instances of this class hold the prototype-data for creation of AlbumTemplate-s.<br/>
	 * This class also functions as a base-class for AlbumTemplate-s and AlbumPrototype.Data.
	 * @param <T> AlbumTemplate
	 */
	public static class Data<T extends SmugObjectData<?>> extends SmugObjectData<T> {
		static final long serialVersionUID = 2735651541427397580L;

		/**
		 * This enumeration defines how images can be sorted within albums.
		 */
		public static enum SORT_METHOD implements IEnumValue<String> {
			Position,
			Caption,
			FileName,
			Date,
			DateTime,
			DateTimeOriginal;
			public String value() { return toString(); }
		}
		
		/**
		 * This enumeration defines whether the SORT_METHOD is done ascending or descending.
		 */
		public static enum SORT_ORDER implements IEnumValue<Boolean> {
			Asc  (Boolean.FALSE),
			Desc (Boolean.TRUE);
			
			static private Map<Boolean, SORT_ORDER> mMap;
			static public  SORT_ORDER get(Boolean val) { return val != null ? mMap.get(val) : null; }
			
			private final Boolean mValue;
			SORT_ORDER(Boolean value) { 
				mValue = value; 
				if (mMap == null)
					mMap = new TreeMap<Boolean, SORT_ORDER>();
				mMap.put(mValue, this); 
			}
			/**
			 * Returns the boolean representation of this SORT_ORDER as known by Smugmug.
			 * @see com.streetsofboston.smugmug.v1_2_1.system.IEnumValue#value()
			 */
			public Boolean value()    { return mValue; }
		}

		/**
		 * This enumeration defines an album's view-style, how the images are presented within an album.
		 */
		public static enum VIEW_STYLE implements IEnumValue<Integer> {
			ViewerChoice(0),
			Smugmug(3),
			SmugmugSmall(10),
			Traditional(4),
			AllThumbs(7),
			SlideShow(8),
			Journal(9),
			FilmStrip(11),
			Critique(12);
			
			static private Map<Integer, VIEW_STYLE> mMap;
			static public  VIEW_STYLE get(Integer val) { return val != null ? mMap.get(val) : null; }

			private final Integer mId;
			VIEW_STYLE (Integer id) { 
				mId = id; 
				if (mMap == null)
					mMap = new TreeMap<Integer, VIEW_STYLE>();
				mMap.put(id, this); 
			}
			/**
			 * Returns the numerical representation of this VIEW_STYLE, as known by Smugmug.
			 * @see com.streetsofboston.smugmug.v1_2_1.system.IEnumValue#value()
			 */
			public Integer value()  { return mId; }
		}
		
		/**
		 * This enumeration defines which method is used when visitors order prints from
		 * this album. If set to TrueColor, Smugmug will not any color-correction before 
		 * printing. If set to AutoColor, Smugmug will apply some automatic color-correction
		 * before before printing.
		 */
		public static enum PRINT_COLOR implements IEnumValue<Boolean> {
			TrueColor  (Boolean.FALSE),
			AutoColor (Boolean.TRUE);
			
			static private Map<Boolean, PRINT_COLOR> mMap;
			static public  PRINT_COLOR get(Boolean val) { return val != null ? mMap.get(val) : null; }
			
			private final Boolean mValue;
			PRINT_COLOR(Boolean value) { 
				mValue = value; 
				if (mMap == null)
					mMap = new TreeMap<Boolean, PRINT_COLOR>();
				mMap.put(value, this); 
			}
			/**
			 * Returns the boolean representation of this PRINT_COLOR as known by Smugmug.
			 * @see com.streetsofboston.smugmug.v1_2_1.system.IEnumValue#value()
			 */
			public Boolean value( )    { return mValue; }
		}

		protected Data(Session session) {
			super(session);
			bundle();
		}
		
		protected Data(Session session, int ID, String title) {
			super(session, ID);
			mName.set(title);
			bundle();
		}
		
		protected String getCacheName() {
			return CACHE;
		}

		/* Attribute list */
		/*
		 * getHighlightID
		 */
		protected final SmugAttribute<Integer> mHighlightImageID = new SmugAttribute<Integer>("HighlightImageID");
		
		/*
		 * Essentials: id (part of superclass),getAlbumTemplateName
		 */
		protected final SmugAttribute<String> mName = new SmugAttribute<String>("Title");
		
		/*
		 * Extras: isGeography  
		 */
		protected final SmugAttribute<Boolean> mShowGeography = new SmugAttribute<Boolean>("ShowGeography");
		/*
		 * Look & Feel: isHeader,isClean,isExif,isFilenames,getTemplateID,getSortMethod,isSortDirection
		 */
		protected final SmugAttribute<Boolean> mHasCustomappearance = new SmugAttribute<Boolean>("HasSmugmugAppearance");
		protected final SmugAttribute<Boolean> mIsClean = new SmugAttribute<Boolean>("IsClean");
		protected final SmugAttribute<Boolean> mShowExif = new SmugAttribute<Boolean>("ShowExif");
		protected final SmugAttribute<Boolean> mShowFilenames = new SmugAttribute<Boolean>("ShowFilenames");
		protected final SmugAttribute<VIEW_STYLE> mViewStyleID = new SmugAttribute<VIEW_STYLE>("ViewStyleID");
		protected final SmugAttribute<SORT_METHOD>  mSortMethod = new SmugAttribute<SORT_METHOD>("SortMethod");
		protected final SmugAttribute<SORT_ORDER> mSortOrder = new SmugAttribute<SORT_ORDER>("SortOrder");
		
		/*
		 * Security & Privacy:getPassword,getPasswordHint,isPublic,isWorldSearchable
		 * isSmugSearchable,isExternal,isProtected,isWatermarking,getWatermarkID,
		 * isHideOwner,isLarges,isOriginals,isXLarges,isX2Larges,isX3Larges
		 */
		protected final SmugAttribute<String> mPassword = new SmugAttribute<String>("Password");
		protected final SmugAttribute<String> mPasswordHint = new SmugAttribute<String>("PasswordHint");
		protected final SmugAttribute<Boolean> mShowOnHomepage = new SmugAttribute<Boolean>("ShowOnHomepage");
		protected final SmugAttribute<Boolean> mIsWorldSearchable = new SmugAttribute<Boolean>("IsWorldSearchable");
		protected final SmugAttribute<Boolean> mIsSmugSearchable = new SmugAttribute<Boolean>("IsSmugSearchable");
		protected final SmugAttribute<Boolean> mAllowExternalLinks = new SmugAttribute<Boolean>("AllowExternalLinks");
		protected final SmugAttribute<Boolean> mIsProtected = new SmugAttribute<Boolean>("IsProtected");
		protected final SmugAttribute<Boolean> mHasCustomWatermark = new SmugAttribute<Boolean>("HasCustomWatermark");
		protected final SmugAttribute<Integer> mWatermarkID = new SmugAttribute<Integer>("WatermarkID");
		protected final SmugAttribute<Boolean> mIsOwnerInfoHidden = new SmugAttribute<Boolean>("IsOwnerInfoHidden");
		protected final SmugAttribute<Boolean> mIsLargestLarge = new SmugAttribute<Boolean>("IsLargestLarge");
		protected final SmugAttribute<Boolean> mIsLargestOriginal = new SmugAttribute<Boolean>("IsLargetsOriginal");
		protected final SmugAttribute<Boolean> mIsLargestXLarge = new SmugAttribute<Boolean>("IsLargestXLarge");
		protected final SmugAttribute<Boolean> mIsLargestX2Large = new SmugAttribute<Boolean>("IsLargestX2Large");
		protected final SmugAttribute<Boolean> mIsLargestX3Large = new SmugAttribute<Boolean>("IsLargestX3Large");
		// protected final SmugAttribute<String> mLargestSize = new SmugAttribute<String>("LargestSize");
		
		/*
		 * Social: isCanRank,isFriendEdit,isFamilyEdit,isComments,isShare
		 */
		protected final SmugAttribute<Boolean> mAllowRankPhotos = new SmugAttribute<Boolean>("AllowRankPhotos");
		protected final SmugAttribute<Boolean> mAllowEditsByFriends = new SmugAttribute<Boolean>("AllowEditsByFriends");
		protected final SmugAttribute<Boolean> mAllowEditsByFamily = new SmugAttribute<Boolean>("AllowEditsByFamily");
		protected final SmugAttribute<Boolean> mAllowComments = new SmugAttribute<Boolean>("AllowComments");
		protected final SmugAttribute<Boolean> mShowEasyShareButton = new SmugAttribute<Boolean>("ShowEasyShareButton");

		/*
		 * Printing & Sales: isPrintable,isDefaultColor,getProofDays,getBackprinting
		 */
		protected final SmugAttribute<Boolean> mIsPrintable = new SmugAttribute<Boolean>("IsPrintable");
		protected final SmugAttribute<PRINT_COLOR> mPrintColor = new SmugAttribute<PRINT_COLOR>("PrintColor");
		protected final SmugAttribute<Integer> mNumberOfProofDays = new SmugAttribute<Integer>("NumberOfProofDays");
		protected final SmugAttribute<String>  mBackPrintString = new SmugAttribute<String>("BackPrintString");

		/*
		 * Photo Sharpening:isUnsharpAmount,isUnsharpRadius,isUnsharpThreshold,isUnsharpSigma
		 */
		protected final SmugAttribute<Float> mUnsharpAmount = new SmugAttribute<Float>("UnsharpAmount");
		protected final SmugAttribute<Float> mUnsharpRadius = new SmugAttribute<Float>("UnsharpRadius");
		protected final SmugAttribute<Float> mUnsharpThreshold = new SmugAttribute<Float>("UnsharpThreshold");
		protected final SmugAttribute<Float> mUnsharpSigma = new SmugAttribute<Float>("UnsharpSigma");

		/*
		 * Community: getCommunityID
		 */
		protected final SmugAttribute<Integer> mCommunityID = new SmugAttribute<Integer>("CommunityID");

		
		/**
		 * @return the Name
		 */
		public String getName() {
			return mName.get();
		}

		/**
		 * @param name the Name to set
		 */
		public void setName(String name) {
			mName.set(name);
		}

		/**
		 * @return the ShowGeography
		 */
		public Boolean getShowGeography() {
			return mShowGeography.get();
		}

		/**
		 * @param showGeography the ShowGeography to set
		 */
		protected void setShowGeography(Boolean showGeography) {
			mShowGeography.set(showGeography);
		}

		/**
		 * @return the HighlightImageID
		 */
		public Integer getHighlightImageID() {
			return mHighlightImageID.get();
		}
		
		/**
		 * @return The Highlight Image. It's the image with the ID returned by {@link #getHighlightImageID}.
		 * @throws SmugmugException
		 */
		public Image getHighlightImage() throws SmugmugException {
			if (mHighlightImageID.isUndefined() || mHighlightImageID.get()==IHasID.NO_ID)
				return null;
			
			return (new ImagePrototype(getSession())).get(mHighlightImageID.get());
		}

		/**
		 * @param ID the HighlightImageID to set
		 */
		protected void setHighlightImageID(Integer ID) {
			mHighlightImageID.set(ID);
		}

		/**
		 * @param img The Highlight Image.
		 */
		protected void setHighlightImage(Image img) {
			mHighlightImageID.set(img != null ? img.getID() : null);
		}
		
		/**
		 * @return the HasSmugmugAppearance
		 */
		public Boolean getHasCustomAppearance() {
			return mHasCustomappearance.get();
		}

		/**
		 * @param hasSmugmugAppearance the HasSmugmugAppearance to set
		 */
		public void setHasCustomAppearance(Boolean hasSmugmugAppearance) {
			mHasCustomappearance.set(hasSmugmugAppearance);
		}

		/**
		 * @return the IsClean
		 */
		public Boolean getIsClean() {
			return mIsClean.get();
		}

		/**
		 * @param isClean the IsClean to set
		 */
		public void setIsClean(Boolean isClean) {
			mIsClean.set(isClean);
		}

		/**
		 * @return the ShowExif
		 */
		public Boolean getShowExif() {
			return mShowExif.get();
		}

		/**
		 * @param showExif the ShowExif to set
		 */
		public void setShowExif(Boolean showExif) {
			mShowExif.set(showExif);
		}

		/**
		 * @return the ShowFilenames
		 */
		public Boolean getShowFilenames() {
			return mShowFilenames.get();
		}

		/**
		 * @param showFilenames the ShowFilenames to set
		 */
		public void setShowFilenames(Boolean showFilenames) {
			mShowFilenames.set(showFilenames);
		}

		/**
		 * @return the ViewStyleID
		 */
		public VIEW_STYLE getViewStyleID() {
			return mViewStyleID.get();
		}

		/**
		 * @param viewStyleID the ViewStyleID to set
		 */
		public void setViewStyleID(VIEW_STYLE viewStyleID) {
			mViewStyleID.set(viewStyleID);
		}

		/**
		 * @return the SortMethod
		 */
		public SORT_METHOD getSortMethod() {
			return mSortMethod.get();
		}

		/**
		 * @param sortMethod the SortMethod to set
		 */
		public void setSortMethod(SORT_METHOD sortMethod) {
			mSortMethod.set(sortMethod);
		}

		/**
		 * @return the SortOrder
		 */
		public SORT_ORDER getSortOrder() {
			return mSortOrder.get();
		}

		/**
		 * @param sortOrder the SortOrder to set
		 */
		public void setSortOrder(SORT_ORDER sortOrder) {
			mSortOrder.set(sortOrder);
		}

		/**
		 * @return the Password
		 */
		public String getPassword() {
			return mPassword.get();
		}

		/**
		 * @param password the Password to set
		 */
		public void setPassword(String password) {
			mPassword.set(password);
		}

		/**
		 * @return the PasswordHint
		 */
		public String getPasswordHint() {
			return mPasswordHint.get();
		}

		/**
		 * @param passwordHint the PasswordHint to set
		 */
		public void setPasswordHint(String passwordHint) {
			mPasswordHint.set(passwordHint);
		}

		/**
		 * @return the ShowOnHomepage
		 */
		public Boolean getShowOnHomepage() {
			return mShowOnHomepage.get();
		}

		/**
		 * @param showOnHomepage the ShowOnHomepage to set
		 */
		public void setShowOnHomepage(Boolean showOnHomepage) {
			mShowOnHomepage.set(showOnHomepage);
		}

		/**
		 * @return the IsWorldSearchable
		 */
		public Boolean getIsWorldSearchable() {
			return mIsWorldSearchable.get();
		}

		/**
		 * @param isWorldSearchable the IsWorldSearchable to set
		 */
		public void setIsWorldSearchable(Boolean isWorldSearchable) {
			mIsWorldSearchable.set(isWorldSearchable);
		}

		/**
		 * @return the IsSmugSearchable
		 */
		public Boolean getIsSmugSearchable() {
			return mIsSmugSearchable.get();
		}

		/**
		 * @param isSmugSearchable the IsSmugSearchable to set
		 */
		public void setIsSmugSearchable(Boolean isSmugSearchable) {
			mIsSmugSearchable.set(isSmugSearchable);
		}

		/**
		 * @return the AllowExternalLinks
		 */
		public Boolean getAllowExternalLinks() {
			return mAllowExternalLinks.get();
		}

		/**
		 * @param allowExternalLinks the AllowExternalLinks to set
		 */
		public void setAllowExternalLinks(Boolean allowExternalLinks) {
			mAllowExternalLinks.set(allowExternalLinks);
		}

		/**
		 * @return the IsProtected
		 */
		public Boolean getIsProtected() {
			return mIsProtected.get();
		}

		/**
		 * @param isProtected the IsProtected to set
		 */
		public void setIsProtected(Boolean isProtected) {
			mIsProtected.set(isProtected);
		}

		/**
		 * @return the HasCustomWatermark
		 */
		public Boolean getHasCustomWatermark() {
			return mHasCustomWatermark.get();
		}

		/**
		 * @param hasCustomWatermark the HasCustomWatermark to set
		 */
		public void setHasCustomWatermark(Boolean hasCustomWatermark) {
			mHasCustomWatermark.set(hasCustomWatermark);
		}

		/**
		 * @return the WatermarkID
		 */
		public Integer getWatermarkID() {
			return mWatermarkID.get();
		}

//		public Watermark getWatermark() throws SmugmugException {
//			if (mWatermarkID.isUndefined() || mWatermarkID.get()==IHasID.NO_ID)
//				return null;
//		
//			return (new WatermarkPrototype(getSession())).get(mWatermarkID.get());
//		}
//
		/**
		 * @param watermarkID the WatermarkID to set
		 */
		protected void setWatermarkID(Integer watermarkID) {
			mWatermarkID.set(watermarkID);
		}
		
//		public void setWatermark(Watermark watermark) {
//			mWatermarkID.set(watermark != null ? watermark.getID() : null);
//		}
//
		/**
		 * @return the IsOwnerInfoHidden
		 */
		public Boolean getIsOwnerInfoHidden() {
			return mIsOwnerInfoHidden.get();
		}

		/**
		 * @param isOwnerInfoHidden the IsOwnerInfoHidden to set
		 */
		public void setIsOwnerInfoHidden(Boolean isOwnerInfoHidden) {
			mIsOwnerInfoHidden.set(isOwnerInfoHidden);
		}

		/**
		 * If called the largest pics shown in the album are 'Medium'.
		 */
		public void setIsLargestMedium() {
			mIsLargestLarge.set(false);
			mIsLargestXLarge.set(false);
			mIsLargestX2Large.set(false);
			mIsLargestX3Large.set(false);
			mIsLargestOriginal.set(false);
		}

		/**
		 * @return the IsLargestLarge
		 */
		public Boolean getIsLargestLarge() {
			return mIsLargestLarge.get();
		}

		/**
		 * If called the largest pics shown in the album are 'Large'.
		 */
		public void setIsLargestLarge() {
			mIsLargestLarge.set(true);
			mIsLargestXLarge.set(false);
			mIsLargestX2Large.set(false);
			mIsLargestX3Large.set(false);
			mIsLargestOriginal.set(false);
		}

		/**
		 * @return the IsLargetsOriginal
		 */
		public Boolean getIsLargestOriginal() {
			return mIsLargestOriginal.get();
		}

		/**
		 * If called the largest pics shown in the album are 'Original'.
		 */
		public void setIsLargestOriginal() {
			mIsLargestLarge.set(false);
			mIsLargestXLarge.set(false);
			mIsLargestX2Large.set(false);
			mIsLargestX3Large.set(false);
			mIsLargestOriginal.set(true);
		}

		/**
		 * @return the IsLargestxLarge
		 */
		public Boolean getIsLargestXLarge() {
			return mIsLargestXLarge.get();
		}

		/**
		 * If called the largest pics shown in the album are 'XLarge'.
		 */
		public void setIsLargestXLarge() {
			mIsLargestLarge.set(false);
			mIsLargestXLarge.set(true);
			mIsLargestX2Large.set(false);
			mIsLargestX3Large.set(false);
			mIsLargestOriginal.set(false);
		}

		/**
		 * @return the IsLargestX2Large
		 */
		public Boolean getIsLargestX2Large() {
			return mIsLargestX2Large.get();
		}

		/**
		 * If called the largest pics shown in the album are 'X2Large'.
		 */
		public void setIsLargestX2Large() {
			mIsLargestLarge.set(false);
			mIsLargestXLarge.set(false);
			mIsLargestX2Large.set(true);
			mIsLargestX3Large.set(false);
			mIsLargestOriginal.set(false);
		}

		/**
		 * @return the IsLargestX3Large
		 */
		public Boolean getIsLargestX3Large() {
			return mIsLargestX3Large.get();
		}

		/**
		 * If called the largest pics shown in the album are 'X3Large'.
		 */
		public void setIsLargestX3Large() {
			mIsLargestLarge.set(false);
			mIsLargestXLarge.set(false);
			mIsLargestX2Large.set(false);
			mIsLargestX3Large.set(true);
			mIsLargestOriginal.set(false);
		}

		/**
		 * Images can be limited by size in which they are shown in this gallery/album on smugmug to prevent theft.
		 * The largest allowed size by this album is returned by this method.
		 * @return The largest allowed size.
		 */
		public Image.IMAGE_SIZE getLargestImageSize() {
			if (mIsLargestLarge.isUndefined())
				return null;
			
			if (getIsLargestOriginal()) {
				return Image.IMAGE_SIZE.Original;
			}
			else if (getIsLargestX3Large()) {
				return Image.IMAGE_SIZE.X3Large;
			}
			else if (getIsLargestX2Large()) {
				return Image.IMAGE_SIZE.X2Large;
			}
			else if (getIsLargestXLarge()) {
				return Image.IMAGE_SIZE.XLarge;
			}
			else if (getIsLargestLarge()) {
				return Image.IMAGE_SIZE.Large;
			}
			else
				return Image.IMAGE_SIZE.Medium;
		}

		/**
		 * This is an alternative to calling one of the setIsLargest[xxxx]() methods.
		 * This specifies the largest image-size that can be shown by this album (or
		 * the album created from this album-template).
		 * @param maxSize
		 */
		public void setLargestImageSize(Image.IMAGE_SIZE maxSize) {
			switch(maxSize) {
			case Large:
				setIsLargestLarge();
				break;
			case XLarge:
				setIsLargestXLarge();
				break;
			case X2Large:
				setIsLargestX2Large();
				break;
			case X3Large:
				setIsLargestX3Large();
				break;
			case Original:
				setIsLargestOriginal();
				break;
			default:
				setIsLargestMedium();
				break;
			}
		}
		
		/**
		 * @return the AllowRankPhotos
		 */
		public Boolean getAllowRankPhotos() {
			return mAllowRankPhotos.get();
		}

		/**
		 * @param allowRankPhotos the AllowRankPhotos to set
		 */
		protected void setAllowRankPhotos(Boolean allowRankPhotos) {
			mAllowRankPhotos.set(allowRankPhotos);
		}

		/**
		 * @return the AllowEditsByFriends
		 */
		public Boolean getAllowEditsByFriends() {
			return mAllowEditsByFriends.get();
		}

		/**
		 * @param allowEditsByFriends the AllowEditsByFriends to set
		 */
		public void setAllowEditsByFriends(Boolean allowEditsByFriends) {
			mAllowEditsByFriends.set(allowEditsByFriends);
		}

		/**
		 * @return the AllowEditsByFamily
		 */
		public Boolean getAllowEditsByFamily() {
			return mAllowEditsByFamily.get();
		}

		/**
		 * @param allowEditsByFamily the AllowEditsByFamily to set
		 */
		public void setAllowEditsByFamily(Boolean allowEditsByFamily) {
			mAllowEditsByFamily.set(allowEditsByFamily);
		}

		/**
		 * @return the AllowComments
		 */
		public Boolean getAllowComments() {
			return mAllowComments.get();
		}

		/**
		 * @param allowComments the AllowComments to set
		 */
		public void setAllowComments(Boolean allowComments) {
			mAllowComments.set(allowComments);
		}

		/**
		 * @return the ShowEasyShareButton
		 */
		public Boolean getShowEasyShareButton() {
			return mShowEasyShareButton.get();
		}

		/**
		 * @param showEasyShareButton the ShowEasyShareButton to set
		 */
		public void setShowEasyShareButton(Boolean showEasyShareButton) {
			mShowEasyShareButton.set(showEasyShareButton);
		}

		/**
		 * @return the IsPrintable
		 */
		public Boolean getIsPrintable() {
			return mIsPrintable.get();
		}

		/**
		 * @param isPrintable the IsPrintable to set
		 */
		public void setIsPrintable(Boolean isPrintable) {
			mIsPrintable.set(isPrintable);
		}

		/**
		 * @return the IsPrintedInDefaultColor
		 */
		public PRINT_COLOR getPrintColor() {
			return mPrintColor.get();
		}

		/**
		 * @param isPrintedInDefaultColor the IsPrintedInDefaultColor to set
		 */
		public void setPrintColor(PRINT_COLOR isPrintedInDefaultColor) {
			mPrintColor.set(isPrintedInDefaultColor);
		}

		/**
		 * @return the NumberOfProofDays
		 */
		public Integer getNumberOfProofDays() {
			return mNumberOfProofDays.get();
		}

		/**
		 * @param numberOfProofDays the NumberOfProofDays to set
		 */
		public void setNumberOfProofDays(Integer numberOfProofDays) {
			mNumberOfProofDays.set(numberOfProofDays);
		}

		/**
		 * @return the BackPrintString
		 */
		public String getBackPrintString() {
			return mBackPrintString.get();
		}

		/**
		 * @param backPrintString the BackPrintString to set
		 */
		public void setBackPrintString(String backPrintString) {
			mBackPrintString.set(backPrintString);
		}

		/**
		 * @return the UnsharpAmount
		 */
		public Float getUnsharpAmount() {
			return mUnsharpAmount.get();
		}
		
		/**
		 * @param unsharpAmount the UnsharpAmount to set
		 */
		public void setUnsharpAmount(Float unsharpAmount) {
			mUnsharpAmount.set(unsharpAmount);
		}

		/**
		 * @return the UnsharpRadius
		 */
		public Float getUnsharpRadius() {
			return mUnsharpRadius.get();
		}
		
		/**
		 * @param unsharpRadius the UnsharpRadius to set
		 */
		public void setUnsharpRadius(Float unsharpRadius) {
			mUnsharpRadius.set(unsharpRadius);
		}

		/**
		 * @return the UnsharpThreshold
		 */
		public Float getUnsharpThreshold() {
			return mUnsharpThreshold.get();
		}
		
		/**
		 * @param unsharpThreshold the UnsharpThreshold to set
		 */
		public void setUnsharpThreshold(Float unsharpThreshold) {
			mUnsharpThreshold.set(unsharpThreshold);
		}

		/**
		 * @return the UnsharpSigma
		 */
		public Float getUnsharpSigma() {
			return mUnsharpSigma.get();
		}
		
		/**
		 * @param unsharpSigma the UnsharpSigma to set
		 */
		public void setUnsharpSigma(Float unsharpSigma) {
			mUnsharpSigma.set(unsharpSigma);
		}
		
		/**
		 * @return the CommunityID
		 */
		public Integer getCommunityID() {
			return mCommunityID.get();
		}

//	public Community getCommunity() throws SmugmugException {
//		if (mCommunityID.isUndefined() || mCommunityID.get()==IHasID.NO_ID)
//			return null;
//	
//		return (new CommunityPrototype(getSession())).get(mCommunityID.get());
//	}
//
		/**
		 * @param communityID the CommunityID to set
		 */
		public void setCommunityID(Integer communityID) {
			mCommunityID.set(communityID);
		}
		
//		public void setCommunity(Community community) {
//			mCommunityID.set(community != null ? community.getID() : null);
//		}
//
	}
}
