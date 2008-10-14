/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.view.Logger;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class TransferQueueProcessor implements Runnable
{
	private Logger log = null;
	private LinkedBlockingQueue<ITransferQueueItem> queue = null;
	private Vector<ITransferQueueItem> processedItemList = null;
	
	public TransferQueueProcessor(LinkedBlockingQueue<ITransferQueueItem> queue, Vector<ITransferQueueItem> processedItemList)
	{
		this.log = Logger.getInstance();
		this.queue = queue;
		this.processedItemList = processedItemList;
		
		this.log.printLogLine("initializing TransferQueueProcessor ... queue size: " + this.queue.size() + " items");		
	}
	
	public void run()
	{
		this.log.printLogLine("running TransferQueueProcessor in separate Thread ...");
		
		ITransferQueueItem item = this.queue.poll(); //Retrieves and removes the head of this queue, or null  if this queue is empty
		while (item != null)
		{
			//start processing
			item.process();
			this.processedItemList.add(item);
			
			//get next item
			item = this.queue.poll();
		}
	}

}
