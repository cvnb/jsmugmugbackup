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

import static com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData.toKeywordsStringForAlbum;
import static com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData.toValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Create;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get;
import com.streetsofboston.smugmug.v1_2_1.exceptions.ObjectDoesNotExistException;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.IHasID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugAttribute;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Use this class to create instances of Album or to retrieve them from the
 * Smugmug repository. For example:<br/>
 * <code>
 * AlbumPrototype apt = new AlbumPrototype(session);<br/>
 * Data&lt;Album> data = apt.data();<br/>
 * data.setCategory(someCategory);<br/>
 * data.setName("Album title");<br/>
 * data.setDescription("Test Album");<br/>
 * data.setAllowComments(false);<br/>
 * data.setViewStyleID(VIEW_STYLE.Traditional);<br/>
 * data.setUnsharpAmount(0.40f);<br/>
 * Album alb = apt.create();<br/>
 * </code>
 * @author Anton Spaans
 */
public class AlbumPrototype 
			extends PrototypeHelper<Album> 
			implements ISmugObjectPrototype<Album> {

	private static final String CACHE = "Album";

	private final Data<Album> mData; 

	/**
	 * Creates a new instance of an Album Prototype.
	 * 
	 * @param session Session-account information.
	 */
	public AlbumPrototype(Session session) {
		mData = new Data<Album>(session);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#create()
	 */
	public Album create() throws SmugmugException {
		if (mData.mName.isUndefined()) 
			throw new SmugmugException("The Name is not set for "+mData);

		if (mData.mCategoryID.isUndefined() && mData.mSubCategoryID.isUndefined()) 
			throw new SmugmugException("The CategoryID and SubCategoryID are not set for "+this);

		Create.CreateResponse resp = new Create().execute(
				APIVersionConstants.SECURE_SERVER_URL,

					mData.getSession().getAPIKey(), mData.getSession().getSessionID(),
					
					mData.getName(), mData.getDescription(), toKeywordsStringForAlbum(mData.getKeywords()), 
					GUID.getGUIDInteger(mData.getCategoryID()), GUID.getGUIDInteger(mData.getSubCategoryID()), mData.getShowGeography(),
					mData.getAlbumTemplateID(), mData.getShowExif(), mData.getIsClean(),
					mData.getHasCustomAppearance(), mData.getShowFilenames(), toValue(mData.getViewStyleID()),
					toValue(mData.getSortMethod()), toValue(mData.getSortOrder()), mData.getPosition(),
					mData.getSquareThunbs(), //cadam
					mData.getPassword(),mData.getPasswordHint(), mData.getIsProtected(),
					mData.getShowOnHomepage(), mData.getIsOwnerInfoHidden(), mData.getAllowExternalLinks(), 
					mData.getIsSmugSearchable(), mData.getIsWorldSearchable(), mData.getIsLargestLarge(),
					mData.getIsLargestXLarge(), mData.getIsLargestX2Large(), mData.getIsLargestX3Large(),
					mData.getIsLargestOriginal(), mData.getHasCustomWatermark(), mData.getWatermarkID(),
					mData.getShowEasyShareButton(), mData.getAllowRankPhotos(), mData.getAllowComments(),
					mData.getAllowEditsByFamily(), mData.getAllowEditsByFriends(), mData.getCommunityID(),  
					mData.getIsPrintable(), mData.getNumberOfProofDays(), mData.getBackPrintString(), 
					toValue(mData.getPrintColor()), mData.getUnsharpAmount(), mData.getUnsharpRadius(),
					mData.getUnsharpThreshold(), mData.getUnsharpSigma());

				// Attributes valid for Create (but not for Update):

				// Attributes valid for Update (but not for Create):
				//		mData.getHighlightImageID();
		
				// Read-only attributes, not used in Create nor Update:
				//		mData.getImageCount();
				//		mData.getLastUpdated();

				// But what about these?
				//		mData.getThemeID();

		SmugmugException.check(resp);
		
		final Album ret = new Album(mData.getSession(), resp.getAlbumID(), mData.getCategoryID() != null ? mData.getCategoryID().getIntID() : null, mData.getName());
		ret.set(mData);

		return postCreate(ret);
	}

	/** @see com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper#get(int) */
	public Album get(int ID) throws SmugmugException {
		if (ID == IHasID.NO_ID) 
			throw new SmugmugException("The ID is not specified.");
		
		Album album;
		// First see if image is already in the cache.
		album = data().findInCache(ID);
		if (album != null)
			return album;
		
		// Try to find it in the smugmug repository.
		album = new Album(mData.getSession(), ID);
		try {
			album.getInfo();
		} catch (ObjectDoesNotExistException odnee) {
			return null;
		}
		postGetAll(album);
		return album;
	}
		
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#data()
	 */
	public Data<Album> data() {
		return mData;
	}

	public Class<Album> getClassForPrototype() {
		return Album.class;
	}
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll()
	 */
	public Collection<Album> getAll() throws SmugmugException {
		Get.GetResponse resp = new Get().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				mData.getSession().getAPIKey(), mData.getSession().getSessionID(), 
				mData.getSession().getOtherAccount().getNickName(), false, 
				mData.getSession().getOtherAccount().getSitePassword());

		SmugmugException.check(resp);

		Collection<com.kallasoft.smugmug.api.json.entity.Album> list = resp.getAlbumList();
		Collection<Album> retList = new ArrayList<Album>(list.size());
		for (com.kallasoft.smugmug.api.json.entity.Album album : list) {
			final com.kallasoft.smugmug.api.json.entity.Category smugCat = album.getCategory();
			final Album myAlbum = new Album(mData.getSession(), album.getID(), smugCat.getID(), album.getTitle());
			myAlbum.setSubCategoryID(album.getSubCategory()!=null? GUID.create(SubCategory.class, album.getSubCategory().getID()):null);
			myAlbum.guid().setKey(album.getAlbumKey());
			myAlbum.sync();
			
			retList.add(myAlbum);
		}

		return postGetAll(retList, true, GUID.NO_GUID);
	}

	/**
	 * This is the same as {@link #getAll()}
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObjectPrototype#getAll(java.lang.String)
	 */
	public Collection<Album> getAll(GUID notUsed) throws SmugmugException {
		return getAll();
	}
	
	/**
	 * Returns all the albums from the cache that have their categoryID and or subcategoryID set.
	 * @param categoryID
	 * @param subCategoryID
	 * @return The collection of albums.
	 * @throws SmugmugException
	 */
	public Collection<Album> getAllFromCache(Integer categoryID, Integer subCategoryID) throws SmugmugException {
		GUID parentId = null;
		if (subCategoryID != null && subCategoryID!=IHasID.NO_ID)
			parentId = GUID.create(SubCategory.class, subCategoryID);
		else if (categoryID != null && categoryID!=IHasID.NO_ID)
			parentId = GUID.create(Category.class, subCategoryID);
		
		if (parentId != null)
			return getAllFromCache(parentId);
		else
			return new ArrayList<Album>();
	}

	/** 
	 * Instances of this class hold the prototype-data for creation of Album-s.<br/>
	 * This class also functions as a base-class for Album-s.
	 * @param <T> Album
	 */
	public static class Data<T extends SmugObjectData<?>> extends AlbumTemplatePrototype.Data<T> {
		static final long serialVersionUID = 5686450756585820085L;

		protected Data(Session session) {
			super(session);
			bundle();
		}
		


		protected Data(Session session, int ID, Integer categoryID, String name) {
			super(session, ID, name);
			mCategoryID.set(GUID.create(Category.class,categoryID));
			bundle();
		}

		protected Data(Category category, int ID, String name) {
			this(category.getSession(), ID, category.getID(), name);
		}
	 
		protected String getCacheName() {
			return CACHE;
		}
		
		/* Attribute list */
		/*
		 * getAlbumTemplateID,getPosition,getImageCount,getLastUpdated
		 */
		protected final SmugAttribute<Integer> mAlbumTemplateID = new SmugAttribute<Integer>("AlbumTemplateID");
		protected final SmugAttribute<Integer> mPosition = new SmugAttribute<Integer>("Position");
		protected final SmugAttribute<Integer> mImageCount = new SmugAttribute<Integer>("ImageCount");
		protected final SmugAttribute<Date>    mLastUpdated = new SmugAttribute<Date>("LastUpdated");
		protected final SmugAttribute<String> mCategoryName = new SmugAttribute<String>("CategoryName");
		protected final SmugAttribute<String> mSubCategoryName = new SmugAttribute<String>("SubCategoryName");

		/*
		 * Essentials: id (part of superclass),getAlbumTemplateName
		 * 
		 */
		protected final SmugAttribute<GUID> mCategoryID = new SmugAttribute<GUID>("CategoryID");
		
		/*
		 * Extras: isGeography 
		 *  
		 */
		protected final SmugAttribute<GUID> mSubCategoryID = new SmugAttribute<GUID>("SubCategoryID");
		protected final SmugAttribute<String> mDescription = new SmugAttribute<String>("Description");
		protected final SmugAttribute<ArrayList<String>> mKeywords = new SmugAttribute<ArrayList<String>>("Keywords");

		/*
		 * Look & Feel: isHeader,isClean,isExif,isFilenames,getTemplateID,getSortMethod,isSortDirection
		 * 
		 */
		protected final SmugAttribute<Integer> mThemeID = new SmugAttribute<Integer>("ThemeID");

		/*
		 * Security & Privacy:getPassword,getPasswordHint,isPublic,isWorldSearchable
		 * isSmugSearchable,isExternal,isProtected,isWatermarking,getWatermarkID,
		 * isHideOwner,isLarges,isOriginals
		 */
		
		/*
		 * Social: isCanRank,isFriendEdit,isFamilyEdit,isComments,isShare
		 */

		/*
		 * Printing & Sales: isPrintable,isDefaultColor,getProofDays,getBackprinting
		 */

		/*
		 * Photo Sharpening:
		 */
		/*
		 * Community: getCommunityID
		 */

		/**
		 * @return the AlbumTemplateID
		 */
		public Integer getAlbumTemplateID() {
			return mAlbumTemplateID.get();
		}

		/**
		 * @param albumTemplateID the AlbumTemplateID to set
		 */
		public void setAlbumTemplateID(Integer albumTemplateID) {
			mAlbumTemplateID.set(albumTemplateID);
		}

		/**
		 * @return The AlbumTemplate used to create an Album.
		 * @throws SmugmugException
		 */
		public AlbumTemplate getAlbumTemplate() throws SmugmugException {
			if (mAlbumTemplateID.isUndefined() || mAlbumTemplateID.get()==IHasID.NO_ID)
				return null;
			
			AlbumTemplatePrototype atFactory = new AlbumTemplatePrototype(getSession());
			return atFactory.get(getAlbumTemplateID());
		}

		/**
		 * @param albumTemplate The AlbumTemplate used to create an Album.
		 */
		public void setAlbumTemplate(AlbumTemplate albumTemplate) {
			setAlbumTemplateID(albumTemplate != null ? albumTemplate.getID() : null);
		}
		
		/**
		 * @return the Position
		 */
		public Integer getPosition() {
			return mPosition.get();
		}

		/**
		 * @param position the Position to set
		 */
		public void setPosition(Integer position) {
			mPosition.set(position);
		}

		/**
		 * @return the ImageCount
		 */
		public Integer getImageCount() {
			return mImageCount.get();
		}
//		protected void setImageCount(Integer imageCount) {
//			mImageCount.assign(imageCount);
//		}

		/**
		 * @return the LastUpdated
		 */
		public Date getLastUpdated() {
			return mLastUpdated.get();
		}
//		protected void setLastUpdated(Date lastUpdated) {
//			mLastUpdated.assign(lastUpdated);
//		}

		/**
		 * @return the CategoryName
		 */
		public String getCategoryName() {
			return mCategoryName.get();
		}

		/**
		 * @return the SubCategoryName
		 */
		public String getSubCategoryName() {
			return mSubCategoryName.get();
		}

		/**
		 * @return the CategoryID
		 */
		public GUID getCategoryID() {
			return mCategoryID.get();
		}

		/**
		 * @param categoryID the CategoryID to set
		 */
		public void setCategoryID(GUID categoryID) {
			GUID curGUID = mCategoryID.get();
			if (curGUID != null && curGUID.equals(categoryID))
				return;
			
			mCategoryID.set(categoryID);
		}

		/**
		 * @return The Category to which this Album belongs.
		 * @throws SmugmugException
		 */
		public Category getCategory() throws SmugmugException {
			if (mCategoryID.isUndefined() || !mCategoryID.get().hasGUID())
				return null;
			
			CategoryPrototype catFactory = new CategoryPrototype(getSession());
			return catFactory.get(getCategoryID());
		}

		/**
		 * @param category The Category to which this Album belongs.
		 */
		public void setCategory(Category category) {
			setCategoryID(category != null ? category.guid() : null);
		}
		
		/**
		 * @return the SubCategoryID
		 */
		public GUID getSubCategoryID() {
			return mSubCategoryID.get();
		}

		/**
		 * @param subCategoryID the SubCategoryID to set
		 */
		public void setSubCategoryID(GUID subCategoryID) {
			GUID curGUID = mSubCategoryID.get();
			if (curGUID != null && curGUID.equals(subCategoryID))
				return;

			mSubCategoryID.set(subCategoryID);
		}

		/**
		 * @return The SubCategory to which this Album belongs.
		 * @throws SmugmugException
		 */
		public SubCategory getSubCategory() throws SmugmugException {
			if (mSubCategoryID.isUndefined() || !mSubCategoryID.get().hasGUID())
				return null;
			
			SubCategoryPrototype scatFactory = new SubCategoryPrototype(getSession());
			return scatFactory.get(getSubCategoryID());
		}

		/**
		 * @param scategory The SubCategory to which this Album belongs.
		 */
		public void setSubCategory(SubCategory scategory) {
			setSubCategoryID(scategory != null ? scategory.guid() : null);
		}
		
		/**
		 * @return the Description
		 */
		public String getDescription() {
			return mDescription.get();
		}

		/**
		 * @param description the Description to set
		 */
		public void setDescription(String description) {
			mDescription.set(description);
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
		 * @return the ThemeID
		 */
		public Integer getThemeID() {
			return mThemeID.get();
		}

		/**
		 * @param themeID the ThemeID to set
		 */
		protected void setThemeID(Integer themeID) {
			mThemeID.set(themeID);
		}

//		public Theme getTheme() throws SmugmugException {
//			if (mThemeID.isUndefined() || mThemeID.get()==IHasID.NO_ID)
//				return null;
//			
//			ThemePrototype theme = new ThemePrototype(getSession());
//			return theme.get(getThemeID());
//		}
//
//		public void setTheme(Theme theme) {
//			setAlbumTemplateID(theme != null ? theme.getID() : null);
//		}
		
		//cadam - Quickfix
		public Boolean getSquareThunbs() {
			// TODO Auto-generated method stub
			//return null;
			return false;
		}
	}		
}
