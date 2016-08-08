package com.jfc.ftp.service;

import javax.swing.JProgressBar;

import com.jfc.ftp.tools.FTPThread;
/**
 * FTP操作接口
 * @author SavageGarden
 *
 */
public interface FTPService {

	public static final String FTP_SERVICE_NAME = "com.jfc.ftp.service.impl.SocketFTPService";
	/**
	 * 根据服务器地址、端口获得ftp链接
	 * @param host
	 * @param port
	 * @return
	 */
	public String getFTPConnect(String host, int port);
	/**
	 * 执行登录
	 * @param user
	 * @param pswd
	 * @return
	 */
	public boolean doLoginFTP(String user, String pswd);
	/**
	 * 得到指定路径下的文件列表
	 * @param dir
	 * @return
	 */
	public String getListFile(String dir);
	/**
	 * 下载指定文件
	 * @param filepath
	 * @param savepath
	 */
	public void download(int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * 下载指定文件
	 * @param filepath
	 * @param savepath
	 */
	public void download(String filepath, String savepath);
	/**
	 * 使用断点续传方式下载文件
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransfer(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * 使用多线程、断点续传方式下载文件
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransferByThread(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath);
	/**
	 * 上传指定文件
	 * @param filepath
	 * @param savepath
	 */
	public void upload(String filepath, String savepath); 
	/**
	 * 执行FTP命令
	 * @param command
	 */
	public String doFTPCommand(String command);
	/**
	 * 向服务端发送命令
	 * @param request
	 * @return
	 */
	public void sendRequest(String request);
	/**
	 * 得到服务器的响应信息
	 * @return
	 */
	public String getResponseStrings();
	public String readRespond();
	public String readDataRespond(boolean passiveFlag);
	/**
	 * 关闭FTP链接
	 *
	 */
	public void closeFTP();
}
