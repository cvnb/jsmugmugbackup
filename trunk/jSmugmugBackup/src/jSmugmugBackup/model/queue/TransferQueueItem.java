/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.model.login.*;
import jSmugmugBackup.trash_bin.ISmugMugConnector;
import jSmugmugBackup.trash_bin.SmugmugConnector;
import jSmugmugBackup.view.Logger;

import java.io.*;

import com.streetsofboston.smugmug.v1_2_1.system.GUID;


public class TransferQueueItem implements ITransferQueueItem
{
	private Logger log = null;
	private ISmugMugConnector smugmugConnector = null;
	private TransferQueueItemActionEnum action = null;
	//private ISmugmugLogin loginToken = null;
	
	private GUID albumGUID = null;
	private File fileDescriptor = null;
	
	private GUID imageGUID = null;
	private String fileName = null;
	
	private boolean result_processed;
	private boolean result_successful;
	private String result_message;
	private long result_transferedBytes;
	
	private TransferQueueItem(TransferQueueItemActionEnum action)
	{
		this.log = Logger.getInstance();
		//this.log.printLogLine("new TransferQueueItem()");
		
		this.smugmugConnector = new SmugmugConnector();
		//this.smugmugConnector.setLoginToken(loginToken);
		//this.smugmugConnector.login();
		
		this.action = action;
		//this.loginToken = loginToken;
		
		this.result_processed = false;
		this.result_successful = false;
		this.result_message = "";
		this.result_transferedBytes = 0;
	}
	public TransferQueueItem(TransferQueueItemActionEnum action, GUID albumGUID, File fileDescriptor) throws TransferQueueException
	{
		//constructor for upload items
		this(action);
		if (this.action.equals(TransferQueueItemActionEnum.UPLOAD))
		{
			this.albumGUID = albumGUID;
			this.fileDescriptor = fileDescriptor;			
		}
		else throw new TransferQueueException("only the \"UPLOAD\" action is applicable for this constructor!");
	}
	public TransferQueueItem(TransferQueueItemActionEnum action, GUID imageGUID, String fileName) throws TransferQueueException
	{
		//constructor for download or verify items
		this(action);
		if ( (this.action.equals(TransferQueueItemActionEnum.DOWNLOAD)) ||
			 (this.action.equals(TransferQueueItemActionEnum.VERIFY)) )
		{
			this.imageGUID = imageGUID;
			this.fileName = fileName;			
		}
		else throw new TransferQueueException("only the \"DOWNLOAD\" or \"VERIFY\" action is applicable for this constructor!");
	}

	
	public void process()
	{
		//this.log.printLogLine("TransferQueueItem.process()");
		
		if (this.action.equals(TransferQueueItemActionEnum.UPLOAD))
		{
			this.result_successful = this.smugmugConnector.uploadFile(this.albumGUID, this.fileDescriptor);		
		}
		else if (this.action.equals(TransferQueueItemActionEnum.DOWNLOAD))
		{
			this.result_successful = this.smugmugConnector.downloadFile(this.imageGUID, this.fileName);
		}
		else if (this.action.equals(TransferQueueItemActionEnum.VERIFY))
		{
			this.result_successful = this.smugmugConnector.verifyFile();
		}
		else this.log.printLogLine("error in TransferQueueItem.process()");
		
		this.result_transferedBytes = this.smugmugConnector.getTransferedBytes();
		this.result_processed = true;
	}

	public ITransferQueueItemProcessResults getResults()
	{		
		return new TransferQueueItemProcessResults(this.result_processed, this.result_successful, this.result_message, this.result_transferedBytes);
	}
}
