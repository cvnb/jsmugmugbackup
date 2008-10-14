/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;


import jSmugmugBackup.model.Constants;
import jSmugmugBackup.view.Logger;

import com.streetsofboston.smugmug.v1_2_1.exceptions.SmugmugException;
import com.streetsofboston.smugmug.v1_2_1.system.Session;

/*
 * SmugmugLogin is a Singleton
 * WARNING: concurrent access probably fails!
 */
public abstract class SmugmugLogin implements ISmugmugLogin
{	
	private Logger log = null;
	private Session mSession = null;
//	private String userEmail = null;
//	private String password = null;
	
	public SmugmugLogin()
	{
		this.log = Logger.getInstance();
	}
	
	public Session login()
	{
		if (this.mSession != null) return this.mSession;

		
		this.log.printLogLine("logging in ... ");
//		if ( (this.userEmail == null) && (this.password == null) )
//		{
//			this.userEmail = this.requestUserEmail();
//			this.password = this.requestPassword();
//		}
		

    	try
    	{
//    		this.mSession = Session.login(this.userEmail, this.password);
    		this.mSession = Session.login(this.requestUserEmail(), this.requestPassword());
    		this.log.printLogLine("... login ok!");
    		this.log.printLogLine("successfully logged into SmugMug!");
    		
    		return this.mSession;
    	}
    	catch (SmugmugException e)
    	{
    		this.log.printLogLine(" ... login failed!");
    		if ( !e.getMessage().endsWith("invalid login") )
    		{
	    		this.log.printLogLine(e.getMessage());
	    		e.printStackTrace();
    		}
    	}
    	
    	return null;    	
	}
	
	public Session reLogin()
	{
		if (this.mSession == null) return this.login();
		
		boolean repeat = false;
		
		do
		{
			try
			{ 
				this.mSession = this.mSession.reLogin();
				return this.mSession;
			}
			catch (SmugmugException e)
			{
				this.log.printLogLine("A SmugmugException occured during relogin, aborting!");
				this.log.printLogLine("  ERROR: Message :" + e.getMessage());
				e.printStackTrace();
				
				this.mSession = null;
				return null;
			}
			catch (java.lang.RuntimeException e)
			{				
				this.log.printLogLine("relogin failed, retrying ...");
				this.log.printLogLine("  ERROR: A java.lang.RuntimeException occured during relogin!");
				this.log.printLogLine("  ERROR: Message:" + e.getMessage());
				if (e.getMessage() != null) this.log.printLogLine("  ERROR: Message: cause:" + e.getCause().getMessage());
				this.log.printLogLine("  ERROR: waiting a few secs");
				this.pause(Constants.retryWait);
				
				repeat = true;
			}
		} while (repeat); //infinite loop until repeat becomes false
		
		
		this.mSession = null;
		return null;
	}
	
	public Session getToken()
	{
		//if we're not logged in, try to log in ... this could be annoying in some cases
		//if (this.mSession == null) return this.login();
		
		return this.mSession;
	}

	public void logout()
	{
    	//logout from smugmug
    	if (this.mSession != null)
    	{
    		this.log.printLog("logging out ... ");
//    		this.userEmail = null;
//    		this.password = null;
    		try
    		{    			
    			this.mSession.logout();
    			this.log.printLogLine("ok");
    		}
    		catch(Exception e)
    		{
    			this.log.printLogLine("failed");
    			e.printStackTrace();
    		}
    	}		
	}
	
	
	protected abstract String requestUserEmail();
	protected abstract String requestPassword();
	
	protected void pause(long millisecs)
	{
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {}
	}
}
