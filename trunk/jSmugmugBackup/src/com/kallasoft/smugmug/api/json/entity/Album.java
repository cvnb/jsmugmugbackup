package com.kallasoft.smugmug.api.json.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;

/**
 * Class used to define an album and all the properties it may or may not
 * contain.
 * <p>
 * This class also defines all the argument value constants for
 * <code>TemplateID</code>, <code>SortMethod</code>,
 * <code>SortDirection</code> and <code>DefaultColor</code> that can be
 * passed or returned to or from an {@link Album} or {@link AlbumTemplate}.
 * <p>
 * <strong>NOTE</strong>: Boolean values of <code>0</code> and <code>1</code>
 * are represented by <code>String</code>s because the SmugMug API only
 * accepts the numerical values for <code>false</code> and <code>true</code>
 * respectively and cannot accept textual representations (e.g. "True", "Yes",
 * "false", "no", etc.) which is what {@link Boolean#toString()} returns when
 * invoked. So the literal values of <code>0</code> and <code>1</code> are
 * represented by <code>String</code>s to control that.
 * 
 * @author Riyad Kalla
 */
public class Album {
	/*
	 * MASTER CLASS: This class is the master class for all Album-related
	 * properties. That includes all Album operations like create/changeSettings
	 * as well as AlbumTemplates and the same associated operations.
	 */

	/**
	 * Used to define the Viewer Choice (0) template ID.
	 */
	public static final Integer TEMPLATE_ID_VIEWER_CHOICE = Integer.valueOf(0);

	/**
	 * Used to define the SmugMug (3) template ID.
	 */
	public static final Integer TEMPLATE_ID_SMUG_MUG = Integer.valueOf(3);

	/**
	 * Used to define the Traditional (4) template ID.
	 */
	public static final Integer TEMPLATE_ID_TRADITIONAL = Integer.valueOf(4);

	/**
	 * Used to define the All Thumbs (7) template ID.
	 */
	public static final Integer TEMPLATE_ID_ALL_THUMBS = Integer.valueOf(7);

	/**
	 * Used to define the Slideshow (8) template ID.
	 */
	public static final Integer TEMPLATE_ID_SLIDESHOW = Integer.valueOf(8);

	/**
	 * Used to define the Journal (9) template ID.
	 */
	public static final Integer TEMPLATE_ID_JOURNAL = Integer.valueOf(9);

	/**
	 * Used to define the SmugMug Small (10) template ID.
	 */
	public static final Integer TEMPLATE_ID_SMUG_MUG_SMALL = Integer
			.valueOf(10);

	/**
	 * Used to define the Filmstrip (11) template ID.
	 */
	public static final Integer TEMPLATE_ID_FILMSTRIP = Integer.valueOf(11);

	/**
	 * Used to define the "Position" sort method.
	 */
	public static final String SORT_METHOD_POSITION = "Position";

	/**
	 * Used to define the "Caption" sort method.
	 */
	public static final String SORT_METHOD_CAPTION = "Caption";

	/**
	 * Used to define the "FileName" sort method.
	 */
	public static final String SORT_METHOD_FILE_NAME = "FileName";

	/**
	 * Used to define the "Date" sort method.
	 */
	public static final String SORT_METHOD_DATE = "Date";

	/**
	 * Used to define the "DateTime" sort method.
	 */
	public static final String SORT_METHOD_DATE_TIME = "DateTime";

	/**
	 * Used to define the "DateTimeOriginal" sort method.
	 */
	public static final String SORT_METHOD_DATE_TIME_ORIGINAL = "DateTimeOriginal";

	/**
	 * Used to define the Ascending (0) sort direction.
	 */
	public static final String SORT_DIRECTION_ASCENDING = "0";

	/**
	 * Used to define the Descending (1) sort direction.
	 */
	public static final String SORT_DIRECTION_DESCENDING = "1";

	/**
	 * Used to define the Default Color value of "True Color".
	 */
	public static final String DEFAULT_COLOR_TRUE_COLOR = "0";

	/**
	 * Used to define the Default Color value of "Auto Color".
	 */
	public static final String DEFAULT_COLOR_AUTO_COLOR = "1";

	/* Required fields */

	private Integer id;

	private String albumKey;

	private String title;

	/* Common fields */

	private String description;

	private String keywords;

	private Category category;

	private Category subCategory;

	private Boolean geography;

	private Integer albumTemplateID;

	/* Look and Feel fields */

	private Boolean exif;

	private Boolean clean;

	private Boolean header;

	private Boolean filenames;

	private Integer templateID;

	private String sortMethod;

	private Boolean sortDirection;

	private Integer position;

	private Integer imageCount;

	private Image highlight;

	private Boolean squareThumbs;

	/* Security & Privacy fields */

	private String password;

	private String passwordHint;

	private Boolean passworded; // 1.2.1 property only

	private Boolean isProtected;

	private Boolean isPublic;

	private Boolean hideOwner;

	private Boolean external;

	private Boolean smugSearchable;

	private Boolean worldSearchable;

	private Boolean larges;

	private Boolean xLarges;

	private Boolean x2Larges;

	private Boolean x3Larges;

	private Boolean originals;

	private Boolean watermarking;

	private Integer watermarkID;

	/* Social fields */

	private Boolean share;

	private Boolean canRank;

	private Boolean comments;

	private Boolean familyEdit;

	private Boolean friendEdit;

	/* Community fields */

	private Integer communityID;

	/* Printing & Sales fields */

	private Boolean printable;

	private Integer proofDays;

	private String backprinting;

	private Boolean defaultColor;

	/* Photo Sharpening fields */

	private Float unsharpAmount;

	private Float unsharpRadius;

	private Float unsharpThreshold;

	private Float unsharpSigma;

	/* Misc fields */
	private String lastUpdated;

	/**
	 * Construct a new album by safely parsing all known album values out of the
	 * given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and parsing continues.
	 * 
	 * @param albumObject
	 *            The {@link JSONObject} that will be queried for all the known
	 *            properties for this album.
	 * 
	 * @throws JSONException
	 *             if any JSONObject-related exception occurs.
	 */
	public Album(JSONObject albumObject) throws JSONException {
		/*
		 * If albumObject is null, leave everything set to the default value of
		 * null and return.
		 */
		if (albumObject == null)
			return;

		/* Parse required fields */
		id = JSONUtils.getIntegerSafely(albumObject, "id");
		albumKey = JSONUtils.getStringSafely(albumObject, "Key");
		title = JSONUtils.getStringSafely(albumObject, "Title");

		/* Parse common fields */
		description = JSONUtils.getStringSafely(albumObject, "Description");
		keywords = JSONUtils.getStringSafely(albumObject, "Keywords");

		if (!albumObject.isNull("Category"))
			category = new Category(albumObject.getJSONObject("Category"));

		if (!albumObject.isNull("SubCategory"))
			subCategory = new Category(albumObject.getJSONObject("SubCategory"));

		geography = JSONUtils.getBooleanSafely(albumObject, "Geography");
		albumTemplateID = JSONUtils.getIntegerSafely(albumObject,
				"AlbumTemplateID");

		/* Parse look & feel fields */
		exif = JSONUtils.getBooleanSafely(albumObject, "EXIF");
		clean = JSONUtils.getBooleanSafely(albumObject, "Clean");
		header = JSONUtils.getBooleanSafely(albumObject, "Header");
		filenames = JSONUtils.getBooleanSafely(albumObject, "Filenames");

		if (!albumObject.isNull("Template"))
			templateID = JSONUtils.getIntegerSafely(albumObject
					.getJSONObject("Template"), "id");

		sortMethod = JSONUtils.getStringSafely(albumObject, "SortMethod");
		sortDirection = JSONUtils
				.getBooleanSafely(albumObject, "SortDirection");
		position = JSONUtils.getIntegerSafely(albumObject, "Position");
		imageCount = JSONUtils.getIntegerSafely(albumObject, "ImageCount");

		if (!albumObject.isNull("Highlight"))
			highlight = new Image(albumObject.getJSONObject("Highlight"));

		squareThumbs = JSONUtils.getBooleanSafely(albumObject, "SquareThumbs");

		/* Parse security & privacy fields */
		password = JSONUtils.getStringSafely(albumObject, "Password");
		passwordHint = JSONUtils.getStringSafely(albumObject, "PasswordHint");
		passworded = JSONUtils.getBooleanSafely(albumObject, "Passworded");
		isProtected = JSONUtils.getBooleanSafely(albumObject, "Protected");
		isPublic = JSONUtils.getBooleanSafely(albumObject, "Public");
		hideOwner = JSONUtils.getBooleanSafely(albumObject, "HideOwner");
		external = JSONUtils.getBooleanSafely(albumObject, "External");
		smugSearchable = JSONUtils.getBooleanSafely(albumObject,
				"SmugSearchable");
		worldSearchable = JSONUtils.getBooleanSafely(albumObject,
				"WorldSearchable");
		larges = JSONUtils.getBooleanSafely(albumObject, "Larges");
		xLarges = JSONUtils.getBooleanSafely(albumObject, "XLarges");
		x2Larges = JSONUtils.getBooleanSafely(albumObject, "X2Larges");
		x3Larges = JSONUtils.getBooleanSafely(albumObject, "X3Larges");
		originals = JSONUtils.getBooleanSafely(albumObject, "Originals");
		watermarking = JSONUtils.getBooleanSafely(albumObject, "Watermarking");

		if (!albumObject.isNull("Watermark"))
			watermarkID = JSONUtils.getIntegerSafely(albumObject
					.getJSONObject("Watermark"), "id");

		/* Parse social fields */
		share = JSONUtils.getBooleanSafely(albumObject, "Share");
		canRank = JSONUtils.getBooleanSafely(albumObject, "CanRank");
		comments = JSONUtils.getBooleanSafely(albumObject, "Comments");
		familyEdit = JSONUtils.getBooleanSafely(albumObject, "FamilyEdit");
		friendEdit = JSONUtils.getBooleanSafely(albumObject, "FriendEdit");

		/* Parse community fields */
		if (!albumObject.isNull("Community"))
			communityID = JSONUtils.getIntegerSafely(albumObject
					.getJSONObject("Community"), "id");

		/* Parse printing & sales fields */
		printable = JSONUtils.getBooleanSafely(albumObject, "Printable");
		proofDays = JSONUtils.getIntegerSafely(albumObject, "ProofDays");
		backprinting = JSONUtils.getStringSafely(albumObject, "Backprinting");
		defaultColor = JSONUtils.getBooleanSafely(albumObject, "DefaultColor");

		/* Parse sharpening fields */
		unsharpAmount = JSONUtils.getFloatSafely(albumObject, "UnsharpAmount");
		unsharpRadius = JSONUtils.getFloatSafely(albumObject, "UnsharpRadius");
		unsharpThreshold = JSONUtils.getFloatSafely(albumObject,
				"UnsharpThreshold");
		unsharpSigma = JSONUtils.getFloatSafely(albumObject, "UnsharpSigma");

		/* Parse misc fields */
		lastUpdated = JSONUtils.getStringSafely(albumObject, "LastUpdated");
	}

	/**
	 * Construct a new album with all the given property values.
	 * 
	 * @param id
	 *            The ID of the album.
	 * @param albumKey
	 *            The security key for the album.
	 * @param title
	 *            The title of the album.
	 * @param description
	 *            A description of the album.
	 * @param keywords
	 *            The keywords associated with the album.
	 * @param category
	 *            The category that this album belongs to.
	 * @param subCategory
	 *            The subcategory that this album belongs to.
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
	 * @param imageCount
	 *            The number of images contained in the album.
	 * @param highlight
	 *            The {@link Image} (must have ID and ImageKey set) that will be
	 *            highlighted for this album.
	 * @param squareThumbs
	 *            <code>true</code> if thumbnails (Tiny and Thumbnail sizes)
	 *            are generated in a square aspect ratio, <code>false</code>
	 *            in order to generate them using the original aspect ratio.
	 * @param password
	 *            The password used to password-protect the album if it is
	 *            password protected.
	 * @param passwordHint
	 *            The password hint for the password if there is one.
	 * @param passworded
	 *            <code>true</code> if the album is password protected ,
	 *            otherwise <code>false</code>.
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
	 * @param lastUpdated
	 *            The date when the album was last updated.
	 */
	public Album(Integer id, String albumKey, String title, String description,
			String keywords, Category category, Category subCategory,
			Boolean geography, Integer albumTemplateID, Boolean exif,
			Boolean clean, Boolean header, Boolean filenames,
			Integer templateID, String sortMethod, Boolean sortDirection,
			Integer position, Integer imageCount, Image highlight,
			Boolean squareThumbs, String password, String passwordHint,
			Boolean passworded, Boolean isProtected, Boolean isPublic,
			Boolean hideOwner, Boolean external, Boolean smugSearchable,
			Boolean worldSearchable, Boolean larges, Boolean xLarges,
			Boolean x2Larges, Boolean x3Larges, Boolean originals,
			Boolean watermarking, Integer watermarkID, Boolean share,
			Boolean canRank, Boolean comments, Boolean familyEdit,
			Boolean friendEdit, Integer communityID, Boolean printable,
			Integer proofDays, String backprinting, Boolean defaultColor,
			Float unsharpAmount, Float unsharpRadius, Float unsharpThreshold,
			Float unsharpSigma, String lastUpdated) {

		/* Set Required fields */
		this.id = id;
		this.albumKey = albumKey;
		this.title = title;

		/* Set Common fields */
		this.description = description;
		this.keywords = keywords;
		this.category = category;
		this.subCategory = subCategory;
		this.geography = geography;
		this.albumTemplateID = albumTemplateID;

		/* Set Look & Feel fields */
		this.exif = exif;
		this.clean = clean;
		this.header = header;
		this.filenames = filenames;
		this.templateID = templateID;
		this.sortMethod = sortMethod;
		this.sortDirection = sortDirection;
		this.position = position;
		this.imageCount = imageCount;
		this.highlight = highlight;
		this.squareThumbs = squareThumbs;

		/* Set Security & Privacy fields */
		this.password = password;
		this.passwordHint = passwordHint;
		this.passworded = passworded;
		this.isProtected = isProtected;
		this.isPublic = isPublic;
		this.hideOwner = hideOwner;
		this.external = external;
		this.smugSearchable = smugSearchable;
		this.worldSearchable = worldSearchable;
		this.larges = larges;
		this.xLarges = xLarges;
		this.x2Larges = x2Larges;
		this.x3Larges = x3Larges;
		this.originals = originals;
		this.watermarking = watermarking;
		this.watermarkID = watermarkID;

		/* Set Social fields */
		this.share = share;
		this.canRank = canRank;
		this.comments = comments;
		this.familyEdit = familyEdit;
		this.friendEdit = friendEdit;

		/* Set Community fields */
		this.communityID = communityID;

		/* Set Printing & Sales fields */
		this.printable = printable;
		this.proofDays = proofDays;
		this.backprinting = backprinting;
		this.defaultColor = defaultColor;

		/* Set Sharpening fields */
		this.unsharpAmount = unsharpAmount;
		this.unsharpRadius = unsharpRadius;
		this.unsharpThreshold = unsharpThreshold;
		this.unsharpSigma = unsharpSigma;

		/* Set Misc fields */
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		return Album.class.getName() + "[id=" + getID() + ", albumKey="
				+ getAlbumKey() + ", title=" + getTitle() + ", description="
				+ getDescription() + ", keywords=" + getKeywords()
				+ ", category=" + getCategory() + ", subCategory="
				+ getSubCategory() + ", geography=" + isGeography()
				+ ", albumTemplateID=" + getAlbumTemplateID() + ", EXIF="
				+ isExif() + ", clean=" + isClean() + ", header=" + isHeader()
				+ ", filenames=" + isFilenames() + ", templateID="
				+ getTemplateID() + ", sortMethod=" + getSortMethod()
				+ ", sortDirection=" + getSortDirection() + ", position="
				+ getPosition() + ", imageCount=" + getImageCount()
				+ ", highlight=" + getHighlight() + ", squareThumbs="
				+ isSquareThumbs() + ", password=" + getPassword()
				+ ", passwordHint=" + getPasswordHint() + ", passworded="
				+ isPassworded() + ", protected" + isProtected() + ", public"
				+ isPublic() + ", hideOwner=" + isHideOwner() + ", external="
				+ isExternal() + ", smugSearchable=" + isSmugSearchable()
				+ ", worldSearchable=" + isWorldSearchable() + ", larges="
				+ isLarges() + ", xLarges=" + isXLarges() + ", X2Larges"
				+ isX2Larges() + ", X3Larges=" + isX3Larges() + ", originals="
				+ isOriginals() + ", watermarking=" + isWatermarking()
				+ ", watermarkID=" + getWatermarkID() + ", share=" + isShare()
				+ ", canRank=" + canRank() + ", comments=" + isComments()
				+ ", familyEdit=" + isFamilyEdit() + ", friendEdit="
				+ isFriendEdit() + ", communityID=" + getCommunityID()
				+ ", printable=" + isPrintable() + ", proofDays="
				+ getProofDays() + ", backprinting=" + getBackprinting()
				+ ", defaultColor=" + isDefaultColor() + ", unsharpAmount="
				+ getUnsharpAmount() + ", unsharpRadius=" + getUnsharpRadius()
				+ ", unsharpThreshold=" + getUnsharpThreshold()
				+ ", unsharpSigma=" + getUnsharpSigma() + ", lastUpdated="
				+ getLastUpdated() + "]";
	}

	/**
	 * Used to get the ID of the album.
	 * 
	 * @return the ID of the album.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Used to get the security key for the album.
	 * 
	 * @return the security key for the album.
	 */
	public String getAlbumKey() {
		return albumKey;
	}

	/**
	 * Used to get the title of the album.
	 * 
	 * @return the title of the album.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Used to get a description of the album.
	 * 
	 * @return a description of the album.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Used to get the keywords associated with the album.
	 * 
	 * @return the keywords associated with the album.
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * Used to get the category that this album belongs to.
	 * 
	 * @return the category that this album belongs to.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Used to get the subcategory that this album belongs to.
	 * 
	 * @return the subcategory that this album belongs to.
	 */
	public Category getSubCategory() {
		return subCategory;
	}

	/**
	 * Used to determine if the album allows the use of geo-tagging images
	 * (location).
	 * 
	 * @return <code>true</code> if the album allows the use of geo-tagging
	 *         images (location), otherwise <code>false</code>.
	 */
	public Boolean isGeography() {
		return geography;
	}

	/**
	 * Used to get the ID of the album template used to configure this album.
	 * 
	 * @return the ID of the album template used to configure this album.
	 */
	public Integer getAlbumTemplateID() {
		return albumTemplateID;
	}

	/**
	 * Used to determine if the album allows accessing the EXIF image data.
	 * 
	 * @return <code>true</code> if the album allows accessing the EXIF image
	 *         data, otherwise <code>false</code>.
	 */
	public Boolean isExif() {
		return exif;
	}

	/**
	 * Used to determine if the album should display using the "clean" style.
	 * 
	 * @return <code>true</code> if the album should display using the "clean"
	 *         style, otherwise <code>false</code>.
	 */
	public Boolean isClean() {
		return clean;
	}

	/**
	 * Used to determine if the album will display a custom header.
	 * 
	 * @return <code>true</code> if the album will display a custom header,
	 *         otherwise <code>false</code>.
	 */
	public Boolean isHeader() {
		return header;
	}

	/**
	 * Used to determine if filenames will be shown when there is no caption for
	 * an image in this album.
	 * 
	 * @return <code>true</code> if filenames will be shown when there is no
	 *         caption for an image in this album, otherwise <code>false</code>.
	 */
	public Boolean isFilenames() {
		return filenames;
	}

	/**
	 * Used to get the ID of the template that will be used to display the
	 * album.
	 * <p>
	 * This can be:
	 * <ul>
	 * <li>Viewer Choice (Recommended!): 0</li>
	 * <li>SmugMug: 3</li>
	 * <li>Traditional: 4</li>
	 * <li>All Thumbs: 7</li>
	 * <li>Slideshow: 8</li>
	 * <li>Journal: 9</li>
	 * <li>SmugMug Small: 10</li>
	 * <li>Filmstrip: 11</li>
	 * </ul>
	 * 
	 * @return the ID of the template that will be used to display the album.
	 */
	public Integer getTemplateID() {
		return templateID;
	}

	/**
	 * The sort method used for the images in this album.
	 * <p>
	 * This can be:
	 * <ul>
	 * <li>"Position" (Sorts by user-specified position)</li>
	 * <li>"Caption" (Sorts by the image captions)</li>
	 * <li>"FileName" (Sorts by the filename of each photo)</li>
	 * <li>"Date" (Sorts by the date uploaded to SmugMug)</li>
	 * <li>"DateTime" (Sorts by the date last modified, as told by EXIF data -
	 * Many files don't have this field set correctly)</li>
	 * <li>"DateTimeOriginal" (Sorts by the date taken, as told by EXIF data -
	 * Many cameras don't report this properly)</li>
	 * </ul>
	 * 
	 * @return the sort method used for the images in this album.
	 */
	public String getSortMethod() {
		return sortMethod;
	}

	/**
	 * Used to determine the direction (ascending [false] or descending [true])
	 * that images are sorted in this album.
	 * 
	 * @return <code>false</code> (0) for ascending and <code>true</code>
	 *         (1) for descending.
	 */
	public Boolean getSortDirection() {
		return sortDirection;
	}

	/**
	 * Used to get the position of the album within it's category (starting with
	 * 1).
	 * 
	 * @return the position of the album within it's category (starting with 1).
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Used to get the number of images contained in the album.
	 * 
	 * @return the number of images contained in the album.
	 */
	public Integer getImageCount() {
		return imageCount;
	}

	/**
	 * Used to get the {@link Image} (must have ID and ImageKey set) that will
	 * be highlighted for this album.
	 * 
	 * @return the {@link Image} (must have ID and ImageKey set) that will be
	 *         highlighted for this album.
	 */
	public Image getHighlight() {
		return highlight;
	}

	/**
	 * Used to determine if the album should generate thumbnails (Tiny and
	 * Thumbnail sizes) in a square aspect ratio or using the original aspect
	 * ratio.
	 * 
	 * @return <code>true</code> if thumbnails (Tiny and Thumbnail sizes) are
	 *         generated in a square aspect ratio, <code>false</code> in order
	 *         to generate them using the original aspect ratio.
	 */
	public Boolean isSquareThumbs() {
		return squareThumbs;
	}

	/**
	 * Used to get the password used to password-protect the album if it is
	 * password protected.
	 * 
	 * @return the password used to password-protect the album if it is password
	 *         protected.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Used to get the password hint for the password if there is one.
	 * 
	 * @return the password hint for the password if there is one.
	 */
	public String getPasswordHint() {
		return passwordHint;
	}

	/**
	 * Used to determine if the album is password protected.
	 * <p>
	 * This is available with 1.2.1+ only.
	 * 
	 * @return <code>true</code> if the album is password protected, otherwise
	 *         returns <code>false</code>.
	 */
	public Boolean isPassworded() {
		return passworded;
	}

	/**
	 * Used to determine if the album is using heavy image protection.
	 * 
	 * @return <code>true</code> if the album is using heavy image protection,
	 *         otherwise <code>false</code>.
	 */
	public Boolean isProtected() {
		return isProtected;
	}

	/**
	 * Used to determine if the album is publicly accessible.
	 * 
	 * @return <code>true</code> if the album is publicly accessible,
	 *         otherwise returns <code>false</code>.
	 */
	public Boolean isPublic() {
		return isPublic;
	}

	/**
	 * Used to determine if the album should mask the URL and name of the image
	 * owner.
	 * 
	 * @return <code>true</code> if the album should mask the URL and name of
	 *         the image owner, otherwise <code>false</code>.
	 */
	public Boolean isHideOwner() {
		return hideOwner;
	}

	/**
	 * Used to determine if the album allows external linking.
	 * 
	 * @return <code>true</code> if the album allows external linking,
	 *         otherwise <code>false</code>.
	 */
	public Boolean isExternal() {
		return external;
	}

	/**
	 * Used to determine if the album is SmugMug-searchable.
	 * 
	 * @return <code>true</code> if the album is SmugMug-searchable, otherwise
	 *         <code>false</code>.
	 */
	public Boolean isSmugSearchable() {
		return smugSearchable;
	}

	/**
	 * Used to determine if the album is world-searchable (e.g. Google, Yahoo,
	 * etc.).
	 * 
	 * @return <code>true</code> if the album is world-searchable (e.g.
	 *         Google, Yahoo, etc.), otherwise <code>false</code>.
	 */
	public Boolean isWorldSearchable() {
		return worldSearchable;
	}

	/**
	 * Used to determine if the album allows viewing of large-size images.
	 * 
	 * @return <code>true</code> if the album allows viewing of large-size
	 *         images, otherwise <code>false</code>.
	 */
	public Boolean isLarges() {
		return larges;
	}

	/**
	 * Used to determine if the album allows viewing of xlarge-size images.
	 * 
	 * @return <code>true</code> if the album allows viewing of xlarge-size
	 *         images, otherwise <code>false</code>.
	 */
	public Boolean isXLarges() {
		return xLarges;
	}

	/**
	 * Used to determine if the album allows viewing of xxlarge-size images.
	 * 
	 * @return <code>true</code> if the album allows viewing of xxlarge-size
	 *         images, otherwise <code>false</code>.
	 */
	public Boolean isX2Larges() {
		return x2Larges;
	}

	/**
	 * Used to determine if the album allows viewing of xxxlarge-size images.
	 * 
	 * @return <code>true</code> if the album allows viewing of xxxlarge-size
	 *         images, otherwise <code>false</code>.
	 */
	public Boolean isX3Larges() {
		return x3Larges;
	}

	/**
	 * Used to determine if the image's originals can be seen.
	 * 
	 * @return <code>true</code> if the image's originals can be seen,
	 *         otherwise <code>false</code>.
	 */
	public Boolean isOriginals() {
		return originals;
	}

	/**
	 * Used to determine if the album uses watermarking.
	 * 
	 * @return <code>true</code> if the album uses watermarking, otherwise
	 *         <code>false</code>.
	 */
	public Boolean isWatermarking() {
		return watermarking;
	}

	/**
	 * Used to get the ID of the watermark that will be used on the images.
	 * 
	 * @return the ID of the watermark that will be used on the images.
	 */
	public Integer getWatermarkID() {
		return watermarkID;
	}

	/**
	 * Used to determine if the album enables the "Easy Sharing" button and
	 * features.
	 * 
	 * @return <code>true</code> if the album enables the "Easy Sharing"
	 *         button and features, otherwise <code>false</code>.
	 */
	public Boolean isShare() {
		return share;
	}

	/**
	 * Used to determine if the album can be ranked.
	 * 
	 * @return <code>true</code> if the album can be ranked, otherwise
	 *         <code>false</code>.
	 */
	public Boolean canRank() {
		return canRank;
	}

	/**
	 * Used to determine if the album allows comments.
	 * 
	 * @return <code>true</code> if the album allows comments, otherwise
	 *         <code>false</code>.
	 */
	public Boolean isComments() {
		return comments;
	}

	/**
	 * Used to determine if the album allows captions and keywords to be edited
	 * by "family".
	 * 
	 * @return <code>true</code> if the album allows captions and keywords to
	 *         be edited by "family", otherwise <code>false</code>.
	 */
	public Boolean isFamilyEdit() {
		return familyEdit;
	}

	/**
	 * Used to determine if the album allows captions and keywords to be edited
	 * by "friends".
	 * 
	 * @return <code>true</code> if the album allows captions and keywords to
	 *         be edited by "friends", otherwise <code>false</code>.
	 */
	public Boolean isFriendEdit() {
		return friendEdit;
	}

	/**
	 * Used to get the ID of the community that this album belongs to.
	 * 
	 * @return the ID of the community that this album belongs to.
	 */
	public Integer getCommunityID() {
		return communityID;
	}

	/**
	 * Used to determine if prints can be purchased for the images in this
	 * album.
	 * 
	 * @return <code>true</code> if prints can be purchased for the images in
	 *         this album, otherwise <code>false</code>.
	 */
	public Boolean isPrintable() {
		return printable;
	}

	/**
	 * Used to get the number of days to delay printing for this album.
	 * 
	 * @return the number of days to delay printing for this album.
	 */
	public Integer getProofDays() {
		return proofDays;
	}

	/**
	 * Used to get the optional text that can be printed on the backs of the
	 * prints.
	 * 
	 * @return the optional text that can be printed on the backs of the prints.
	 */
	public String getBackprinting() {
		return backprinting;
	}

	/**
	 * Used to determine if the album defaults to using "True" or "AutoColor"
	 * when it's in the cart.
	 * 
	 * @return <code>true</code> if the album defaults to using "True" or
	 *         "AutoColor" when it's in the cart, otherwise <code>false</code>.
	 */
	public Boolean isDefaultColor() {
		return defaultColor;
	}

	/**
	 * Used to get the unsharp mask value.
	 * <p>
	 * This value controls how sharp of a mask is being applied to resized
	 * versions of the original image (e.g. Large, XLarge, etc.).
	 * 
	 * @return the unsharp mask value.
	 */
	public Float getUnsharpAmount() {
		return unsharpAmount;
	}

	/**
	 * Used to get the unsharp radius value.
	 * <p>
	 * This value controls how large of a radius the sharpening effect has when
	 * applied to resized versions of the original image (e.g. Large, XLarge,
	 * etc.). Larger values effect more of the image.
	 * 
	 * @return the unsharp radius value.
	 */
	public Float getUnsharpRadius() {
		return unsharpRadius;
	}

	/**
	 * Used to get the unsharp threshold value.
	 * <p>
	 * This value controls how many adjacent pixels are considered as an edge
	 * when the sharpening effect is applied to resized versions of the original
	 * image (e.g. Large, XLarge, etc.). Larger values effect more of the image.
	 * 
	 * @return the unsharp threshold value.
	 */
	public Float getUnsharpThreshold() {
		return unsharpThreshold;
	}

	/**
	 * Used to get the unsharp sigma value.
	 * 
	 * @return the unsharp sigma value.
	 */
	public Float getUnsharpSigma() {
		return unsharpSigma;
	}

	/**
	 * Used to get the date the album was last updated.
	 * 
	 * @return the date the album was last updated.
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}
}
