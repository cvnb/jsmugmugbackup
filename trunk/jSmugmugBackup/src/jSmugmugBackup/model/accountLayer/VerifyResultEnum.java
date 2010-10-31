/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jSmugmugBackup.model.accountLayer;

/**
 *
 * @author paul
 */
public enum VerifyResultEnum
{
    Null,
    UploadedMultipleTimes,
    IgnoreTagAndFilesizeLimit,
    IgnoreTagExists,
    FilesizeLimit,
    NotUploaded,
    NoLocalFileFound,
    //MD5VideoFailed,
    Ok

}
