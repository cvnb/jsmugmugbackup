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

import java.util.List;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.ChangeSettings;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Delete;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.GetInfo;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.GetStats;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.ReSort;
import com.streetsofboston.smugmug.v1_2_1.exceptions.ObjectDoesNotExistException;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.Account;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.Stats;

/**
 * Instances of this class represent Albums/Galleries on Smugmug. 
 * @author Anton Spaans
 */
public class Album 
	extends AlbumPrototype.Data<Album> 
	implements ISmugObject<Album> {

	static final long serialVersionUID = 6313179311514919843L;

	private Stats<Album> mStats;
	
	protected Album(Session session, int ID) {
		super(session, ID, 0, null);
	}
	
	protected Album(Session session, int ID, Integer categoryID, String name) {
		super(session, ID, categoryID, name);
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
	public Album getInfo() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);
			
		GetInfo.GetInfoResponse resp1 = new GetInfo().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID(), guid().getKey(), 
				getObjectPassword(), getSession().getOtherAccount().getSitePassword());

		try {
			SmugmugException.check(resp1, this);
		}
		catch (SmugmugException se) {
			if (se.getCode()==5 && se.getMessage().contains("invalid"))
				throw new ObjectDoesNotExistException(getID(), Album.class);
		}
		if (resp1.getAlbum().getID()!=getID())
			throw new ObjectDoesNotExistException(getID(), Album.class);
		
		com.kallasoft.smugmug.api.json.entity.Album resp = resp1.getAlbum();
		lock();
		try {
			clear();

			this.setName(resp.getTitle());
			this.setCategoryID(GUID.create(Category.class, resp.getCategory().getID()));
			this.setSubCategoryID(GUID.create(SubCategory.class, resp.getSubCategory().getID()));

			this.setBackPrintString(resp.getBackprinting());
			this.setCommunityID(resp.getCommunityID());
			this.setDescription(resp.getDescription());
			this.setHighlightImageID(resp.getHighlight().getID());
			this.mImageCount.set(resp.getImageCount());
			this.setKeywords(toKeywords(resp.getKeywords()));
			this.mLastUpdated.set(strToDate(resp.getLastUpdated(), true));
			this.setPassword(resp.getPassword());
			this.setPasswordHint(resp.getPasswordHint());
			this.setPosition(resp.getPosition());
			this.setNumberOfProofDays(resp.getProofDays());
			this.setSortMethod(resp.getSortMethod()!=null?SORT_METHOD.valueOf(resp.getSortMethod()):null);
			this.setViewStyleID(VIEW_STYLE.get(resp.getTemplateID()));
			this.setWatermarkID(resp.getWatermarkID());
			this.setAllowRankPhotos(resp.canRank());
			this.setIsClean(resp.isClean());
			this.setAllowComments(resp.isComments());
			this.setPrintColor(PRINT_COLOR.get(resp.isDefaultColor()));
			this.setShowExif(resp.isExif());
			this.setAllowExternalLinks(resp.isExternal());
			this.setAllowEditsByFamily(resp.isFamilyEdit());
			this.setShowFilenames(resp.isFilenames());
			this.setAllowEditsByFriends(resp.isFriendEdit());
			this.setShowGeography(resp.isGeography());
			this.setHasCustomAppearance(resp.isHeader());
			this.setIsOwnerInfoHidden(resp.isHideOwner());
			this.mIsLargestLarge.set(resp.isLarges());
			this.mIsLargestOriginal.set(resp.isOriginals());
			this.setIsPrintable(resp.isPrintable());
			this.setIsProtected(resp.isProtected());
			this.setShowOnHomepage(resp.isPublic());
			this.setShowEasyShareButton(resp.isShare());
			this.setIsSmugSearchable(resp.isSmugSearchable());
			this.setSortOrder(SORT_ORDER.get(resp.getSortDirection()));
			this.setHasCustomWatermark(resp.isWatermarking());
			this.setIsWorldSearchable(resp.isWorldSearchable());

			this.mIsLargestXLarge.set(resp.isXLarges());
			this.mIsLargestX2Large.set(resp.isX2Larges());
			this.mIsLargestX3Large.set(resp.isX3Larges());
			this.setUnsharpAmount(resp.getUnsharpAmount());
			this.setUnsharpRadius(resp.getUnsharpRadius());
			this.setUnsharpSigma(resp.getUnsharpSigma());
			this.setUnsharpThreshold(resp.getUnsharpThreshold());
			
			setLargestImageSize(getLargestImageSize());
			return PrototypeHelper.postUpdate(this);
		} 
		finally {
			unlock();
		}
	}

	/** 
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#update()
	 */
	public Album update() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);

		Album mData = null;
		lock();
		try {
			if (mCategoryID.isUndefined() && mSubCategoryID.isUndefined()) 
				throw new SmugmugException("The CategoryID and SubCategoryID are not set for "+this);
			if (mName.isUndefined() || mName.get().length() == 0)
				throw new SmugmugException("The Name is not set for " + this);
			if (!hasChanged())
				return this;
			
			setReturnNullIfNotChanged(true);
			mData = copy();
		} finally {
			setReturnNullIfNotChanged(false);
			unlock();
		}
		
		ChangeSettings.ChangeSettingsResponse resp = new ChangeSettings().execute(
				APIVersionConstants.SECURE_SERVER_URL, getSession().getAPIKey(), getSession().getSessionID(),
				mData.getID(),
				mData.getName(), mData.getDescription(), toKeywordsStringForAlbum(mData.getKeywords()), 
				GUID.getGUIDInteger(mData.getCategoryID()), GUID.getGUIDInteger(mData.getSubCategoryID()), mData.getShowGeography(),
				mData.getAlbumTemplateID(), mData.getShowExif(), mData.getIsClean(),
				mData.getHasCustomAppearance(), mData.getShowFilenames(), toValue(mData.getViewStyleID()),
				toValue(mData.getSortMethod()), toValue(mData.getSortOrder()), mData.getPosition(), mData.getHighlightImageID(),
				mData.getSquareThunbs(), // by cadam
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

		// GetInfo return all above attrs and the one below:

		// But what about these?
		//		mData.getThemeID();

		SmugmugException.check(resp, this);
		
		return PrototypeHelper.postUpdate(this);
	}
	
	/**
	 * If the album's sort method is set to Position, the image-positions can be re-ordered 
	 * using this method.
	 * 
	 * @param method Caption, FileName or DateTime.
	 * @param order Asc or Desc
	 * @return True if re-ordering was successful.
	 * @throws SmugmugException
	 */
	public boolean repositionImages(SORT_METHOD method, SORT_ORDER order) throws SmugmugException {
		final String smMethod, smOrder;

		lock();
		try {
			smMethod = method.toString();
			smOrder = order.toString().toUpperCase();
		} finally {
			unlock();
		}
		ReSort.ReSortResponse resp = new ReSort().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] { 
				getSession().getAPIKey(), getSession().getSessionID(), 
				toNumber(getID()), 
				smMethod, smOrder });

		SmugmugException.check(resp, this);

		return true;
	}
	
	/**
	 * Returns the album statistics on Smugmug.
	 * @param year Year of statistics
	 * @param month Month of statistics.
	 * @param getImageStatsAsWell If true, return the stats for all album's images as well.
	 * @return The statistics for this album.
	 * @throws SmugmugException 
	 */
	public Stats<Album> getStatistics(int year, int month, boolean getImageStatsAsWell) throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);

		GetStats.GetStatsResponse resp = new GetStats().execute(
				APIVersionConstants.SECURE_SERVER_URL,
				getSession().getAPIKey(), getSession().getSessionID(), 
				getID(),
				month, year,
				getImageStatsAsWell);

		SmugmugException.check(resp, this);
		
		final List<com.kallasoft.smugmug.api.json.entity.ImageTransferStats> imgList = resp.getAlbumTransferStats().getImageTransferStatsList();
		if (imgList != null) {
			final ImagePrototype pt = new ImagePrototype(getSession());
			pt.data().setAlbumID(guid());

			for (com.kallasoft.smugmug.api.json.entity.ImageTransferStats kImage : imgList) {
				try {
					final Image image        = pt.get(kImage.getID());
					final Stats<Image> stats = new Stats<Image>(image, year, month,
							kImage.getBytes(),
							kImage.getTiny(),
							kImage.getThumb(),
							kImage.getSmall(),
							kImage.getMedium(),
							kImage.getLarge(),
							kImage.getXLarge(), kImage.getX2Large(), kImage.getX3Large(),
							kImage.getOriginal(),
							kImage.getVideo320(), kImage.getVideo640(),
							kImage.getVideo960(), kImage.getVideo1280());
					
					
					image.setStatistics(stats);
				}
				catch (SmugmugException se) {
					continue;
				}
			}
		}
		
		mStats = new Stats<Album>(this, year, month, 
				resp.getAlbumTransferStats().getBytes(),
				resp.getAlbumTransferStats().getTiny(),
				resp.getAlbumTransferStats().getThumb(),
				resp.getAlbumTransferStats().getSmall(),
				resp.getAlbumTransferStats().getMedium(),
				resp.getAlbumTransferStats().getLarge(),
				resp.getAlbumTransferStats().getXLarge(), resp.getAlbumTransferStats().getX2Large(), resp.getAlbumTransferStats().getX3Large(),
				resp.getAlbumTransferStats().getOriginal(),
				resp.getAlbumTransferStats().getVideo320(), resp.getAlbumTransferStats().getVideo640(),
				resp.getAlbumTransferStats().getVideo960(), resp.getAlbumTransferStats().getVideo1280());
		
		return mStats;
	}
	
	/**
	 * Get the statistics that were obtained from Smugmug most recently.
	 * Return null if no stats were obtained yet. Call getStatistics(month) to obtain them.
	 * @return The image's stats.
	 */
	public Stats<Album> getStatistics() {
		return mStats;
	}
	
	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.AlbumPrototype.Data#getAlbumTemplateID()
	 * Always returns null.
	 */
	public Integer getAlbumTemplateID() {
		return null;
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.AlbumPrototype.Data#setAlbumTemplateID(java.lang.Integer)
	 * Does nothing.
	 */
	public void setAlbumTemplateID(Integer albumTemplateID) {
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.AlbumPrototype.Data#getAlbumTemplate()
	 * Always returns null.
	 */
	public AlbumTemplate getAlbumTemplate() throws SmugmugException {
		return null;
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.AlbumPrototype.Data#setAlbumTemplate(com.streetsofboston.smugmug.v1_2_1.AlbumTemplate)
	 * Does nothing.
	 */
	public void setAlbumTemplate(AlbumTemplate albumTemplate) {
	}

	protected String figureOutOwnersNickName() throws SmugmugException {
		final AlbumPrototype apt = new AlbumPrototype(getSession());
		apt.getAll();

		return mOwnerNickName!=null && mOwnerNickName.length()>0 ? mOwnerNickName : Account.UNKNOWN_NICKNAME;
	}

	/**
	 * @return The ID of the parent.
	 */
	public GUID getParentId() {
		if (mSubCategoryID.isUndefined() && mCategoryID.isUndefined())
			return null;
		
		if (!mSubCategoryID.isUndefined())
			return mSubCategoryID.get();
		else if (!mCategoryID.isUndefined())
			return mCategoryID.get();
		else
			return null;
	}
	
	private Album copy() {
		final Album tempAlbum = new Album(getSession(), getID());
		tempAlbum.assign(this);
		return tempAlbum;
	}
}
