/*
 * Created on Oct 1, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.model.queue;

public interface ITransferQueueItem
{
    void process();
    ITransferQueueItemProcessResults getResults();
    long getFileSize();
}
