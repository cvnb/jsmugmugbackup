/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;


import jSmugmugBackup.config.GlobalConfig;
import jSmugmugBackup.model.smugmugLayer.*;
import jSmugmugBackup.view.*;

import java.io.*;
import java.util.Vector;



public class TransferQueueItem implements ITransferQueueItem
{
    private GlobalConfig config = null;
	private Logger log = null;
	private ISmugmugConnectorNG smugmugConnector = null;
	private TransferQueueItemActionEnum action = null;
	
	private int albumID;
	private int imageID;
	private File fileDescriptor = null;
	private long fileSize = 0; // needed for pretty output
    private Vector<String> tags = null;

	
	private boolean result_processed;
	private boolean result_successful;
	private TransferQueueItemActionEnum result_action;
	private int result_id;
	private String result_message;
	private long result_transferedBytes;
	
	public TransferQueueItem(TransferQueueItemActionEnum action, int id, File fileDescriptor, long fileSize, Vector<String> tags)
	{
        this.config = GlobalConfig.getInstance();
		this.log = Logger.getInstance();
		//this.log.printLogLine("new TransferQueueItem()");
		
		this.smugmugConnector = new SmugmugConnectorNG();
		//this.smugmugConnector.setLoginToken(loginToken);
		//this.smugmugConnector.login();
		
		this.action = action;
		//this.loginToken = loginToken;
		
		this.result_processed = false;
		this.result_successful = false;
		this.result_action = action;
		this.result_id = 0;
		this.result_message = "";
		this.result_transferedBytes = 0;
		
		this.fileDescriptor = fileDescriptor;
        this.fileSize = fileSize;
        this.tags = tags;
		if (this.action.equals(TransferQueueItemActionEnum.UPLOAD))
		{
			this.albumID = id;						
		}
		else if ( (this.action.equals(TransferQueueItemActionEnum.DOWNLOAD)) /* ||
			      (this.action.equals(TransferQueueItemActionEnum.VERIFY))*/ )
		{
			this.imageID = id;
		}
	}


	
	public void process()
	{
		//this.log.printLogLine("TransferQueueItem.process()");
		
		if (this.action.equals(TransferQueueItemActionEnum.UPLOAD))
		{
            // performing relogin for each queue item might improve stability during long lasting queue operations
            if ( this.config.getConstantHeavyRelogin() ) { this.smugmugConnector.relogin(); }

			this.result_id = this.smugmugConnector.uploadFile(this.albumID, this.fileDescriptor, null, this.tags);
			
			//this should be safe to assume
			if (this.result_id == 0) { this.result_successful = false; }
			else { this.result_successful = true; }
		}
		else if (this.action.equals(TransferQueueItemActionEnum.DOWNLOAD))
		{
            // performing relogin for each queue item might improve stability during long lasting queue operations
            if ( this.config.getConstantHeavyRelogin()  ) { this.smugmugConnector.relogin(); }

			//this.result_successful = this.smugmugConnector.downloadFile(this.imageID, this.fileName);
			this.smugmugConnector.downloadFile(this.imageID, this.fileDescriptor);
		}
		/*
		else if (this.action.equals(TransferQueueItemActionEnum.VERIFY))
		{
			//this.result_successful = this.smugmugConnector.verifyFile();
			//this.smugmugConnector.verifyFile();
		}
		*/
		else this.log.printLogLine("error in TransferQueueItem.process()");
		
		this.result_transferedBytes = this.smugmugConnector.getTransferedBytes();
		this.result_processed = true;
	}

	public ITransferQueueItemProcessResults getResults()
	{		
		return new TransferQueueItemProcessResults(this.result_processed, this.result_successful, this.result_action, this.result_id, this.result_message, this.result_transferedBytes);
	}

	public long getFileSize()
	{
        return this.fileSize;
	}
}
