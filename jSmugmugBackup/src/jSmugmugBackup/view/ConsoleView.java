/*
 * Created on Nov 28, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import jSmugmugBackup.model.Constants;
import jSmugmugBackup.model.ITransferDialogResult;
import jSmugmugBackup.model.Model;
import jSmugmugBackup.model.TransferDialogResult;
import jSmugmugBackup.model.accountLayer.IAlbum;
import jSmugmugBackup.model.accountLayer.ICategory;
import jSmugmugBackup.model.accountLayer.IImage;
import jSmugmugBackup.model.accountLayer.IRootElement;
import jSmugmugBackup.model.accountLayer.ISubcategory;
import jSmugmugBackup.view.login.ILoginView;
import jSmugmugBackup.view.login.LoginViewConsole_1_5;
import jSmugmugBackup.view.login.LoginViewConsole_1_6;

import java.awt.event.ActionListener;
import java.io.Console;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class ConsoleView implements IView
{
	private Model model = null;
	private Logger log = null;
	
	private Console console = null;
	private Vector<String> lastInputTokenVector = null; //only the last input is beeing stored here
	
	private ActionListener loginButtonListener = null;
	private ActionListener uploadDialogButtonListener = null;
	private ActionListener uploadStartButtonListener = null;
	private ActionListener downloadDialogButtonListener = null;
	private ActionListener downloadStartButtonListener = null;
	private ActionListener verifyDialogButtonListener = null;
	private ActionListener verifyStartButtonListener = null;
	private ActionListener deleteDialogButtonListener = null;
	private ActionListener deleteStartButtonListener = null;
	private ActionListener refreshButtonListener = null;
	private ActionListener sortButtonListener = null;
	private ActionListener quitButtonListener = null;
	
	public ConsoleView(Model model)
	{
		this.model = model;
		this.model.setView(this);

		this.console = System.console(); //allocate console
		if(console == null) { this.model.quitApplication(); }
		
		this.log = Logger.getInstance();
		this.log.registerView(this);
	}

	public void start()
	{
		this.log.printLogLine("jSmugmugBackup v" + Constants.version);

		this.console.printf("type \"help\" for available commands\n");
		
		//todo: read commands from commandline
		String command = "";
		while (true)
		{			
			this.console.printf("jSmugmugBackup> ");
			
			command = this.console.readLine();
			command = command.toLowerCase(); //make it independent from case
			this.lastInputTokenVector = new Vector<String>();
			StringTokenizer st = new StringTokenizer(command);
			while (st.hasMoreTokens())
			{
				String currentToken = st.nextToken();
				//handle parenthesis
				if ( (currentToken.startsWith("\"")) || (currentToken.startsWith("\'")) )
				{
					do
					{
						if ( st.hasMoreTokens() ) { currentToken = currentToken + st.nextToken(); }
						else { this.console.printf("INPUT ERROR: problem while parsing parenthesis"); }
					} while ( (!currentToken.endsWith("\"")) && (!currentToken.endsWith("\'")) );
					
					//remove leading and tailing characters ... which are hopefully parenthesis
					this.console.printf("parenthesis detected: %s" + currentToken);
					currentToken = currentToken.substring(1, currentToken.length()-2);
					this.console.printf("finally: %s" + currentToken);
				}				
				
				this.lastInputTokenVector.add( currentToken );
			}

			String action = this.lastInputTokenVector.firstElement();
			if (action.equals("help"))
			{
				if (this.lastInputTokenVector.size() > 1)
				{
					this.printHelp(this.lastInputTokenVector.get(1));
				}
				else { this.printHelp(null); }

			}
			else if (action.equals("login"))
			{
				this.loginButtonListener.actionPerformed(null);	//trigger the login-button action listener
			}
			else if (action.equals("list"))
			{
				//this.loginButtonListener.actionPerformed(null);	//trigger the login-button action listener
				this.refreshButtonListener.actionPerformed(null);
			}
			else if (action.equals("sort"))
			{
				//this.loginButtonListener.actionPerformed(null);
				this.sortButtonListener.actionPerformed(null);
			}
			else if (action.equals("upload"))
			{
				//this.loginButtonListener.actionPerformed(null);
				this.uploadDialogButtonListener.actionPerformed(null);
				this.uploadStartButtonListener.actionPerformed(null);
			}
			else if (action.equals("download"))
			{
				//this.loginButtonListener.actionPerformed(null);
				this.downloadDialogButtonListener.actionPerformed(null);
				this.downloadStartButtonListener.actionPerformed(null);
			}
			else if (action.equals("verify"))
			{
				//this.loginButtonListener.actionPerformed(null);
				this.verifyDialogButtonListener.actionPerformed(null);
				this.verifyStartButtonListener.actionPerformed(null);
			}
			else if (action.equals("quit"))
			{
				this.model.quitApplication();
			}
		}		
		
	}
	
	public void addLoginButtonListener(ActionListener listener)          { this.loginButtonListener = listener; }
	public void addUploadDialogButtonListener(ActionListener listener)   { this.uploadDialogButtonListener = listener; }
	public void addUploadStartButtonListener(ActionListener listener)    { this.uploadStartButtonListener = listener; }
	public void addDownloadDialogButtonListener(ActionListener listener) { this.downloadDialogButtonListener = listener; }
	public void addDownloadStartButtonListener(ActionListener listener)  { this.downloadStartButtonListener = listener; }
	public void addVerifyDialogButtonListener(ActionListener listener)   { this.verifyDialogButtonListener = listener; }
	public void addVerifyStartButtonListener(ActionListener listener)    { this.verifyStartButtonListener = listener; }
	public void addDeleteDialogButtonListener(ActionListener listener)   { this.deleteDialogButtonListener = listener; }
	public void addDeleteStartButtonListener(ActionListener listener)    { this.deleteStartButtonListener = listener; }
	public void addRefreshButtonListener(ActionListener listener)        { this.refreshButtonListener = listener; }
	public void addSortButtonListener(ActionListener listener)           { this.sortButtonListener = listener; }
	public void addQuitButtonListener(ActionListener listener)           { this.quitButtonListener = listener; }

	

	public ILoginView getLoginMethod()
	{
		ILoginView loginMethod = null;

		if (this.lastInputTokenVector.size() == 1) // command: login
		{
			loginMethod = new LoginViewConsole_1_6();
		}
		else if (this.lastInputTokenVector.size() == 2) // command: login <email>
		{
			String username = this.lastInputTokenVector.get(1);
			loginMethod = new LoginViewConsole_1_6(username, null);
		}
		else
		{
			this.console.printf("INPUT ERROR: incorrect number of parameters, try \"help <action>\"\n");
		}

		//loginMethod = new LoginViewConsole_1_6();
		
    	return loginMethod;
	}

	public void printLog(String text)
	{
		this.console.printf(text);
	}
	
	public void showError(String errMessage) {
		// TODO Auto-generated method stub

	}

	public void showBusyStart(String waitingMessage)
	{
		/* noop */
	}

	public void showBusyStop()
	{
		/* noop */
	}

	public void refreshFileListing(IRootElement smugmugRoot)
	{		
		//display listing on console
		//this.log.printLogLine("Nickname: " + accountListing.getNickName());
		for (ICategory c : smugmugRoot.getCategoryList())
		{
			this.log.printLogLine("    category: " + c.getName());
			
			for (ISubcategory sc : c.getSubcategoryList())
			{
				this.log.printLogLine("        subCategory: " + sc.getName());
				for (IAlbum a : sc.getAlbumList())
				{
					this.log.printLogLine("            album: " + a.getName());
					for (IImage i : a.getImageList())
					{
						this.log.printLogLine("                image: " + i.getName());
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				this.log.printLogLine("        album: " + a.getName());
				for (IImage i : a.getImageList())
				{
					this.log.printLogLine("            image: " + i.getName());
				}
			}
		}
	}
	
	public ITransferDialogResult showListDialog()
	{
		String category = null;
		String subCategory = null;
		String album = null;
		if (this.lastInputTokenVector.size() == 1) // command: list
		{
			//everything is already initialized with null
			/* noop */
		}
		else if (this.lastInputTokenVector.size() == 4) // command: list <category> <subcategory> <album>
		{
			category = this.lastInputTokenVector.get(1);
			subCategory = this.lastInputTokenVector.get(2);
			album = this.lastInputTokenVector.get(3);
		}
		else
		{
			this.console.printf("INPUT ERROR: incorrect number of parameters, try \"help <action>\"\n");
		}
		
		return new TransferDialogResult(category, subCategory, album, null);
	}
	
	public ITransferDialogResult showSortDialog()
	{
		/*
		String category = null;
		String subCategory = null;
		String album = null;
		if (this.lastInputTokenVector.size() == 1) // command: sort
		{
			//everything is already initialized with null
			// noop
			
		}
		else if (this.lastInputTokenVector.size() == 4) // command: sort <category> <subcategory> <album>
		{
			category = this.lastInputTokenVector.get(1);
			subCategory = this.lastInputTokenVector.get(2);
			album = this.lastInputTokenVector.get(3);
		}
		else
		{
			this.console.printf("INPUT ERROR: incorrect number of parameters, try \"help sort\"");
		}
		
		return new TransferDialogResult(category, subCategory, album, null);
		*/
		
		//code is identical to list dialog
		return this.showListDialog();
	}
	
	public ITransferDialogResult showUploadDialog()
	{
		String category = null;
		String subCategory = null;
		String album = null;
		String dir = null;
		if (this.lastInputTokenVector.size() == 2) // command: upload <dir>
		{
			dir = this.lastInputTokenVector.get(1);
		}
		else if (this.lastInputTokenVector.size() == 4) // command: upload <category> <subcategory> <album> <dir>
		{
			category = this.lastInputTokenVector.get(1);
			subCategory = this.lastInputTokenVector.get(2);
			album = this.lastInputTokenVector.get(3);
			dir = this.lastInputTokenVector.get(4);
		}
		else
		{
			this.console.printf("INPUT ERROR: incorrect number of parameters, try \"help <action>\"\n");
		}
		
		return new TransferDialogResult(category, subCategory, album, dir);
	}
	
	public ITransferDialogResult showDownloadDialog()
	{
		//code is identical to upload dialog
		return this.showUploadDialog();
	}
	
	public ITransferDialogResult showVerifyDialog()
	{
		//code is identical to upload dialog
		return this.showUploadDialog();
	}

	public ITransferDialogResult showDeleteDialog()
	{
		// not implemented
		return null;
	}

	
	//-------------------- private -----------------------------
	private void printHelp(String command)
	{
		if ( (command == null) || (command.equals("")) )
		{
			this.console.printf("actions:\n");
			this.console.printf("  help [<action>] : print this help\n");
			this.console.printf("  login [<email>]                                   : log into your smugmug account\n");
			//this.console.printf("  logout                                            : logout\n");
			this.console.printf("  list [<category> <subcategory> <album>]           : list images\n");
			this.console.printf("  sort [<category> <subcategory> <album>]           : sort images by name\n");
			this.console.printf("  upload [<category> <subcategory> <album>] <dir>   : upload images to smugmug\n");
			this.console.printf("  download [<category> <subcategory> <album>] <dir> : download images from smugmug\n");
			this.console.printf("  verify [<category> <subcategory> <album>] <dir>   : compare local contents with your data on smugmug\n");
			this.console.printf("  quit                                              : quit application\n");
			this.console.printf("\n");
			this.console.printf("%s", Constants.helpNotes);
		}
		else if (command.equals("help"))
		{
			this.console.printf("usage: help [<action>]\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <action>     ... print more info on the specified action (optional)\n");
		}
		else if (command.equals("login"))
		{
			this.console.printf("usage: login [<email>]\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <email>      ... your email address or account name (optional)\n");
		}
//		else if (command.equals("logout"))
//		{
//			this.console.printf("usage: logout\n");
//			this.console.printf("\n");
//			this.console.printf("  ... no further info available\n");
//		}
		else if (command.equals("list"))
		{
			this.console.printf("usage: list [<category> <subcategory> <album>]\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <category>    ... only show images that match the given category name, use \"null\" for all categories (optional)\n");
			this.console.printf("  <subcategory> ... only show images that match the given subcategory name, use \"null\" for all subcategories (optional)\n");
			this.console.printf("  <album>       ... only show images that match the given album name, use \"null\" for all albums (optional)\n");
		}
		else if (command.equals("sort"))
		{
			this.console.printf("usage: sort [<category> <subcategory> <album>]\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <category>    ... only sort albums that match the given category name, use \"null\" for all categories (optional)\n");
			this.console.printf("  <subcategory> ... only sort albums that match the given subcategory name, use \"null\" for all subcategories (optional)\n");
			this.console.printf("  <album>       ... only sort albums that match the given album name, use \"null\" for all albums (optional)\n");
		}
		else if (command.equals("upload"))
		{
			this.console.printf("usage: upload [<category> <subcategory> <album>] <dir>\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <category>    ... use the given string as category name for uploaded images, use \"null\" for automatic (optional)\n");
			this.console.printf("  <subcategory> ... use the given string as subcategory name for uploaded images, use \"null\" for automatic (optional)\n");
			this.console.printf("  <album>       ... use the given string as album name for uploaded images, use \"null\" for automatic (optional)\n");
			this.console.printf("  <dir>         ... location of the files that should be uploaded (required)\n");
		}
		else if (command.equals("download"))
		{
			this.console.printf("usage: download [<category> <subcategory> <album>] <dir>\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <category>    ... download only images that match the given category name, use \"null\" for all categories (optional)\n");
			this.console.printf("  <subcategory> ... download only images that match the given subcategory name, use \"null\" for all subcategories (optional)\n");
			this.console.printf("  <album>       ... download only images that match the given album name, use \"null\" for all albums (optional)\n");
			this.console.printf("  <dir>         ... location of the files that should be uploaded (required)\n");
		}
		else if (command.equals("verify"))
		{
			this.console.printf("usage: verify [<category> <subcategory> <album>] <dir>\n");
			this.console.printf("\n");
			this.console.printf("options:\n");
			this.console.printf("  <category>    ... verify only images that match the given category name, use \"null\" for all categories (optional)\n");
			this.console.printf("  <subcategory> ... verify only images that match the given subcategory name, use \"null\" for all subcategories (optional)\n");
			this.console.printf("  <album>       ... verify only images that match the given album name, use \"null\" for all albums (optional)\n");
			this.console.printf("  <dir>         ... location of the local files that should be verified (required)\n");
		}
		else if (command.equals("quit"))
		{
			this.console.printf("usage: quit\n");
			this.console.printf("\n");
			this.console.printf("  ... no further info available\n");
		}
		else
		{
			this.console.printf("no help available, the specified parameter is not an action\n");
		}
	}

}
