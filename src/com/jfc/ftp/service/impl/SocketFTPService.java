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
	 * ���ݷ�������ַ���˿ڻ��ftp����
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
                System.out.println("��ȡ���ݳ�ʱ!");
            else
                System.out.println("���ӳ�ʱ");
		} catch(Exception e) {
			e.printStackTrace();
			LogTools.logger.error(e.getMessage());
		}
		return readRespond();
	}
	/**
	 * ִ�е�¼
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
	 * �õ�ָ��·���µ��ļ��б�
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
				// ���lineΪnull����˵��������EOF��
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
	 * ����ָ���ļ�<br>
	 * �����ļ��еĵݹ�����
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
	 * ����ָ���ļ�<br>
	 * ����������ͨ�ļ�������������ʾ
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
	 * ʹ�öϵ�������ʽ�����ļ�<br>
	 * ���Ȳ���Ҫ�����·�������޻����ļ�(��.wfmlΪ��׺)<br>
	 * 		������	���¿�ʼ����<br>
	 * 		����		����<br>
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
			//�趨�ļ�ָ��(����λ��)
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
	 * ʹ�ö��̡߳��ϵ�������ʽ�����ļ�<br>
	 * ���Ȳ���Ҫ�����·�������޻����ļ�(��.wfmlΪ��׺)<br>
	 * 		������	���¿�ʼ����<br>
	 * 		����		����<br>
 	 * ���¿�ʼ����<br>
 	 * 		��ȡ�����ļ�Ҫ�ֶ��ٸ��߳��������ļ�<br>
	 * 		�����ļ���С���߳�����������ÿ���߳�Ҫ���ص��ļ���С���ļ���<br>
	 * 		��ʼ�ϵ���������<br>
	 * ��������<br>
	 * 		��ʼ�ϵ���������<br>
	 * @param rowIndex
	 * @param filepath
	 * @param filesize
	 * @param savepath
	 */
	public void resumeBrokenTransferByThread(final FTPThread ftpThread, final int rowIndex, final String filepath, final long filesize, final String savepath) {
		final String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
		//�õ�Ҫ�������߳�����
		final String[] downloadSizeArray = SystemTools.getDownloadSizeArray(filesize, ftpThread.threadCount);
		//���Ƚ����л����ļ���С����Ϊ��ǰ�̵߳��Ѷ���С
		for(int i = 0; i < ftpThread.threadCount; i ++) {
			File tempFile = new File(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME + i);
			ftpThread.hadRead += tempFile.length();
			
		}
		//Ȼ�����߳����������µ������߳�
		for(int i = 0; i < ftpThread.threadCount; i ++) {
			final int index = i;
			Thread resumeThread = new Thread(){
				BufferedInputStream dataInput = null;
				BufferedOutputStream dataOutput = null;
				byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
				SocketFTPService socketFTPService = new SocketFTPService();
				//��ǰ�̵߳Ļ����ļ�
				File tempFile = new File(savepath + File.separator + filename + Constant.DOWNLOAD_TEMP_NAME + index);
				//��ǰ�߳�Ҫ���ص��ļ���С
				String downsize = downloadSizeArray[index].split(":")[1];
				//��ǰ�߳�Ҫ���ص��ļ���ʼ��
				String skipsize = downloadSizeArray[index].split(":")[0];
				public void run() {
					socketFTPService.getFTPConnect(host, port);
					if(socketFTPService.doLoginFTP(user, pswd)) {
						try {
							socketFTPService.doFTPCommand("TYPE I");
							socketFTPService.dataSocket = socketFTPService.getDataSocket();
							//socketFTPService.dataSocket.setSoTimeout(1000);
							//�趨�ļ�ָ��(����λ��)
							socketFTPService.doFTPCommand("REST " + (Integer.parseInt(skipsize) + tempFile.length()));							
							socketFTPService.doFTPCommand("RETR " + filepath);
							dataInput = new BufferedInputStream(socketFTPService.dataSocket.getInputStream());
							dataOutput = new BufferedOutputStream(new FileOutputStream(tempFile, true));
							int read = 0;
							//��hadRead(�Ѷ��ļ���С)��Ϊ�����ļ��Ĵ�С
							int hadRead = (int)tempFile.length();
							System.out.println("�߳�" + index + "������ " + hadRead);
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
									System.out.println("��" + index + "���߳��������" + hadRead + ",�������" + ftpThread.hadRead);
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
	 * �ϴ�ָ���ļ�
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
	 * ִ��FTP����
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
	 * �����˷�������
	 * @param request
	 * @return
	 */
	public void sendRequest(String request){
		serverOutput.print(request + "\r\n");
		serverOutput.flush();
	}
	/**
	 * �õ�����������Ӧ
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
			// ��ȡ������������"XXX "��ͷ��ʣ�ಿ�֡�
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
	 * �õ�ftp����ִ�к�ķ��ؽ��
	 * @param passiveFlag
	 * @return
	 */
	public String readDataRespond(boolean passiveFlag) {
		return "";
		
	}
	/**
	 * �õ��������ݵ�Socket
	 * <br>���������
	 * <br>ftp���ӷ�Ϊ����ģʽ:����ģʽ�ͱ���ģʽ
	 * <p>����ģʽ(ʹ��PORT����):
	 * <br>�ͻ������ӷ���˺�,�ͻ��˴�ĳ���˿ڹ�����������Դ�������
	 * <br>����������ģʽ��Ҫ���ݿͻ��˴򿪵Ķ˿����ȵõ�ServerSocket,Ȼ��õ��������ݵ�Socket
	 * <br>serverSocket = new ServerSocket(port);
	 * <br>dataSocket = serverSocket.accept();
	 * <p>����ģʽ(ʹ��PASV����):
	 * <br>�ͻ������ӷ���˺�,����˴�ĳ���˿ڹ��ͻ��������Դ�������
	 * <br>����������ģʽ��Ҫ���ݷ���˴򿪵Ķ˿ںͷ����IPֱ�ӵõ��������ݵ�Socket
	 * <br>ִ��PASV����,�õ����¸�ʽ����Ϣ
	 * <br>227 Entering Passive Mode (127,0,0,1,5,31)
	 * <br>Ȼ���������Ϣ,����IP��port
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
	 * �ر�FTP����
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
