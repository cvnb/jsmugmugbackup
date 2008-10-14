/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;

import javax.swing.JFrame;

import jSmugmugBackup.view.*;


public class SmugmugLoginSwing extends SmugmugLogin
{
	private Logger log = null;
	private SwingView view = null;

	private String initUserEmail = null;
	private String initPassword = null;
	
	
//	public SmugmugLoginSwing(String userEmail, String password)
//	{
//		this.initUserEmail = userEmail;
//		this.initPassword = password;
//	}
	public SmugmugLoginSwing(SwingView view)
	{
		this.log = Logger.getInstance();
		this.view = view;
		

	}


	protected String requestUserEmail()
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


	protected String requestPassword()
	{
		return this.initPassword;
	}

}
