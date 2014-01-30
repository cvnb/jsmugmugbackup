/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.accountLayer;


/**
 *
 * @author paul
 */
public interface IAlbumMonthlyStatistics
{
    int getMonth();
    int getYear();
    int getAlbumID();
    int getBytes();
    int getThumb();
    int getTiny();
    int getMedium();
    int getLarge();
    int getXLarge();
    int getX2Large();
    int getX3Large();
    float getOriginal();
    float getVideo320();
    float getVideo640();
    float getVideo960();
    float getVideo1280();


}
