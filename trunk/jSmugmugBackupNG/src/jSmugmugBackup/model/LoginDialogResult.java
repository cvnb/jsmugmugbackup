/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model;

/**
 *
 * @author paul
 */
public class LoginDialogResult implements ILoginDialogResult
{
    private String loginUsername = null;
    private String loginPassword = null;

    public LoginDialogResult(String username, String password)
    {
        this.loginUsername = username;
        this.loginPassword = password;

    }

    public String getLoginUsername()
    {
        return this.loginUsername;
    }

    public String getLoginPassword()
    {
        return this.loginPassword;
    }

}
