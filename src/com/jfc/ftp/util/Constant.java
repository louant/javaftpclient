package com.jfc.ftp.util;

/**
 * 定义系统中用到的常量
 * @author SavageGarden
 *
 */
public class Constant {

	/**
	 * 配置文件路径
	 */
	public static String PROPFILENAME = "../config/jfc_ftp.properties";
	/**
	 * 中文资源文件路径
	 */
	public static String RESOURCES_FILENAME_ZH = "../config/resources_zh.properties";
	/**
	 * 英文资源文件路径
	 */
	public static String RESOURCES_FILENAME_EN = "../config/resources_en.properties";
	/**
	 * 文件类型常量	硬盘
	 */
	public static String FILE_TYPE_DISK = "Disk";
	/**
	 * 文件类型常量	文件夹
	 */
	public static String FILE_TYPE_DIRE = "Dire";
	/**
	 * 文件类型常量	文件
	 */
	public static String FILE_TYPE_FILE = "File";
	
	/**
	 * Win环境下的根系统名称
	 */
	public static String WINDOWS_ROOT_NAME = "我的电脑";
	/**
	 * Lin环境下的根系统名称
	 */
	public static String LINUX_ROOT_NAME = "/";
	
	/**
	 * 使用断点续传下载时的缓存文件后缀名
	 */
	public static String DOWNLOAD_TEMP_NAME = ".wfml";
	/**
	 * 使用断点续传下载时的配置文件后缀名
	 */
	public static String DOWNLOAD_PROP_NAME = ".mlwf";
	/**
	 * 缓冲区字节数组大小
	 */
	public static int TEMP_BYTE_LENGTH = 1024;
	
	/**
	 * 存放保存站点信息的xml文件的路径
	 */
	public static String XMLPATH = "F:\\eclipse-lomboz-3.2\\workspace\\javaftpclient\\xml";
	/**
	 * 保存站点信息的xml文件的文件名
	 */
	public static String FTP_SITE_LIST_XML_FILE_NAME = "ftpsitelist.xml";
	/**
	 * 保存下载信息的xml文件的文件名
	 */
	public static String DOWNLOAD_XML_FILE_NAME = "downloadlist.xml";
	/**
	 * 保存站点信息的xml文件的element名
	 */
	public static String ELEMENT_FTP_SITE = "ftpsite";
	/**
	 * 保存下载信息的xml文件的element名
	 */
	public static String ELEMENT_DOWNLOAD = "download";
	/**
	 * 多线程断点续传方式下载文件时建立线程数量的配置项名称
	 */
	public static String RESUME_THREAD_COUNT_PROPNAME = "thread_count";
	/**
	 * 多线程断点续传方式下载文件时建立线程的默认数量
	 */
	public static String RESUME_THREAD_COUNT_DEFAULT = "4";
	/**
	 * 线程状态	尚未启动
	 */
	public static int THREAD_STATUS_NEW = 0;
	/**
	 * 线程状态	正在执行
	 */
	public static int THREAD_STATUS_RUNNABLE = 1;
	/**
	 * 线程状态	正在等待
	 */
	public static int THREAD_STATUS_WAITING = 2;
	/**
	 * 线程状态	已经退出
	 */
	public static int THREAD_STATUS_STOP = 3;
	/**
	 * 下载方式	普通
	 */
	public static int DOWNLOAD_TYPE_COMMON = 0;
	/**
	 * 下载方式	断点续传
	 */
	public static int DOWNLOAD_TYPE_RBT = 1;
	/**
	 * 下载方式	多线程断点续传
	 */
	public static int DOWNLOAD_TYPE_RBT_THREAD = 2;
	
	
	
}
