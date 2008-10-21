/*
 * Created on Oct 5, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.swing_dialogs;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JDialog;

public class SwingLoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel_username = null;
	private JLabel jLabel_password = null;
	private JTextField jTextField_username = null;
	private JPasswordField jPasswordField_password = null;
	private JButton jButton_login = null;
	private JButton jButton_cancel = null;

	/**
	 * @param owner
	 */
	public SwingLoginDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel_password = new JLabel();
			jLabel_password.setBounds(new Rectangle(15, 55, 80, 15));
			jLabel_password.setText("Password");
			jLabel_username = new JLabel();
			jLabel_username.setBounds(new Rectangle(15, 30, 80, 15));
			jLabel_username.setText("Username");			
			jContentPane = new JPanel();
			jContentPane.setLayout(null);			
			jContentPane.add(jLabel_username, null);
			jContentPane.add(jLabel_password, null);
			jContentPane.add(getJTextField_username(), null);
			jContentPane.add(getJPasswordField_password(), null);
			jContentPane.add(getJButton_login(), null);
			jContentPane.add(getJButton_cancel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField_username	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	public JTextField getJTextField_username() {
		if (jTextField_username == null) {
			jTextField_username = new JTextField();
			jTextField_username.setBounds(new Rectangle(110, 30, 160, 15));
		}
		return jTextField_username;
	}

	/**
	 * This method initializes jPasswordField_password	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	public JPasswordField getJPasswordField_password() {
		if (jPasswordField_password == null) {
			jPasswordField_password = new JPasswordField();
			jPasswordField_password.setBounds(new Rectangle(110, 55, 160, 15));
		}
		return jPasswordField_password;
	}

	/**
	 * This method initializes jButton_login	
	 * 	
	 * @return javax.swing.JButton	
	 */
	public JButton getJButton_login() {
		if (jButton_login == null) {
			jButton_login = new JButton();
			jButton_login.setBounds(new Rectangle(160, 100, 80, 25));
			jButton_login.setText("Ok");
			jButton_login.addActionListener(new OkButtonListener());
		}
		return jButton_login;
	}

	/**
	 * This method initializes jButton_cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton_cancel() {
		if (jButton_cancel == null) {
			jButton_cancel = new JButton();
			jButton_cancel.setBounds(new Rectangle(40, 100, 80, 25));
			jButton_cancel.setText("Cancel");
			jButton_cancel.addActionListener(new CancelButtonListener());
		}
		return jButton_cancel;
	}
	
	//-----------------------------------------------------------------
	// Action listener
	class OkButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}		
	}
	
	// Action listener
	class CancelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			jTextField_username.setText("");
			jPasswordField_password.setText("");
			
			setVisible(false);
		}		
	}
	
}
