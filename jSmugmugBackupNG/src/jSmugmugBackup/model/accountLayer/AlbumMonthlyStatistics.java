/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.accountLayer;

import java.io.Serializable;

/**
 *
 * @author paul
 */
public class AlbumMonthlyStatistics implements IAlbumMonthlyStatistics, Serializable
{
    private int month   = 0;
    private int year    = 0;
    private int albumID = 0;
    private int bytes   = 0;
    private int thumb   = 0;
    private int tiny    = 0;
    private int medium  = 0;
    private int large   = 0;
    private int xLarge  = 0;
    private int x2Large = 0;
    private int x3Large = 0;
    private float original  = 0.0f;
    private float video320  = 0.0f;
    private float video640  = 0.0f;
    private float video960  = 0.0f;
    private float video1280 = 0.0f;

    public AlbumMonthlyStatistics()
    {
        // initialize without values ... not really useful, but prevents null-pointer exception later on ...
    }

    public AlbumMonthlyStatistics( int month, int year, int albumID, int bytes,
                                   int thumb, int tiny, int medium, int large, int xLarge, int x2Large, int x3Large,
                                   float original, float video320, float video640, float video960, float videeo1280)
    {
        this.month = month;
        this.year = year;

        this.albumID = albumID;
        this.bytes = bytes;
        this.thumb = thumb;
        this.tiny = tiny;
        this.medium = medium;
        this.large = large;
        this.xLarge = xLarge;
        this.x2Large = x2Large;
        this.x3Large = x3Large;

        this.original  = original;
        this.video320  = video320;
        this.video640  = video640;
        this.video960  = video960;
        this.video1280 = videeo1280;

    }

    // copy constructor
    public AlbumMonthlyStatistics(IAlbumMonthlyStatistics albumStatistics)
    {
        this( albumStatistics.getMonth(), albumStatistics.getYear(), albumStatistics.getAlbumID(), albumStatistics.getBytes(),
                albumStatistics.getThumb(), albumStatistics.getTiny(), albumStatistics.getMedium(), albumStatistics.getLarge(),
                albumStatistics.getXLarge(), albumStatistics.getX2Large(), albumStatistics.getX3Large(),
                albumStatistics.getOriginal(), albumStatistics.getVideo320(), albumStatistics.getVideo640(),
                albumStatistics.getVideo960(), albumStatistics.getVideo1280());
    }

    public int getMonth()   { return this.month; }
    public int getYear()    { return this.year; }
    public int getAlbumID() { return this.albumID; }
    public int getBytes()   { return this.bytes; }
    public int getThumb()   { return this.thumb; }
    public int getTiny()    { return this.tiny; }
    public int getMedium()  { return this.medium; }
    public int getLarge()   { return this.large; }
    public int getXLarge()  { return this.xLarge; }
    public int getX2Large() { return this.x2Large; }
    public int getX3Large() { return this.x3Large; }
    public float getOriginal()  { return this.original; }
    public float getVideo320()  { return this.video320; }
    public float getVideo640()  { return this.video640; }
    public float getVideo960()  { return this.video960; }
    public float getVideo1280() { return this.video1280; }

}
