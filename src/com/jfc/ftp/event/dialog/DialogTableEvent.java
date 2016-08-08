package com.jfc.ftp.event.dialog;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.jfc.ftp.event.FTPEvent;
import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.model.ftpsite.FTPSiteModel;
import com.jfc.ftp.tools.FTPTools;
import com.jfc.ftp.tools.SystemTools;

public class DialogTableEvent extends FTPEvent {
	
	public static void ftpsiteAddMouseListener(){
		mainFrame.serverManagerDialog.ftpSiteTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				Point p = event.getPoint();
				int index = mainFrame.serverManagerDialog.ftpSiteTable.rowAtPoint(p);
				String ftpSiteName = (String)mainFrame.serverManagerDialog.ftpSiteTableModel.getValueAt(index, 0);
				FTPSiteModel ftpSite = null;
				for(Object obj : mainFrame.serverManagerDialog.ftpSiteTableModel.ftpSiteList) {
					ftpSite = (FTPSiteModel)obj;
					if(ftpSite.getName().equals(ftpSiteName)) {
						break;
					}
				}
				mainFrame.serverManagerDialog.nameTextField.setText(ftpSite.getName());
				mainFrame.serverManagerDialog.hostTextField.setText(ftpSite.getHost());
				mainFrame.serverManagerDialog.userTextField.setText(ftpSite.getUser());
				mainFrame.serverManagerDialog.pswdTextField.setText(ftpSite.getPswd());
				mainFrame.serverManagerDialog.portTextField.setText(ftpSite.getPort());
				if(event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1){
					mainFrame.ftpTools = new FTPTools(
							ftpSite.getHost(),
							Integer.parseInt(ftpSite.getPort()),
							ftpSite.getUser(),
							ftpSite.getPswd());
					mainFrame.serverManagerDialog.setVisible(false);
				} 
			}
		});
	}
}
