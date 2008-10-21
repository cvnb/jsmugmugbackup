/*
 * Created on Oct 4, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.swing_dialogs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JButton;

public class SwingDownloadDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel_category = null;
	private JLabel jLabel_subcategory = null;
	private JLabel jLabel_album = null;
	private JLabel jLabel_directory = null;
	private JComboBox jComboBox_category = null;
	private JComboBox jComboBox_subcategory = null;
	private JComboBox jComboBox_album = null;
	private JTextField jTextField_directory = null;
	private JButton jButton_browse = null;
	private JButton jButton_cancel = null;
	private JButton jButton_download = null;

	/**
	 * This is the default constructor
	 */
	public SwingDownloadDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(350, 450);
		this.setContentPane(getJContentPane());
		this.setTitle("Download");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel_directory = new JLabel();
			jLabel_directory.setBounds(new Rectangle(10, 70, 80, 15));
			jLabel_directory.setText("directory");
			jLabel_album = new JLabel();
			jLabel_album.setBounds(new Rectangle(10, 45, 80, 15));
			jLabel_album.setText("Album");
			jLabel_subcategory = new JLabel();
			jLabel_subcategory.setBounds(new Rectangle(10, 25, 80, 15));
			jLabel_subcategory.setText("Subcategory");
			jLabel_category = new JLabel();
			jLabel_category.setBounds(new Rectangle(10, 5, 80, 15));
			jLabel_category.setText("Category");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel_category, null);
			jContentPane.add(jLabel_subcategory, null);
			jContentPane.add(jLabel_album, null);
			jContentPane.add(jLabel_directory, null);
			jContentPane.add(getJComboBox_category(), null);
			jContentPane.add(getJComboBox_subcategory(), null);
			jContentPane.add(getJComboBox_album(), null);
			jContentPane.add(getJTextField_directory(), null);
			jContentPane.add(getJButton_browse(), null);
			jContentPane.add(getJButton_cancel(), null);
			jContentPane.add(getJButton_download(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jComboBox_category	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_category() {
		if (jComboBox_category == null) {
			jComboBox_category = new JComboBox();
			jComboBox_category.setBounds(new Rectangle(100, 5, 220, 15));
		}
		return jComboBox_category;
	}

	/**
	 * This method initializes jComboBox_subcategory	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_subcategory() {
		if (jComboBox_subcategory == null) {
			jComboBox_subcategory = new JComboBox();
			jComboBox_subcategory.setBounds(new Rectangle(100, 25, 220, 15));
		}
		return jComboBox_subcategory;
	}

	/**
	 * This method initializes jComboBox_album	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox_album() {
		if (jComboBox_album == null) {
			jComboBox_album = new JComboBox();
			jComboBox_album.setBounds(new Rectangle(100, 45, 220, 15));
		}
		return jComboBox_album;
	}

	/**
	 * This method initializes jTextField_directory	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField_directory() {
		if (jTextField_directory == null) {
			jTextField_directory = new JTextField();
			jTextField_directory.setBounds(new Rectangle(100, 70, 220, 15));
		}
		return jTextField_directory;
	}

	/**
	 * This method initializes jButton_browse	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_browse() {
		if (jButton_browse == null) {
			jButton_browse = new JButton();
			jButton_browse.setBounds(new Rectangle(100, 85, 220, 15));
			jButton_browse.setText("browse");
		}
		return jButton_browse;
	}

	/**
	 * This method initializes jButton_cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_cancel() {
		if (jButton_cancel == null) {
			jButton_cancel = new JButton();
			jButton_cancel.setBounds(new Rectangle(40, 380, 100, 25));
			jButton_cancel.setText("Cancel");
			jButton_cancel.addActionListener(new CancelButtonListener());
		}
		return jButton_cancel;
	}

	/**
	 * This method initializes jButton_download	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_download() {
		if (jButton_download == null) {
			jButton_download = new JButton();
			jButton_download.setBounds(new Rectangle(180, 380, 100, 25));
			jButton_download.setText("Download");
		}
		return jButton_download;
	}
	
	// Action listener
	class CancelButtonListener implements ActionListener
	{
		//@Override
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
