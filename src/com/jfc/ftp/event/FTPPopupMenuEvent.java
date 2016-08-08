package com.jfc.ftp.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.jfc.ftp.tools.FTPThread;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.FileUtil;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;
/**
 * 用于弹出菜单的事件处理
 * @author SavageGarden
 *
 */
public class FTPPopupMenuEvent extends FTPEvent {

	/**
	 * 为客户端的上传添加处理
	 * @param mainFrame
	 * @param menuItem
	 */
	public static void clientUploadMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread() {
					public void run() {
						int index = mainFrame.clientTable.getSelectedRow();
						String filepath = mainFrame.clientTextFieldHost.getText() + File.separator + mainFrame.clientTableModel.getValueAt(index, 1);
						String savepath = mainFrame.serverTextFieldHost.getText();
						String filetype = (String)mainFrame.clientTableModel.getValueAt(index, 3);
						mainFrame.ftpTools.upload(filepath, filetype, savepath);
						String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
						mainFrame.serverTableModel.showFileList(dataStr);
					}
				};
				thread.start();
			}
		});
	}
	/**
	 * 为客户端的删除添加处理
	 * @param mainFrame
	 * @param menuItem
	 */
	public static void clientDeleteMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename = (String)mainFrame.clientTableModel.getValueAt(mainFrame.clientTable.getSelectedRow(), 1);
				int i = JOptionPane.showConfirmDialog(null, "确认删除 " + filename + " 吗？");
				if(i == JOptionPane.OK_OPTION) {
					FileUtil.deleteDir(mainFrame.clientTextFieldHost.getText() + File.separator + filename);
				}
				mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
			}
		});
	}
	/**
	 * 为客户端的修改文件名添加处理
	 * @param mainFrame
	 * @param menuItem
	 */
	public static void clientRenameMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//使选中行可编辑
				int index = mainFrame.clientTable.getSelectedRow();
				mainFrame.clientTable.editCellAt(index, 1);
				final String oldfile = mainFrame.clientTextFieldHost.getText() + File.separator + mainFrame.clientTableModel.getValueAt(index, 1);
				//初始化时为处于编辑状态的行添加事件处理
				if(!mainFrame.initFlag) {
					mainFrame.clientTable.getCellEditor().addCellEditorListener(new CellEditorListener() {

						public void editingCanceled(ChangeEvent e) {
							// TODO Auto-generated method stub
							
						}
						/**
						 * 编辑完成时获取原来的文件名和新的文件名,修改文件名
						 */
						public void editingStopped(ChangeEvent e) {
							String newfile = "";
							newfile = (String)mainFrame.clientTable.getCellEditor().getCellEditorValue();
							newfile = mainFrame.clientTextFieldHost.getText() + File.separator + newfile;
							try {
								FileUtil.rename(oldfile, newfile);
								mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
							} catch(Exception ex) {
								JOptionPane.showMessageDialog(null,"修改文件名发生错误！");
							}
						}
					});
				}
			}
		});
	}
	/**
	 * 为服务端的下载添加处理
	 * @param menuItem
	 */
	public static void serverDownloadMenuAddActionListener(final JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = "";
				if(mainFrame.serverTextFieldHost.getText().equals("/")) {
					path = "/" + mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1); 
				} else {
					path = mainFrame.serverTextFieldHost.getText() + "/" + mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1);
				}
				final String filepath = path;
				final String savepath = mainFrame.clientTextFieldHost.getText();
				final String filetype = (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 3);
				final long filesize = Long.parseLong((String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 2));

				
				if(filetype.equals(Constant.FILE_TYPE_FILE)) {
					//下载的是文件则使用断点续传方式下载
					final String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
					File temp_file = FileUtil.searchFile(savepath,  filename + Constant.DOWNLOAD_TEMP_NAME);
					mainFrame.statusTableModel.addProgressBar(temp_file == null ? 0 : (int)temp_file.length());
					//下载线程
					Thread thread = new Thread() {
						int rowIndex = mainFrame.statusTable.getRowCount() - 1;
						public void run() {
							mainFrame.statusTableModel.setValueAt(filename, rowIndex, 0);
							mainFrame.statusTableModel.setValueAt(filesize, rowIndex, 2);
							FTPThread ftpThread = null;
							//根据选择的不同的右键菜单，使用不同的下载方式
							if(menuItem.getText().equals(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD))) {
								ftpThread = new FTPThread(Constant.DOWNLOAD_TYPE_COMMON, rowIndex, filepath, filesize, savepath);
							} else if(menuItem.getText().equals(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_RBT))) {
								ftpThread = new FTPThread(Constant.DOWNLOAD_TYPE_RBT, rowIndex, filepath, filesize, savepath);
							} else if(menuItem.getText().equals(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_RBT_THREAD))) {
								ftpThread = new FTPThread(Constant.DOWNLOAD_TYPE_RBT_THREAD, rowIndex, filepath, filesize, savepath);
							}
							SystemTools.downloadThreadList.add(ftpThread);
							System.out.println("添加了一个下载项，下标为" + (SystemTools.downloadThreadList.size() - 1));
							//mainFrame.ftpTools.ResumeBrokenTransfer(rowIndex, filepath, filesize, savepath);
							mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
						}
					};
					thread.start();
				} else if(filetype.equals(Constant.FILE_TYPE_DIRE)){
					//下载的是目录则使用普通方式下载
					mainFrame.ftpTools.download(filepath, filetype, savepath);
					mainFrame.clientTableModel.showFileList(mainFrame.clientTextFieldHost.getText());
				}
			}
		});
	}
	/**
	 * 为服务端的删除添加处理
	 * @param mainFrame
	 * @param menuItem
	 */
	public static void serverDeleteMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename = (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1);
				String filetype = (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 3);
				int i = JOptionPane.showConfirmDialog(null, "确认删除 " + filename + " 吗？");
				if(i == JOptionPane.OK_OPTION) {
					mainFrame.ftpTools.deleteDir(mainFrame.serverTextFieldHost.getText() + "/" + filename, filetype);
					String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
					mainFrame.serverTableModel.showFileList(dataStr);
				}
			}
		});
	}
	/**
	 * 为服务端的修改文件名添加处理
	 * @param mainFrame
	 * @param menuItem
	 */
	public static void serverRenameMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//使选中行可编辑
				mainFrame.serverTable.editCellAt(mainFrame.serverTable.getSelectedRow(), 1);
				//初始化时为处于编辑状态的行添加事件处理
				if(!mainFrame.initFlag) {
					mainFrame.serverTable.getCellEditor().addCellEditorListener(new CellEditorListener() {

						public void editingCanceled(ChangeEvent e) {
							// TODO Auto-generated method stub
							
						}
						/**
						 * 编辑完成时获取原来的文件名和新的文件名,修改文件名
						 */
						public void editingStopped(ChangeEvent e) {
							String oldfile = "";
							String newfile = "";
							if(mainFrame.serverTextFieldHost.getText().equals("/")) {
								oldfile = "/" + (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1);
								newfile = "/" + (String)mainFrame.serverTable.getCellEditor().getCellEditorValue();
							} else {
								oldfile = mainFrame.serverTextFieldHost.getText() + "/" + (String)mainFrame.serverTableModel.getValueAt(mainFrame.serverTable.getSelectedRow(), 1);
								newfile = mainFrame.serverTextFieldHost.getText() + "/" + (String)mainFrame.serverTable.getCellEditor().getCellEditorValue();
							}
							try {
								mainFrame.ftpTools.rename(oldfile, newfile);
								String dataStr = mainFrame.ftpTools.getListFile(mainFrame.serverTextFieldHost.getText());
								mainFrame.serverTableModel.showFileList(dataStr);
							} catch(Exception ex) {
								JOptionPane.showMessageDialog(null,"修改文件名发生错误！");
							}
						}
					});
				}
			}
		});
	}
	/**
	 * 为下载区的"暂停下载"菜单添加事件处理
	 * @param menuItem
	 */
	public static void statusWaitMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mainFrame.statusTable.getSelectedRow();
				Thread thread = (Thread)SystemTools.downloadThreadList.get(index);
				try {
					thread.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	/**
	 * 为下载区的"继续下载"菜单添加事件处理
	 * @param menuItem
	 */
	public static void statusGoonMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mainFrame.statusTable.getSelectedRow();
				Thread thread = (Thread)SystemTools.downloadThreadList.get(index);
				try {
					thread.notify();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	/**
	 * 为下载区的"取消下载"菜单添加事件处理
	 * @param menuItem
	 */
	public static void statusDeleMenuAddActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mainFrame.statusTable.getSelectedRow();
				Thread thread = (Thread)SystemTools.downloadThreadList.get(index);
				try {
					thread.interrupt();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
