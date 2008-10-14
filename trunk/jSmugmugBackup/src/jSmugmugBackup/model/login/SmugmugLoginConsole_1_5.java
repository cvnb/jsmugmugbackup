/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;


import java.util.Scanner;

public class SmugmugLoginConsole_1_5 extends SmugmugLogin
{
	private String initUserEmail = null;


	public SmugmugLoginConsole_1_5()
	{
		this.initUserEmail = null;
	}

	public SmugmugLoginConsole_1_5(String userEmail)
	{
		this.initUserEmail = userEmail;
	}


	@Override
	protected String requestUserEmail()
	{
		if (this.initUserEmail != null) { return this.initUserEmail; }
		
		String userEmail = null;
		System.out.print("    Username (Email): ");
		Scanner in = new Scanner(System.in);
		userEmail = in.nextLine();
		return userEmail;
	}
	
	@Override
	protected String requestPassword()
	{
		String password = null;
		
		//this should be Java 5 compatible
		System.out.print("    Password: ");
		Scanner in = new Scanner(System.in);
		password = in.nextLine();
		
		return password;
	}

}
