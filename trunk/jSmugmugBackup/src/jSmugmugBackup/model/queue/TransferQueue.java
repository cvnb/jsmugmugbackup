/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.view.Logger;

import java.util.Vector;
import java.util.concurrent.*;

public class TransferQueue implements ITransferQueue
{
	private Logger log = null;
	private Thread queueProcessorThread;
	private LinkedBlockingQueue<ITransferQueueItem> queue = null;
	private Vector<ITransferQueueItem> processedItemList = null;

	public TransferQueue()
	{
		this.log = Logger.getInstance();
		this.queue = new LinkedBlockingQueue<ITransferQueueItem>();
		this.processedItemList = new Vector<ITransferQueueItem>();
	}
	
	public void add(ITransferQueueItem item)
	{
		//this.log.printLogLine("  adding item to queue");
		
		//Adds the specified element to the tail of this queue, waiting if necessary for space to become available.
		try { this.queue.put(item); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}

	public boolean remove(ITransferQueueItem item)
	{
		//Removes a single instance of the specified element from this queue, if it is present.
		return this.queue.remove(item);
	}
	
	public boolean isProcessing()
	{
		return this.queueProcessorThread.isAlive();
	}

	public void startAsyncProcessing()
	{
		this.queueProcessorThread = new Thread(new TransferQueueProcessor(this.queue, this.processedItemList));
		this.queueProcessorThread.start();

	}

	public void startSyncProcessing()
	{
		this.log.printLogLine("TransferQueue.startSyncProcessing()");
		
		this.queueProcessorThread = new Thread(new TransferQueueProcessor(this.queue, this.processedItemList));
		this.queueProcessorThread.start();
		try { this.queueProcessorThread.join(); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public void stopAsyncProcessing()
	{
		this.queueProcessorThread.stop();
	}

	public Vector<ITransferQueueItem> getProcessedItemList()
	{
		return this.processedItemList;
	}

}
