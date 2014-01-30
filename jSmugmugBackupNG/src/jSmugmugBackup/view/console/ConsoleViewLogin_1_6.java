/*
 * Created on Oct 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.console;

import jSmugmugBackup.view.ILoginView;
import jSmugmugBackup.model.*;
import java.util.Scanner;

public class ConsoleViewLogin_1_6 implements ILoginView
{
    private String initUserEmail;
    private String initPassword;

    public ConsoleViewLogin_1_6()
    {
        this.initUserEmail = null;
        this.initPassword = null;
    }

    public ConsoleViewLogin_1_6(String userEmail, String password)
    {
        this.initUserEmail = userEmail;
        this.initPassword  = password;
    }

    public ILoginDialogResult getLoginDialogResult()
    {
        this.initUserEmail = this.requestUserEmail();
        this.initPassword  = this.requestPassword();

        return new LoginDialogResult(this.initUserEmail, this.initPassword);
    }

    //--------------------------------------------------------------------------

    private String requestUserEmail()
    {
        if (this.initUserEmail != null)
        {
            System.out.println("    username: " + this.initUserEmail);
            return this.initUserEmail;
        }

        String userEmail = null;
        System.out.print("    username: ");
        Scanner in = new Scanner(System.in);
        userEmail = in.nextLine();

        return userEmail;
    }

    private String requestPassword()
    {
        if (this.initPassword != null) { return this.initPassword; }

        String password = null;

        java.io.Console console = System.console(); //seems not to work in the eclipse console, returns null
        if(console != null)
        {
            password = new String(System.console().readPassword("    password: "));
        }
        else
        {
            System.out.println("error, couldn't allocate console (running eclipse?) ... let's do it oldschool ...");

            System.out.print("    password: ");
            Scanner in = new Scanner(System.in);
            password = in.nextLine();
        }

        return password;
    }


}
