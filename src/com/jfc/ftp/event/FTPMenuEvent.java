package com.jfc.ftp.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.gui.dialog.FTPServerManagerDialog;
import com.jfc.ftp.tools.SystemTools;

/**
 * 用于处理菜单的事件
 * 
 * @author SavageGarden
 *
 */
public class FTPMenuEvent extends FTPEvent {

	private final static Logger logger = Logger.getLogger(FTPMenuEvent.class);
	
	public static void addActionListenerToConn(JMenuItem menuItem){
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.serverManagerDialog = new FTPServerManagerDialog(mainFrame);
				mainFrame.serverManagerDialog.setVisible(true);
			}
		});
	}
	public static void addActionListenerToExit(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.serverTableModel.clear();
				mainFrame.serverTextFieldHost.setText("server");
				mainFrame.ftpTools.closeFTP();
			}
		});
	}
}
