package com.jfc.ftp.gui;

import java.awt.Dimension;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.jfc.ftp.event.FTPMenuEvent;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

/**
 * 用于处理主菜单的创建
 * @author SavageGarden
 *
 */
public class FTPMainMenu {
	/**
	 * 创建菜单	File
	 * 	--Connect To FTP Server
	 * 	--Exit
	 * @param menubar
	 */
	public static void createMenuFile(JMenuBar menubar) {
		JMenu menuFile = new JMenu(PropertyUtil.getResources(ResourcesConstant.MAINMENU_FILE));
		JMenuItem menuItem1 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_FILE_CONNECT));
		FTPMenuEvent.addActionListenerToConn(menuItem1);
		JMenuItem menuItem2 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_FILE_DISCONNECT));
		FTPMenuEvent.addActionListenerToExit(menuItem2);
		menuFile.setPreferredSize(new Dimension(60, 20));
		menuFile.add(menuItem1);
		menuFile.addSeparator();
		menuFile.add(menuItem2);
		if(menubar != null){
			menubar.add(menuFile);
		}
	}
	/**
	 * 创建菜单	Options
	 * 	--
	 * 	--
	 * @param menubar
	 */
	public static void createMenuOptions(JMenuBar menubar) {
		JMenu menuFile = new JMenu(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION));
		JMenuItem menuItem1 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION_OPEN));
		JMenuItem menuItem2 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION_UPLOAD));
		JMenuItem menuItem3 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION_DOWNLOAD));
		JMenuItem menuItem4 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION_RENAME));
		JMenuItem menuItem5 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_OPTION_DELETE));
		menuFile.setPreferredSize(new Dimension(60, 20));
		menuFile.add(menuItem1);
		menuFile.addSeparator();
		menuFile.add(menuItem2);
		menuFile.addSeparator();
		menuFile.add(menuItem3);
		menuFile.addSeparator();
		menuFile.add(menuItem4);
		menuFile.addSeparator();
		menuFile.add(menuItem5);
		if(menubar != null){
			menubar.add(menuFile);
		}
	}
	/**
	 * 创建菜单	View
	 * 	--
	 * 	--
	 * @param menubar
	 */
	public static void createMenuView(JMenuBar menubar) {
		JMenu menuFile = new JMenu(PropertyUtil.getResources(ResourcesConstant.MAINMENU_VIEW));
		JMenuItem menuItem1 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_VIEW_HIDDEN));
		menuFile.setPreferredSize(new Dimension(60, 20));
		menuFile.add(menuItem1);
		if(menubar != null){
			menubar.add(menuFile);
		}
	}
	/**
	 * 创建菜单	Info
	 * 	--
	 * 	--
	 * @param menubar
	 */
	public static void createMenuInfo(JMenuBar menubar) {
		JMenu menuFile = new JMenu(PropertyUtil.getResources(ResourcesConstant.MAINMENU_HELP));
		JMenuItem menuItem1 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_HELP_GETHELP));
		JMenuItem menuItem2 = new JMenuItem(PropertyUtil.getResources(ResourcesConstant.MAINMENU_HELP_ABOUT));
		menuFile.setPreferredSize(new Dimension(60, 20));
		menuFile.add(menuItem1);
		menuFile.addSeparator();
		menuFile.add(menuItem2);
		if(menubar != null){
			menubar.add(menuFile);
		}
	}
}
