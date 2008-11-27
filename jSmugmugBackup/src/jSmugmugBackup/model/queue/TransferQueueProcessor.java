/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.model.Helper;
import jSmugmugBackup.view.Logger;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class TransferQueueProcessor implements Runnable
{
	private Logger log = null;
	private LinkedBlockingQueue<ITransferQueueItem> queue = null;
	private Vector<ITransferQueueItem> processedItemList = null;
	private long queue_size_byte;
	private long startTime;
	
	public TransferQueueProcessor(LinkedBlockingQueue<ITransferQueueItem> queue, Vector<ITransferQueueItem> processedItemList)
	{
		this.log = Logger.getInstance();
		this.queue = queue;
		this.processedItemList = processedItemList;
		
		// compute queue size
		this.queue_size_byte = 0;
		for (ITransferQueueItem item : queue)
		{
			this.queue_size_byte += item.getFileSize();
		}		
    	double queue_size_mb = (double)this.queue_size_byte/(1024*1024);
		DecimalFormat df = new DecimalFormat("0.0");		
		
		
		this.log.printLogLine(Helper.getCurrentTimeString() + " initializing TransferQueueProcessor (items: " + this.queue.size() + ", size: " + df.format(queue_size_mb) + " mb) ... ok");

		//getting start time
		this.startTime = (new Date()).getTime();
	}
	
	public void run()
	{
		this.log.printLogLine(Helper.getCurrentTimeString() + " running TransferQueueProcessor in separate Thread ...");
		
		ITransferQueueItem item = this.queue.poll(); //Retrieves and removes the head of this queue, or null  if this queue is empty
		while (item != null)
		{
			//start processing
			item.process();
			this.processedItemList.add(item);
			
			//estimate remaining time
			long currTransferedBytes = 0;
			for (ITransferQueueItem processedItem : this.processedItemList) { currTransferedBytes += processedItem.getFileSize(); }
			long elapsedTime = (new Date()).getTime() - startTime;
			double estimatedTotalTime = (double)elapsedTime / ( (double)currTransferedBytes / (double)this.queue_size_byte );
			long estimatedRemainingTime = (long)estimatedTotalTime - elapsedTime;
			this.log.printLogLine(" ... " + Helper.getDurationTimeString(estimatedRemainingTime) + " remaining");
			
			//get next item
			item = this.queue.poll();
		}
	}

}
