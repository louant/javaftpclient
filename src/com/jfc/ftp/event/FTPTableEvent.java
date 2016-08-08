package com.jfc.ftp.event;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.jfc.ftp.tools.FTPThread;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;

/**
 * 用于处理<br>
 * 客户端文件浏览器、服务端文件浏览器、状态区浏览器<br>
 * 的事件
 * @author SavageGarden
 *
 */
public class FTPTableEvent extends FTPEvent {

	private final static Logger logger = Logger.getLogger(FTPTableEvent.class);
	/**
	 * 客户端文件浏览器添加鼠标事件
	 * @param table
	 */
	public static void clientAddMouseListener(){
		mainFrame.clientTable.addMouseListener(new MouseAdapter(){
			/**
			 * 添加鼠标事件
			 * <br>单击时使选中行
			 * <br>双击时如果是文件夹则显示该目录下的文件
			 */
			public void mouseClicked(MouseEvent event){
				Point p = event.getPoint();
				int index = mainFrame.clientTable.rowAtPoint(p);
				String newpath = "";
				if(mainFrame.clientTextFieldHost.getText().equals(SystemTools.getRootName())) {
					newpath = (String)mainFrame.clientTableModel.getValueAt(index, 1);
				} else {
					newpath = mainFrame.clientTextFieldHost.getText() + File.separator + mainFrame.clientTableModel.getValueAt(index, 1);
				}
				if(event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1){
					if(mainFrame.clientTableModel.getValueAt(index, 3).equals(Constant.FILE_TYPE_FILE)){
						logger.debug("是文件不是目录");
					} else {
						mainFrame.clientTextFieldHost.setText(newpath);
						mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
					}
				} else if(event.getClickCount() == 1 && SwingUtilities.isRightMouseButton(event)){
					mainFrame.clientTable.changeSelection(index, 1, false, false);
					mainFrame.clientPopupMenu.show(mainFrame.clientTable, p.x, p.y);
				}
			}
		});
	}
	/**
	 * 服务端文件浏览器添加鼠标事件
	 * @param table
	 */
	public static void serverAddMouseListener() {
		mainFrame.serverTable.addMouseListener(new MouseAdapter(){
			/**
			 * 添加鼠标事件
			 * <br>单击时使选中行变色以示区别
			 * <br>双击时如果是文件夹则显示该目录下的文件
			 */
			public void mouseClicked(MouseEvent event){
				Point p = event.getPoint();
				int index = mainFrame.serverTable.rowAtPoint(p);
				String newpath = "";
				if(mainFrame.serverTextFieldHost.getText().equals("/")) {
					newpath = mainFrame.serverTextFieldHost.getText() + mainFrame.serverTableModel.getValueAt(index, 1);
				} else {
					newpath = mainFrame.serverTextFieldHost.getText() + "/" + mainFrame.serverTableModel.getValueAt(index, 1);
				}
				if(event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1){
					if(mainFrame.serverTableModel.getValueAt(index, 3).equals(Constant.FILE_TYPE_DIRE)){
						mainFrame.serverTextFieldHost.setText(newpath);
						String dataStr = mainFrame.ftpTools.getListFile(newpath);
						mainFrame.serverTableModel.showFileList(dataStr);
					}else{
						logger.debug("是文件不是目录");
					}
				} else if(event.getClickCount() == 1 && SwingUtilities.isRightMouseButton(event)){
					mainFrame.serverTable.changeSelection(index, 1, false, false);
					mainFrame.serverPopupMenu.show(mainFrame.serverTable, p.x, p.y);
				}
			}
		});
	}
	/**
	 * 状态区下载浏览器添加鼠标事件处理
	 *
	 */
	public static void statusAddMouseListener() {
		mainFrame.statusTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event){
				Point p = event.getPoint();
				int index = mainFrame.statusTable.rowAtPoint(p);
				
				if(event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1) {
					FTPThread ftpThread = (FTPThread)SystemTools.downloadThreadList.get(index);
					System.out.println("选择了第" + index + "个下载线程");
					System.out.println("线程ID为" + ftpThread.getId());
					try {
						if(ftpThread.getStatus() == Constant.THREAD_STATUS_RUNNABLE) {
							System.out.println("开始等待");
							ftpThread.setStatus(Constant.THREAD_STATUS_WAITING);
							ftpThread.setEclipsedTime(ftpThread.getEclipsedTime() + (System.currentTimeMillis() - ftpThread.getStartTime()));
							System.out.println("eclipse:" + ftpThread.getEclipsedTime());
						} else if(ftpThread.getStatus() == Constant.THREAD_STATUS_WAITING) {
							System.out.println("开始运行");
							ftpThread.setStatus(Constant.THREAD_STATUS_RUNNABLE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(event.getClickCount() == 1 && SwingUtilities.isRightMouseButton(event)){
					mainFrame.statusPopupMenu.show(mainFrame.statusTable, p.x, p.y);
				}
			}
		});
	}
}
