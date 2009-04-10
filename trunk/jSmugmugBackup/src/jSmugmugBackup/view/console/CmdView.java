package jSmugmugBackup.view.console;

import jSmugmugBackup.view.*;
import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.ILoginView;
import jSmugmugBackup.view.console.ConsoleViewLogin_1_5;
import jSmugmugBackup.view.console.ConsoleViewLogin_1_6;


import java.awt.event.*;
import java.util.Vector;


public class CmdView implements IView
{
    private GlobalConfig config = null;
	private Model model = null;
	private Logger log = null;
	
	private String[] cmd_args = null;
	
	private ActionListener loginButtonListener = null;
	private ActionListener uploadDialogButtonListener = null;
	private ActionListener downloadDialogButtonListener = null;
	private ActionListener verifyDialogButtonListener = null;
	private ActionListener deleteDialogButtonListener = null;
	private ActionListener refreshButtonListener = null;
	private ActionListener sortButtonListener = null;
	private ActionListener quitButtonListener = null;
    private ActionListener syncProcessQueueButtonListener = null;
    private ActionListener asyncProcessQueueStartButtonListener = null;
	
	
	public CmdView(Model model, String[] cmd_args)
	{
        this.config = GlobalConfig.getInstance();
		this.model = model;
		this.model.setView(this);
		this.log = Logger.getInstance();
		this.log.registerView(this);
		this.cmd_args = cmd_args;
	}
	
	public void start()
	{
		this.log.printLogLine("jSmugmugBackup v" + this.config.getConstantVersion());
		
		if ( this.cmd_args.length == 0 ) this.printHelp();
		else if ( this.findArgumentFromCommandline("help") ) this.printHelp();
		else if ( this.findArgumentFromCommandline("list") )
		{
			this.loginButtonListener.actionPerformed(null);	//trigger the login-button action listener
			this.refreshButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("sort") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.sortButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("upload") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.uploadDialogButtonListener.actionPerformed(null);
			//this.uploadStartButtonListener.actionPerformed(null);
            this.syncProcessQueueButtonListener.actionPerformed(null);
            //this.asyncProcessQueueStartButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("download") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.downloadDialogButtonListener.actionPerformed(null);
			//this.downloadStartButtonListener.actionPerformed(null);
            this.syncProcessQueueButtonListener.actionPerformed(null);
            //this.asyncProcessQueueStartButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("verify") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.verifyDialogButtonListener.actionPerformed(null);
			//this.verifyStartButtonListener.actionPerformed(null);
            this.syncProcessQueueButtonListener.actionPerformed(null);
            //this.asyncProcessQueueStartButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("recursive-delete") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.deleteDialogButtonListener.actionPerformed(null);
			//this.deleteStartButtonListener.actionPerformed(null);
            this.syncProcessQueueButtonListener.actionPerformed(null);
		}

		else this.printHelp();
		
		this.model.quitApplication();
	}


	public void addLoginButtonListener(ActionListener listener)          { this.loginButtonListener = listener; }
	public void addUploadDialogButtonListener(ActionListener listener)   { this.uploadDialogButtonListener = listener; }
	public void addDownloadDialogButtonListener(ActionListener listener) { this.downloadDialogButtonListener = listener; }
	public void addVerifyDialogButtonListener(ActionListener listener)   { this.verifyDialogButtonListener = listener; }
	public void addDeleteDialogButtonListener(ActionListener listener)   { this.deleteDialogButtonListener = listener; }
	public void addListButtonListener(ActionListener listener)        { this.refreshButtonListener = listener; }
	public void addSortButtonListener(ActionListener listener)           { this.sortButtonListener = listener; }
	public void addQuitButtonListener(ActionListener listener)           { this.quitButtonListener = listener; }
	public void addSyncProcessQueueButtonListener(ActionListener listener)   { this.syncProcessQueueButtonListener = listener; }

    public void addASyncProcessQueueStartButtonListener(ActionListener listener)   { /*this.asyncProcessQueueStartButtonListener = listener;*/ }
    //public void addASyncProcessQueueFinishedListener(ActionListener listener)   { /* ... */ }

    public void notifyASyncProcessQueueFinished()
    {
        //throw new UnsupportedOperationException("Not supported yet.");
        this.log.printLogLine("(asyncchronous) queue processing finished");
    }

	public void updateFileListing(IRootElement smugmugRoot)
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
					this.log.printLog("            album: " + a.getName());
                    if (a.getTags() != null) { this.log.printLog( " (" + Helper.getKeywords(a.getTags()) + ")" ); }
                    this.log.printLog("\n"); //finish this line
					for (IImage i : a.getImageList())
					{
						this.log.printLog("                image: " + i.getName());
                        if (i.getTags() != null) { this.log.printLog( " (" + Helper.getKeywords(i.getTags()) + ")" ); }
                        this.log.printLog("\n"); //finish this line
					}
				}
			}
			
			for (IAlbum a : c.getAlbumList())
			{
				this.log.printLog("        album: " + a.getName());
                if (a.getTags() != null) { this.log.printLog( " (" + Helper.getKeywords(a.getTags()) + ")" ); }
                this.log.printLog("\n"); //finish this line
				for (IImage i : a.getImageList())
				{
					this.log.printLog("            image: " + i.getName());
                    if (i.getTags() != null) { this.log.printLog( " (" + Helper.getKeywords(i.getTags()) + ")" ); }
                    this.log.printLog("\n"); //finish this line
				}
			}
		}
	}


	public void showError(String errMessage)
	{
		System.out.println(errMessage);
	}
	
	public void showBusyStart(String waitingMessage)
	{
		/* noop */
	}
	
	public void showBusyStop()
	{
		/* noop */
	}

	public void printLog(String text)
	{
		System.out.print(text);
	}


    public ILoginDialogResult showLoginDialog()
	{
		String account_email    = this.extractArgumentValueFromCommandline("email");
		String account_password = this.extractArgumentValueFromCommandline("password");

		ILoginView loginView = null;

    	//this should allow the program to run, even if only java 1.5 is available
    	if (java.lang.System.getProperty("java.specification.version").equals("1.5"))
    	{
    		loginView = new ConsoleViewLogin_1_5(account_email, account_password);
    	}
    	else //assuming we have Java 1.6 or higher
    	{
    		loginView = new ConsoleViewLogin_1_6(account_email, account_password);
    	}

    	return loginView.getLoginDialogResult();
	}

	public ITransferDialogResult showListDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
        String albumKeywords = this.extractArgumentValueFromCommandline("albumKeywords");
		
		return new TransferDialogResult(category, subCategory, album, null, albumKeywords);
	}
	
	public ITransferDialogResult showSortDialog()
	{
		/*
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		
		return new TransferDialogResult(category, subCategory, album, null);
		*/
		
		//method is identical to the list dialog
		return this.showListDialog();
	}
	
	public ITransferDialogResult showUploadDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
        String albumKeywords = this.extractArgumentValueFromCommandline("albumKeywords");
		String pics_dir = this.extractDirectoryFromCommandline();
		
		return new TransferDialogResult(category, subCategory, album, pics_dir, albumKeywords);
	}
	
	public ITransferDialogResult showDownloadDialog()
	{
		/*
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
		*/
		
		//method is identical to the upload dialog
		return this.showUploadDialog();
	}
	
	public ITransferDialogResult showVerifyDialog()
	{
		/*
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
		*/

		//method is identical to the upload dialog
		return this.showUploadDialog();
	}
	
	public ITransferDialogResult showDeleteDialog()
	{
		/*
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
		*/

		//method is identical to the upload dialog
		return this.showUploadDialog();
	}

	//---------------------- private -----------------------------------------------
	private void printHelp()
	{
		this.log.printLogLine("... up- and downloading files from Smugmug.com");
		this.log.printLogLine("usage:");
		this.log.printLogLine("     jSmugmugBackup                          : gui interface");
		//this.log.printLogLine("     jSmugmugBackup --console                : console interface (Java 1.6 only)");
		this.log.printLogLine("     jSmugmugBackup [action] [options ... ]  : commandline interface");
		this.log.printLogLine("");
		this.log.printLogLine("actions:");
		this.log.printLogLine("     --help         : print this help");
		this.log.printLogLine("     --list         : list contents of your smumgmug account");
		this.log.printLogLine("     --sort         : sort categories, subcategories, albums");
		this.log.printLogLine("     --upload       : upload files to smugmug, requires \"--dir\" option");
		this.log.printLogLine("     --download     : download files from smugmug, requires \"--dir\" option");
		this.log.printLogLine("     --verify       : compare local files and files on smugmug, requires \"--dir\" option");
		this.log.printLogLine("options:");
        //this.log.printLogLine("     --pretend             : don't change anything on smugmug, just print what would be done");
		this.log.printLogLine("     --email={username}    : specify the email-address or the username used to log into smugmug (optional)");
		this.log.printLogLine("     --password={password} : specify the password used to log into smugmug, optional (optional)");
		this.log.printLogLine("     --category={name}     : perform the given action only on the given category (optional)");
		this.log.printLogLine("     --subcategory={name}  : perform the given action only on the given subcategory (optional)");
		this.log.printLogLine("     --album={name}        : perform the given action only on the given album (optional)");
        this.log.printLogLine("     --albumKeywords={keywords} : perform the given action only using the given keywords, separated by \"; \" (optional)");
		this.log.printLogLine("     --dir={directory}     : the local base dir for the actions");
		this.log.printLogLine("");
		this.log.printLogLine(this.config.getConstantHelpNotes());

		
		//this.log.printLogLine("     jSmugmugBackup --help");
		//this.log.printLogLine("     jSmugmugBackup --list [--email={username}]");
		//this.log.printLogLine("     jSmugmugBackup --upload [--email={username}] [--category={name}] [--subcategory={name}] --dir={photo_dir}");
		//this.log.printLogLine("     jSmugmugBackup --download [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}] --dir={target_dir}");
		//this.log.printLogLine("     jSmugmugBackup --verify [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}] --dir={target_dir}");
		//undocumented feature ...
		//this.log.printLogLine("     jSmugmugBackup --recursive-delete [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}]");
	}
	
	private boolean findArgumentFromCommandline(String argumentName)
	{
		for (String arg : this.cmd_args)
		{
			if (arg.startsWith("--" + argumentName))
			{
				return true;
			}
		}
		return false;		
	}
	private String extractArgumentValueFromCommandline(String argumentName)
	{
		String result = null;
		int i = 0;
		boolean concat_mode = false;
		while ( i < this.cmd_args.length )
		{
			String arg = this.cmd_args[i];
			
			if (concat_mode == false)
			{
				if (arg.startsWith("--" + argumentName + "="))
				{
					result = arg.substring(arg.indexOf("=") + 1);
					concat_mode = true;
				}
			}
			else // concat_mode == true
			{
				//if (arg.startsWith("--") || arg.startsWith("/"))
				if ( arg.startsWith("--") )
				{
					//we've reached the next argument
					break;
				}
				else 
				{
					//append
					result = result + " " + arg;
				}
			}			
			i++;
		}
		return result;
	}
	private String extractDirectoryFromCommandline()
	{
		String dir = this.extractArgumentValueFromCommandline("dir");
		
		//if not dir argument is given, assume the current directory
		if (dir == null) { dir = "."; }
		
		//add a tailing slash
		if (dir.endsWith("/") == false) { dir = dir + "/"; }
		
		return dir;
	}

}
