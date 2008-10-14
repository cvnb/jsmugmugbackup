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

import static com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData.toBoolean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.users.GetTree;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.Account;
import com.streetsofboston.smugmug.v1_2_1.system.Session;
import com.streetsofboston.smugmug.v1_2_1.system.SmugObjectData;

/**
 * Don't user this class directly. Instead, use the Account class to
 * access this class' methods.
 * @author Anton Spaans
 *
 */
public class User extends Account {

	public User(Account account) {
		super(account.getSession(), account.getNickName(), account.getSitePassword(), account.getID());
	}
	
	@SuppressWarnings("unchecked")
	public Collection<? extends SmugObjectData<?>>[] getSmugObjects() throws SmugmugException {
//		public static final String[] ARGUMENTS = { "APIKey", "SessionID",
//			"NickName", "Heavy", "SitePassword" };
		final Session session = getSession();
		GetTree.GetTreeResponse resp = (new GetTree()).execute(
				APIVersionConstants.SECURE_SERVER_URL,
				new String[] {
					session.getAPIKey(), session.getSessionID(),
					getNickName(), toBoolean(false), getSitePassword()
				});
		
		SmugmugException.check(resp);

		Collection<Category>    catList   = new ArrayList<Category>();
		Collection<SubCategory> scatList  = new ArrayList<SubCategory>();
		Collection<Album>       albumList = new ArrayList<Album>();
		

		List<com.kallasoft.smugmug.api.json.entity.Category> cats = resp.getCategoryList();
		for (com.kallasoft.smugmug.api.json.entity.Category cat : cats) {
			List<com.kallasoft.smugmug.api.json.entity.Category> subcats = cat.getSubCategoryList();

			final Category myCat = new Category(session, cat.getID(), cat.getName());
			myCat.sync();
			catList.add(myCat);
			
			for (com.kallasoft.smugmug.api.json.entity.Category subcat : subcats) {
				List<com.kallasoft.smugmug.api.json.entity.Album> albums = subcat.getAlbumList();

				final SubCategory mySCat = new SubCategory(session, subcat.getID(), myCat.getID(), subcat.getName());
				mySCat.sync();
				scatList.add(mySCat);

				for (com.kallasoft.smugmug.api.json.entity.Album album : albums) {
					final Album myAlbum = new Album(session, album.getID(), myCat.getID(), album.getTitle());
					myAlbum.setSubCategory(mySCat);
					myAlbum.sync();
					albumList.add(myAlbum);
				}
			}

			List<com.kallasoft.smugmug.api.json.entity.Album>    albums  = cat.getAlbumList();
			for (com.kallasoft.smugmug.api.json.entity.Album album : albums) {
				final Album myAlbum = new Album(session, album.getID(), myCat.getID(), album.getTitle());
				myAlbum.sync();
				albumList.add(myAlbum);
			}
		}
	
		final Collection<? extends SmugObjectData<?>>[] ret = new Collection[3];
		ret[0] = catList;
		ret[1] = scatList;
		ret[2] = albumList;
		return ret;
	}
}
