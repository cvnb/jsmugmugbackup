package jSmugmugBackup.view;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.accountLayer.*;


import java.awt.event.*;
import java.util.Vector;


public interface IView
{
    void start();

    //ILoginView getLoginMethod();
    void updateFileListing(IRootElement smugmugRoot);
    void showStatistics(Vector<IAlbum> albumList);

    ILoginDialogResult showLoginDialog();
    ITransferDialogResult showListDialog();
    ITransferDialogResult showSortDialog();
    ITransferDialogResult showAutotagDialog();
    ITransferDialogResult showStatisticsDialog();
    ITransferDialogResult showOsmlayerDialog();
    ITransferDialogResult showKmllayerDialog();
    ITransferDialogResult showUploadDialog();
    ITransferDialogResult showDownloadDialog();
    ITransferDialogResult showDownloadURLDialog();
    ITransferDialogResult showVerifyDialog();
    ITransferDialogResult showDeleteDialog();

    void printLog(String text);
    void showError(String errMessage);
    void showBusyStart(String waitingMessage);
    void showBusyStop();

    void addLoginButtonListener(ActionListener listener);
    void addListButtonListener(ActionListener listener);
    void addSortButtonListener(ActionListener listener);
    void addAutotagButtonListener(ActionListener listener);
    void addStatisticsButtonListener(ActionListener listener);
    void addOsmlayerButtonListener(ActionListener listener);
    void addKmllayerButtonListener(ActionListener listener);
    void addUploadDialogButtonListener(ActionListener listener);
    void addDownloadDialogButtonListener(ActionListener listener);
    void addDownloadURLDialogButtonListener(ActionListener listener);
    void addVerifyDialogButtonListener(ActionListener listener);
    void addDeleteDialogButtonListener(ActionListener listener);
    void addQuitButtonListener(ActionListener listener);
    void addSyncProcessQueueButtonListener(ActionListener listener);

    void addASyncProcessQueueStartButtonListener(ActionListener listener);
    //void addASyncProcessQueueFinishedListener(ActionListener listener);
    void notifyASyncProcessQueueFinished();

}
