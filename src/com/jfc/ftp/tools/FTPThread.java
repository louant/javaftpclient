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
 * 为断点续传下载文件而启动新的线程
 * @author SavageGarden
 *
 */
public class FTPThread extends Thread{
	/**
	 * 站点URL
	 */
	private String host;
	/**
	 * 站点端口
	 */
	private int port;
	/**
	 * 用户
	 */
	private String user;
	/**
	 * 密码
	 */
	private String pswd;
	/**
	 * 当前线程的FTP操作接口实现类
	 */
	private FTPService ftpService;

	/**
	 * 第几个下载项
	 */
	private int rowIndex;
	/**
	 * 要下载的文件路径
	 */
	private String filepath;
	/**
	 * 要下载的文件大小
	 */
	private long filesize;
	/**
	 * 要下载的文件保存路径
	 */
	private String savepath;
	
	/**
	 * 标记文件已下载量
	 */
	public int hadRead = 0;
	/**
	 * 下载线程开始时间
	 */
	public long startTime = 0;
	/**
	 * 下载线程已耗时间
	 */
	public long eclipsedTime = 0;
	/**
	 * 下载线程结束时间
	 */
	public long endTime = 0;
	/**
	 * 当前下载线程的互斥锁
	 */
	public Lock ftpThreadLock;
	public int threadCount = 0;
	/**
	 * 当前下载线程的状态
	 */
	private int status = Constant.THREAD_STATUS_NEW;
	/**
	 * 下载线程的下载方式
	 */
	public int downloadType = 0;
	/**
	 * 是否已经合并文件
	 */
	private boolean hadMerger = false;
	/**
	 * 下载线程的下载对象
	 */
	private DownloadModel downloadModel = new DownloadModel();
	/**
	 * 下载线程的状态
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
	 * 下载线程的构造方法<br>
	 * 根据已经取得连接的FTPTools得到连接信息<br>
	 * 根据参数取得下载信息
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
	 * 以普通方式下载文件
	 * @param filepath
	 * @param savepath
	 */
	public void download(int rowIndex, String filepath, long filesize, String savepath) {
		ftpService.download(rowIndex, filepath, filesize, savepath);
	}
	/**
	 * 以断点续传的方式下载文件
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
	 * 以多线程、断点续传的方式下载文件
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
	 * 在指定文件路径下查找临时文件合并为一个文件
	 * @param filepath
	 * @param threadCount
	 */
	public void mergerTempFile(String filepath, int threadCount) {
		System.out.println("开始合并文件");
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
		System.out.println("合并文件完成");
		deleteTempFile(filepath, threadCount);
	}
	/**
	 * 合并文件完成后删除临时文件
	 * @param filepath
	 * @param threadCount
	 */
	public void deleteTempFile(String filepath, int threadCount) {
		if(isHadMerger()) {
			System.out.println("开始删除临时文件");
			for(int i = 0; i < threadCount; i ++) {
				File tempFile = new File(filepath + Constant.DOWNLOAD_TEMP_NAME + i);
				tempFile.delete();
			}
			System.out.println("临时文件删除完成");
		}
	}
}
