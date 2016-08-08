package com.jfc.ftp.main;

import javax.swing.JFrame;

import com.jfc.ftp.event.FTPEvent;
import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.listener.FTPMainFrameListener;
import com.jfc.ftp.tools.SystemTools;
/**
 * Æô¶¯³ÌÐò
 * @author SavageGarden
 *
 */
public class JavaFTPClient {

	public static void main(String args[]) {
		FTPMainFrame mainFrame = new FTPMainFrame();
		SystemTools.mainFrame = mainFrame;
		FTPEvent.mainFrame = mainFrame;
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new FTPMainFrameListener());
		mainFrame.setVisible(true);
	}
}