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

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;
import com.streetsofboston.smugmug.v1_2_1.system.Session;

/**
 * Instances of this class represent Album-templates on Smugmug. 
 * @author Anton Spaans
 */
public class AlbumTemplate 
	extends AlbumTemplatePrototype.Data<AlbumTemplate> 
	implements ISmugObject<AlbumTemplate> {

	static final long serialVersionUID = -4687975765910574222L;

	protected AlbumTemplate(Session session, int ID, String title) {
		super(session, ID, title);
		bundle();
	}

//	protected AlbumTemplate(Session session, int categoryID, String title) {
//		super(session, title);
//		mCategoryID.set(categoryID);
//		bundle();
//	}

//	protected AlbumTemplate(Session session, int ID, int categoryID, String title) {
//		super(session, ID, title);
//		mCategoryID.set(categoryID);
//		bundle();
//	}

//	protected AlbumTemplate(Category category, String title) {
//		this(category.getSession(), category.getID(), title);
//	}
	
//	protected AlbumTemplate(Category category, int ID, String title) {
//		this(category.getSession(), ID, category.getID(), title);
//	}
 
//	protected AlbumTemplate(SubCategory subCategory, String title) {
//	this(category.getSession(), category.getID(), title);
//}

//protected AlbumTemplate(SubCategory subCategory, int ID, String title) {
//	this(category.getSession(), ID, category.getID(), title);
//}

	public AlbumTemplate getInfo() throws SmugmugException {
		final AlbumTemplatePrototype catFact = new AlbumTemplatePrototype(getSession());
		return catFact.refresh(this);
	}

	/**
	 * @throws SmugmugException. This method is not (yet) implemented.
	 */
	public void delete() throws SmugmugException {
		throw new SmugmugException(new NoSuchMethodException(getClass().getName()+".delete"));
	}

	/**
	 * @throws SmugmugException. This method is not (yet) implemented.
	 */
	public AlbumTemplate update() throws SmugmugException {
		throw new SmugmugException(new NoSuchMethodException(getClass().getName()+".update"));
	}

	protected String figureOutOwnersNickName() {
		return getSession().me().getNickName();
	}
	
	/**
	 * @return The ID of the parent.
	 */
	public GUID getParentId() {
		return null;
	}
}
