package jSmugmugBackup.view;

import jSmugmugBackup.model.*;
import jSmugmugBackup.model.data.*;
import jSmugmugBackup.model.login.*;
import jSmugmugBackup.view.login.ILoginView;
import jSmugmugBackup.view.login.LoginViewSwing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Dimension;

public class SwingView extends JFrame implements IView
{
	private Logger log = null;
	private Model model = null;
	private SwingUploadDialog uploadDialog = null;  //  @jve:decl-index=0:visual-constraint="582,10"
	private SwingDownloadDialog downloadDialog = null;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane_log = null;
	private JTextArea jTextArea_log = null;
	private JLabel jLabel_log = null;
	private JButton jButton_login = null;
	private JButton jButton_refresh = null;
	private JButton jButton_upload = null;
	private JButton jButton_download = null;
	private JButton jButton_quit = null;

	private JScrollPane jScrollPane_queue = null;
	private JTextArea jTextArea_queue = null;
	private JLabel jLabel_queue = null;

	private JScrollPane jScrollPane_files = null;
	private JTree jTree_files = null;
	private DefaultMutableTreeNode files_root_node = null;


	/**
	 * This is the default constructor
	 */
	public SwingView(Model model)
	{
		super();
		initialize();
		
		this.model = model;
		this.model.setView(this);
		this.log = Logger.getInstance();
		this.log.registerView(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(560, 452);
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("jSmugmugBackup v" + Constants.version);
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel_queue = new JLabel();
			jLabel_queue.setBounds(new Rectangle(10, 235, 50, 15));
			jLabel_queue.setText("queue:");
			jLabel_log = new JLabel();
			jLabel_log.setBounds(new Rectangle(10, 315, 40, 15));
			jLabel_log.setText("log:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJScrollPane_log(), null);
			jContentPane.add(jLabel_log, null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJButton_refresh(), null);
			jContentPane.add(getJButton_upload(), null);
			jContentPane.add(getJButton_download(), null);
			jContentPane.add(getJButton_quit(), null);
			jContentPane.add(getJScrollPane_queue(), null);
			jContentPane.add(jLabel_queue, null);
			jContentPane.add(getJScrollPane_files(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_log() {
		if (jScrollPane_log == null) {
			jScrollPane_log = new JScrollPane();
			jScrollPane_log.setBounds(new Rectangle(10, 330, 450, 60));
			jScrollPane_log.setViewportView(getJTextArea());
		}
		return jScrollPane_log;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea_log == null) {
			jTextArea_log = new JTextArea();
			jTextArea_log.setEditable(false);
		}
		return jTextArea_log;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton_login == null) {
			jButton_login = new JButton();
			jButton_login.setBounds(new Rectangle(470, 5, 75, 15));
			jButton_login.setText("Login");
		}
		return jButton_login;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_refresh() {
		if (jButton_refresh == null) {
			jButton_refresh = new JButton();
			jButton_refresh.setBounds(new Rectangle(10, 400, 100, 15));
			jButton_refresh.setText("Refresh");
		}
		return jButton_refresh;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_upload() {
		if (jButton_upload == null) {
			jButton_upload = new JButton();
			jButton_upload.setBounds(new Rectangle(120, 400, 100, 15));
			jButton_upload.setText("Upload");
		}
		return jButton_upload;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_download() {
		if (jButton_download == null) {
			jButton_download = new JButton();
			jButton_download.setBounds(new Rectangle(230, 400, 100, 15));
			jButton_download.setText("Download");
		}
		return jButton_download;
	}

	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_quit() {
		if (jButton_quit == null) {
			jButton_quit = new JButton();
			jButton_quit.setBounds(new Rectangle(470, 400, 75, 15));
			jButton_quit.setText("Quit");
		}
		return jButton_quit;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_queue() {
		if (jScrollPane_queue == null) {
			jScrollPane_queue = new JScrollPane();
			jScrollPane_queue.setBounds(new Rectangle(10, 250, 450, 60));
			jScrollPane_queue.setViewportView(getJTextArea_queue());
		}
		return jScrollPane_queue;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea_queue() {
		if (jTextArea_queue == null) {
			jTextArea_queue = new JTextArea();
			jTextArea_queue.setEditable(false);
		}
		return jTextArea_queue;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_files() {
		if (jScrollPane_files == null) {
			jScrollPane_files = new JScrollPane();
			jScrollPane_files.setBounds(new Rectangle(10, 10, 450, 220));
			jScrollPane_files.setViewportView(getJTree_files());
		}
		return jScrollPane_files;
	}

	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getJTree_files() {
		if (jTree_files == null)
		{
			this.files_root_node = new DefaultMutableTreeNode("SmugMug Account");
			jTree_files = new JTree(this.files_root_node);
		}
		return jTree_files;
	}

	
	//------------------------------------
	//@Override
	public void addLoginButtonListener(ActionListener listener) {
		this.jButton_login.addActionListener(listener);
	}
	//@Override
	public void addDownloadDialogButtonListener(ActionListener listener) {
		this.jButton_download.addActionListener(listener);		
	}
	//@Override
	public void addUploadDialogButtonListener(ActionListener listener) {
		this.jButton_upload.addActionListener(listener);		
	}
	//@Override
	public void addRefreshButtonListener(ActionListener listener) {
		this.jButton_refresh.addActionListener(listener);
	}
	//@Override
	public void addQuitButtonListener(ActionListener listener) {
		this.jButton_quit.addActionListener(listener);		
	}

	public void addDownloadStartButtonListener(ActionListener listener) {
				
	}

	public void addUploadStartButtonListener(ActionListener listener)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void addVerifyDialogButtonListener(ActionListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void addVerifyStartButtonListener(ActionListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void addDeleteDialogButtonListener(ActionListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void addDeleteStartButtonListener(ActionListener listener) {
		// TODO Auto-generated method stub
		
	}

	

	//@Override
	public void refreshFileListing(AccountListing accountListing)
	{
		//this.files_root_node = new DefaultMutableTreeNode(accountListing.getNickName());
		
		for (ICategoryType cat : accountListing.getCategoryList())
		{
			DefaultMutableTreeNode categoryTreeNode = new DefaultMutableTreeNode(cat.getName());
			for (IAlbumType a : cat.getAlbumList())
			{
				DefaultMutableTreeNode albumTreeNode = new DefaultMutableTreeNode(a.getName());
				for (IImageType i : a.getImageList())
				{
					albumTreeNode.add(new DefaultMutableTreeNode(i.getName()));
				}
				categoryTreeNode.add(albumTreeNode);
			}
			
			this.files_root_node.add(categoryTreeNode);
		}
		
		//expand the tree
		for (int row=0; row < this.getJTree_files().getRowCount(); row++)
		{
			this.getJTree_files().expandRow(row);
		}
	}
	
    public void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }


	//@Override
	public ILoginView getLoginToken()
	{
		//ILoginToken loginToken = new LoginToken(this.jTextField_username.getText(), this.jPasswordField_password.getText());
		//ISmugmugLogin loginToken = new SmugmugLoginSwing(this.jTextField_username.getText(), this.jPasswordField_password.getText());
		ILoginView loginToken = new LoginViewSwing(this);
		
		return loginToken;
	}
	
	public ITransferDialogResult showUploadDialog()
	{
		this.uploadDialog = new SwingUploadDialog();
		this.uploadDialog.setSize(new Dimension(350, 450));
		this.uploadDialog.setModal(true);
		this.uploadDialog.setVisible(true);		
		
		return null;
	}

	//@Override
	public TransferDialogResult showDownloadDialog()
	{
		this.downloadDialog = new SwingDownloadDialog();
		this.downloadDialog.setSize(new Dimension(350, 450));
		this.downloadDialog.setModal(true);
		this.downloadDialog.setVisible(true);
		
		return null;
	}
	
	public ITransferDialogResult showDeleteDialog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ITransferDialogResult showVerifyDialog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void printLog(String text)
	{
		this.jTextArea_log.append(text);
	}
	//------------------------------------


}  //  @jve:decl-index=0:visual-constraint="10,10"
