/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

public class TransferQueueItemProcessResults implements ITransferQueueItemProcessResults
{
	private boolean processed;
	private boolean successful;
	private String message;
	private long transferedBytes;
	
	public TransferQueueItemProcessResults(boolean processed, boolean successful, String message, long transferedBytes)
	{
		this.processed = processed;
		this.successful = successful;
		this.message = message;
		this.transferedBytes = transferedBytes;
	}

	public boolean isProcessed()
	{
		return this.processed;
	}

	public boolean wasSuccessful()
	{
		return this.successful;
	}

	public String getMessage()
	{
		return this.message;
	}

	public long getTransferedBytes()
	{
		return this.transferedBytes;
	}
}
