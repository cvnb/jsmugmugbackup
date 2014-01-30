/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.view.*;

import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.*;

public class TransferQueue implements ITransferQueue
{
    private Logger log = null;
    private Thread queueProcessorThread;
    private LinkedBlockingQueue<ITransferQueueItem> queue = null;
    private Vector<ITransferQueueItem> processedItemList = null;
    private ActionListener asyncProcessQueueFinishedListener = null;

    // Protected constructor is sufficient to suppress unauthorized calls to the constructor
    protected TransferQueue()
    {
        this.log = Logger.getInstance();
        this.queue = new LinkedBlockingQueue<ITransferQueueItem>();
        this.processedItemList = new Vector<ITransferQueueItem>();
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.instance , not before.
     */
    private static class TransferQueueHolder
    {
        private final static TransferQueue INSTANCE = new TransferQueue();
    }

    public static TransferQueue getInstance()
    {
        return TransferQueueHolder.INSTANCE;
    }

    public void addASyncProcessQueueFinishedListener(ActionListener listener)   { this.asyncProcessQueueFinishedListener = listener; }
    //--------------------------------------------------------------------------


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
        this.log.printLogLine(LogLevelEnum.Message, 0, "TransferQueue.startAsyncProcessing()");

        this.queueProcessorThread = new Thread(new TransferQueueProcessor(this.queue, this.processedItemList, this.asyncProcessQueueFinishedListener));
        this.queueProcessorThread.start();

    }

    public void startSyncProcessing()
    {
        this.log.printLogLine(LogLevelEnum.Message, 0, "TransferQueue.startSyncProcessing()");

        this.queueProcessorThread = new Thread(new TransferQueueProcessor(this.queue, this.processedItemList, null));
        this.queueProcessorThread.start();
        try { this.queueProcessorThread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("deprecation")
    public void stopAsyncProcessing()
    {
        this.queueProcessorThread.stop();
    }

    public Vector<ITransferQueueItem> getProcessedItemList()
    {
        return this.processedItemList;
    }

}
