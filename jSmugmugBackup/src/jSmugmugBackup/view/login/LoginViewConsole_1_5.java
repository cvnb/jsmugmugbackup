/*
 * Created on Oct 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.login;

import java.util.Scanner;

public class LoginViewConsole_1_5 implements ILoginView
{
	private String initUserEmail = null;


	public LoginViewConsole_1_5()
	{
		this.initUserEmail = null;
	}

	public LoginViewConsole_1_5(String userEmail)
	{
		this.initUserEmail = userEmail;
	}


	public String requestUserEmail()
	{
		if (this.initUserEmail != null) { return this.initUserEmail; }
		
		String userEmail = null;
		System.out.print("    Username (Email): ");
		Scanner in = new Scanner(System.in);
		userEmail = in.nextLine();
		return userEmail;
	}
	

	public String requestPassword()
	{
		String password = null;
		
		//this should be Java 5 compatible
		System.out.print("    Password: ");
		Scanner in = new Scanner(System.in);
		password = in.nextLine();
		
		return password;
	}

}
