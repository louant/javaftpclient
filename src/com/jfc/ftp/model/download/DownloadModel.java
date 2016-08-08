package com.jfc.ftp.model.download;

public class DownloadModel {

	/**
	 * 属于哪一个FTP站点的下载信息
	 */
	private String ftpsite = "";
	/**
	 * 要下载的文件路径(服务端)
	 */
	private String filepath = "";
	/**
	 * 要保存的文件路径(客户端)
	 */
	private String savepath = "";
	/**
	 * 第几个下载项
	 */
	private int rowIndex = 0;
	/**
	 * 下载文件的大小
	 */
	private long filesize = 0;
	/**
	 * 已下载文件大小
	 */
	private long hadRead = 0;
	/**
	 * 已下载文件耗时
	 */
	private long eclipsedTime = 0;
	/**
	 * 使用的下载线程数量
	 */
	private int threadCount = 2;
	/**
	 * 下载类型
	 */
	private int downloadType = 0;
	
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public String getFtpsite() {
		return ftpsite;
	}
	public void setFtpsite(String ftpsite) {
		this.ftpsite = ftpsite;
	}
	public long getHadRead() {
		return hadRead;
	}
	public void setHadRead(long hadRead) {
		this.hadRead = hadRead;
	}
	public long getEclipsedTime() {
		return eclipsedTime;
	}
	public void setEclipsedTime(long eclipsedTime) {
		this.eclipsedTime = eclipsedTime;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public String getSavepath() {
		return savepath;
	}
	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public int getDownloadType() {
		return downloadType;
	}
	public void setDownloadType(int downloadType) {
		this.downloadType = downloadType;
	}
	/**
	 * 覆盖了toString(),返回ftp服务器站点基本信息
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("ftpsite:" + ftpsite + ";");
		buffer.append("filepath:" + filepath + ";");
		buffer.append("savepath:" + savepath + ";");
		buffer.append("rowIndex:" + rowIndex + ";");
		buffer.append("filesize:" + filesize + ";");
		buffer.append("hadRead:" + hadRead + ";");
		buffer.append("eclipsedTime:" + eclipsedTime + ";");
		buffer.append("threadCount:" + threadCount + ";");
		buffer.append("downloadType:" + downloadType + ";");

		return buffer.toString();
	}
}
