package com.kallasoft.smugmug.api.json.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;

/**
 * Class used to define an album template and all the properties it contains.
 * <p>
 * Album templates are used as templates for creating new albums or applying
 * properties all at the same time to existing ones.
 * <p>
 * The valid values for <code>SortMethod</code> and <code>TemplateID</code>
 * can be found defined as constants in the {@link Album} class.
 * 
 * @author Riyad Kalla
 */
public class AlbumTemplate {
	/* Parameters all copied from the Album class, which is the master copy */

	/* Required fields */

	private Integer id;

	private String albumTemplateName;

	private Boolean geography;

	/* Look and Feel fields */

	private Boolean exif;

	private Boolean clean;

	private Boolean header;

	private Boolean filenames;

	private Integer templateID;

	private String sortMethod;

	private Boolean sortDirection;

	private Boolean squareThumbs;

	/* Security & Privacy fields */

	private String password;

	private String passwordHint;

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

	/**
	 * Construct a new album by safely parsing all known album template values
	 * out of the given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and ignored and parsing continues.
	 * 
	 * @param albumTemplateObject
	 *            The {@link JSONObject} that will be queried for all the known
	 *            properties for this album template.
	 * 
	 * @throws JSONException
	 *             if any JSONObject-related exception occurs.
	 */
	public AlbumTemplate(JSONObject albumTemplateObject) throws JSONException {
		/*
		 * If albumTemplateObject is null, leave everything set to the default
		 * value of null and return.
		 */
		if (albumTemplateObject == null)
			return;

		/* Parse required fields */
		id = JSONUtils.getIntegerSafely(albumTemplateObject, "id");
		albumTemplateName = JSONUtils.getStringSafely(albumTemplateObject,
				"AlbumTemplateName");

		/* Parse common fields */
		geography = JSONUtils
				.getBooleanSafely(albumTemplateObject, "Geography");

		/* Parse look & feel fields */
		exif = JSONUtils.getBooleanSafely(albumTemplateObject, "EXIF");
		clean = JSONUtils.getBooleanSafely(albumTemplateObject, "Clean");
		header = JSONUtils.getBooleanSafely(albumTemplateObject, "Header");
		filenames = JSONUtils
				.getBooleanSafely(albumTemplateObject, "Filenames");

		if (!albumTemplateObject.isNull("Template"))
			templateID = JSONUtils.getIntegerSafely(albumTemplateObject
					.getJSONObject("Template"), "id");

		sortMethod = JSONUtils.getStringSafely(albumTemplateObject,
				"SortMethod");
		sortDirection = JSONUtils.getBooleanSafely(albumTemplateObject,
				"SortDirection");
		squareThumbs = JSONUtils.getBooleanSafely(albumTemplateObject,
				"SquareThumbs");

		/* Parse security & privacy fields */
		password = JSONUtils.getStringSafely(albumTemplateObject, "Password");
		passwordHint = JSONUtils.getStringSafely(albumTemplateObject,
				"PasswordHint");
		isProtected = JSONUtils.getBooleanSafely(albumTemplateObject,
				"Protected");
		isPublic = JSONUtils.getBooleanSafely(albumTemplateObject, "Public");
		hideOwner = JSONUtils
				.getBooleanSafely(albumTemplateObject, "HideOwner");
		external = JSONUtils.getBooleanSafely(albumTemplateObject, "External");
		smugSearchable = JSONUtils.getBooleanSafely(albumTemplateObject,
				"SmugSearchable");
		worldSearchable = JSONUtils.getBooleanSafely(albumTemplateObject,
				"WorldSearchable");
		larges = JSONUtils.getBooleanSafely(albumTemplateObject, "Larges");
		xLarges = JSONUtils.getBooleanSafely(albumTemplateObject, "XLarges");
		x2Larges = JSONUtils.getBooleanSafely(albumTemplateObject, "X2Larges");
		x3Larges = JSONUtils.getBooleanSafely(albumTemplateObject, "X3Larges");
		originals = JSONUtils
				.getBooleanSafely(albumTemplateObject, "Originals");
		watermarking = JSONUtils.getBooleanSafely(albumTemplateObject,
				"Watermarking");

		if (!albumTemplateObject.isNull("Watermark"))
			watermarkID = JSONUtils.getIntegerSafely(albumTemplateObject
					.getJSONObject("Watermark"), "id");

		/* Parse social fields */
		share = JSONUtils.getBooleanSafely(albumTemplateObject, "Share");
		canRank = JSONUtils.getBooleanSafely(albumTemplateObject, "CanRank");
		comments = JSONUtils.getBooleanSafely(albumTemplateObject, "Comments");
		familyEdit = JSONUtils.getBooleanSafely(albumTemplateObject,
				"FamilyEdit");
		friendEdit = JSONUtils.getBooleanSafely(albumTemplateObject,
				"FriendEdit");

		/* Parse community fields */
		if (!albumTemplateObject.isNull("Community"))
			communityID = JSONUtils.getIntegerSafely(albumTemplateObject
					.getJSONObject("Community"), "id");

		/* Parse printing & sales fields */
		printable = JSONUtils
				.getBooleanSafely(albumTemplateObject, "Printable");
		proofDays = JSONUtils
				.getIntegerSafely(albumTemplateObject, "ProofDays");
		backprinting = JSONUtils.getStringSafely(albumTemplateObject,
				"Backprinting");
		defaultColor = JSONUtils.getBooleanSafely(albumTemplateObject,
				"DefaultColor");

		/* Parse sharpening fields */
		unsharpAmount = JSONUtils.getFloatSafely(albumTemplateObject,
				"UnsharpAmount");
		unsharpRadius = JSONUtils.getFloatSafely(albumTemplateObject,
				"UnsharpRadius");
		unsharpThreshold = JSONUtils.getFloatSafely(albumTemplateObject,
				"UnsharpThreshold");
		unsharpSigma = JSONUtils.getFloatSafely(albumTemplateObject,
				"UnsharpSigma");
	}

	/**
	 * Construct an album template with the given values.
	 * 
	 * @param id
	 *            The ID of the album.
	 * @param albumTemplateName
	 *            The name of the album template.
	 * @param geography
	 *            <code>true</code> if the album allows the use of geo-tagging
	 *            images (location), otherwise <code>false</code>.
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
	 */
	public AlbumTemplate(Integer id, String albumTemplateName,
			Boolean geography, Boolean exif, Boolean clean, Boolean header,
			Boolean filenames, Integer templateID, String sortMethod,
			Boolean sortDirection, Boolean squareThumbs, String password,
			String passwordHint, Boolean isProtected, Boolean isPublic,
			Boolean hideOwner, Boolean external, Boolean smugSearchable,
			Boolean worldSearchable, Boolean larges, Boolean xLarges,
			Boolean x2Larges, Boolean x3Larges, Boolean originals,
			Boolean watermarking, Integer watermarkID, Boolean share,
			Boolean canRank, Boolean comments, Boolean familyEdit,
			Boolean friendEdit, Integer communityID, Boolean printable,
			Integer proofDays, String backprinting, Boolean defaultColor,
			Float unsharpAmount, Float unsharpRadius, Float unsharpThreshold,
			Float unsharpSigma) {
		/* Set Required fields */
		this.id = id;
		this.albumTemplateName = albumTemplateName;

		/* Set Common fields */
		this.geography = geography;

		/* Set Look & Feel fields */
		this.exif = exif;
		this.clean = clean;
		this.header = header;
		this.filenames = filenames;
		this.templateID = templateID;
		this.sortMethod = sortMethod;
		this.sortDirection = sortDirection;
		this.squareThumbs = squareThumbs;

		/* Set Security & Privacy fields */
		this.password = password;
		this.passwordHint = passwordHint;
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
	}

	@Override
	public String toString() {
		return AlbumTemplate.class.getName() + "[id=" + getID()
				+ ", albumTemplateName=" + getAlbumTemplateName()
				+ ", geography=" + isGeography() + ", EXIF=" + isExif()
				+ ", clean=" + isClean() + ", header=" + isHeader()
				+ ", filenames=" + isFilenames() + ", templateID="
				+ getTemplateID() + ", sortMethod=" + getSortMethod()
				+ ", sortDirection=" + getSortDirection() + ", squareThumbs="
				+ isSquareThumbs() + ", password=" + getPassword()
				+ ", passwordHint=" + getPasswordHint() + ", protected"
				+ isProtected() + ", public" + isPublic() + ", hideOwner="
				+ isHideOwner() + ", external=" + isExternal()
				+ ", smugSearchable=" + isSmugSearchable()
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
				+ ", unsharpSigma=" + getUnsharpSigma() + "]";
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
	 * Used to get the name of the album template.
	 * 
	 * @return the name of the album template.
	 */
	public String getAlbumTemplateName() {
		return albumTemplateName;
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
}
