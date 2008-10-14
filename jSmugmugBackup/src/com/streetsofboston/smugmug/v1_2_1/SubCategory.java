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

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.subcategories.Delete;
import com.kallasoft.smugmug.api.json.v1_2_1.subcategories.Rename;
import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.GUID;
import com.streetsofboston.smugmug.v1_2_1.system.ISmugObject;
import com.streetsofboston.smugmug.v1_2_1.system.PrototypeHelper;
import com.streetsofboston.smugmug.v1_2_1.system.Session;

/**
 * Instances of this class represent SubCategories on Smugmug. 
 * @author Anton Spaans
 */
public class SubCategory 
			extends SubCategoryPrototype.Data<SubCategory>
			implements ISmugObject<SubCategory> {

	static final long serialVersionUID = -8862908047853173455L;

	protected SubCategory(Session session, int ID, int categoryID, String name) {
		super(session, ID, categoryID, name);
	}

	/**
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#getInfo()
	 */
	public SubCategory getInfo() throws SmugmugException {
		final SubCategoryPrototype catFact = new SubCategoryPrototype(getSession());
		return catFact.refresh(this);
	}

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
	 * @see com.streetsofboston.smugmug.v1_2_1.system.ISmugObject#update()
	 */
	public SubCategory update() throws SmugmugException {
		if (!hasID()) 
			throw new SmugmugException("The ID is not set for "+this);

		lock();
		try {
			if (mName.isUndefined() || mName.get().length() == 0)
				throw new SmugmugException("The Title is not set for " + this);
			if (!hasChanged())
				return this;
			Rename.RenameResponse resp = new Rename().execute(
					APIVersionConstants.SECURE_SERVER_URL,
							getSession().getAPIKey(),
							getSession().getSessionID(), getID(),
							getName());
			SmugmugException.check(resp, this);
			return PrototypeHelper.postUpdate(this);
		} finally {
			unlock();
		}
	}

	/**
	 * @return The ID of the parent.
	 */
	public GUID getParentId() {
		return mCategoryID.isUndefined() ? null : mCategoryID.get();
	}
}
