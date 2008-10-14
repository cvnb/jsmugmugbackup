/*
 * Created on Sep 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class SwingUploadDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JComboBox jComboBox_category = null;
	private JLabel jLabel_category = null;
	private JLabel jLabel_subcategory = null;
	private JComboBox jComboBox_subcategory = null;
	private JLabel jLabel_files = null;
	private JLabel jLabel_album = null;
	private JComboBox jComboBox_album = null;
	private JScrollPane jScrollPane_files = null;
	private JTree jTree_files = null;
	private JLabel jLabel_options = null;
	private JButton jButton_upload = null;
	private JButton jButton_cancel = null;
	private JLabel jLabel_options_public = null;
	private JCheckBox jCheckBox = null;
	private JButton jButton_importfiles = null;
	/**
	 * This is the default constructor
	 */
	public SwingUploadDialog() {
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
		this.setTitle("Upload");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel_options_public = new JLabel();
			jLabel_options_public.setBounds(new Rectangle(25, 235, 60, 15));
			jLabel_options_public.setText("public:");
			jLabel_options = new JLabel();
			jLabel_options.setBounds(new Rectangle(10, 215, 80, 15));
			jLabel_options.setText("options");
			jLabel_album = new JLabel();
			jLabel_album.setBounds(new Rectangle(10, 45, 80, 15));
			jLabel_album.setText("Album");
			jLabel_files = new JLabel();
			jLabel_files.setBounds(new Rectangle(10, 70, 80, 15));
			jLabel_files.setText("files:");
			jLabel_subcategory = new JLabel();
			jLabel_subcategory.setBounds(new Rectangle(10, 25, 80, 15));
			jLabel_subcategory.setText("Subcategory");
			jLabel_category = new JLabel();
			jLabel_category.setBounds(new Rectangle(10, 5, 80, 15));
			jLabel_category.setText("Category");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJComboBox_category(), null);
			jContentPane.add(jLabel_category, null);
			jContentPane.add(jLabel_subcategory, null);
			jContentPane.add(getJComboBox_subcategory(), null);
			jContentPane.add(jLabel_files, null);
			jContentPane.add(jLabel_album, null);
			jContentPane.add(getJComboBox_album(), null);
			jContentPane.add(getJScrollPane_files(), null);
			jContentPane.add(jLabel_options, null);
			jContentPane.add(getJButton_upload(), null);
			jContentPane.add(getJButton_cancel(), null);
			jContentPane.add(jLabel_options_public, null);
			jContentPane.add(getJCheckBox(), null);
			jContentPane.add(getJButton_importfiles(), null);
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
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane_files() {
		if (jScrollPane_files == null) {
			jScrollPane_files = new JScrollPane();
			jScrollPane_files.setBounds(new Rectangle(10, 85, 310, 120));
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
		if (jTree_files == null) {
			jTree_files = new JTree();
		}
		return jTree_files;
	}

	/**
	 * This method initializes jButton_upload	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_upload() {
		if (jButton_upload == null) {
			jButton_upload = new JButton();
			jButton_upload.setBounds(new Rectangle(180, 380, 100, 25));
			jButton_upload.setText("Upload");
		}
		return jButton_upload;
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
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setBounds(new Rectangle(90, 235, 20, 15));
		}
		return jCheckBox;
	}

	/**
	 * This method initializes jButton_importfiles	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_importfiles() {
		if (jButton_importfiles == null) {
			jButton_importfiles = new JButton();
			jButton_importfiles.setBounds(new Rectangle(100, 70, 220, 15));
			jButton_importfiles.setText("import files");
		}
		return jButton_importfiles;
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
