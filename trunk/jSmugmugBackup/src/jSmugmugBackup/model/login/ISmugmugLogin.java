/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;


public interface ISmugmugLogin
{
	Session login();
	Session reLogin();
    Session getToken();
    void logout();
}

