package com.jfc.ftp.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.swing.SwingUtilities;

import com.jfc.ftp.service.FTPService;
import com.jfc.ftp.tools.FTPThread;
import com.jfc.ftp.tools.LogTools;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.FileUtil;

public class SocketFTPService implements FTPService{

	public String host;
	public int port;
	public String user;
	public String pswd;
	public Socket ftpSocket;
	public ServerSocket serverSocket;
	public Socket dataSocket;
	public InputStream serverInput;
	public PrintStream serverOutput;
	public BufferedReader serverReader;
	public boolean passiveFlag = true;
	
	/**
	 * 根据服务器地址、端口获得ftp链接
	 * @param host
	 * @param port
	 * @return
	 */
	public String getFTPConnect(String host, int port) {
		try {
			ftpSocket = new Socket(host, port);
			ftpSocket.setSoTimeout(10000);
			if(ftpSocket != null) {
				this.host = host;
				this.port = port;
			}
			serverOutput = new PrintStream(new BufferedOutputStream(ftpSocket.getOutputStream()), true, "GBK");
			serverReader = new BufferedReader(new InputStreamReader(ftpSocket.getInputStream()));
		} catch(SocketTimeoutException e) {
			if (!ftpSocket.isClosed() && ftpSocket.isConnected())
                System.out.println("读取数据超时!");
            else
                System.out.println("连接超时");
		} catch(Exception e) {
			e.printStackTrace();
			LogTools.logger.error(e.getMessage());
		}
		return readRespond();
	}
	/**
	 * 执行登录
	 * @param user
	 * @param pswd
	 * @return
	 */
	public boolean doLoginFTP(String user, String pswd) {
		String responseStr = doFTPCommand("USER " + user);
		if(responseStr.length() > 0) {
			responseStr = doFTPCommand("PASS " + pswd);
			if(responseStr.length() > 0){
				this.user = user;
				this.pswd = pswd;
				return true;
			}
		}
		return false;
	}
	/**
	 * 得到指定路径下的文件列表
	 * @param dir
	 * @return
	 */
	public String getListFile(String dir){
		//this.passiveFlag = passiveFlag;
		try {
			//if(dataSocket == null) {
				dataSocket = getDataSocket();
			//}
			doFTPCommand("LIST -al " + dir);
			BufferedReader data_br = null;
			data_br = new BufferedReader(
					new InputStreamReader(dataSocket.getInputStream(), Charset.forName("GBK")));
			String line;
			String lines_str = "";
			while (true) {
				line = data_br.readLine();
				// 如果line为null，则说明读到了EOF。
				if (line == null)
					break;
				lines_str += line + '\n';		
			}
			if (lines_str == "")
				lines_str += '\n';
			return lines_str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 下载指定文件<br>
	 * 用于文件夹的递归下载
	 * @param filepath
	 * @param savepath
	 */
	public void download(String filepath, String savepath) {
		String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
		BufferedInputStream dataInput = null;
		BufferedOutputStream dataOutput = null;
		byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
		try {
			doFTPCommand("TYPE I");
			dataSocket = getDataSocket();
			doFTPCommand("RETR " + filepath);
			dataInput = new BufferedInputStream(dataSocket.getInputStream());
			dataOutput = new BufferedOutputStream(new FileOutputStream(new File(savepath + File.separator + filename)));
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
			dataOutput.close();
			dataInput.close();
			readRespond();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				dataOutput.flush();
				dataOutput.close();
				dataInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
		
	}
	/**
	 * 下载指定文件<br>
	 * 用于下载普通文件，带进度条显示
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void download(int rowIndex, String filepath, long filesize, String savepath) {
		String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
		BufferedInputStream dataInput = null;
		BufferedOutputStream dataOutput = null;
		byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
		try {
			doFTPCommand("TYPE I");
			dataSocket = getDataSocket();
			doFTPCommand("RETR " + filepath);
			dataInput = new BufferedInputStream(dataSocket.getInputStream());
			dataOutput = new BufferedOutputStream(new FileOutputStream(new File(savepath + File.separator + filename)));
			int read = 0;
			int hadRead = 0;
			long startTime = System.currentTimeMillis();
			SystemTools.addObserver();
			while((read = dataInput.read(temp, 0, temp.length)) != -1) {
				dataOutput.write(temp, 0, read);
				hadRead += read;
				if(hadRead % 4096 == 0) {
					dataOutput.flush();
				}
				SystemTools.getCurrentSpeed(rowIndex, startTime, hadRead);
				SystemTools.getPrice(rowIndex, hadRead, filesize);
				SwingUtilities.invokeLater(SystemTools.updateProgressBarRunnable);
			}
			dataOutput.flush();
			dataOutput.close();
			dataInput.close();
			SystemTools.getFinalSpeed(rowIndex, startTime, filesize, Constant.DOWNLOAD_TYPE_COMMON);
			readRespond();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				dataOutput.flush();
				dataOutput.close();
				dataInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
		
	}
	
	/**
	 * 使用断点续传方式下载文件<br>
	 * 首先查找要保存的路径下有无缓存文件(以.wfml为后缀)<br>
	 * 		不存在	重新开始下载<br>
	 * 		存在		继续<br>
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransfer(final FTPThread ftpThread, int rowIndex, String filepath, long filesize, String savepath){
		String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
		BufferedInputStream dataInput = null;
		BufferedOutputStream dataOutput = null;
		File tempFile = new File(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME);
		long tempsize = tempFile.length();
		byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
		try {
			doFTPCommand("TYPE I");
			dataSocket = getDataSocket();
			//设定文件指针(下载位置)
			doFTPCommand("REST " + tempsize);
			doFTPCommand("RETR " + filepath);
			dataInput = new BufferedInputStream(dataSocket.getInputStream());
			dataOutput = new BufferedOutputStream(new FileOutputStream(tempFile, true));
			int read = 0;
			int hadRead = (int)tempsize;
			ftpThread.hadRead = hadRead;
			ftpThread.setStartTime(System.currentTimeMillis());
			SystemTools.addObserver();
			while(hadRead < filesize) {
				if(ftpThread.getStatus() != Constant.THREAD_STATUS_RUNNABLE) {
					ftpThread.setStartTime(System.currentTimeMillis());
				}
				while(ftpThread.getStatus() == Constant.THREAD_STATUS_RUNNABLE && (read = dataInput.read(temp, 0, temp.length)) != -1) {
					dataOutput.write(temp, 0, read);
					hadRead += read;
					ftpThread.hadRead = hadRead;
					if(hadRead % 4096 == 0) {
						dataOutput.flush();
					}
					SystemTools.getCurrentSpeed(rowIndex, ftpThread.getEclipsedTime(), ftpThread.getStartTime(), hadRead);
					SystemTools.getPrice(rowIndex, hadRead, filesize);
					SwingUtilities.invokeLater(SystemTools.updateProgressBarRunnable);
				}
				if(hadRead == filesize) {
					dataOutput.flush();
					dataOutput.close();
					dataInput.close();
					ftpThread.setEclipsedTime(ftpThread.getEclipsedTime() + (System.currentTimeMillis() - ftpThread.getStartTime()));
					SystemTools.getFinalSpeed(rowIndex, ftpThread.getEclipsedTime(), filesize, Constant.DOWNLOAD_TYPE_RBT);
					FileUtil.rename(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME, savepath + File.separator + filename);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			try {
				dataOutput.flush();
				dataOutput.close();
				dataInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	/**
	 * 使用多线程、断点续传方式下载文件<br>
	 * 首先查找要保存的路径下有无缓存文件(以.wfml为后缀)<br>
	 * 		不存在	重新开始下载<br>
	 * 		存在		继续<br>
 	 * 重新开始下载<br>
 	 * 		读取配置文件要分多少个线程来下载文件<br>
	 * 		按照文件大小、线程数量来分配每个线程要下载的文件大小、文件名<br>
	 * 		开始断点续传下载<br>
	 * 继续下载<br>
	 * 		开始断点续传下载<br>
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransferByThread(final FTPThread ftpThread, final int rowIndex, final String filepath, final long filesize, final String savepath) {
		final String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
		//得到要创建的线程数量
		final String[] downloadSizeArray = SystemTools.getDownloadSizeArray(filesize, ftpThread.threadCount);
		//首先将所有缓存文件大小汇总为当前线程的已读大小
		for(int i = 0; i < ftpThread.threadCount; i ++) {
			File tempFile = new File(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME + i);
			ftpThread.hadRead += tempFile.length();
			
		}
		//然后按照线程数量启动新的下载线程
		for(int i = 0; i < ftpThread.threadCount; i ++) {
			final int index = i;
			Thread resumeThread = new Thread(){
				BufferedInputStream dataInput = null;
				BufferedOutputStream dataOutput = null;
				byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
				SocketFTPService socketFTPService = new SocketFTPService();
				//当前线程的缓存文件
				File tempFile = new File(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME + index);
				//当前线程要下载的文件大小
				String downsize = downloadSizeArray[index].split(":")[1];
				//当前线程要下载的文件起始点
				String skipsize = downloadSizeArray[index].split(":")[0];
				public void run() {
					socketFTPService.getFTPConnect(host, port);
					if(socketFTPService.doLoginFTP(user, pswd)) {
						try {
							socketFTPService.doFTPCommand("TYPE I");
							socketFTPService.dataSocket = socketFTPService.getDataSocket();
							//socketFTPService.dataSocket.setSoTimeout(1000);
							//设定文件指针(下载位置)
							socketFTPService.doFTPCommand("REST " + (Integer.parseInt(skipsize) + tempFile.length()));							
							socketFTPService.doFTPCommand("RETR " + filepath);
							dataInput = new BufferedInputStream(socketFTPService.dataSocket.getInputStream());
							dataOutput = new BufferedOutputStream(new FileOutputStream(tempFile, true));
							int read = 0;
							//将hadRead(已读文件大小)置为缓存文件的大小
							int hadRead = (int)tempFile.length();
							System.out.println("线程" + index + "已下载 " + hadRead);
							ftpThread.setStartTime(System.currentTimeMillis());
							SystemTools.addObserver();
							while(hadRead < Integer.parseInt(downsize)) {
								if(ftpThread.getStatus() != Constant.THREAD_STATUS_RUNNABLE) {
									ftpThread.setStartTime(System.currentTimeMillis());
								}
								while(ftpThread.getStatus() == Constant.THREAD_STATUS_RUNNABLE && (read = dataInput.read(temp)) != -1) {
									int temp_hadRead = hadRead;
									if((temp_hadRead += read) > Integer.parseInt(downsize)) {
										read = Integer.parseInt(downsize) - hadRead;
									}
									dataOutput.write(temp, 0, read);
									hadRead += read;
									if(hadRead % 4096 == 0) {
										dataOutput.flush();
									}
									ftpThread.ftpThreadLock.lock();
									try {
										ftpThread.hadRead += read;
									} finally {
										ftpThread.ftpThreadLock.unlock();
									}
									SystemTools.getCurrentSpeed(rowIndex, ftpThread.getEclipsedTime(), ftpThread.getStartTime(), ftpThread.hadRead);
									SystemTools.getPrice(rowIndex, ftpThread.hadRead, filesize);
									SwingUtilities.invokeLater(SystemTools.updateProgressBarRunnable);
								}
								if(hadRead == Integer.parseInt(downsize)) {
									System.out.println("第" + index + "个线程完成下载" + hadRead + ",完成下载" + ftpThread.hadRead);
									dataOutput.flush();
									dataOutput.close();
									dataInput.close();
									ftpThread.setCompleted(ftpThread.getCompleted() + 1);
									System.out.println(ftpThread.getCompleted());
								}
								if(ftpThread.getCompleted() == ftpThread.threadCount && ftpThread.hadRead == filesize) {
									ftpThread.setEclipsedTime(ftpThread.getEclipsedTime() + (System.currentTimeMillis() - ftpThread.getStartTime()));
									SystemTools.getFinalSpeed(rowIndex, ftpThread.getEclipsedTime(), filesize, Constant.DOWNLOAD_TYPE_RBT_THREAD);
									ftpThread.mergerTempFile(savepath + File.separator + filename, ftpThread.threadCount);
								}
							}
						} catch(IOException e) {
							e.printStackTrace();
							try {
								dataOutput.flush();
								dataOutput.close();
								dataInput.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			};
			resumeThread.start();
		}
	}
	/**
	 * 上传指定文件
	 * @param filepath
	 * @param savepath
	 */
	public void upload(String filepath, String savepath) {
		BufferedInputStream dataInput = null;
		BufferedOutputStream dataOutput = null;
		byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
		try {
			doFTPCommand("TYPE I");
			doFTPCommand("CWD " + savepath);
			dataSocket = getDataSocket();
			doFTPCommand("STOR " + filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.length()));
			
			dataInput = new BufferedInputStream(new FileInputStream(new File(filepath)));
			dataOutput = new BufferedOutputStream(dataSocket.getOutputStream());
			int read = 0;
			while ((read = dataInput.read(temp, 0, temp.length)) != -1) {
				dataOutput.write(temp, 0, read);
				dataOutput.flush();
			}
			dataOutput.flush();
			dataOutput.close();
			dataInput.close();
		} catch(IOException e) {
			e.printStackTrace();
			try {
				dataOutput.flush();
				dataOutput.close();
				dataInput.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	/**
	 * 执行FTP命令
	 * @param command
	 */
	public String doFTPCommand(String command) {
		System.out.println("Client:--->\n" + command + "\n");
		sendRequest(command);
		String responStr = readRespond();
		System.out.println("Server:<---\n" + responStr + "\n");
		return responStr;
	}
	/**
	 * 向服务端发送命令
	 * @param request
	 * @return
	 */
	public void sendRequest(String request){
		serverOutput.print(request + "\r\n");
		serverOutput.flush();
	}
	/**
	 * 得到服务器的响应
	 * @return
	 */
	public String getResponseStrings(){
		int ch;
		StringBuffer buffer = new StringBuffer();
		try{
			serverInput = ftpSocket.getInputStream();
			while((ch = serverInput.read()) != -1){
				buffer.append((char)ch);
			}
			serverInput.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	public String readRespond() {
		boolean isMultiLine = false;
		try { 
			if (ftpSocket.isClosed())
				throw new IOException("Server Disconnected!");
			String str = serverReader.readLine() + '\n';
			String result_str = str;
			if (str != null && str.length() >=4 && str.charAt(3) == '-') {
				isMultiLine = true;
				String respond_no = str.substring(0, 3);
				do {
					str = serverReader.readLine() + '\n';
					result_str = result_str +  str; 
				} while(!(str.length() >= 4 && str.substring(0, 3).equals(respond_no) && str.charAt(3) == ' '));
			} 
			// 读取服务器反馈以"XXX "开头的剩余部分。
			while (isMultiLine && serverReader.ready()) {
				str = serverReader.readLine();
				result_str = result_str +  str;
			}
			return result_str;
		} catch(Exception e) {
			return "";
		}
	}
	/**
	 * 得到ftp命令执行后的返回结果
	 * @param passiveFlag
	 * @return
	 */
	public String readDataRespond(boolean passiveFlag) {
		return "";
		
	}
	/**
	 * 得到传输数据的Socket
	 * <br>分两种情况
	 * <br>ftp链接分为两种模式:主动模式和被动模式
	 * <p>主动模式(使用PORT命令):
	 * <br>客户端链接服务端后,客户端打开某个端口供服务端链接以传输数据
	 * <br>所以在这种模式下要根据客户端打开的端口首先得到ServerSocket,然后得到传输数据的Socket
	 * <br>serverSocket = new ServerSocket(port);
	 * <br>dataSocket = serverSocket.accept();
	 * <p>被动模式(使用PASV命令):
	 * <br>客户端链接服务端后,服务端打开某个端口供客户端链接以传输数据
	 * <br>所以在这种模式下要根据服务端打开的端口和服务端IP直接得到传输数据的Socket
	 * <br>执行PASV命令,得到如下格式的信息
	 * <br>227 Entering Passive Mode (127,0,0,1,5,31)
	 * <br>然后解析此信息,根据IP和port
	 * <br>dataSocket = new Socket(address, port);
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private Socket getDataSocket() throws UnknownHostException, IOException {
		if(passiveFlag){
			String responseStr = doFTPCommand("PASV");
			int index = responseStr.indexOf("(");
			int lastindex = responseStr.lastIndexOf(")");
			String str = responseStr.substring(index + 1, lastindex);
			String address = 
				str.split(",")[0] + "." + 
				str.split(",")[1] + "." +
				str.split(",")[2] + "." +
				str.split(",")[3];
			int port = Integer.parseInt(str.split(",")[5]) + 256 * Integer.parseInt(str.split(",")[4]);
			dataSocket = new Socket(address, port);
		} else {
			serverSocket = new ServerSocket(123);
			dataSocket = serverSocket.accept();
		}
		return dataSocket;
	}
	/**
	 * 关闭FTP链接
	 *
	 */
	public void closeFTP() {
		try {
			ftpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
