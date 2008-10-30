/*
 * Created on Oct 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.login;

import java.util.Scanner;

public class LoginViewConsole_1_6 implements ILoginView
{
	private String initUserEmail;
	private String initPassword;
	
	public LoginViewConsole_1_6()
	{
		this.initUserEmail = null;
		this.initPassword = null;
	}
	
	public LoginViewConsole_1_6(String userEmail, String password)
	{
		this.initUserEmail = userEmail;
		this.initPassword  = password;
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
		if (this.initPassword != null) { return this.initPassword; }
		
		String password = null;
		
		java.io.Console console = System.console(); //seems not to work in the eclipse console, returns null
		if(console != null)
		{
			password = new String(System.console().readPassword("    Password: "));
		}
		else
		{
			System.out.println("error, couldn't allocate console (running eclipse?) ... let's do it oldschool ...");
    		
    		System.out.print("    Password: ");
    		Scanner in = new Scanner(System.in);
    		password = in.nextLine();
		}
		
		return password;
	}
}
