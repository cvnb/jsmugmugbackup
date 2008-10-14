/*
 * Created on Sep 7, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.data.*;
import jSmugmugBackup.model.login.*;

import java.awt.event.*;


public interface IView
{
	ISmugmugLogin getLoginToken();
	void refreshFileListing(AccountListing accountListing);
	ITransferDialogResult showUploadDialog();
	ITransferDialogResult showDownloadDialog();
	ITransferDialogResult showVerifyDialog();
	ITransferDialogResult showDeleteDialog();
	void printLog(String text);
	void showError(String errMessage);
	
	
	void addLoginButtonListener(ActionListener listener);
	void addRefreshButtonListener(ActionListener listener);
	void addUploadDialogButtonListener(ActionListener listener);
	void addUploadStartButtonListener(ActionListener listener);
	void addDownloadDialogButtonListener(ActionListener listener);
	void addDownloadStartButtonListener(ActionListener listener);
	void addVerifyDialogButtonListener(ActionListener listener);
	void addVerifyStartButtonListener(ActionListener listener);
	void addDeleteDialogButtonListener(ActionListener listener);
	void addDeleteStartButtonListener(ActionListener listener);
	void addQuitButtonListener(ActionListener listener);

}
