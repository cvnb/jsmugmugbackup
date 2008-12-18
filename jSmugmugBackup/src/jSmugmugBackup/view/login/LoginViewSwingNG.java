///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package jSmugmugBackup.view.login;
//
//import jSmugmugBackup.model.Helper;
//import jSmugmugBackup.view.Logger;
//import jSmugmugBackup.view.ng.SwingViewNG;
//import jSmugmugBackup.view.ng.SwingViewNGLoginDialog;
//import javax.swing.JFrame;
//
///**
// *
// * @author paul
// */
//public class LoginViewSwingNG implements ILoginView
//{
//	private Logger log = null;
//	private SwingViewNG view = null;
//
//	private String initUserEmail = null;
//	private String initPassword = null;
//
//
////	public LoginViewSwing(String userEmail, String password)
////	{
////		this.initUserEmail = userEmail;
////		this.initPassword = password;
////	}
//	public LoginViewSwingNG(SwingViewNG view)
//	{
//		this.log = Logger.getInstance();
//		this.view = view;
//
//
//	}
//
//
//	public String requestUserEmail()
//	{
//        this.log.printLogLine("requesting user email");
//
//		SwingViewNGLoginDialog dialog = null;
//		dialog = new SwingViewNGLoginDialog( this.view.getFrame(), true );
//		dialog.setVisible(true);
//
//
//        /*
//		//dirty hack - wait until ok button was pressed
//		//...
//		Helper.pause(100); // just wait a little
//		while (dialog.isVisible()) { this.log.printLogLine("requesting user email: wait"); Helper.pause(100); }
//		//username and password should now be available in view
//         * */
//
//
//		this.initUserEmail = dialog.getUsernameTextField_Text();
//		this.initPassword = dialog.getPasswordTextField_Password();
//
//        this.log.printLogLine("requesting user email: returning");
//
//		return this.initUserEmail;
//	}
//
//
//	public String requestPassword()
//	{
//		return this.initPassword;
//	}
//
//}
