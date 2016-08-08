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
 * ����� ������ʾ�û�����
 * @author SavageGarden
 *
 */
public class FTPMainFrame extends JFrame{

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * ����
	 */
	private static int WIDTH;
	/**
	 * ����
	 */
	private static int HEIGHT;
	/**
	 * ���˵�
	 */
	private JMenuBar menubar;
	/**
	 * �ͻ����Ҽ��˵�
	 */
	public JPopupMenu clientPopupMenu;
	/**
	 * ������Ҽ��˵�
	 */
	public JPopupMenu serverPopupMenu;
	/**
	 * ״̬���Ҽ��˵�
	 */
	public JPopupMenu statusPopupMenu;
	/**
	 * ���ڷ��ò˵���Panel
	 */
	private JPanel menuPanel;
	/**
	 * �������ò�����ť��Panel
	 */
	private JPanel filePanel;
	/**
	 * �������ÿͻ��ˡ�������ļ��������Panel
	 */
	private JPanel contPanel;
	/**
	 * ���ÿͻ����ļ��������Panel
	 */
	private JScrollPane clientScrollPane1;
	/**
	 * ���÷�����ļ��������Panel
	 */
	private JScrollPane serverScrollPane1;
	/**
	 * ����״̬����Panel
	 */
	public JScrollPane statusScrollPane1;

	/**
	 * ������ʾ�ͻ���Ŀ¼
	 */
	public static JTextField clientTextFieldHost;
	/**
	 * ������ʾ�����Ŀ¼
	 */
	public static JTextField serverTextFieldHost;
	
	/**
	 * ���ڽ�������FTP����˵Ĳ���
	 */
	public static FTPServerManagerDialog serverManagerDialog;
	/**
	 * ����˱��ģ��
	 */
	public static ServerTableModel serverTableModel;
	/**
	 * �ͻ��˱��ģ��
	 */
	public static ClientTableModel clientTableModel;
	/**
	 * ״̬�����ģ��
	 */
	public static StatusTableModel statusTableModel;
	
	/**
	 * ����˱��
	 */
	public static JTable serverTable;
	/**
	 * �ͻ��˱��
	 */
	public static JTable clientTable;
	/**
	 * ״̬�����
	 */
	public static JTable statusTable;
	/**
	 * FTP��������
	 */
	public static FTPTools ftpTools;
	
	public static boolean initFlag = false;
	
	/**
	 * ������,��ʼ�����
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
		
		//�����˵���ӵ����
		createMenu();
		
		//�����ļ���������
		createFilePanel();
		
		//��������������ӵ����
		createCont();
		
		//�����ͻ����ļ��������ӵ�������
		createClientPanel();
		
		//����������ļ��������ӵ�������
		createServerPanel();
		
		//����״̬����ӵ����
		createStatusPanel();
		
		//�����Ҽ��˵�
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
	 * �����˵�
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
	 * �����ͻ��ˡ�����ˡ�״̬�����Ҽ��˵�
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
	 * �����ļ�������
	 *
	 */
	private void createFilePanel(){
		filePanel = new JPanel();
		filePanel.setLayout(null);
		filePanel.setBounds(0, 20, WIDTH, 20);
	    getContentPane().add(filePanel);
	    
	    //���ÿͻ��˲�����ť
	    JButton clientHomeButton = new JButton(new ImageIcon("images/file/folder_home.png"));
	    JButton clientGoupButton = new JButton(new ImageIcon("images/file/folder_goup.png"));
	    JButton clientRefreshButton = new JButton(new ImageIcon("images/file/folder_refresh.png"));
	    JButton clientAddButton = new JButton(new ImageIcon("images/file/folder_add.png"));
	    JButton clientDeleButton = new JButton(new ImageIcon("images/file/folder_dele.png"));

	    //��������Ŀ¼��ť����¼�����
	    FTPButtonEvent.clientHomeAddMouseListener(clientHomeButton);
	    clientHomeButton.setBounds(4, 0, 20, 20);
	    filePanel.add(clientHomeButton);
	    
	    //�������ϼ�Ŀ¼��ť����¼�����
	    FTPButtonEvent.clientGoupAddMouseListener(clientGoupButton);
	    clientGoupButton.setBounds(26, 0, 20, 20);
	    filePanel.add(clientGoupButton);
	    
	    //��ˢ�°�ť����¼�����
	    FTPButtonEvent.clientRefreshAddMouseListener(clientRefreshButton);
	    clientRefreshButton.setBounds(48, 0, 20, 20);
	    filePanel.add(clientRefreshButton);
	    
	    //�������ļ��а�ť����¼�����
	    FTPButtonEvent.clientAddFolderAddMouseListener(clientAddButton);
	    clientAddButton.setBounds(70, 0, 20, 20);
	    filePanel.add(clientAddButton);
	    
	    FTPButtonEvent.clientDeleAddMouseListener(clientDeleButton);
	    clientDeleButton.setBounds(92, 0, 20, 20);
	    filePanel.add(clientDeleButton);
	    
	    //���ÿͻ���Ŀ¼��
	    clientTextFieldHost = new JTextField();
	    clientTextFieldHost.setText(FileUtil.getUserHome());
	    clientTextFieldHost.setBounds(116, 0, WIDTH/2-116, 20);
	    FTPTextFieldEvent.clientAddActionListener(clientTextFieldHost);
	    filePanel.add(clientTextFieldHost);

	    //���÷���˲�����ť
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
	    
	    //���÷���˶�Ŀ¼��
	    serverTextFieldHost = new JTextField();
	    serverTextFieldHost.setText("server");
	    serverTextFieldHost.setBounds(WIDTH/2 + 116, 0, WIDTH/2-116, 20);
	    filePanel.add(serverTextFieldHost);
	}
	/**
	 * �����ͻ����ļ������
	 *
	 */
	private void createClientPanel() {
		clientTableModel = new ClientTableModel();
		//�õ�ָ��·���µ��ļ��б�
		clientTableModel.showFileList(clientTextFieldHost.getText());
		
		clientTable = new JTable(clientTableModel);    
		//����ʾ����
		clientTable.setShowGrid(false);
		//���ɱ༭
		//clientTable.setEnabled(true);
		//����ѡ��ģʽ
		clientTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clientTable.setDefaultEditor(JTextField.class, new DefaultCellEditor(new JTextField()));
		FTPTableEvent.clientAddMouseListener();
	    //���õ�һ����ʾͼ��
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
	 * ����������ļ������
	 *
	 */
	private void createServerPanel() {
		serverTableModel = new ServerTableModel();
		serverTable = new JTable(serverTableModel);    
		//����ʾ����
		serverTable.setShowGrid(false);
		//���ɱ༭
		serverTable.setEnabled(true);
		//����ѡ��ģʽ
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
	 * ����״̬��
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
