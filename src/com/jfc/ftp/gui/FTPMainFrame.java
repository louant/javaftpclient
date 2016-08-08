package com.jfc.ftp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import com.jfc.ftp.event.FTPButtonEvent;
import com.jfc.ftp.event.FTPTableEvent;
import com.jfc.ftp.event.FTPTextFieldEvent;
import com.jfc.ftp.gui.dialog.FTPServerManagerDialog;
import com.jfc.ftp.gui.render.ProgressBarRenderer;
import com.jfc.ftp.model.table.ClientTableModel;
import com.jfc.ftp.model.table.ServerTableModel;
import com.jfc.ftp.model.table.StatusTableModel;
import com.jfc.ftp.tools.FTPTools;
import com.jfc.ftp.util.FileUtil;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;
/**
 * 主面板 用于显示用户界面
 * @author SavageGarden
 *
 */
public class FTPMainFrame extends JFrame{

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * 面板宽
	 */
	private static int WIDTH;
	/**
	 * 面板高
	 */
	private static int HEIGHT;
	/**
	 * 主菜单
	 */
	private JMenuBar menubar;
	/**
	 * 客户端右键菜单
	 */
	public JPopupMenu clientPopupMenu;
	/**
	 * 服务端右键菜单
	 */
	public JPopupMenu serverPopupMenu;
	/**
	 * 状态区右键菜单
	 */
	public JPopupMenu statusPopupMenu;
	/**
	 * 用于放置菜单的Panel
	 */
	private JPanel menuPanel;
	/**
	 * 用来放置操作按钮的Panel
	 */
	private JPanel filePanel;
	/**
	 * 用来放置客户端、服务端文件浏览器的Panel
	 */
	private JPanel contPanel;
	/**
	 * 放置客户端文件浏览器的Panel
	 */
	private JScrollPane clientScrollPane1;
	/**
	 * 放置服务端文件浏览器的Panel
	 */
	private JScrollPane serverScrollPane1;
	/**
	 * 放置状态区的Panel
	 */
	public JScrollPane statusScrollPane1;

	/**
	 * 用于显示客户端目录
	 */
	public static JTextField clientTextFieldHost;
	/**
	 * 用于显示服务端目录
	 */
	public static JTextField serverTextFieldHost;
	
	/**
	 * 用于接受链接FTP服务端的参数
	 */
	public static FTPServerManagerDialog serverManagerDialog;
	/**
	 * 服务端表格模型
	 */
	public static ServerTableModel serverTableModel;
	/**
	 * 客户端表格模型
	 */
	public static ClientTableModel clientTableModel;
	/**
	 * 状态区表格模型
	 */
	public static StatusTableModel statusTableModel;
	
	/**
	 * 服务端表格
	 */
	public static JTable serverTable;
	/**
	 * 客户端表格
	 */
	public static JTable clientTable;
	/**
	 * 状态区表格
	 */
	public static JTable statusTable;
	/**
	 * FTP操作工具
	 */
	public static FTPTools ftpTools;
	
	public static boolean initFlag = false;
	
	/**
	 * 构造器,初始化面板
	 *
	 */
	public FTPMainFrame(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		WIDTH = screenSize.width/2;
		HEIGHT = screenSize.height/2;
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle(PropertyUtil.getResources(ResourcesConstant.MAINFRAME_TITLE));
		this.setVisible(false);
		
		//创建菜单添加到面板
		createMenu();
		
		//创建文件操作项到面板
		createFilePanel();
		
		//创建内容区域添加到面板
		createCont();
		
		//创建客户端文件浏览器添加到内容区
		createClientPanel();
		
		//创建服务端文件浏览器添加到内容区
		createServerPanel();
		
		//创建状态区添加到面板
		createStatusPanel();
		
		//创建右键菜单
		createPopupMenu();
		initFlag = true;
	}
	
	private void createCont() {
		contPanel = new JPanel();
		contPanel.setLayout(null);
		contPanel.setBounds(0, 30, WIDTH, 300);
		getContentPane().add(contPanel);
	}

	/**
	 * 创建菜单
	 */
	private void createMenu() {
		menubar = new JMenuBar();
		FTPMainMenu.createMenuFile(menubar);
		FTPMainMenu.createMenuOptions(menubar);
		FTPMainMenu.createMenuView(menubar);
		FTPMainMenu.createMenuInfo(menubar);
		this.add(menubar);
		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		menuPanel.add(menubar, BorderLayout.NORTH);
		menuPanel.setPreferredSize(new Dimension(WIDTH/2, 20));
		
		menuPanel.setBounds(0, 0, WIDTH, 15);
		getContentPane().add(menuPanel);
	}
	/**
	 * 创建客户端、服务端、状态区的右键菜单
	 *
	 */
	private void createPopupMenu() {
		clientPopupMenu = new JPopupMenu();
		FTPPopupMenu.createClientPopupMenu(clientPopupMenu);
		serverPopupMenu = new JPopupMenu();
		FTPPopupMenu.createServerPopupMenu(serverPopupMenu);
		statusPopupMenu = new JPopupMenu();
		FTPPopupMenu.createStatusPopupMenu(statusPopupMenu);
	}
	/**
	 * 创建文件操作项
	 *
	 */
	private void createFilePanel(){
		filePanel = new JPanel();
		filePanel.setLayout(null);
		filePanel.setBounds(0, 20, WIDTH, 20);
	    getContentPane().add(filePanel);
	    
	    //设置客户端操作按钮
	    JButton clientHomeButton = new JButton(new ImageIcon("images/file/folder_home.png"));
	    JButton clientGoupButton = new JButton(new ImageIcon("images/file/folder_goup.png"));
	    JButton clientRefreshButton = new JButton(new ImageIcon("images/file/folder_refresh.png"));
	    JButton clientAddButton = new JButton(new ImageIcon("images/file/folder_add.png"));
	    JButton clientDeleButton = new JButton(new ImageIcon("images/file/folder_dele.png"));

	    //给返回主目录按钮添加事件处理
	    FTPButtonEvent.clientHomeAddMouseListener(clientHomeButton);
	    clientHomeButton.setBounds(4, 0, 20, 20);
	    filePanel.add(clientHomeButton);
	    
	    //给返回上级目录按钮添加事件处理
	    FTPButtonEvent.clientGoupAddMouseListener(clientGoupButton);
	    clientGoupButton.setBounds(26, 0, 20, 20);
	    filePanel.add(clientGoupButton);
	    
	    //给刷新按钮添加事件处理
	    FTPButtonEvent.clientRefreshAddMouseListener(clientRefreshButton);
	    clientRefreshButton.setBounds(48, 0, 20, 20);
	    filePanel.add(clientRefreshButton);
	    
	    //给创建文件夹按钮添加事件处理
	    FTPButtonEvent.clientAddFolderAddMouseListener(clientAddButton);
	    clientAddButton.setBounds(70, 0, 20, 20);
	    filePanel.add(clientAddButton);
	    
	    FTPButtonEvent.clientDeleAddMouseListener(clientDeleButton);
	    clientDeleButton.setBounds(92, 0, 20, 20);
	    filePanel.add(clientDeleButton);
	    
	    //设置客户端目录域
	    clientTextFieldHost = new JTextField();
	    clientTextFieldHost.setText(FileUtil.getUserHome());
	    clientTextFieldHost.setBounds(116, 0, WIDTH/2-116, 20);
	    FTPTextFieldEvent.clientAddActionListener(clientTextFieldHost);
	    filePanel.add(clientTextFieldHost);

	    //设置服务端操作按钮
	    JButton serverHomeButton = new JButton(new ImageIcon("images/file/folder_home.png"));
	    JButton serverGoupButton = new JButton(new ImageIcon("images/file/folder_goup.png"));
	    JButton serverRefreshButton = new JButton(new ImageIcon("images/file/folder_refresh.png"));
	    JButton serverAddButton = new JButton(new ImageIcon("images/file/folder_add.png"));
	    JButton serverDeleButton = new JButton(new ImageIcon("images/file/folder_dele.png"));
	    
	    FTPButtonEvent.serverHomeAddMouseListener(serverHomeButton);
	    serverHomeButton.setBounds(WIDTH/2 + 4, 0, 20, 20);
	    filePanel.add(serverHomeButton);
	    
	    FTPButtonEvent.serverGoupAddMouseListener(serverGoupButton);
	    serverGoupButton.setBounds(WIDTH/2 + 26, 0, 20, 20);
	    filePanel.add(serverGoupButton);
	    
	    FTPButtonEvent.serverRefreshAddMouseListener(serverRefreshButton);
	    serverRefreshButton.setBounds(WIDTH/2 + 48, 0, 20, 20);
	    filePanel.add(serverRefreshButton);
	    
	    FTPButtonEvent.serverAddFolderAddMouseListener(serverAddButton);
	    serverAddButton.setBounds(WIDTH/2 + 70, 0, 20, 20);
	    filePanel.add(serverAddButton);
	    
	    FTPButtonEvent.serverDeleAddMouseListener(serverDeleButton);
	    serverDeleButton.setBounds(WIDTH/2 + 92, 0, 20, 20);
	    filePanel.add(serverDeleButton);
	    
	    //设置服务端端目录域
	    serverTextFieldHost = new JTextField();
	    serverTextFieldHost.setText("server");
	    serverTextFieldHost.setBounds(WIDTH/2 + 116, 0, WIDTH/2-116, 20);
	    filePanel.add(serverTextFieldHost);
	}
	/**
	 * 创建客户端文件浏览器
	 *
	 */
	private void createClientPanel() {
		clientTableModel = new ClientTableModel();
		//得到指定路径下的文件列表
		clientTableModel.showFileList(clientTextFieldHost.getText());
		
		clientTable = new JTable(clientTableModel);    
		//不显示网格
		clientTable.setShowGrid(false);
		//不可编辑
		//clientTable.setEnabled(true);
		//设置选择模式
		clientTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clientTable.setDefaultEditor(JTextField.class, new DefaultCellEditor(new JTextField()));
		FTPTableEvent.clientAddMouseListener();
	    //设置第一列显示图标
	    TableColumn iconColumn = clientTable.getColumn("Client");
		iconColumn.setMaxWidth(clientTable.getRowHeight());
		iconColumn.setMinWidth(clientTable.getRowHeight());
		iconColumn.setCellRenderer(clientTable.getDefaultRenderer(ImageIcon.class));
		//clientTable.getSelectionModel().setSelectionInterval(0, 0);
		clientScrollPane1 = new JScrollPane(clientTable);
		clientScrollPane1.setBounds(0, 30, WIDTH/2, 270);
		contPanel.add(clientScrollPane1);
	}
	/**
	 * 创建服务端文件浏览器
	 *
	 */
	private void createServerPanel() {
		serverTableModel = new ServerTableModel();
		serverTable = new JTable(serverTableModel);    
		//不显示网格
		serverTable.setShowGrid(false);
		//不可编辑
		serverTable.setEnabled(true);
		//设置选择模式
		serverTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		FTPTableEvent.serverAddMouseListener();
		TableColumn iconColumn = serverTable.getColumn("Server");
		iconColumn.setMaxWidth(serverTable.getRowHeight());
		iconColumn.setMinWidth(serverTable.getRowHeight());
		iconColumn.setCellRenderer(serverTable.getDefaultRenderer(ImageIcon.class));
		serverScrollPane1 = new JScrollPane(serverTable);
		serverScrollPane1.setBounds(WIDTH/2, 30, WIDTH/2, 270);
		contPanel.add(serverScrollPane1);
	}
	/**
	 * 创建状态区
	 *
	 */
	private void createStatusPanel() {
		statusTableModel = new StatusTableModel();
		statusTable = new JTable(statusTableModel); 
		FTPTableEvent.statusAddMouseListener();
		TableColumn iconColumn = statusTable.getColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_STATUS));
		//iconColumn.setMaxWidth(statusTable.getRowHeight());
		//iconColumn.setMinWidth(statusTable.getRowHeight());
		//iconColumn.setCellRenderer(statusTable.getDefaultRenderer(JProgressBar.class));
	    iconColumn.setCellRenderer(new ProgressBarRenderer());
		statusScrollPane1 = new JScrollPane(statusTable);
		statusScrollPane1.setBounds(0, 330, WIDTH, HEIGHT-330);
		getContentPane().add(statusScrollPane1);
	}
	
}
