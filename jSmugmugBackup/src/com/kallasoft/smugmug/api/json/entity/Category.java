package com.kallasoft.smugmug.api.json.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kallasoft.smugmug.api.json.util.JSONUtils;
import com.kallasoft.smugmug.api.util.APIUtils;

/**
 * Class used to define a category (or subcategory) and the subcategories and
 * albums it contains.
 * <p>
 * In some cases when the instance represents a subcategory, there will be a
 * value present for parentCategoryID, otherwise in most cases that value will
 * be <code>null</code>.
 * 
 * @author Riyad Kalla
 */
public class Category {
	private Integer id;

	private String name;

	private Integer parentCategoryID;

	private List<Album> albumList = new ArrayList<Album>();

	private List<Category> subCategoryList = new ArrayList<Category>();

	/**
	 * Construct a new category or subcategory by safely parsing all known
	 * values out of the given {@link JSONObject}.
	 * <p>
	 * If a property is not available from the {@link JSONObject}, it's value
	 * is simply set to <code>null</code> and ignored and parsing continues.
	 * 
	 * @param categoryObject
	 *            The {@link JSONObject} that will be queried for all the known
	 *            properties for this category or subcategory.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>categoryObject</code> is null.
	 * 
	 * @throws JSONException
	 *             if any JSONObject-related exception occurs.
	 */
	public Category(JSONObject categoryObject) throws IllegalArgumentException,
			JSONException {
		if (categoryObject == null)
			throw new IllegalArgumentException("categoryObject cannot be null");

		/* Parse the known values */
		id = JSONUtils.getIntegerSafely(categoryObject, "id");
		name = JSONUtils.getStringSafely(categoryObject, "Title");

		/*
		 * If name is still null, we are probably running against the 1.2.1 API
		 * which now uses the "Name" argument. So we don't have to provide a
		 * completely separate implementation of GetTree for 1.2.1, we just do
		 * the simple check here.
		 */
		if (APIUtils.isEmpty(name))
			name = JSONUtils.getStringSafely(categoryObject, "Name");

		if (!categoryObject.isNull("Category"))
			parentCategoryID = JSONUtils.getIntegerSafely(categoryObject
					.getJSONObject("Category"), "id");

		/*
		 * It's possible, like with users.getTree, for a Category to contain an
		 * array of Albums and SubCategories. We try and load those now for this
		 * particular Category.
		 */
		if (!categoryObject.isNull("Albums"))
			this.albumList = parseAlbumList(categoryObject
					.getJSONArray("Albums"));

		/*
		 * Because subcategory parsing uses this same class, it will recurse and
		 * parse embedded Album objects as necessary as well. So by the time
		 * this portion of code completes, the subcategory list will also
		 * contain it's embedded albums if there were any.
		 */
		if (!categoryObject.isNull("SubCategories"))
			this.subCategoryList = parseSubCategoryList(categoryObject
					.getJSONArray("SubCategories"));
	}

	/**
	 * Construct a category or subcategory with the given parameters.
	 * 
	 * @param id
	 *            The ID of the category or subcategory.
	 * @param name
	 *            The name of the category or subcategory.
	 */
	public Category(Integer id, String name) {
		this(id, name, null);
	}

	/**
	 * Construct a category or subcategory with the given parameters.
	 * 
	 * @param id
	 *            The ID of the category or subcategory.
	 * @param name
	 *            The name of the category or subcategory.
	 * @param parentCategoryID
	 *            The ID of the parent category to this subcategory.
	 */
	public Category(Integer id, String name, Integer parentCategoryID) {
		this(id, name, parentCategoryID, null, null);
	}

	/**
	 * Construct a category or subcategory with the given parameters and
	 * optional lists of subcategories and albums that it may contain.
	 * 
	 * @param id
	 *            The ID of the category or subcategory.
	 * @param name
	 *            The name of the category or subcategory.
	 * @param parentCategoryID
	 *            The ID of the parent category to this subcategory.
	 * @param albumList
	 *            The list of {@link Album} that belong to this category or
	 *            subcategory, if any.
	 * @param subCategoryList
	 *            A list of {@link Category} that represent the subcategories of
	 *            the given category, if any.
	 */
	public Category(Integer id, String name, Integer parentCategoryID,
			List<Album> albumList, List<Category> subCategoryList) {
		this.id = id;
		this.name = name;
		this.parentCategoryID = parentCategoryID;
		this.albumList = albumList;
		this.subCategoryList = subCategoryList;
	}

	@Override
	public String toString() {
		return Category.class.getName() + "[id=" + getID() + ", name="
				+ getName() + ", parentCategoryID=" + getParentCategoryID()
				+ ", albumListSize=" + getAlbumList().size()
				+ ", subCategoryListSize=" + getSubCategoryList().size() + "]";
	}

	/**
	 * Used to get the ID of the category or subcategory.
	 * 
	 * @return the ID of the category or subcategory.
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * Used to get the name of the category or subcategory.
	 * 
	 * @return the name of the category or subcategory.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Used to get the ID of the parent category of subcategory. Unless this
	 * instance represents a subcategory, this value will usually return
	 * <code>null</code>.
	 * 
	 * @return the ID of the parent category of subcategory.
	 */
	public Integer getParentCategoryID() {
		return parentCategoryID;
	}

	/**
	 * Used to get a list of the albums (if any) that this category or
	 * subcategory has associated with it.
	 * 
	 * @return a list of the albums (if any) that this category or subcategory
	 *         has associated with it.
	 */
	public List<Album> getAlbumList() {
		return albumList;
	}

	/**
	 * Used to get a list of the subcategories (if any) that this category has
	 * associated with it.
	 * 
	 * @return a list of the subcategories (if any) that this category has
	 *         associated with it.
	 */
	public List<Category> getSubCategoryList() {
		return subCategoryList;
	}

	/**
	 * Used to parse the list of albums that can occur under categories or
	 * subcategory nodes.
	 * 
	 * @param albumArray
	 *            The {@link JSONArray} that will be parsed for the containing
	 *            {@link Album} entities.
	 * 
	 * @return an list of {@link Album} instances.
	 * 
	 * @throws JSONException
	 *             if any error JSONObject-specific error occur.
	 */
	protected List<Album> parseAlbumList(JSONArray albumArray)
			throws JSONException {
		List<Album> albumList = new ArrayList<Album>();

		if (albumArray != null) {
			for (int i = 0, length = albumArray.length(); i < length; i++)
				albumList.add(new Album(albumArray.getJSONObject(i)));
		}

		return albumList;
	}

	/**
	 * Used to parse the list of subcategories that can occur under category
	 * nodes.
	 * 
	 * @param subCategoryArray
	 *            The {@link JSONArray} that will be parsed for the containing
	 *            subcategory entities.
	 * 
	 * @return an list of {@link Category} instances.
	 * 
	 * @throws JSONException
	 *             if any error JSONObject-specific error occur.
	 */
	protected List<Category> parseSubCategoryList(JSONArray subCategoryArray)
			throws JSONException {
		List<Category> subCategoryList = new ArrayList<Category>();

		if (subCategoryArray != null) {
			for (int i = 0, length = subCategoryArray.length(); i < length; i++)
				subCategoryList.add(new Category(subCategoryArray
						.getJSONObject(i)));
		}

		return subCategoryList;
	}
}
