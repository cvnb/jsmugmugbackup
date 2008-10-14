/* Copyright 2007 kallasoft
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
package com.kallasoft.smugmug.api.json.v1_2_1.albums;

import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This class is a convenience class extending the base
 * {@link com.kallasoft.smugmug.api.json.v1_2_0.albums.Create} implementation.
 * 
 * @author Riyad Kalla
 * @version 1.2.1
 * @see com.kallasoft.smugmug.api.json.v1_2_0.albums.Create
 */
public class Create extends com.kallasoft.smugmug.api.json.v1_2_0.albums.Create {
	/**
	 * Construct a new method instance that can be executed.
	 */
	public Create() {
		this(METHOD_NAME, ARGUMENTS);
	}

	/**
	 * Construct a new method instance that can be executed with the given
	 * arguments.
	 * 
	 * @param methodName
	 *            The name of the SmugMug JSON API method that this
	 *            <em>Method</em> represents.
	 * @param arguments
	 *            The names of the arguments that this method accepts.
	 */
	public Create(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	@Override
	public CreateResponse execute(String url, String[] argumentValues) {
		return new CreateResponse(executeImpl(url, argumentValues));
	}

	@Override
	public CreateResponse execute(String url, String apiKey, String sessionID,
			String title, Integer categoryID) {
		return execute(url, apiKey, sessionID, title, null, null, categoryID,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null);
	}

	@Override
	public CreateResponse execute(String url, String apiKey, String sessionID,
			String title, String description, String keywords,
			Integer categoryID, Integer subCategoryID, Boolean geography,
			Integer albumTemplateID, Boolean exif, Boolean clean,
			Boolean header, Boolean filenames, Integer templateID,
			String sortMethod, Boolean sortDirection, Integer position,
			Boolean squareThumbs, String password, String passwordHint,
			Boolean isProtected, Boolean isPublic, Boolean hideOwner,
			Boolean external, Boolean smugSearchable, Boolean worldSearchable,
			Boolean larges, Boolean xLarges, Boolean x2Larges,
			Boolean x3Larges, Boolean originals, Boolean watermarking,
			Integer watermarkID, Boolean share, Boolean canRank,
			Boolean comments, Boolean familyEdit, Boolean friendEdit,
			Integer communityID, Boolean printable, Integer proofDays,
			String backprinting, Boolean defaultColor, Float unsharpAmount,
			Float unsharpRadius, Float unsharpThreshold, Float unsharpSigma) {
		return execute(url, new String[] { apiKey, sessionID, title,
				description, keywords, APIUtils.toString(categoryID),
				APIUtils.toString(subCategoryID), APIUtils.toString(geography),
				APIUtils.toString(albumTemplateID), APIUtils.toString(exif),
				APIUtils.toString(clean), APIUtils.toString(header),
				APIUtils.toString(filenames), APIUtils.toString(templateID),
				sortMethod, APIUtils.toString(sortDirection),
				APIUtils.toString(position), APIUtils.toString(squareThumbs),
				password, passwordHint, APIUtils.toString(isProtected),
				APIUtils.toString(isPublic), APIUtils.toString(hideOwner),
				APIUtils.toString(external), APIUtils.toString(smugSearchable),
				APIUtils.toString(worldSearchable), APIUtils.toString(larges),
				APIUtils.toString(xLarges), APIUtils.toString(x2Larges),
				APIUtils.toString(x3Larges), APIUtils.toString(originals),
				APIUtils.toString(watermarking),
				APIUtils.toString(watermarkID), APIUtils.toString(share),
				APIUtils.toString(canRank), APIUtils.toString(comments),
				APIUtils.toString(familyEdit), APIUtils.toString(friendEdit),
				APIUtils.toString(communityID), APIUtils.toString(printable),
				APIUtils.toString(proofDays), APIUtils.toString(backprinting),
				APIUtils.toString(defaultColor),
				APIUtils.toString(unsharpAmount),
				APIUtils.toString(unsharpRadius),
				APIUtils.toString(unsharpThreshold),
				APIUtils.toString(unsharpSigma) });
	}

	/**
	 * This class is a convenience class extending the base
	 * {@link com.kallasoft.smugmug.api.json.v1_2_0.albums.Create.CreateResponse}
	 * implementation.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.1
	 * @see com.kallasoft.smugmug.api.json.v1_2_0.albums.Create.CreateResponse
	 */
	public class CreateResponse extends
			com.kallasoft.smugmug.api.json.v1_2_0.albums.Create.CreateResponse {
		public CreateResponse(String responseText) {
			super(responseText);
		}
	}
}
