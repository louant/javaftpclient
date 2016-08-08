package com.jfc.ftp.model.file;

public class FileModel {

	private String fileImage = "";
	private String fileName = "";
	private String fileType = "";
	private String fileSize = "";
	private String fileDate = "";
	
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileImage() {
		return fileImage;
	}
	public void setFileImage(String fileImage) {
		this.fileImage = fileImage;
	}
	
	/**
	 * 覆盖了toString(),返回文件基本信息
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("fileName:" + fileName + ";");
		buffer.append("fileType:" + fileType + ";");
		buffer.append("fileSize:" + fileSize + ";");
		buffer.append("fileDate:" + fileDate + ";");

		return buffer.toString();
	}
}
