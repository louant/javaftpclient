package com.jfc.ftp.event.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.jfc.ftp.event.FTPEvent;
import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.gui.dialog.FTPServerManagerDialog;
import com.jfc.ftp.model.ftpsite.FTPSiteModel;
import com.jfc.ftp.tools.FTPTools;
import com.jfc.ftp.tools.SystemTools;

/**
 * 用于处理弹出窗口中的按钮事件
 * 
 * @author SavageGarden
 *
 */
public class DialogButtonEvent extends FTPEvent {

	private final static Logger logger = Logger.getLogger(DialogButtonEvent.class);
	
	/**
	 * 为链接按钮添加事件处理
	 * @param dialog
	 * @param button
	 */
	public static void addActionListenerToConn(final  FTPServerManagerDialog dialog, JButton button) {
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String name = dialog.nameTextField.getText();
				String host = dialog.hostTextField.getText();
				String port = dialog.portTextField.getText();
				String user = dialog.userTextField.getText();
				String pswd = dialog.pswdTextField.getText();
				FTPSiteModel ftpSite = null;
				boolean checkFlag = false;
				for(Object obj : mainFrame.serverManagerDialog.ftpSiteTableModel.ftpSiteList) {
					ftpSite = (FTPSiteModel)obj;
					if(ftpSite.getName().equals(name)) {
						checkFlag = true;
						break;
					}
				}
				if(!checkFlag) {
					ftpSite.setName(name);
					ftpSite.setHost(host);
					ftpSite.setPort(port);
					ftpSite.setUser(user);
					ftpSite.setPswd(pswd);
					SystemTools.addFTPSite(ftpSite);
				}
				mainFrame.ftpTools = new FTPTools(host, Integer.parseInt(port), user, pswd);
				dialog.setVisible(false);
			}
		});
	}
	/**
	 * 为结束按钮添加事件处理
	 * @param dialog
	 * @param button
	 */
	public static void addActionListenerToCancel(final  FTPServerManagerDialog dialog, JButton button) {
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.dispose();
			}
		});
	}
}
