/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.accountLayer;

/**
 *
 * @author paul
 */
public class TransferStatistics
{
    // encapsulation statistics data into an object, so we can with references

    public int estimatedImageCount = 0;
    public int estimatedAlbumCount = 0;


    public final double completionStep = 0.1;
    public double currCompletionStep = completionStep;
    public int imageCount = 0;

}
