package jSmugmugBackup.controller;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.queue.*;
import jSmugmugBackup.view.*;

import jSmugmugBackup.view.ng.SwingViewNG;
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
        this.view.addListButtonListener(new ListButtonListener());
        this.view.addSortButtonListener(new SortButtonListener());
        this.view.addAutotagButtonListener(new AutotagButtonListener());
        this.view.addStatisticsButtonListener(new StatisticsButtonListener());
        this.view.addOsmlayerButtonListener(new OsmlayerButtonListener());
        this.view.addUploadDialogButtonListener(new UploadDialogButtonListener());
        //this.view.addUploadStartButtonListener(new UploadStartButtonListener());
        this.view.addDownloadDialogButtonListener(new DownloadDialogButtonListener());
        this.view.addDownloadURLDialogButtonListener(new DownloadURLDialogButtonListener());
        //this.view.addDownloadStartButtonListener(new DownloadStartButtonListener());
        this.view.addVerifyDialogButtonListener(new VerifyDialogButtonListener());
        //this.view.addVerifyStartButtonListener(new VerifyStartButtonListener());
        this.view.addDeleteDialogButtonListener(new DeleteDialogButtonListener());
        //this.view.addDeleteStartButtonListener(new DeleteStartButtonListener());
        this.view.addQuitButtonListener(new QuitButtonListener());
        this.view.addSyncProcessQueueButtonListener(new SyncProcessQueueButtonListener());

        this.view.addASyncProcessQueueStartButtonListener(new ASyncProcessQueueStartButtonListener());
        
        ITransferQueue transferQueue = TransferQueue.getInstance();
        transferQueue.addASyncProcessQueueFinishedListener(new ASyncProcessQueueFinishedListener());

        // rotten hack: the Listers must be registered before the CmdView can start
        //              to work. When constructing the object this is not yet the case.
        this.view.start();
	}
	
	class LoginButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
            view.showBusyStart("logging in ...");
			model.login(view.showLoginDialog());

            //dirty hack
            if (view instanceof SwingViewNG)
            {
                view.showBusyStop();

                view.showBusyStart("downloading account data ...");
                model.list(new TransferDialogResult(null, null, null, null, null, null, null, /*null,*/ null));
            }

            view.showBusyStop();
		}
	}	
	class ListButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
            view.showBusyStart("downloading account data ...");
            model.list(view.showListDialog());
            view.showBusyStop();
		}

	}	
	class SortButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.sort(view.showSortDialog());
		}
	}
	class AutotagButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.autotag(view.showAutotagDialog());
		}
	}
	class StatisticsButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.statistics(view.showStatisticsDialog());
		}
	}
	class OsmlayerButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.osmlayer(view.showOsmlayerDialog());
		}
	}
	class UploadDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.upload(view.showUploadDialog());
		}
	}	
	class DownloadDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.download(view.showDownloadDialog());
		}		
	}
	class DownloadURLDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.downloadURL(view.showDownloadURLDialog());
		}		
	}
	class VerifyDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.verify(view.showVerifyDialog());
		}		
	}	
	class DeleteDialogButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.delete(view.showDeleteDialog());
		}		
	}	
	class QuitButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			model.quitApplication();			
		}		
	}
    class SyncProcessQueueButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
		{
			model.startSyncProcessingQueue();
		}
    }
    class ASyncProcessQueueStartButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
		{
            model.startASyncProcessingQueue();
		}
    }
    class ASyncProcessQueueFinishedListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
		{


            model.finishASyncProcessingQuene();
            view.notifyASyncProcessQueueFinished();

		}
    }
    
}

