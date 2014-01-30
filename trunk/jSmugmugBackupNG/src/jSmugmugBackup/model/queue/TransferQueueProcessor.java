/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import jSmugmugBackup.model.Helper;
import jSmugmugBackup.view.*;

import java.awt.event.ActionListener;
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
    private ActionListener asyncProcessQueueFinishedListener = null;

    public TransferQueueProcessor(LinkedBlockingQueue<ITransferQueueItem> queue, Vector<ITransferQueueItem> processedItemList, ActionListener finishListener)
    {
        this.log = Logger.getInstance();
        this.queue = queue;
        this.processedItemList = processedItemList;
        this.asyncProcessQueueFinishedListener = finishListener;

        // compute queue size
        this.queue_size_byte = 0;
        for (ITransferQueueItem item : queue)
        {
            this.queue_size_byte += item.getFileSize();
        }
        double queue_size_mb = (double)this.queue_size_byte/(1024*1024);
        DecimalFormat df = new DecimalFormat("0.0");


        this.log.printLogLine(LogLevelEnum.Message, 0, Helper.getCurrentTimeString() + " initializing TransferQueueProcessor (items: " + this.queue.size() + ", size: " + df.format(queue_size_mb) + " mb) ... ok");

        //getting start time
        this.startTime = (new Date()).getTime();
    }

    public void run()
    {
        this.log.printLogLine(LogLevelEnum.Message, 0, Helper.getCurrentTimeString() + " running TransferQueueProcessor in separate Thread ...");

        int itemCount = 0;
        int itemTotalNumber = this.queue.size(); //total number of items in queue
        ITransferQueueItem item = this.queue.poll(); //Retrieves and removes the head of this queue, or null  if this queue is empty
        while (item != null)
        {
            //start processing
            item.process();
            this.processedItemList.add(item);

            //estimate remaining time, generate output
            itemCount++;
            long currTransferedBytes = 0;
            for (ITransferQueueItem processedItem : this.processedItemList) { currTransferedBytes += processedItem.getFileSize(); }
            long elapsedTime = (new Date()).getTime() - startTime;
            double estimatedTotalTime = (double)elapsedTime / ( (double)currTransferedBytes / (double)this.queue_size_byte );
            long estimatedRemainingTime = (long)estimatedTotalTime - elapsedTime;
            this.log.printLogLine(LogLevelEnum.Message, 0, ", " + itemCount + "/" + itemTotalNumber + ", " + Helper.getDurationTimeString(estimatedRemainingTime) + ")");
            //this.log.printLogLine("DEBUG: elasped time=" + elapsedTime + ",\tcurrTransferedBytes=" + currTransferedBytes + ",\tqueue_size_byte=" + this.queue_size_byte);

            //get next item
            item = this.queue.poll();
        }

        //notify the controller
        if (this.asyncProcessQueueFinishedListener != null)
        {
            this.asyncProcessQueueFinishedListener.actionPerformed(null);
        }
    }

}
