package jSmugmugBackup.view.console;

import jSmugmugBackup.view.*;
import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.accountLayer.*;
import jSmugmugBackup.model.*;
import jSmugmugBackup.view.ILoginView;
import jSmugmugBackup.view.console.ConsoleViewLogin_1_5;
import jSmugmugBackup.view.console.ConsoleViewLogin_1_6;


import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Calendar;
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
    private ActionListener downloadURLDialogButtonListener = null;
	private ActionListener verifyDialogButtonListener = null;
	private ActionListener deleteDialogButtonListener = null;
	private ActionListener refreshButtonListener = null;
	private ActionListener sortButtonListener = null;
    private ActionListener autotagButtonListener = null;
    private ActionListener statisticsButtonListener = null;
    private ActionListener osmlayerButtonListener = null;
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
		else if ( this.findArgumentFromCommandline("autotag") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.autotagButtonListener.actionPerformed(null);
		}
		else if ( this.findArgumentFromCommandline("stats") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.statisticsButtonListener.actionPerformed(null);
		}
        else if ( this.findArgumentFromCommandline("osmlayer") )
		{
			this.loginButtonListener.actionPerformed(null);
			this.osmlayerButtonListener.actionPerformed(null);
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

            if ( this.findArgumentFromCommandline("url") ) { this.downloadURLDialogButtonListener.actionPerformed(null); }
            else { this.downloadDialogButtonListener.actionPerformed(null); }


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

	public void addLoginButtonListener(ActionListener listener)              { this.loginButtonListener = listener; }
	public void addUploadDialogButtonListener(ActionListener listener)       { this.uploadDialogButtonListener = listener; }
	public void addDownloadDialogButtonListener(ActionListener listener)     { this.downloadDialogButtonListener = listener; }
    public void addDownloadURLDialogButtonListener(ActionListener listener)  { this.downloadURLDialogButtonListener = listener; }
	public void addVerifyDialogButtonListener(ActionListener listener)       { this.verifyDialogButtonListener = listener; }
	public void addDeleteDialogButtonListener(ActionListener listener)       { this.deleteDialogButtonListener = listener; }
	public void addListButtonListener(ActionListener listener)               { this.refreshButtonListener = listener; }
	public void addSortButtonListener(ActionListener listener)               { this.sortButtonListener = listener; }
    public void addAutotagButtonListener(ActionListener listener)            { this.autotagButtonListener = listener; }
    public void addStatisticsButtonListener(ActionListener listener)         { this.statisticsButtonListener = listener; }
	public void addOsmlayerButtonListener(ActionListener listener)           { this.osmlayerButtonListener = listener; }
    public void addQuitButtonListener(ActionListener listener)               { this.quitButtonListener = listener; }
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
    public void showStatistics(Vector<IAlbum> albumList)
    {
        Calendar calendar = Calendar.getInstance();
        Vector<Integer> monthVector = new Vector<Integer>();
        Vector<Integer> yearVector = new Vector<Integer>();
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++)
        {
            monthVector.add(calendar.get(Calendar.MONTH) + 1);
            yearVector.add(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.MONTH, -1);
        }


        Vector<Integer> totalBytesVector = new Vector<Integer>();
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++) { totalBytesVector.add(0); }
        this.log.printLogLine("");
        this.log.printLogLine("Statistics:");
        
        this.log.printLog("album                                                                 |    ");
        for (int i = this.config.getConstantStatisticsHistoryMonth() - 1; i >= 0 ; i--) { this.log.printLogFixedWidthRAL(monthVector.get(i).toString(), 2); this.log.printLog("/" + yearVector.get(i) + "    |    "); }
        this.log.printLogLine("");

        this.log.printLog("----------------------------------------------------------------------|");
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++) { this.log.printLog("---------------|"); }
        this.log.printLogLine("-");

        for (IAlbum a : albumList)
        {
            if (a.getStatistics().size() > 0)
            {
                Vector<Integer> bytesVector = new Vector<Integer>();
                for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++)
                {
                    for (IAlbumMonthlyStatistics stats : a.getStatistics())
                    {
                        if ( (stats.getYear() == yearVector.get(i)) && (stats.getMonth() == monthVector.get(i)) ) { bytesVector.add(stats.getBytes()); }
                    }
                    totalBytesVector.set(i, totalBytesVector.get(i) + bytesVector.get(i));
                }

                
                boolean zeroBytes = true;
                for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++)
                {
                    if (bytesVector.get(i).intValue() != 0) { zeroBytes = false; }
                }
                if ( !zeroBytes )
                {
                    Vector<Double> megabytesVector = new Vector<Double>();
                    for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++)
                    {
                        megabytesVector.add( (float)bytesVector.get(i) / (1024.0 * 1024.0) );
                    }

                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(1);
                    nf.setMinimumFractionDigits(1);

                    this.log.printLogFixedWidth(a.getFullName(), 70); this.log.printLog("|");
                    for (int i = this.config.getConstantStatisticsHistoryMonth() - 1; i >= 0 ; i--)
                    {
                        this.log.printLogFixedWidthRAL(nf.format(megabytesVector.get(i)) + " mb", 15); this.log.printLog("|");
                    }
                    this.log.printLogLine("");
                }
                
            }
        }

        
        this.log.printLog("----------------------------------------------------------------------|");
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++) { this.log.printLog("---------------|"); }
        this.log.printLogLine("-");

        boolean zeroTotalBytes = true;
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++)
        {
            if (totalBytesVector.get(i).intValue() != 0) { zeroTotalBytes = false; }
        }
        if ( !zeroTotalBytes )
        {

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);

            this.log.printLogFixedWidth("total", 70); this.log.printLog("|");
            Vector<Double> totalMegaBytesVector = new Vector<Double>();
            for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++) { totalMegaBytesVector.add(0.0); }
            for (int i = this.config.getConstantStatisticsHistoryMonth() - 1; i >= 0 ; i--)
            {
                totalMegaBytesVector.set(i, (float)totalBytesVector.get(i) / (1024.0 * 1024.0) );
                
                this.log.printLogFixedWidthRAL(nf.format(totalMegaBytesVector.get(i)) + " mb", 15); this.log.printLog("|");
            }
            
            this.log.printLogLine("");
        }
        this.log.printLog("----------------------------------------------------------------------|");
        for (int i = 0; i < this.config.getConstantStatisticsHistoryMonth(); i++) { this.log.printLog("---------------|"); }
        this.log.printLogLine("-");

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
		String account_email    = this.extractArgumentValueFromCommandline("user");
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
		
		return new TransferDialogResult(category, subCategory, album, null, albumKeywords, null);
	}
	public ITransferDialogResult showSortDialog()
	{
		return this.showListDialog();
	}
    public ITransferDialogResult showAutotagDialog()
	{
		return this.showListDialog();
	}
    public ITransferDialogResult showStatisticsDialog()
	{
		return this.showListDialog();
	}
    public ITransferDialogResult showOsmlayerDialog()
    {
        return this.showUploadDialog();
    }
    public ITransferDialogResult showUploadDialog()
	{
		String category = this.extractArgumentValueFromCommandline("category");
		String subCategory = this.extractArgumentValueFromCommandline("subcategory");
		String album = this.extractArgumentValueFromCommandline("album");
        String albumKeywords = this.extractArgumentValueFromCommandline("albumKeywords");
		String pics_dir = this.extractDirectoryFromCommandline();
		
		return new TransferDialogResult(category, subCategory, album, pics_dir, albumKeywords, null);
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
    public ITransferDialogResult showDownloadURLDialog()
    {
        String pics_dir = this.extractDirectoryFromCommandline();
        String url = this.extractArgumentValueFromCommandline("url");

        return new TransferDialogResult(null, null, null, pics_dir, null, url);
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
        this.log.printLogLine("     --autotag      : assign tags based on the album name");
        this.log.printLogLine("     --stats        : show statistics");
        this.log.printLogLine("     --osmlayer     : create a layer file to be used with OpenStreetMap, requires \"--dir\" option");
        this.log.printLogLine("     --upload       : upload files to smugmug, requires \"--dir\" option");
		this.log.printLogLine("     --download     : download files from smugmug, requires \"--dir\" option");
		this.log.printLogLine("     --verify       : compare local files and files on smugmug, requires \"--dir\" option");
		this.log.printLogLine("options:");
        //this.log.printLogLine("     --pretend                  : don't change anything on smugmug, just print what would be done");
		this.log.printLogLine("     --user={username or email}  : specify the email-address or the username used to log into smugmug (optional)");
		this.log.printLogLine("     --password={password}       : specify the password used to log into smugmug, optional (optional)");
		this.log.printLogLine("     --category={name}           : perform the given action only on the given category (optional)");
		this.log.printLogLine("     --subcategory={name}        : perform the given action only on the given subcategory (optional)");
		this.log.printLogLine("     --album={name}              : perform the given action only on the given album (optional)");
        this.log.printLogLine("     --albumKeywords={keywords}  : perform the given action only using the given keywords, separated by \"; \" (optional)");
        this.log.printLogLine("     --url={smugmug album url}   : only in conjunction with the \"download\" action, downloads the album specified a url");
		this.log.printLogLine("     --dir={directory}           : the local base dir for the actions");
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
