package com.jfc.ftp.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.FileUtil;

public class FTPButtonEvent extends FTPEvent {

	private static String oldPath = "";
	private static String newPath = "";
	
	/**
	 * 为客户端的返回主目录按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientHomeButton
	 */
	public static void clientHomeAddMouseListener(final JButton clientHomeButton) {
		clientHomeButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 改写目录域的值<br>
			 * 重新获取文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				mainFrame.clientTextFieldHost.setText(FileUtil.getUserHome());
				mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
				
			}
		});
	}
	/**
	 * 为客户端的返回上级目录按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientGoupButton
	 */
	public static void clientGoupAddMouseListener(final JButton clientGoupButton) {
		clientGoupButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 改写目录域的值<br>
			 * 重新获取文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				oldPath = mainFrame.clientTextFieldHost.getText();
				if(oldPath.indexOf(File.separator) >= 0) {
					newPath = oldPath.substring(0, oldPath.lastIndexOf(File.separator));
				} else {
					//if(FileUtil.isDisk(oldPath)) {
						newPath = SystemTools.getRootName();
					//}
				}
				mainFrame.clientTextFieldHost.setText(newPath);
				mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
				
			}
		});
	}
	/**
	 * 为客户端的刷新按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientRefreshButton
	 */
	public static void clientRefreshAddMouseListener(final JButton clientRefreshButton) {
		clientRefreshButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 重新获取文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
			}
		});
	}
	/**
	 * 为客户端的创建文件夹按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientAddButton
	 */
	public static void clientAddFolderAddMouseListener(final JButton clientAddButton) {
		clientAddButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 首先创建新的一行<br>
			 * 确定文件夹名称后如果合法则创建文件,刷新当前目录下的文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				Object[] rowData = {new ImageIcon(), "..", "", "", ""};
				rowData[0] = new ImageIcon("images/folder.png");
				mainFrame.clientTableModel.addRow(rowData);
				int index = mainFrame.clientTableModel.getRowCount();
				mainFrame.clientTable.changeSelection(index-1, 1, false, false);
				if(mainFrame.clientTableModel.isCellEditable(index-1, 1)) {
					mainFrame.clientTable.editCellAt(index-1, 1);
					mainFrame.clientTable.getCellEditor().addCellEditorListener(new CellEditorListener() {

						public void editingCanceled(ChangeEvent e) {
							//nothing to do
							
						}

						public void editingStopped(ChangeEvent e) {
							String filename = "";
							filename = (String)mainFrame.clientTable.getCellEditor().getCellEditorValue();
							filename = mainFrame.clientTextFieldHost.getText() + File.separator + filename;
							try {
								FileUtil.createFolder(filename);
								mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(null,"创建文件错误！");
							}
						}
					});
				}
			}
		});
	}
	/**
	 * 为客户端的删除按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientDeleButton
	 */
	public static void clientDeleAddMouseListener(final JButton clientDeleButton) {
		clientDeleButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 如果是文件则直接删除<br>
			 * 如果是文件夹则在得到用户确认后递归删除
			 */
			public void mouseClicked(MouseEvent e) {
				String filename = (String)mainFrame.clientTableModel.getValueAt(mainFrame.clientTable.getSelectedRow(), 1);
				int i = JOptionPane.showConfirmDialog(null, "确认删除 " + filename + " 吗？");
				if(i == 0) {
					FileUtil.deleteDir(mainFrame.clientTextFieldHost.getText() + File.separator + filename);
				}
				mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
			}
		});
	}
	/**
	 * 为服务端的返回主目录按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param serverHomeButton
	 */
	public static void serverHomeAddMouseListener(final JButton serverHomeButton) {
		serverHomeButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				mainFrame.serverTextFieldHost.setText(Constant.LINUX_ROOT_NAME);
				String dataStr = mainFrame.ftpTools.getListFile(Constant.LINUX_ROOT_NAME);
				mainFrame.serverTableModel.showFileList(dataStr);
			}
		});
	}
	/**
	 * 为服务端的返回上级目录按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param clientGoupButton
	 */
	public static void serverGoupAddMouseListener(final JButton serverGoupButton) {
		serverGoupButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 改写目录域的值<br>
			 * 重新获取文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				oldPath = mainFrame.serverTextFieldHost.getText();
				if(oldPath.lastIndexOf("/") > 0) {
					newPath = oldPath.substring(0, oldPath.lastIndexOf("/"));
				} else if(oldPath.indexOf("/") == 0){
					//if(FileUtil.isDisk(oldPath)) {
						newPath = "/";
					//}
				}
				mainFrame.serverTextFieldHost.setText(newPath);
				String dataStr = mainFrame.ftpTools.getListFile(newPath);
				mainFrame.serverTableModel.showFileList(dataStr);
			}
		});
	}
	/**
	 * 为服务端的刷新按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param serverRefreshButton
	 */
	public static void serverRefreshAddMouseListener(final JButton serverRefreshButton) {
		serverRefreshButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 重新获取文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
				mainFrame.serverTableModel.showFileList(dataStr);
			}
		});
	}
	/**
	 * 为服务端的创建文件夹按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param serverAddButton
	 */
	public static void serverAddFolderAddMouseListener(final JButton serverAddButton) {
		serverAddButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 首先创建新的一行<br>
			 * 确定文件夹名称后如果合法则创建文件,刷新当前目录下的文件列表
			 */
			public void mouseClicked(MouseEvent e) {
				Object[] rowData = {new ImageIcon(), "..", "", "", ""};
				rowData[0] = new ImageIcon("images/folder.png");
				mainFrame.serverTableModel.addRow(rowData);
				int index = mainFrame.serverTableModel.getRowCount();
				mainFrame.serverTable.changeSelection(index-1, 1, false, false);
				if(mainFrame.serverTableModel.isCellEditable(index-1, 1)) {
					mainFrame.serverTable.editCellAt(index-1, 1);
					mainFrame.serverTable.getCellEditor().addCellEditorListener(new CellEditorListener() {

						public void editingCanceled(ChangeEvent e) {
							//nothing to do
							
						}

						public void editingStopped(ChangeEvent e) {
							String folderName = "";
							folderName = (String)mainFrame.serverTable.getCellEditor().getCellEditorValue();
							try {
								mainFrame.ftpTools.createFolder(folderName);
								String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
								mainFrame.serverTableModel.showFileList(dataStr);
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(null,"创建文件错误！");
							}
						}
					});
				}
			}
		});
	}
	/**
	 * 为服务端的删除按钮添加鼠标事件处理
	 * @param mainFrame
	 * @param serverDeleButton
	 */
	public static void serverDeleAddMouseListener(final JButton serverDeleButton) {
		serverDeleButton.addMouseListener(new MouseAdapter() {
			/**
			 * 捕获鼠标点击事件<br>
			 * 如果是文件则直接删除<br>
			 * 如果是文件夹则在得到用户确认后递归删除
			 */
			public void mouseClicked(MouseEvent e) {
				String filename = (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1);
				String filetype = (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 3);
				int i = JOptionPane.showConfirmDialog(null, "确认删除 " + filename + " 吗？");
				if(i == 0) {
					mainFrame.ftpTools.deleteDir(mainFrame.serverTextFieldHost.getText() + "/" + filename, filetype);
					String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
					mainFrame.serverTableModel.showFileList(dataStr);
				}
			}
		});
	}
}
