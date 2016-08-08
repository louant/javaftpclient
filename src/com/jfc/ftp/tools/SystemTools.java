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
 * 系统相关工具
 * @author SavageGarden
 *
 */
public class SystemTools {

	/**
	 * 主面板对象
	 */
	public static FTPMainFrame mainFrame;
	public static Thread downloadThread;
	/**
	 * 用于显示下载进度
	 */
	public static String showValue = "0:0";
	/**
	 * 用于显示当前下载速度
	 */
	public static String tempStr = "0:0b/s";
	/**
	 * 用于显示最终下载速度
	 */
	public static String speedStr = "0:0b/s";
	/**
	 * 观察对象，进度条
	 */
	public static ProgressBarObservable progressBarObservable = new ProgressBarObservable();
	/**
	 * 观察对象，速度
	 */
	public static SpeedObservable speedObservable = new SpeedObservable();
	/**
	 * 更新下载线程的进度和速度的线程
	 */
	public static Runnable updateProgressBarRunnable = new Runnable() {
		public void run() {
			progressBarObservable.setShowValue(SystemTools.showValue);
			speedObservable.setSpeedStr(SystemTools.speedStr);
		}
	};
	/**
	 * 用于存储没有下载完成的线程
	 */
	public static ArrayList downloadThreadList = new ArrayList();
	/**
	 * 用于存储已经下载完成的记录
	 */
	public static ArrayList downloadCompletedList = new ArrayList();
	/**
	 * 得到根系统名称
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
	 * 获取使用断点续传方式下载文件时的配置信息
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
	 * 将断点续传需要的配置信息写入配置文件
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
	 * 更新进度条值
	 * @param price
	 */
	public static void updateProgressBar(String showValue) {
		mainFrame.statusTableModel.setValueAt(Integer.parseInt(showValue.split(":")[1]), Integer.parseInt(showValue.split(":")[0]), 1);
	}
	/**
	 * 更新即时速度
	 * @param speedStr
	 */
	public static void updateCurrentSpeed(String speedStr){
		mainFrame.statusTableModel.setValueAt(speedStr.split(":")[1], Integer.parseInt(speedStr.split(":")[0]), 3);
	}
	/**
	 * 获取指定行完成百分比
	 * @param rowIndex
	 * @param hadRead
	 * @param filesize
	 */
	public static void getPrice(int rowIndex, long hadRead, long filesize) {
		showValue = rowIndex + ":" + ((hadRead * 100) / filesize);
	}
	/**
	 * 获得指定行即时下载速度
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
	 * 获得指定行即时下载速度
	 * @param rowIndex
	 * @param startTime
	 * @param hadRead
	 * @return
	 */
	public static void getCurrentSpeed(int rowIndex, long startTime, long hadRead) {
		getCurrentSpeed(rowIndex, 0, startTime, hadRead);
	}
	/**
	 * 获取指定行最终下载速度
	 * @param rowIndex
	 * @param eclipsedTime
	 * @param startTime
	 * @param endTime
	 * @param filesize
	 */
	public static void getFinalSpeed(int rowIndex, long time, long filesize, int downloadType) {
		long eclipsedTime = 0;
		if(downloadType == Constant.DOWNLOAD_TYPE_COMMON) {
			//如果是普通下载,这里的time视为开始时间
			eclipsedTime = System.currentTimeMillis() - time;
		} else {
			//如果不是普通下载,这里的time视为已耗时间
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
		tempStr += "\t\t" + "共计用时" + getTimeCount(eclipsedTime);
		speedStr = rowIndex + ":" + tempStr;
	}
	/**
	 * 根据毫秒数得到中文的时间信息
	 * @param eclipsedTime
	 * @return
	 */
	public static String getTimeCount(long eclipsedTime) {
		String timeCount = "";
		if(eclipsedTime < 1000) {
			timeCount = eclipsedTime + "豪秒";
		}else if(eclipsedTime / 1000 < 60) {
			timeCount = eclipsedTime / 1000 + "秒";
		} else if (eclipsedTime / 1000 < 3600){
			timeCount = eclipsedTime % 6000 + "分" + (eclipsedTime - (eclipsedTime % 6000) * 6000) + "秒";
		} 
		return timeCount;
	}
	/**
	 * 从xml文件中读取已保存的站点信息
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
	 * 将站点信息添加到xml文件
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
	 * 根据要下载的文件大小、线程数量得到每个线程应该下载的文件大小<br>
	 * 首先取余<br>
	 * 然后分别得到每个线程从哪个文件指针开始下载以及要下载的大小是多少
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
	 * 从xml文件中读取指定站点的已下载文件信息<br>
	 * 并根据下载信息启动相应的下载线程
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
	 * 将下载信息添加到xml文件
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
