package com.jfc.ftp.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.model.file.FileModel;
import com.jfc.ftp.service.FTPService;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.FileUtil;
import com.jfc.ftp.util.PropertyUtil;


/**
 * FTP操作工具
 * @author SavageGarden
 *
 */
public class FTPTools extends Thread {
	public static String host;
	public static int port;
	public static String user;
	public static String pswd;
	private FTPService ftpService;
	private boolean connFlag = true;
	private int cpuCoreNumber;
	public final ExecutorService pool;
	public final CompletionService completionService; 
	public static FTPMainFrame mainFrame = SystemTools.mainFrame;
	
	/**
	 * 构造方法,初始化参数,启动线程
	 * @param host
	 * @param port
	 * @param user
	 * @param pswd
	 */
	public FTPTools (String host, int port, String user, String pswd) {
		super("FTPTools");
		this.host = host;
		this.port = port;
		this.user = user;
		this.pswd = pswd;
		cpuCoreNumber = Runtime.getRuntime().availableProcessors(); 
		//pool = Executors.newCachedThreadPool();
		pool = Executors.newFixedThreadPool(cpuCoreNumber);  
		completionService = new ExecutorCompletionService(pool); 
		this.start();
	}
	public void run(){
		getFTPService();
		getFTPConnect(host, port);
		if(doLoginFTP(user, pswd)) {
			mainFrame.serverTextFieldHost.setText("/");
			doFTPCommand("CWD /");
			String dataStr = ftpService.getListFile("");
			mainFrame.serverTableModel.showFileList(dataStr);
			mainFrame.statusTableModel.showDownloadList(host);
		}
	}
	/**
	 * 获取FTPService接口实现类<br>
	 * 首先从配置文件中找<br>
	 * 如果没有则加载默认的实现类
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void getFTPService(){
		try {
			ftpService = (FTPService)Class.forName(PropertyUtil.getProperty("ftp.service.name", FTPService.FTP_SERVICE_NAME)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 根据服务器地址、端口获得ftp链接
	 * @param host
	 * @param port
	 * @return
	 */
	public String getFTPConnect(String host, int port) {
		return ftpService.getFTPConnect(host, port);
	}
	/**
	 * 执行登录
	 * @param user
	 * @param pswd
	 * @return
	 */
	public boolean doLoginFTP(String user, String pswd) {
		return ftpService.doLoginFTP(user, pswd);
	}
	/**
	 * 得到指定路径下的文件列表
	 * @param dir
	 * @return
	 */
	public String getListFile(String dir){
		doFTPCommand("CWD " + dir);
		readRespond();
		return ftpService.getListFile("");
	}
	/**
	 * 创建文件夹
	 * @param folderName
	 */
	public void createFolder(String folderName) {
		doFTPCommand("MKD " + folderName);
	}
	/**
	 * 修改文件名
	 * @param oldfile
	 * @param newfile
	 */
	public void rename(String oldfile, String newfile) {
		doFTPCommand("RNFR " + oldfile);
		doFTPCommand("RNTO " + newfile);
	}
	/**
	 * 删除文件
	 * <br>如果是文件就使用"DELE"命令删除,
	 * <br>如果是文件夹就先递归删除其中的文件,然后使用"RMD"命令删除
	 * @param filepath
	 */
	public void deleteDir(String filepath, String filetype) {
		if(filetype.equals(Constant.FILE_TYPE_DIRE)) {
			String dataStr = getListFile(filepath);
			filepath = filepath.equals("/")?"":filepath;
			ArrayList fileList = FileUtil.getServerFileList(dataStr);
			for(Object object : fileList){
				FileModel fileModel = (FileModel)object;
				if(fileModel.getFileType().equals(Constant.FILE_TYPE_FILE)) {
					doFTPCommand("DELE " + filepath + "/" +fileModel.getFileName());
				} else if(fileModel.getFileType().equals(Constant.FILE_TYPE_DIRE)){
					deleteDir(filepath + "/" + fileModel.getFileName(), Constant.FILE_TYPE_DIRE);
				}
			}
			doFTPCommand("RMD " + filepath);
		} else if(filetype.equals(Constant.FILE_TYPE_FILE)) {
			doFTPCommand("DELE " + filepath);
		}
		
	}
	/**
	 * 下载文件
	 * <br>如果是文件则直接下载
	 * <br>如果是目录则递归下载
	 * <br>以原名保存到指定目录
	 * @param filepath
	 * @param filetype
	 * @param savepath
	 */
	public void download(String filepath, String filetype, String savepath) {
		if(filetype.equals(Constant.FILE_TYPE_FILE)) {
			readRespond();
			ftpService.download(filepath, savepath);
		} else {
			//首先在客户端创建目录
			String downloadpath = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
			FileUtil.createFolder(savepath + File.separator + downloadpath);
			//获取该目录下的所有文件
			String dataStr = getListFile(filepath);
			ArrayList fileList = FileUtil.getServerFileList(dataStr);
			for(Object object : fileList){
				FileModel fileModel = (FileModel)object;
				if(fileModel.getFileType().equals(Constant.FILE_TYPE_FILE)) {
					//是文件就直接下载
					readRespond();
					ftpService.download(filepath + "/" +fileModel.getFileName(), savepath + File.separator + downloadpath);
				} else if(fileModel.getFileType().equals(Constant.FILE_TYPE_DIRE)){
					//是目录则递归
					download(filepath + "/" + fileModel.getFileName(), Constant.FILE_TYPE_DIRE, savepath + File.separator + downloadpath);
				}
			}
		}
	}
	/**
	 * 上传文件
	 * <br>如果是文件则直接上传
	 * <br>如果是目录则递归上传
	 * <br>以原名保存到指定目录
	 * @param filepath
	 * @param filetype
	 * @param savepath
	 */
	public void upload(String filepath, String filetype, String savepath) {
		if(filetype.equals(Constant.FILE_TYPE_FILE)) {
			readRespond();
			ftpService.upload(filepath, savepath);
		} else {
			//首先在服务端创建目录
			String uploadpath = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
			savepath = savepath.equals("/")?"":savepath;
			createFolder(savepath + "/" + uploadpath);
			//获取客户端目录下的所有文件
			ArrayList fileList = FileUtil.getClientFileList(filepath);
			for(Object object : fileList){
				FileModel fileModel = (FileModel)object;
				if(fileModel.getFileType().equals(Constant.FILE_TYPE_FILE)) {
					//是文件就直接上传
					readRespond();
					ftpService.upload(filepath + File.separator + fileModel.getFileName(), savepath + "/" + uploadpath);
				} else if(fileModel.getFileType().equals(Constant.FILE_TYPE_DIRE)){
					//是目录则递归
					upload(filepath + File.separator + fileModel.getFileName(), Constant.FILE_TYPE_DIRE, savepath + "/" + uploadpath);
				}
			}
		}
	}
	/**
	 * 执行FTP命令
	 * @param command
	 */
	public String doFTPCommand(String command) {
		return ftpService.doFTPCommand(command);
	}
	/**
	 * 得到服务器的响应
	 * @return
	 */
	public String getResponseStrings() {
		return ftpService.getResponseStrings();
	}
	public String readRespond() {
		return ftpService.readRespond();
	}
	public String readDataRespond(boolean passiveFlag){
		return ftpService.readDataRespond(passiveFlag);
	}
	/**
	 * 关闭FTP链接
	 * 结束线程
	 *
	 */
	public void closeFTP() {
		ftpService.closeFTP();
		//stop();
	}
}
