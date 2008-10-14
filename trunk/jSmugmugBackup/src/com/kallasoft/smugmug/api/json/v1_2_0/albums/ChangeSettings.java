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
package com.kallasoft.smugmug.api.json.v1_2_0.albums;

import com.kallasoft.smugmug.api.json.AbstractMethod;
import com.kallasoft.smugmug.api.json.AbstractResponse;
import com.kallasoft.smugmug.api.json.RuntimeJSONException;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * This method will change the settings on an album specified by AlbumID.
 * <p>
 * The session must be established with a valid login. All settings are
 * optional, if omitted, the current value for that setting is preserved.
 * 
 * @author Riyad Kalla
 * @version 1.2.0
 * @see <a
 *      href="http://smugmug.jot.com/WikiHome/1.2.0/smugmug.albums.changeSettings">smugmug.albums.changeSettings
 *      API Doc</a>
 */
public class ChangeSettings extends AbstractMethod {
	/* Parameters all copied from the Album class, which is the master copy */

	/**
	 * Defines the SmugMug JSON API method name that will be called.
	 */
	public static final String METHOD_NAME = "smugmug.albums.changeSettings";

	/**
	 * Defines all the arguments this method takes.
	 * <p>
	 * Values are: "APIKey", "SessionID", "AlbumID", "Title", "Description",
	 * "Keywords", "CategoryID", "SubCategoryID", "Geography",
	 * "AlbumTemplateID", "EXIF", "Clean", "Header", "Filenames", "TemplateID",
	 * "SortMethod", "SortDirection", "Position", "HighlightID", "SquareThumbs",
	 * "Password", "PasswordHint", "Protected", "Public", "HideOwner",
	 * "External", "SmugSearchable", "WorldSearchable", "Larges", "XLarges",
	 * "X2Larges", "X3Larges", "Originals", "Watermarking", "WatermarkID",
	 * "Share", "CanRank", "Comments", "FamilyEdit", "FriendEdit",
	 * "CommunityID", "Printable", "ProofDays", "Backprinting", "DefaultColor",
	 * "UnsharpAmount", "UnsharpRadius", "UnsharpThreshold", "UnsharpSigma"
	 */
	public static final String[] ARGUMENTS = { "APIKey", "SessionID",
			"AlbumID", "Title", "Description", "Keywords", "CategoryID",
			"SubCategoryID", "Geography", "AlbumTemplateID", "EXIF", "Clean",
			"Header", "Filenames", "TemplateID", "SortMethod", "SortDirection",
			"Position", "HighlightID", "SquareThumbs", "Password",
			"PasswordHint", "Protected", "Public", "HideOwner", "External",
			"SmugSearchable", "WorldSearchable", "Larges", "XLarges",
			"X2Larges", "X3Larges", "Originals", "Watermarking", "WatermarkID",
			"Share", "CanRank", "Comments", "FamilyEdit", "FriendEdit",
			"CommunityID", "Printable", "ProofDays", "Backprinting",
			"DefaultColor", "UnsharpAmount", "UnsharpRadius",
			"UnsharpThreshold", "UnsharpSigma" };

	/**
	 * Construct a new method instance that can be executed.
	 */
	public ChangeSettings() {
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
	public ChangeSettings(String methodName, String[] arguments) {
		super(methodName, arguments);
	}

	/**
	 * Used to execute the smugmug.albums.changeSettings method, changing any
	 * settings specified and leaving any settings not specified as they are.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param argumentValues
	 *            The argument values to pass to this method.
	 * 
	 * @return the response returned after the settings have changed.
	 */
	public ChangeSettingsResponse execute(String url, String[] argumentValues) {
		return new ChangeSettingsResponse(executeImpl(url, argumentValues));
	}

	/**
	 * Convenience method used to execute the smugmug.albums.changeSettings
	 * method.
	 * <p>
	 * This method performs necessary conversions on all the argument values
	 * before calling {@link #execute(String, String[])}.
	 * 
	 * @param url
	 *            The URL of the SmugMug server to communicate with.
	 * @param apiKey
	 *            The API Key to use. API keys are issued by SmugMug.
	 * @param sessionID
	 *            The logged in SessionID that represents the user's session.
	 * @param albumID
	 *            The ID of the album to change the settings for.
	 * @param title
	 *            The title of the album.
	 * @param description
	 *            A description of the album.
	 * @param keywords
	 *            The keywords associated with the album.
	 * @param categoryID
	 *            The ID of the category that this album belongs to.
	 * @param subCategoryID
	 *            The ID of the subcategory that this album belongs to.
	 * @param geography
	 *            <code>true</code> if the album allows the use of geo-tagging
	 *            images (location), otherwise <code>false</code>.
	 * @param albumTemplateID
	 *            The ID of the album template used to configure this album.
	 * @param exif
	 *            <code>true</code> if the album allows accessing the EXIF
	 *            image data, otherwise <code>false</code>.
	 * @param clean
	 *            <code>true</code> if the album should display using the
	 *            "clean" style, otherwise <code>false</code>.
	 * @param header
	 *            <code>true</code> if the album will display a custom header,
	 *            otherwise <code>false</code>.
	 * @param filenames
	 *            <code>true</code> if the album allows filenames to be shown
	 *            if there is no caption, otherwise <code>false</code>.
	 * @param templateID
	 *            The ID of the template that will be used to display the album.
	 *            <p>
	 *            This can be:
	 *            <ul>
	 *            <li>Viewer Choice (Recommended!): 0</li>
	 *            <li>SmugMug: 3</li>
	 *            <li>Traditional: 4</li>
	 *            <li>All Thumbs: 7</li>
	 *            <li>Slideshow: 8</li>
	 *            <li>Journal: 9</li>
	 *            <li>SmugMug Small: 10</li>
	 *            <li>Filmstrip: 11</li>
	 *            </ul>
	 * @param sortMethod
	 *            The sort method used for the images in this album.
	 *            <p>
	 *            This can be:
	 *            <ul>
	 *            <li>"Position" (Sorts by user-specified position)</li>
	 *            <li>"Caption" (Sorts by the image captions)</li>
	 *            <li>"FileName" (Sorts by the filename of each photo)</li>
	 *            <li>"Date" (Sorts by the date uploaded to SmugMug)</li>
	 *            <li>"DateTime" (Sorts by the date last modified, as told by
	 *            EXIF data - Many files don't have this field set correctly)</li>
	 *            <li>"DateTimeOriginal" (Sorts by the date taken, as told by
	 *            EXIF data - Many cameras don't report this properly)</li>
	 *            </ul>
	 * @param sortDirection
	 *            <code>false</code> (0) for ascending and <code>true</code>
	 *            (1) for descending.
	 * @param position
	 *            The position of the album within it's category (starting with
	 *            1).
	 * @param highlightID
	 *            The ID of the image that will be highlighted (displayed) for
	 *            this album.
	 * @param squareThumbs
	 *            <code>true</code> if thumbnails (Tiny and Thumbnail sizes)
	 *            are generated in a square aspect ratio, <code>false</code>
	 *            in order to generate them using the original aspect ratio.
	 * @param password
	 *            The password used to password-protect the album if it is
	 *            password protected.
	 * @param passwordHint
	 *            The password hint for the password if there is one.
	 * @param isProtected
	 *            <code>true</code> if the album is using heavy image
	 *            protection, otherwise <code>false</code>.
	 * @param isPublic
	 *            <code>true</code> if the album is publicly accessible,
	 *            otherwise <code>false</code>.
	 * @param hideOwner
	 *            <code>true</code> if the album should mask the URL and name
	 *            of the image owner, otherwise <code>false</code>.
	 * @param external
	 *            <code>true</code> if the album allows external linking,
	 *            otherwise <code>false</code>.
	 * @param smugSearchable
	 *            <code>true</code> if the album is SmugMug-searchable,
	 *            otherwise <code>false</code>.
	 * @param worldSearchable
	 *            <code>true</code> if the album is world-searchable (e.g.
	 *            Google, Yahoo, etc.), otherwise <code>false</code>.
	 * @param larges
	 *            <code>true</code> if the album allows viewing of large-size
	 *            images, otherwise <code>false</code>.
	 * @param xLarges
	 *            <code>true</code> if the album allows viewing of xlarge-size
	 *            images, otherwise <code>false</code>.
	 * @param x2Larges
	 *            <code>true</code> if the album allows viewing of
	 *            xxlarge-size images, otherwise <code>false</code>.
	 * @param x3Larges
	 *            <code>true</code> if the album allows viewing of
	 *            xxxlarge-size images, otherwise <code>false</code>.
	 * @param originals
	 *            <code>true</code> if the album allows the image's originals
	 *            to be seen, otherwise <code>false</code>.
	 * @param watermarking
	 *            <code>true</code> if the album uses watermarking, otherwise
	 *            <code>false</code>.
	 * @param watermarkID
	 *            The ID of the watermark that will be used on the images.
	 * @param share
	 *            <code>true</code> if the album enables the "Easy Sharing"
	 *            button and features, otherwise <code>false</code>.
	 * @param canRank
	 *            <code>true</code> if the album can be ranked, otherwise
	 *            <code>false</code>.
	 * @param comments
	 *            <code>true</code> if the album allows comments, otherwise
	 *            <code>false</code>.
	 * @param familyEdit
	 *            <code>true</code> if the album allows captions and keywords
	 *            to be edited by "family", otherwise <code>false</code>.
	 * @param friendEdit
	 *            <code>true</code> if the album allows captions and keywords
	 *            to be edited by "friends", otherwise <code>false</code>.
	 * @param communityID
	 *            The ID of the community that this album belongs to.
	 * @param printable
	 *            <code>true</code> if the album allows prints to be
	 *            purchased, otherwise <code>false</code>.
	 * @param proofDays
	 *            The number of days to delay printing for this album.
	 * @param backprinting
	 *            The optional text that can be printed on the backs of the
	 *            prints.
	 * @param defaultColor
	 *            <code>true</code> if the album defaults to using "True" or
	 *            "AutoColor" when it's in the cart, otherwise
	 *            <code>false</code>.
	 * @param unsharpAmount
	 *            How weak or strong the sharpness mask will be. A higher value
	 *            will result in a more stark sharpness (Default 0.200). This
	 *            only applies to resized versions of the original image (Large,
	 *            XLarge, etc.) See <a
	 *            href="http://smugmug.com/help/display-quality">http://smugmug.com/help/display-quality</a>
	 *            for more information.
	 * @param unsharpRadius
	 *            The radius on the sharpening effect. Larger values lead to
	 *            more defined sharpening (Default 1.000). This only applies to
	 *            resized versions of the original image (Large, XLarge, etc.)
	 *            See <a
	 *            href="http://smugmug.com/help/display-quality">http://smugmug.com/help/display-quality</a>
	 *            for more information.
	 * @param unsharpThreshold
	 *            The number of adjacent pixels that are considered an "edge"
	 *            before performing a sharpen (Default 0.050). This only applies
	 *            to resized versions of the original image (Large, XLarge,
	 *            etc.) See <a
	 *            href="http://smugmug.com/help/display-quality">http://smugmug.com/help/display-quality</a>
	 *            for more information.
	 * @param unsharpSigma
	 *            (Default 1.000)
	 * 
	 * @return the response returned after the settings have changed.
	 * 
	 * @see #execute(String, String[])
	 */
	public ChangeSettingsResponse execute(String url, String apiKey,
			String sessionID, Integer albumID, String title,
			String description, String keywords, Integer categoryID,
			Integer subCategoryID, Boolean geography, Integer albumTemplateID,
			Boolean exif, Boolean clean, Boolean header, Boolean filenames,
			Integer templateID, String sortMethod, Boolean sortDirection,
			Integer position, Integer highlightID, Boolean squareThumbs,
			String password, String passwordHint, Boolean isProtected,
			Boolean isPublic, Boolean hideOwner, Boolean external,
			Boolean smugSearchable, Boolean worldSearchable, Boolean larges,
			Boolean xLarges, Boolean x2Larges, Boolean x3Larges,
			Boolean originals, Boolean watermarking, Integer watermarkID,
			Boolean share, Boolean canRank, Boolean comments,
			Boolean familyEdit, Boolean friendEdit, Integer communityID,
			Boolean printable, Integer proofDays, String backprinting,
			Boolean defaultColor, Float unsharpAmount, Float unsharpRadius,
			Float unsharpThreshold, Float unsharpSigma) {
		return execute(url, new String[] { apiKey, sessionID,
				APIUtils.toString(albumID), title, description, keywords,
				APIUtils.toString(categoryID),
				APIUtils.toString(subCategoryID), APIUtils.toString(geography),
				APIUtils.toString(albumTemplateID), APIUtils.toString(exif),
				APIUtils.toString(clean), APIUtils.toString(header),
				APIUtils.toString(filenames), APIUtils.toString(templateID),
				sortMethod, APIUtils.toString(sortDirection),
				APIUtils.toString(position), APIUtils.toString(highlightID),
				APIUtils.toString(squareThumbs), password, passwordHint,
				APIUtils.toString(isProtected), APIUtils.toString(isPublic),
				APIUtils.toString(hideOwner), APIUtils.toString(external),
				APIUtils.toString(smugSearchable),
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
	 * Class used to represent the response for the
	 * smugmug.albums.changeSettings method call.
	 * <p>
	 * This response doesn't return any additional arguments. The stat code
	 * returned by {@link AbstractResponse#getStat()} indicates if the operation
	 * was successful or not.
	 * 
	 * @author Riyad Kalla
	 * @version 1.2.0
	 */
	public class ChangeSettingsResponse extends AbstractResponse {
		/**
		 * Construct a response by parsing the necessary values out of the JSON
		 * response text.
		 * 
		 * @param responseText
		 *            The JSON-formatted response text that came back from the
		 *            SmugMug API call.
		 * 
		 * @throws RuntimeJSONException
		 *             if an error occurs while parsing the JSON response text.
		 */
		public ChangeSettingsResponse(String responseText)
				throws RuntimeJSONException {
			/* Parse base response and any error if necessary */
			super(responseText);
		}

		@Override
		public String toString() {
			return ChangeSettingsResponse.class.getName() + "[isError="
					+ isError() + "]";
		}
	}
}
