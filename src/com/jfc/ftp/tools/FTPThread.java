package com.jfc.ftp.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jfc.ftp.model.download.DownloadModel;
import com.jfc.ftp.service.FTPService;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.PropertyUtil;

/**
 * Ϊ�ϵ����������ļ��������µ��߳�
 * @author SavageGarden
 *
 */
public class FTPThread extends Thread{
	/**
	 * վ��URL
	 */
	private String host;
	/**
	 * վ��˿�
	 */
	private int port;
	/**
	 * �û�
	 */
	private String user;
	/**
	 * ����
	 */
	private String pswd;
	/**
	 * ��ǰ�̵߳�FTP�����ӿ�ʵ����
	 */
	private FTPService ftpService;

	/**
	 * �ڼ���������
	 */
	private int rowIndex;
	/**
	 * Ҫ���ص��ļ�·��
	 */
	private String filepath;
	/**
	 * Ҫ���ص��ļ���С
	 */
	private long filesize;
	/**
	 * Ҫ���ص��ļ�����·��
	 */
	private String savepath;
	
	/**
	 * ����ļ���������
	 */
	public int hadRead = 0;
	/**
	 * �����߳̿�ʼʱ��
	 */
	public long startTime = 0;
	/**
	 * �����߳��Ѻ�ʱ��
	 */
	public long eclipsedTime = 0;
	/**
	 * �����߳̽���ʱ��
	 */
	public long endTime = 0;
	/**
	 * ��ǰ�����̵߳Ļ�����
	 */
	public Lock ftpThreadLock;
	public int threadCount = 0;
	/**
	 * ��ǰ�����̵߳�״̬
	 */
	private int status = Constant.THREAD_STATUS_NEW;
	/**
	 * �����̵߳����ط�ʽ
	 */
	public int downloadType = 0;
	/**
	 * �Ƿ��Ѿ��ϲ��ļ�
	 */
	private boolean hadMerger = false;
	/**
	 * �����̵߳����ض���
	 */
	private DownloadModel downloadModel = new DownloadModel();
	/**
	 * �����̵߳�״̬
	 */
	private int completed = 0;
	
	public synchronized int getCompleted() {
		return completed;
	}
	public synchronized void setCompleted(int completed) {
		this.completed = completed;
	}
	public DownloadModel getDownloadModel() {
		return downloadModel;
	}
	public void setDownloadModel(DownloadModel downloadModel) {
		this.downloadModel = downloadModel;
	}
	
	public synchronized int getStatus() {
		return status;
	}
	public synchronized void setStatus(int status) {
		this.status = status;
	}
	public synchronized boolean isHadMerger() {
		return hadMerger;
	}
	public synchronized void setHadMerger(boolean hadMerger) {
		this.hadMerger = hadMerger;
	}
	public synchronized long getEclipsedTime() {
		return eclipsedTime;
	}
	public synchronized void setEclipsedTime(long eclipsedTime) {
		this.eclipsedTime = eclipsedTime;
	}
	public synchronized long getEndTime() {
		return endTime;
	}
	public synchronized void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public synchronized long getStartTime() {
		return startTime;
	}
	public synchronized void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	/**
	 * �����̵߳Ĺ��췽��<br>
	 * �����Ѿ�ȡ�����ӵ�FTPTools�õ�������Ϣ<br>
	 * ���ݲ���ȡ��������Ϣ
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public FTPThread(int downloadType, int rowIndex, String filepath, long filesize, String savepath) {
		super("FTPThread");
		host = FTPTools.host;
		port = FTPTools.port;
		user = FTPTools.user;
		pswd = FTPTools.pswd;
		this.downloadType = downloadType;
		this.rowIndex = rowIndex;
		this.filepath = filepath;
		this.filesize = filesize;
		this.savepath = savepath;
		ftpThreadLock = new ReentrantLock();
		setStatus(Constant.THREAD_STATUS_RUNNABLE);
		start();
	}
	public FTPThread(int downloadType, int rowIndex, String filepath, long filesize, String savepath, int status) {
		super("FTPThread");
		host = FTPTools.host;
		port = FTPTools.port;
		user = FTPTools.user;
		pswd = FTPTools.pswd;
		this.downloadType = downloadType;
		this.rowIndex = rowIndex;
		this.filepath = filepath;
		this.filesize = filesize;
		this.savepath = savepath;
		ftpThreadLock = new ReentrantLock();
		setStatus(status);
		start();
	}
	public void run() {
		getFTPService();
		getFTPConnect(host, port);
		if(doLoginFTP(user, pswd)) {
			threadCount = Integer.parseInt(PropertyUtil.getProperty(Constant.RESUME_THREAD_COUNT_PROPNAME, Constant.RESUME_THREAD_COUNT_DEFAULT));
			
			downloadModel.setFtpsite(host);
			downloadModel.setFilepath(filepath);
			downloadModel.setSavepath(savepath);
			downloadModel.setRowIndex(rowIndex);
			downloadModel.setFilesize(filesize);
			downloadModel.setThreadCount(threadCount);
			downloadModel.setDownloadType(downloadType);
			
			if(downloadType == Constant.DOWNLOAD_TYPE_COMMON) {
				download(rowIndex, filepath, filesize, savepath);
			} else if(downloadType == Constant.DOWNLOAD_TYPE_RBT){
				resumeBrokenTransfer(this, rowIndex, filepath, filesize, savepath);
			} else if(downloadType == Constant.DOWNLOAD_TYPE_RBT_THREAD) {
				resumeBrokenTransferByThread(this, rowIndex, filepath, filesize, savepath);
			}
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
	 * ����ͨ��ʽ�����ļ�
	 * @param filepath
	 * @param savepath
	 */
	public void download(int rowIndex, String filepath, long filesize, String savepath) {
		ftpService.download(rowIndex, filepath, filesize, savepath);
	}
	/**
	 * �Զϵ������ķ�ʽ�����ļ�
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransfer(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath) {
		ftpService.resumeBrokenTransfer(ftpThread, rowIndex, filepath, filesize, savepath);
	}
	/**
	 * �Զ��̡߳��ϵ������ķ�ʽ�����ļ�
	 * @param ftpThread
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransferByThread(FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath) {
		ftpService.resumeBrokenTransferByThread(ftpThread, rowIndex, filepath, filesize, savepath);
	}
	/**
	 * ��ָ���ļ�·���²�����ʱ�ļ��ϲ�Ϊһ���ļ�
	 * @param filepath
	 * @param threadCount
	 */
	public void mergerTempFile(String filepath, int threadCount) {
		System.out.println("��ʼ�ϲ��ļ�");
		try {
			BufferedOutputStream dataOutput = new BufferedOutputStream(new FileOutputStream(filepath));
			byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
			for(int i = 0; i < threadCount; i ++) {
				File tempFile = new File(filepath + Constant.DOWNLOAD_TEMP_NAME + i);
				BufferedInputStream dataInput = new BufferedInputStream(new FileInputStream(tempFile));
				int read = 0;
				int hadRead = 0;
				while((read = dataInput.read(temp, 0, temp.length)) != -1) {
					dataOutput.write(temp, 0, read);
					hadRead += read;
					if(hadRead % 4096 == 0) {
						dataOutput.flush();
					}
				}
				dataOutput.flush();
				dataInput.close();
			}
			dataOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		setHadMerger(true);
		System.out.println("�ϲ��ļ����");
		deleteTempFile(filepath, threadCount);
	}
	/**
	 * �ϲ��ļ���ɺ�ɾ����ʱ�ļ�
	 * @param filepath
	 * @param threadCount
	 */
	public void deleteTempFile(String filepath, int threadCount) {
		if(isHadMerger()) {
			System.out.println("��ʼɾ����ʱ�ļ�");
			for(int i = 0; i < threadCount; i ++) {
				File tempFile = new File(filepath + Constant.DOWNLOAD_TEMP_NAME + i);
				tempFile.delete();
			}
			System.out.println("��ʱ�ļ�ɾ�����");
		}
	}
}
