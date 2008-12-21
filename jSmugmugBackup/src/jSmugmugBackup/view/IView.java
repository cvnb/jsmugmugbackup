/*
 * Created on Sep 7, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import jSmugmugBackup.model.accountLayer.ICategory;
import jSmugmugBackup.model.accountLayer.IRootElement;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.ILoginView;

import java.awt.event.*;
import java.util.Vector;


public interface IView
{
	void start();
	
	//ILoginView getLoginMethod();
	void updateFileListing(IRootElement smugmugRoot);
	
    ILoginDialogResult showLoginDialog();
    ITransferDialogResult showListDialog();
	ITransferDialogResult showSortDialog();
	ITransferDialogResult showUploadDialog();
	ITransferDialogResult showDownloadDialog();
	ITransferDialogResult showVerifyDialog();
	ITransferDialogResult showDeleteDialog();
	void printLog(String text);
	void showError(String errMessage);
	void showBusyStart(String waitingMessage);
	void showBusyStop();
	
	void addLoginButtonListener(ActionListener listener);
	void addListButtonListener(ActionListener listener);
	void addSortButtonListener(ActionListener listener);
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
