/*
 * Created on Oct 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.main;

import java.util.Scanner;

import jSmugmugBackup.abstractionLayerNG.*;
import jSmugmugBackup.abstractionLayerNG.data.*;

public class Tests_main {

	public static void main(String[] args)
	{
		//get login data
		String userEmail = null;
		System.out.print("    Username (Email): ");
		Scanner in = new Scanner(System.in);
		userEmail = in.nextLine();
		
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
    		password = in.nextLine();
		}
		
		
		
		//test SmugmugConnectorNG
		ISmugmugConnectorNG connector = new SmugmugConnectorNG(false);
		connector.login(userEmail, password);
		//connector.getTree();
		connector.getImages();
		connector.logout();

		//test AccountListingProxy
		IAccountListingProxy proxy = new AccountListingProxy();
		proxy.login(userEmail, password);
		proxy.getCategoryList();
		proxy.logout();

	}

}
