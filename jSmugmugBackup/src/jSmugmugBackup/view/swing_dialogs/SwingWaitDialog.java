/*
 * Created on Oct 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jSmugmugBackup.view.swing_dialogs;

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JProgressBar;

public class SwingWaitDialog extends JDialog
{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel_waiting = null;
	private JProgressBar jProgressBar = null;
	private Thread animatorThread = null;  //  @jve:decl-index=0:

	/**
	 * @param owner
	 */
	public SwingWaitDialog(Frame owner) {
		super(owner);

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		
		this.jProgressBar.setValue(10);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel_waiting = new JLabel();
			jLabel_waiting.setBounds(new Rectangle(17, 22, 229, 17));
			jLabel_waiting.setText("waiting ...");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel_waiting, null);
			jContentPane.add(getJProgressBar(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(19, 47, 252, 25));
			jProgressBar.setName("progress");
		}
		return jProgressBar;
	}

	//----------------------------------------
	public void setMessage(String message)
	{
		this.setTitle(message);
	}
	
	public void startProgressAnimation()
	{
		this.setVisible(true);
		
		this.animatorThread = new Thread(new Animator());
		this.animatorThread.start();		
	}
	
	public void stopProgressAnimation()
	{
		this.setVisible(false);

		this.animatorThread.stop();
		this.animatorThread = null;
	}
	
	private class Animator implements Runnable
	{
		public void run()
		{
			while (true)
			{
				try { Thread.sleep(1000); }
				catch (InterruptedException e) { e.printStackTrace(); }

//				int progressValue = jProgressBar.getValue();
//				if (progressValue < 100)
//				{
//					progressValue += 10;
//				}
//				else { progressValue = 0; }
//				jProgressBar.setValue(progressValue);				
			}			
		}
		
	}
}
