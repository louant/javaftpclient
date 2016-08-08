package com.jfc.ftp.model.download;

public class DownloadModel {

	/**
	 * ������һ��FTPվ���������Ϣ
	 */
	private String ftpsite = "";
	/**
	 * Ҫ���ص��ļ�·��(�����)
	 */
	private String filepath = "";
	/**
	 * Ҫ������ļ�·��(�ͻ���)
	 */
	private String savepath = "";
	/**
	 * �ڼ���������
	 */
	private int rowIndex = 0;
	/**
	 * �����ļ��Ĵ�С
	 */
	private long filesize = 0;
	/**
	 * �������ļ���С
	 */
	private long hadRead = 0;
	/**
	 * �������ļ���ʱ
	 */
	private long eclipsedTime = 0;
	/**
	 * ʹ�õ������߳�����
	 */
	private int threadCount = 2;
	/**
	 * ��������
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
	 * ������toString(),����ftp������վ�������Ϣ
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
