/*
 * Created on Oct 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.login;

import jSmugmugBackup.view.Logger;
import jSmugmugBackup.view.SmugmugLoginSwingDialog;
import jSmugmugBackup.view.SwingView;

import javax.swing.JFrame;

public class LoginViewSwing implements ILoginView
{
	private Logger log = null;
	private SwingView view = null;

	private String initUserEmail = null;
	private String initPassword = null;
	
	
//	public LoginViewSwing(String userEmail, String password)
//	{
//		this.initUserEmail = userEmail;
//		this.initPassword = password;
//	}
	public LoginViewSwing(SwingView view)
	{
		this.log = Logger.getInstance();
		this.view = view;
		

	}


	public String requestUserEmail()
	{
		SmugmugLoginSwingDialog dialog = null;
		dialog = new SmugmugLoginSwingDialog( (JFrame)this.view );
		dialog.setModal(true); //doesn't work here
		dialog.setVisible(true);

		
		//dirty hack - wait until ok button was pressed
		//...
		this.pause(100); // just wait a little
		while (dialog.isVisible()) this.pause(100);
		//username and password should nopw be available in view
		
		
		this.initUserEmail = dialog.getJTextField_username().getText();
		this.initPassword = dialog.getJPasswordField_password().getText();
		
		return this.initUserEmail;
	}


	public String requestPassword()
	{
		return this.initPassword;
	}
	
	private void pause(long millisecs)
	{
		try
		{
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {}
	}
}
