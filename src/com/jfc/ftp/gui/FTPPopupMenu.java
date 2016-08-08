package com.jfc.ftp.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.jfc.ftp.event.FTPPopupMenuEvent;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

public class FTPPopupMenu {
	/**
	 * 创建客户端的右键菜单
	 * <br>添加菜单项
	 * <br>添加事件处理
	 * @param clientPopupMenu
	 */
	public static void createClientPopupMenu(JPopupMenu clientPopupMenu) {
		JMenuItem uploadItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_UPLOAD));
		JMenuItem deleteItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DELETE));
		JMenuItem renameItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_RENAME));
		
		clientPopupMenu.add(uploadItem);
		clientPopupMenu.add(deleteItem);
		clientPopupMenu.add(renameItem);
		
		FTPPopupMenuEvent.clientUploadMenuAddActionListener(uploadItem);
		FTPPopupMenuEvent.clientDeleteMenuAddActionListener(deleteItem);
		FTPPopupMenuEvent.clientRenameMenuAddActionListener(renameItem);
	}
	/**
	 * 创建服务端端的右键菜单
	 * <br>添加菜单项
	 * <br>添加事件处理
	 * @param clientPopupMenu
	 */
	public static void createServerPopupMenu(JPopupMenu serverPopupMenu) {
		JMenuItem downloadItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD));
		JMenuItem downloadRBTItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_RBT));
		JMenuItem downloadRBTThreadItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_RBT_THREAD));
		JMenuItem deleteItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DELETE));
		JMenuItem renameItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_RENAME));
		
		serverPopupMenu.add(downloadItem);
		serverPopupMenu.add(downloadRBTItem);
		serverPopupMenu.add(downloadRBTThreadItem);
		serverPopupMenu.add(deleteItem);
		serverPopupMenu.add(renameItem);
		
		FTPPopupMenuEvent.serverDownloadMenuAddActionListener(downloadItem);
		FTPPopupMenuEvent.serverDownloadMenuAddActionListener(downloadRBTItem);
		FTPPopupMenuEvent.serverDownloadMenuAddActionListener(downloadRBTThreadItem);
		
		FTPPopupMenuEvent.serverDeleteMenuAddActionListener(deleteItem);
		FTPPopupMenuEvent.serverRenameMenuAddActionListener(renameItem);
		
	}
	/**
	 * 创建状态区的右键菜单
	 * <br>添加菜单项
	 * <br>添加事件处理
	 * @param clientPopupMenu
	 */
	public static void createStatusPopupMenu(JPopupMenu statusPopupMenu) {
		JMenuItem waitItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_WAIT));
		JMenuItem goonItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_GOON));
		JMenuItem deleItem = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.POPUP_MENU_DOWNLOAD_DELE));
		
		statusPopupMenu.add(waitItem);
		statusPopupMenu.add(goonItem);
		statusPopupMenu.add(deleItem);
		
		FTPPopupMenuEvent.statusWaitMenuAddActionListener(waitItem);
		FTPPopupMenuEvent.statusGoonMenuAddActionListener(goonItem);
		FTPPopupMenuEvent.statusDeleMenuAddActionListener(deleItem);
	}
}
