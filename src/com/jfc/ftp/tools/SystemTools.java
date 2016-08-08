package com.jfc.ftp.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.model.download.DownloadModel;
import com.jfc.ftp.model.ftpsite.FTPSiteModel;
import com.jfc.ftp.tools.observer.progressbar.ProgressBarObservable;
import com.jfc.ftp.tools.observer.progressbar.ProgressBarObserver;
import com.jfc.ftp.tools.observer.speed.SpeedObservable;
import com.jfc.ftp.tools.observer.speed.SpeedObserver;
import com.jfc.ftp.util.Constant;
import com.jfc.ftp.util.JDomUtil;

/**
 * ϵͳ��ع���
 * @author SavageGarden
 *
 */
public class SystemTools {

	/**
	 * ��������
	 */
	public static FTPMainFrame mainFrame;
	public static Thread downloadThread;
	/**
	 * ������ʾ���ؽ���
	 */
	public static String showValue = "0:0";
	/**
	 * ������ʾ��ǰ�����ٶ�
	 */
	public static String tempStr = "0:0b/s";
	/**
	 * ������ʾ���������ٶ�
	 */
	public static String speedStr = "0:0b/s";
	/**
	 * �۲���󣬽�����
	 */
	public static ProgressBarObservable progressBarObservable = new ProgressBarObservable();
	/**
	 * �۲�����ٶ�
	 */
	public static SpeedObservable speedObservable = new SpeedObservable();
	/**
	 * ���������̵߳Ľ��Ⱥ��ٶȵ��߳�
	 */
	public static Runnable updateProgressBarRunnable = new Runnable() {
		public void run() {
			progressBarObservable.setShowValue(SystemTools.showValue);
			speedObservable.setSpeedStr(SystemTools.speedStr);
		}
	};
	/**
	 * ���ڴ洢û��������ɵ��߳�
	 */
	public static ArrayList downloadThreadList = new ArrayList();
	/**
	 * ���ڴ洢�Ѿ�������ɵļ�¼
	 */
	public static ArrayList downloadCompletedList = new ArrayList();
	/**
	 * �õ���ϵͳ����
	 * @return
	 */
	public static String getRootName() {
		Properties prop = System.getProperties();
		if(prop.getProperty("os.name").indexOf("Windows") >= 0) {
			return Constant.WINDOWS_ROOT_NAME;
		}else {
			return Constant.LINUX_ROOT_NAME;
		}
	}
	/**
	 * ��ȡʹ�öϵ�������ʽ�����ļ�ʱ��������Ϣ
	 * @param file
	 * @return
	 */
	public static String getDownloadProp(File file) {
		StringBuffer propBuffer = new StringBuffer();
		try {
			BufferedReader reader = null;
			reader = new BufferedReader(new FileReader(file));
			
			String tempStr = null;
			while((tempStr = reader.readLine()) != null) {
				propBuffer.append(tempStr + ";");
			}
		} catch(Exception e) {
			propBuffer = new StringBuffer();
		}
		return propBuffer.toString();
	}
	/**
	 * ���ϵ�������Ҫ��������Ϣд�������ļ�
	 * @param propStr
	 * @param proppath
	 */
	public static void setDownloadProp(String propStr, String proppath) {
		try {
			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(new File(proppath)));
			String[] propArray = propStr.split(";");
			for(String str : propArray) {
				writer.write(str);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch(Exception e) {
			
		}
	}
	public static void addObserver() {
		progressBarObservable.addObserver(new ProgressBarObserver());
		speedObservable.addObserver(new SpeedObserver());
	}
	/**
	 * ���½�����ֵ
	 * @param price
	 */
	public static void updateProgressBar(String showValue) {
		mainFrame.statusTableModel.setValueAt(Integer.parseInt(showValue.split(":")[1]), Integer.parseInt(showValue.split(":")[0]), 1);
	}
	/**
	 * ���¼�ʱ�ٶ�
	 * @param speedStr
	 */
	public static void updateCurrentSpeed(String speedStr){
		mainFrame.statusTableModel.setValueAt(speedStr.split(":")[1], Integer.parseInt(speedStr.split(":")[0]), 3);
	}
	/**
	 * ��ȡָ������ɰٷֱ�
	 * @param rowIndex
	 * @param hadRead
	 * @param filesize
	 */
	public static void getPrice(int rowIndex, long hadRead, long filesize) {
		showValue = rowIndex + ":" + ((hadRead * 100) / filesize);
	}
	/**
	 * ���ָ���м�ʱ�����ٶ�
	 * @param rowIndex
	 * @param eclipsedTime
	 * @param startTime
	 * @param hadRead
	 * @return
	 */
	public static void getCurrentSpeed(int rowIndex, long eclipsedTime, long startTime, long hadRead) {
		eclipsedTime += System.currentTimeMillis() - startTime;
		long speed = 0;
		if(eclipsedTime / 1000 >= 1) {
			speed = (long)hadRead * 1000 / (long) eclipsedTime;
			if (speed < 1000) {
				tempStr = Long.toString(speed) + "B/s";
			} else if (speed < 1000000) {
				tempStr = Long.toString(speed/1000) + '.' + Long.toString( (speed%1000)/10 ) + "KB/s";
			} else {
				tempStr = Long.toString(speed/1000000) + '.' + Long.toString( (speed%1000000) /10000) + "MB/s";
			}
			speedStr = rowIndex + ":" + tempStr;
		}
	}
	/**
	 * ���ָ���м�ʱ�����ٶ�
	 * @param rowIndex
	 * @param startTime
	 * @param hadRead
	 * @return
	 */
	public static void getCurrentSpeed(int rowIndex, long startTime, long hadRead) {
		getCurrentSpeed(rowIndex, 0, startTime, hadRead);
	}
	/**
	 * ��ȡָ�������������ٶ�
	 * @param rowIndex
	 * @param eclipsedTime
	 * @param startTime
	 * @param endTime
	 * @param filesize
	 */
	public static void getFinalSpeed(int rowIndex, long time, long filesize, int downloadType) {
		long eclipsedTime = 0;
		if(downloadType == Constant.DOWNLOAD_TYPE_COMMON) {
			//�������ͨ����,�����time��Ϊ��ʼʱ��
			eclipsedTime = System.currentTimeMillis() - time;
		} else {
			//���������ͨ����,�����time��Ϊ�Ѻ�ʱ��
			eclipsedTime = time;
		}
		long speed = (long)filesize * 1000 / (long) eclipsedTime;
		if (speed < 1000) {
			tempStr = Long.toString(speed) + "B/s";
		} else if (speed < 1000000) {
			tempStr = Long.toString(speed/1000) + '.' + Long.toString( (speed%1000)/10 ) + "KB/s";
		} else {
			tempStr = Long.toString(speed/1000000) + '.' + Long.toString( (speed%1000000) /10000) + "MB/s";
		}
		tempStr += "\t\t" + "������ʱ" + getTimeCount(eclipsedTime);
		speedStr = rowIndex + ":" + tempStr;
	}
	/**
	 * ���ݺ������õ����ĵ�ʱ����Ϣ
	 * @param eclipsedTime
	 * @return
	 */
	public static String getTimeCount(long eclipsedTime) {
		String timeCount = "";
		if(eclipsedTime < 1000) {
			timeCount = eclipsedTime + "����";
		}else if(eclipsedTime / 1000 < 60) {
			timeCount = eclipsedTime / 1000 + "��";
		} else if (eclipsedTime / 1000 < 3600){
			timeCount = eclipsedTime % 6000 + "��" + (eclipsedTime - (eclipsedTime % 6000) * 6000) + "��";
		} 
		return timeCount;
	}
	/**
	 * ��xml�ļ��ж�ȡ�ѱ����վ����Ϣ
	 *
	 */
	public static ArrayList getFTPSiteList() {
		String xmlfilepath = Constant.XMLPATH + File.separator + Constant.FTP_SITE_LIST_XML_FILE_NAME;
		ArrayList ftpSiteList = new ArrayList();
		try {
			List list = JDomUtil.getRootElement(xmlfilepath).getChildren(Constant.ELEMENT_FTP_SITE);
			for(Object obj : list) {
				Element xmlFTPSite = (Element)obj;
				FTPSiteModel ftpSite = new FTPSiteModel();
				ftpSite.setName(xmlFTPSite.getChildText("name"));
				ftpSite.setHost(xmlFTPSite.getChildText("host"));
				ftpSite.setPort(xmlFTPSite.getChildText("port"));
				ftpSite.setUser(xmlFTPSite.getChildText("user"));
				ftpSite.setPswd(xmlFTPSite.getChildText("pswd"));
				ftpSiteList.add(ftpSite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ftpSiteList;
	}
	/**
	 * ��վ����Ϣ��ӵ�xml�ļ�
	 * @param ftpSite
	 */
	public static void addFTPSite(FTPSiteModel ftpSite) {
		Element xmlFTPSite = new Element(Constant.ELEMENT_FTP_SITE);
		
		xmlFTPSite.addContent(new Element("name").addContent(ftpSite.getName()));
		xmlFTPSite.addContent(new Element("host").addContent(ftpSite.getHost()));
		xmlFTPSite.addContent(new Element("port").addContent(ftpSite.getPort()));
		xmlFTPSite.addContent(new Element("user").addContent(ftpSite.getUser()));
		xmlFTPSite.addContent(new Element("pswd").addContent(ftpSite.getPswd()));
		
		String xmlfilepath = Constant.XMLPATH + File.separator + Constant.FTP_SITE_LIST_XML_FILE_NAME;
		try {
			JDomUtil.getRootElement(xmlfilepath);
			JDomUtil.addElementToFile(xmlFTPSite, xmlfilepath, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * ����Ҫ���ص��ļ���С���߳������õ�ÿ���߳�Ӧ�����ص��ļ���С<br>
	 * ����ȡ��<br>
	 * Ȼ��ֱ�õ�ÿ���̴߳��ĸ��ļ�ָ�뿪ʼ�����Լ�Ҫ���صĴ�С�Ƕ���
	 * @param filesize
	 * @param threadCount
	 * @return
	 */
	public static String[] getDownloadSizeArray(long filesize, int threadCount) {
		int mod = (int)filesize % threadCount;
		String[] array = new String[threadCount];
		array[0] = "0:" + (filesize - mod)/threadCount;
		for(int i = 1; i < threadCount; i ++) {
			if (i < threadCount - 1) {
				array[i] = ((filesize - mod)/threadCount)*i + ":" + (filesize - mod)/threadCount;
			}else {
				array[i] = ((filesize - mod)/threadCount)*i + ":" + (filesize-((filesize - mod)/threadCount)*i);
			}
		}
		return array;
	}
	/**
	 * ��xml�ļ��ж�ȡָ��վ����������ļ���Ϣ<br>
	 * ������������Ϣ������Ӧ�������߳�
	 * @param host
	 */
	public static ArrayList getDownloadList(String host) {
		String xmlfilepath = Constant.XMLPATH + File.separator + Constant.DOWNLOAD_XML_FILE_NAME;
		ArrayList downloadList = new ArrayList();
		try {
			List list = JDomUtil.getRootElement(xmlfilepath).getChildren(Constant.ELEMENT_DOWNLOAD);
			for(Object obj : list) {
				Element xmlDownload = (Element)obj;
				if(xmlDownload.getChildText("ftpsite").equals(host)) {
					final DownloadModel download = new DownloadModel();
					download.setFtpsite(xmlDownload.getChildText("ftpsite"));
					download.setFilepath(xmlDownload.getChildText("filepath"));
					download.setSavepath(xmlDownload.getChildText("savepath"));
					download.setRowIndex(Integer.parseInt(xmlDownload.getChildText("rowIndex")));
					download.setFilesize(Integer.parseInt(xmlDownload.getChildText("filesize")));
					download.setHadRead(Integer.parseInt(xmlDownload.getChildText("hadRead")));
					download.setEclipsedTime(Integer.parseInt(xmlDownload.getChildText("eclipsedTime")));
					download.setThreadCount(Integer.parseInt(xmlDownload.getChildText("threadCount")));
					download.setDownloadType(Integer.parseInt(xmlDownload.getChildText("downloadType")));
					if(download.getDownloadType() != Constant.DOWNLOAD_TYPE_COMMON && download.getHadRead() != download.getFilesize()) {
						downloadList.add(download);
						Thread thread = new Thread() {
							public void run() {
								FTPThread ftpThread = new FTPThread(download.getDownloadType(), downloadThreadList.size(), download.getFilepath(), download.getFilesize(), download.getSavepath(), Constant.THREAD_STATUS_WAITING);
								downloadThreadList.add(ftpThread);
							}
						};
						thread.start();
					} else {
						downloadCompletedList.add(download);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return downloadList;
	}
	/**
	 * ��������Ϣ��ӵ�xml�ļ�
	 * @param downloadList
	 */
	public static void addDownloadMessage(ArrayList downloadList) {
		String xmlfilepath = Constant.XMLPATH + File.separator + Constant.DOWNLOAD_XML_FILE_NAME;
		Element rootElement = null;
		try {
			rootElement = JDomUtil.getRootElement(xmlfilepath);
			rootElement.getChildren().removeAll(rootElement.getChildren(Constant.ELEMENT_DOWNLOAD));
			/*while(rootElement.hasChildren()) {
				rootElement.removeChildren();
			}*/
		} catch(Exception e) {
			e.printStackTrace();
		}
		for(Object object : downloadList) {
			DownloadModel download = (DownloadModel)object;
			Element xmlDownload = new Element(Constant.ELEMENT_DOWNLOAD);
			xmlDownload.addContent(new Element("ftpsite").addContent(download.getFtpsite()));
			xmlDownload.addContent(new Element("filepath").addContent(download.getFilepath()));
			xmlDownload.addContent(new Element("savepath").addContent(download.getSavepath()));
			xmlDownload.addContent(new Element("rowIndex").addContent(download.getRowIndex() + ""));
			xmlDownload.addContent(new Element("filesize").addContent(download.getFilesize() + ""));
			xmlDownload.addContent(new Element("hadRead").addContent(download.getHadRead() + ""));
			xmlDownload.addContent(new Element("eclipsedTime").addContent(download.getEclipsedTime() + ""));
			xmlDownload.addContent(new Element("threadCount").addContent(download.getThreadCount() + ""));
			xmlDownload.addContent(new Element("downloadType").addContent(download.getDownloadType() + ""));
			rootElement.addContent(xmlDownload);
		}
		JDomUtil.outXMLToFile(JDomUtil.doc, new File(xmlfilepath), "UTF-8");
	}
}
