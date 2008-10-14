/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.login;

import com.streetsofboston.smugmug.v1_2_1.system.Session;

public interface ISmugmugLogin
{
	Session login();
	Session reLogin();
    Session getToken();
    void logout();
}

