/*
 * Created on Sep 25, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model;

public class TransferDialogResult implements ITransferDialogResult
{
	private String categoryName;
	private String subCategoryName;
	private String albumName;
	private String targetDir;
    private String albumKeywords;
	
	public TransferDialogResult(String categoryName, String subCategoryName, String albumName, String targetDir, String albumKeywords)
	{
		this.categoryName = categoryName;
		this.subCategoryName = subCategoryName;
		this.albumName = albumName;
		this.targetDir = targetDir;
        this.albumKeywords = albumKeywords;
	}


	//@Override
	public String getCategoryName() { return this.categoryName; }

	//@Override
	public String getSubCategoryName() { return this.subCategoryName; }

	//@Override
	public String getAlbumName() { return this.albumName; }
	
	//@Override
	public String getDir() { return this.targetDir; }

    public String getAlbumKeywords() { return this.albumKeywords; }
}
