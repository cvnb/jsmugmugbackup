package jSmugmugBackup.view;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.data.*;
import jSmugmugBackup.model.login.*;


import java.awt.event.*;


public class CmdView implements IView
{
	private Model model = null;
	private Logger log = null;
	
	private String[] cmd_args = null;
	
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
	private ActionListener quitButtonListener = null;
	
	
	public CmdView(Model model, String[] cmd_args)
	{
		this.model = model;
		this.model.setView(this);
		this.log = Logger.getInstance();
		this.log.registerView(this);
		this.cmd_args = cmd_args;
	}
	
	public void start()
	{
		this.log.printLogLine("jSmumugBackup v" + Constants.version);
		
		if ( this.cmd_args.length == 0 ) this.printHelp();
		else if ( this.cmd_args[0].equals("--help") ) this.printHelp();
		else if ( this.cmd_args[0].equals("--list") )
		{
			this.loginButtonListener.actionPerformed(null);	//trigger the login-button action listener
			this.refreshButtonListener.actionPerformed(null);
		}
		else if ( this.cmd_args[0].equals("--upload") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.uploadDialogButtonListener.actionPerformed(null);
			this.uploadStartButtonListener.actionPerformed(null);
		}
		else if ( this.cmd_args[0].equals("--download") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.downloadDialogButtonListener.actionPerformed(null);
			this.downloadStartButtonListener.actionPerformed(null);
		}
		else if ( this.cmd_args[0].equals("--verify") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.verifyDialogButtonListener.actionPerformed(null);
			this.verifyStartButtonListener.actionPerformed(null);
		}
		else if ( this.cmd_args[0].equals("--recursive-delete") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.deleteDialogButtonListener.actionPerformed(null);
			this.deleteStartButtonListener.actionPerformed(null);
		}

		else this.printHelp();
		
		this.model.quitApplication();
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
	public void addQuitButtonListener(ActionListener listener)           { this.quitButtonListener = listener; }
	

	public void refreshFileListing(AccountListing accountListing)
	{		
		//display listing on console
		this.log.printLogLine("Nickname: " + accountListing.getNickName());
		for (ICategoryType c : accountListing.getCategoryList())
		{
			this.log.printLogLine("    category: " + c.getName());
			
			for (ISubCategoryType sc : c.getSubCategoryList())
			{
				this.log.printLogLine("        subCategory: " + sc.getName());
				for (IAlbumType a : c.getAlbumList())
				{
					this.log.printLogLine("            album: " + a.getName());
					for (IImageType i : a.getImageList())
					{
						this.log.printLogLine("                image: " + i.getName());
					}
				}
			}
			
			for (IAlbumType a : c.getAlbumList())
			{
				this.log.printLogLine("        album: " + a.getName());
				for (IImageType i : a.getImageList())
				{
					this.log.printLogLine("            image: " + i.getName());
				}
			}
		}
	}

	public ISmugmugLogin getLoginToken()
	{		
		String account_email = this.extractArgumentValueFromCommandline("email");
		
		ISmugmugLogin loginToken = null;
		
		/*
		//for speeding up testin a little bit ...
		loginToken = new SmugmugLoginHardcode();
		*/
		
    	//this should allow the program to run, even if only java 1.5 is available
    	if (java.lang.System.getProperty("java.specification.version").equals("1.5"))
    	{
    		loginToken = new SmugmugLoginConsole_1_5(account_email);
    	}
    	else //assuming we have Java 1.6 or higher
    	{
    		loginToken = new SmugmugLoginConsole_1_6(account_email);
    	}
    	
    	
    	
    	
		
		return loginToken;
		
	}

	public void showError(String errMessage)
	{
		System.out.println(errMessage);
	}

	public void printLog(String text)
	{
		System.out.print(text);
	}

	public ITransferDialogResult showUploadDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String pics_dir = this.extractDirectoryFromCommandline();
		
		return new TransferDialogResult(category, subCategory, album, pics_dir);
	}
	
	public ITransferDialogResult showDownloadDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
	}
	
	public ITransferDialogResult showVerifyDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
	}
	
	public ITransferDialogResult showDeleteDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
		String target_dir = this.extractDirectoryFromCommandline();
			    
		return new TransferDialogResult(category, subCategory, album, target_dir);
	}

	//---------------------- private -----------------------------------------------
	private void printHelp()
	{
		this.log.printLogLine("... up- and downloading files from Smugmug.com");
		this.log.printLogLine("usage:");
		this.log.printLogLine("     jSmugmugBackup --help");
		this.log.printLogLine("     jSmugmugBackup --list [--email={username}]");
		this.log.printLogLine("     jSmugmugBackup --upload [--email={username}] [--category={name}] [--subcategory={name}] --dir={photo_dir}");
		this.log.printLogLine("     jSmugmugBackup --download [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}] --dir={target_dir}");
		this.log.printLogLine("     jSmugmugBackup --verify [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}] --dir={target_dir}");
		//undocumented feature ...
		//this.log.printLogLine("     jSmugmugBackup --recursive-delete [--email={username}] [--category={name}] [--subcategory={name}] [--album={name}]");
	}
	
	private boolean extractArgumentFromCommandline(String argumentName)
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
