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
 * FTP��������
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
	 * ���췽��,��ʼ������,�����߳�
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
	 * ��ȡFTPService�ӿ�ʵ����<br>
	 * ���ȴ������ļ�����<br>
	 * ���û�������Ĭ�ϵ�ʵ����
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
	 * ���ݷ�������ַ���˿ڻ��ftp����
	 * @param host
	 * @param port
	 * @return
	 */
	public String getFTPConnect(String host, int port) {
		return ftpService.getFTPConnect(host, port);
	}
	/**
	 * ִ�е�¼
	 * @param user
	 * @param pswd
	 * @return
	 */
	public boolean doLoginFTP(String user, String pswd) {
		return ftpService.doLoginFTP(user, pswd);
	}
	/**
	 * �õ�ָ��·���µ��ļ��б�
	 * @param dir
	 * @return
	 */
	public String getListFile(String dir){
		doFTPCommand("CWD " + dir);
		readRespond();
		return ftpService.getListFile("");
	}
	/**
	 * �����ļ���
	 * @param folderName
	 */
	public void createFolder(String folderName) {
		doFTPCommand("MKD " + folderName);
	}
	/**
	 * �޸��ļ���
	 * @param oldfile
	 * @param newfile
	 */
	public void rename(String oldfile, String newfile) {
		doFTPCommand("RNFR " + oldfile);
		doFTPCommand("RNTO " + newfile);
	}
	/**
	 * ɾ���ļ�
	 * <br>������ļ���ʹ��"DELE"����ɾ��,
	 * <br>������ļ��о��ȵݹ�ɾ�����е��ļ�,Ȼ��ʹ��"RMD"����ɾ��
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
	 * �����ļ�
	 * <br>������ļ���ֱ������
	 * <br>�����Ŀ¼��ݹ�����
	 * <br>��ԭ�����浽ָ��Ŀ¼
	 * @param filepath
	 * @param filetype
	 * @param savepath
	 */
	public void download(String filepath, String filetype, String savepath) {
		if(filetype.equals(Constant.FILE_TYPE_FILE)) {
			readRespond();
			ftpService.download(filepath, savepath);
		} else {
			//�����ڿͻ��˴���Ŀ¼
			String downloadpath = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
			FileUtil.createFolder(savepath + File.separator + downloadpath);
			//��ȡ��Ŀ¼�µ������ļ�
			String dataStr = getListFile(filepath);
			ArrayList fileList = FileUtil.getServerFileList(dataStr);
			for(Object object : fileList){
				FileModel fileModel = (FileModel)object;
				if(fileModel.getFileType().equals(Constant.FILE_TYPE_FILE)) {
					//���ļ���ֱ������
					readRespond();
					ftpService.download(filepath + "/" +fileModel.getFileName(), savepath + File.separator + downloadpath);
				} else if(fileModel.getFileType().equals(Constant.FILE_TYPE_DIRE)){
					//��Ŀ¼��ݹ�
					download(filepath + "/" + fileModel.getFileName(), Constant.FILE_TYPE_DIRE, savepath + File.separator + downloadpath);
				}
			}
		}
	}
	/**
	 * �ϴ��ļ�
	 * <br>������ļ���ֱ���ϴ�
	 * <br>�����Ŀ¼��ݹ��ϴ�
	 * <br>��ԭ�����浽ָ��Ŀ¼
	 * @param filepath
	 * @param filetype
	 * @param savepath
	 */
	public void upload(String filepath, String filetype, String savepath) {
		if(filetype.equals(Constant.FILE_TYPE_FILE)) {
			readRespond();
			ftpService.upload(filepath, savepath);
		} else {
			//�����ڷ���˴���Ŀ¼
			String uploadpath = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
			savepath = savepath.equals("/")?"":savepath;
			createFolder(savepath + "/" + uploadpath);
			//��ȡ�ͻ���Ŀ¼�µ������ļ�
			ArrayList fileList = FileUtil.getClientFileList(filepath);
			for(Object object : fileList){
				FileModel fileModel = (FileModel)object;
				if(fileModel.getFileType().equals(Constant.FILE_TYPE_FILE)) {
					//���ļ���ֱ���ϴ�
					readRespond();
					ftpService.upload(filepath + File.separator + fileModel.getFileName(), savepath + "/" + uploadpath);
				} else if(fileModel.getFileType().equals(Constant.FILE_TYPE_DIRE)){
					//��Ŀ¼��ݹ�
					upload(filepath + File.separator + fileModel.getFileName(), Constant.FILE_TYPE_DIRE, savepath + "/" + uploadpath);
				}
			}
		}
	}
	/**
	 * ִ��FTP����
	 * @param command
	 */
	public String doFTPCommand(String command) {
		return ftpService.doFTPCommand(command);
	}
	/**
	 * �õ�����������Ӧ
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
	 * �ر�FTP����
	 * �����߳�
	 *
	 */
	public void closeFTP() {
		ftpService.closeFTP();
		//stop();
	}
}
