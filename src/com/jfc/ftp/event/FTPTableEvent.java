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
 * ���ڴ���<br>
 * �ͻ����ļ��������������ļ��������״̬�������<br>
 * ���¼�
 * @author SavageGarden
 *
 */
public class FTPTableEvent extends FTPEvent {

	private final static Logger logger = Logger.getLogger(FTPTableEvent.class);
	/**
	 * �ͻ����ļ�������������¼�
	 * @param table
	 */
	public static void clientAddMouseListener(){
		mainFrame.clientTable.addMouseListener(new MouseAdapter(){
			/**
			 * �������¼�
			 * <br>����ʱʹѡ����
			 * <br>˫��ʱ������ļ�������ʾ��Ŀ¼�µ��ļ�
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
						logger.debug("���ļ�����Ŀ¼");
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
	 * ������ļ�������������¼�
	 * @param table
	 */
	public static void serverAddMouseListener() {
		mainFrame.serverTable.addMouseListener(new MouseAdapter(){
			/**
			 * �������¼�
			 * <br>����ʱʹѡ���б�ɫ��ʾ����
			 * <br>˫��ʱ������ļ�������ʾ��Ŀ¼�µ��ļ�
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
						logger.debug("���ļ�����Ŀ¼");
					}
				} else if(event.getClickCount() == 1 && SwingUtilities.isRightMouseButton(event)){
					mainFrame.serverTable.changeSelection(index, 1, false, false);
					mainFrame.serverPopupMenu.show(mainFrame.serverTable, p.x, p.y);
				}
			}
		});
	}
	/**
	 * ״̬������������������¼�����
	 *
	 */
	public static void statusAddMouseListener() {
		mainFrame.statusTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event){
				Point p = event.getPoint();
				int index = mainFrame.statusTable.rowAtPoint(p);
				
				if(event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1) {
					FTPThread ftpThread = (FTPThread)SystemTools.downloadThreadList.get(index);
					System.out.println("ѡ���˵�" + index + "�������߳�");
					System.out.println("�߳�IDΪ" + ftpThread.getId());
					try {
						if(ftpThread.getStatus() == Constant.THREAD_STATUS_RUNNABLE) {
							System.out.println("��ʼ�ȴ�");
							ftpThread.setStatus(Constant.THREAD_STATUS_WAITING);
							ftpThread.setEclipsedTime(ftpThread.getEclipsedTime() + (System.currentTimeMillis() - ftpThread.getStartTime()));
							System.out.println("eclipse:" + ftpThread.getEclipsedTime());
						} else if(ftpThread.getStatus() == Constant.THREAD_STATUS_WAITING) {
							System.out.println("��ʼ����");
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
