package jSmugmugBackup.main;

import jSmugmugBackup.model.Model;
import jSmugmugBackup.view.CmdView;
import jSmugmugBackup.view.IView;

import java.awt.event.*;

public class Controller
{
	
    //... The Controller needs to interact with both the Model and View.
    private Model model;
    private IView  view;
	
	public Controller(Model model, IView view)
	{
        this.model = model;
        this.view  = view;
        
        //... Add listeners to the view.
        this.view.addLoginButtonListener(new LoginButtonListener());
        this.view.addRefreshButtonListener(new RefreshButtonListener());
        this.view.addUploadDialogButtonListener(new UploadDialogButtonListener());
        this.view.addUploadStartButtonListener(new UploadStartButtonListener());
        this.view.addDownloadDialogButtonListener(new DownloadDialogButtonListener());
        this.view.addDownloadStartButtonListener(new DownloadStartButtonListener());
        this.view.addVerifyDialogButtonListener(new VerifyDialogButtonListener());
        this.view.addVerifyStartButtonListener(new VerifyStartButtonListener());
        this.view.addDeleteDialogButtonListener(new DeleteDialogButtonListener());
        this.view.addDeleteStartButtonListener(new DeleteStartButtonListener());
        this.view.addQuitButtonListener(new QuitButtonListener());
        
        // rotten hack: the Listers must be registered before the CmdView can start
        //              to work. When constructing the object this is not yet the case.
        if ( this.view instanceof CmdView )
        {
        	((CmdView) (this.view)).start();
        }
	}
	
	class LoginButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.login(view.getLoginToken());
			//model.getFileListing();
		}
	}
	
	class RefreshButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.getFileListing();
		}		
	}
	
	class UploadDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.uploadPrepare(view.showUploadDialog());
		}
	}
	
	class UploadStartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.startProcessingQueue();
		}
	}
	
	class DownloadDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.downloadPrepare(view.showDownloadDialog());
		}		
	}
	
	class DownloadStartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.startProcessingQueue();
		}		
	}

	class VerifyDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.verifyPrepare(view.showVerifyDialog());
		}		
	}
	
	class VerifyStartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//model.startProcessingQueue();
		}		
	}	
	
	class DeleteDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.deletePrepare(view.showDeleteDialog());
		}		
	}
	
	class DeleteStartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.startProcessingQueue();
		}		
	}
	
	class QuitButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.quitApplication();			
		}		
	}
	
    
}

