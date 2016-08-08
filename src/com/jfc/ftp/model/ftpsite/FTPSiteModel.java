package com.jfc.ftp.model.ftpsite;

public class FTPSiteModel {

	private String name = "";
	private String host = "";
	private String port = "";
	private String user = "";
	private String pswd = "";
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPswd() {
		return pswd;
	}
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * 覆盖了toString(),返回ftp服务器站点基本信息
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("serverName:" + name + ";");
		buffer.append("serverHost:" + host + ";");
		buffer.append("serverPort:" + port + ";");
		buffer.append("serverUser:" + user + ";");
		buffer.append("serverPswd:" + pswd + ";");

		return buffer.toString();
	}
	
}
