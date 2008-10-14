/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;


import java.util.Scanner;

public class SmugmugLoginConsole_1_6 extends SmugmugLogin
{
	private String initUserEmail = null;
	
	public SmugmugLoginConsole_1_6()
	{
		this.initUserEmail = null;
	}
	
	public SmugmugLoginConsole_1_6(String userEmail)
	{
		this.initUserEmail = userEmail;
	}
	
	protected String requestUserEmail()
	{
		if (this.initUserEmail != null) { return this.initUserEmail; }
		
		String userEmail = null;
		System.out.print("    Username (Email): ");
		Scanner in = new Scanner(System.in);
		userEmail = in.nextLine();
		return userEmail;
	}
	
	protected String requestPassword()
	{
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
