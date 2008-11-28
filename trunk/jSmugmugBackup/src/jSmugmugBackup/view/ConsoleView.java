/*
 * Created on Nov 28, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import jSmugmugBackup.model.Constants;
import jSmugmugBackup.model.ITransferDialogResult;
import jSmugmugBackup.model.Model;
import jSmugmugBackup.model.accountLayer.IRootElement;
import jSmugmugBackup.view.login.ILoginView;
import jSmugmugBackup.view.login.LoginViewConsole_1_5;
import jSmugmugBackup.view.login.LoginViewConsole_1_6;

import java.awt.event.ActionListener;

public class ConsoleView implements IView
{
	private Model model = null;
	private Logger log = null;
	
	private ActionListener loginButtonListener = null;
	private ActionListener uploadDialogButtonListener = null;
	private ActionListener uploadStartButtonListener = null;
	private ActionListener downloadDialogButtonListener = null;
	private ActionListener downloadStartButtonListener = null;
	private ActionListener verifyDialogButtonListener = null;
	private ActionListener verifyStartButtonListener = null;
	private ActionListener deleteDialogButtonListener = null;
	private ActionListener deleteStartButtonListener = null;
	private ActionListener refreshButtonListener = null;
	private ActionListener sortButtonListener = null;
	private ActionListener quitButtonListener = null;
	
	public ConsoleView(Model model)
	{
		this.model = model;
		this.model.setView(this);
		this.log = Logger.getInstance();
		this.log.registerView(this);
	}

	public void start()
	{
		this.log.printLogLine("jSmugmugBackup v" + Constants.version);

		//todo: read commands from commandline
		
		this.model.quitApplication();
	}
	
	public void addLoginButtonListener(ActionListener listener)          { this.loginButtonListener = listener; }
	public void addUploadDialogButtonListener(ActionListener listener)   { this.uploadDialogButtonListener = listener; }
	public void addUploadStartButtonListener(ActionListener listener)    { this.uploadStartButtonListener = listener; }
	public void addDownloadDialogButtonListener(ActionListener listener) { this.downloadDialogButtonListener = listener; }
	public void addDownloadStartButtonListener(ActionListener listener)  { this.downloadStartButtonListener = listener; }
	public void addVerifyDialogButtonListener(ActionListener listener)   { this.verifyDialogButtonListener = listener; }
	public void addVerifyStartButtonListener(ActionListener listener)    { this.verifyStartButtonListener = listener; }
	public void addDeleteDialogButtonListener(ActionListener listener)   { this.deleteDialogButtonListener = listener; }
	public void addDeleteStartButtonListener(ActionListener listener)    { this.deleteStartButtonListener = listener; }
	public void addRefreshButtonListener(ActionListener listener)        { this.refreshButtonListener = listener; }
	public void addSortButtonListener(ActionListener listener)           { this.sortButtonListener = listener; }
	public void addQuitButtonListener(ActionListener listener)           { this.quitButtonListener = listener; }

	

	public ILoginView getLoginMethod()
	{
		ILoginView loginMethod = null;
		
    	//this should allow the program to run, even if only java 1.5 is available
    	if (java.lang.System.getProperty("java.specification.version").equals("1.5"))
    	{
    		loginMethod = new LoginViewConsole_1_5();
    	}
    	else //assuming we have Java 1.6 or higher
    	{
    		loginMethod = new LoginViewConsole_1_6();
    	}
    	

    	return loginMethod;
	}

	public void printLog(String text)
	{
		// TODO Auto-generated method stub

	}
	
	public void showError(String errMessage) {
		// TODO Auto-generated method stub

	}

	public void showBusyStart(String waitingMessage)
	{
		/* noop */
	}

	public void showBusyStop()
	{
		/* noop */
	}

	public void refreshFileListing(IRootElement smugmugRoot) {
		// TODO Auto-generated method stub

	}
	
	public ITransferDialogResult showDeleteDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITransferDialogResult showDownloadDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITransferDialogResult showListDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITransferDialogResult showSortDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITransferDialogResult showUploadDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITransferDialogResult showVerifyDialog() {
		// TODO Auto-generated method stub
		return null;
	}

}
