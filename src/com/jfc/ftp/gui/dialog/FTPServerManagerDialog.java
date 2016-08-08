package com.jfc.ftp.gui.dialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.jfc.ftp.event.dialog.DialogButtonEvent;
import com.jfc.ftp.event.dialog.DialogTableEvent;
import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.model.table.FTPSiteTableModel;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

/**
 * 链接FTP服务器的管理窗口
 * @author SavageGarden
 *
 */
public class FTPServerManagerDialog extends JDialog {

	/**
	 * 在此JScrollPane中显示储存的FTP服务器列表
	 */
	private JScrollPane serverListScrollPane;
	public static FTPSiteTableModel ftpSiteTableModel;
	public static JTable ftpSiteTable;
	/**
	 * 在此JPanel中管理FTP服务器的链接信息
	 */
	private JPanel connManagerPanel;
	
	public static JTextField nameTextField;
	public static JTextField hostTextField;
	public static JTextField userTextField;
	public static JTextField pswdTextField;
	public static JTextField portTextField;
	
	private int WIDTH;
	private int HEIGHT;
	
	public FTPServerManagerDialog(FTPMainFrame mainFrame) {
		super(mainFrame, PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_TITLE), true);
		WIDTH = 500;
		HEIGHT = 300;
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		
		createServerListPanel();
		createConnManagerPanel();
	}
	private void createServerListPanel() {
		ftpSiteTableModel = new FTPSiteTableModel();
		ftpSiteTableModel.showSiteList();
		ftpSiteTable = new JTable(ftpSiteTableModel);
		DialogTableEvent.ftpsiteAddMouseListener();
		serverListScrollPane = new JScrollPane(ftpSiteTable);
		serverListScrollPane.setBounds(10, 10, 220, 250);
		getContentPane().add(serverListScrollPane);
	}
	private void createConnManagerPanel() {
		connManagerPanel = new JPanel();
		connManagerPanel.setLayout(null);
		connManagerPanel.setBounds(240, 10, 260, 250);
		getContentPane().add(connManagerPanel);

		//ftp服务器保存名称
		JLabel nameLabel = new JLabel(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_SITENAME));
		nameLabel.setBounds(10, 10, 100, 25);
		nameTextField = new JTextField();
		nameTextField.setBounds(90, 10, 150, 25);
		
		//ftp服务器地址
		JLabel hostLabel = new JLabel(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_SITEURL));
		hostLabel.setBounds(10, 45, 100, 25);
		hostTextField = new JTextField();
		hostTextField.setBounds(90, 45, 150, 25);
		
		
		//用户名
		JLabel userLabel = new JLabel(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_SITEUSER));
		userLabel.setBounds(10, 80, 100, 25);
		userTextField = new JTextField();
		userTextField.setBounds(90, 80, 150, 25);
		
		//密码
		JLabel pswdLabel = new JLabel(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_SITEPASS));
		pswdLabel.setBounds(10, 115, 100, 25);
		pswdTextField = new JPasswordField();
		pswdTextField.setBounds(90, 115, 150, 25);
		
		//端口
		JLabel portLabel = new JLabel(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_SITEPORT));
		portLabel.setBounds(10, 150, 100, 25);
		portTextField = new JTextField("21");
		portTextField.setBounds(90, 150, 150, 25);
		
		JButton connectButton = new JButton(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_BUTTON_CONN));
		DialogButtonEvent.addActionListenerToConn(this, connectButton);
		connectButton.setBounds(10, 220, 100, 25);
		JButton cancelButton = new JButton(PropertyUtil.getResources(ResourcesConstant.MANAGER_DIALOG_BUTTON_CANCEL));
		DialogButtonEvent.addActionListenerToCancel(this, cancelButton);
		cancelButton.setBounds(130, 220, 100, 25);
		
		connManagerPanel.add(nameLabel);
		connManagerPanel.add(nameTextField);
		
		connManagerPanel.add(hostLabel);
		connManagerPanel.add(hostTextField);
		
		
		connManagerPanel.add(userLabel);
		connManagerPanel.add(userTextField);
		
		
		connManagerPanel.add(pswdLabel);
		connManagerPanel.add(pswdTextField);
		
		connManagerPanel.add(portLabel);
		connManagerPanel.add(portTextField);
		
		connManagerPanel.add(connectButton);
		connManagerPanel.add(cancelButton);
		
	}
}
