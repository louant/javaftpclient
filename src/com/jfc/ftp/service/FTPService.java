package com.jfc.ftp.service;

import javax.swing.JProgressBar;

import com.jfc.ftp.tools.FTPThread;
/**
 * FTP�����ӿ�
 * @author SavageGarden
 *
 */
public interface FTPService {

	public static final String FTP_SERVICE_NAME = "com.jfc.ftp.service.impl.SocketFTPService";
	/**
	 * ���ݷ�������ַ���˿ڻ��ftp����
	 * @param host
	 * @param port
	 * @return
	 */
	public String getFTPConnect(String host, int port);
	/**
	 * ִ�е�¼
	 * @param user
	 * @param pswd
	 * @return
	 */
	public boolean doLoginFTP(String user, String pswd);
	/**
	 * �õ�ָ��·���µ��ļ��б�
	 * @param dir
	 * @return
	 */
	public String getListFile(String dir);
	/**
	 * ����ָ���ļ�
	 * @param filepath
	 * @param savepath
	 */
	public void download(int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * ����ָ���ļ�
	 * @param filepath
	 * @param savepath
	 */
	public void download(String filepath, String savepath);
	/**
	 * ʹ�öϵ�������ʽ�����ļ�
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransfer(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * ʹ�ö��̡߳��ϵ�������ʽ�����ļ�
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransferByThread(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * �ϴ�ָ���ļ�
	 * @param filepath
	 * @param savepath
	 */
	public void upload(String filepath, String savepath); 
	/**
	 * ִ��FTP����
	 * @param command
	 */
	public String doFTPCommand(String command);
	/**
	 * �����˷�������
	 * @param request
	 * @return
	 */
	public void sendRequest(String request);
	/**
	 * �õ�����������Ӧ��Ϣ
	 * @return
	 */
	public String getResponseStrings();
	public String readRespond();
	public String readDataRespond(boolean passiveFlag);
	/**
	 * �ر�FTP����
	 *
	 */
	public void closeFTP();
}
