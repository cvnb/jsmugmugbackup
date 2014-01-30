/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

import java.awt.event.ActionListener;
import java.util.Vector;

public interface ITransferQueue
{
    void addASyncProcessQueueFinishedListener(ActionListener listener);


    void add(ITransferQueueItem item);
    boolean remove(ITransferQueueItem item);

    void startSyncProcessing();
    void startAsyncProcessing();
    void stopAsyncProcessing();
    boolean isProcessing();

    //Vector<ITransferQueueItem> getItemList();
    Vector<ITransferQueueItem> getProcessedItemList();
}
