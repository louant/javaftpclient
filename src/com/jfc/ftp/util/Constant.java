package com.jfc.ftp.util;

/**
 * ����ϵͳ���õ��ĳ���
 * @author SavageGarden
 *
 */
public class Constant {

	/**
	 * �����ļ�·��
	 */
	public static String PROPFILENAME = "../config/jfc_ftp.properties";
	/**
	 * ������Դ�ļ�·��
	 */
	public static String RESOURCES_FILENAME_ZH = "../config/resources_zh.properties";
	/**
	 * Ӣ����Դ�ļ�·��
	 */
	public static String RESOURCES_FILENAME_EN = "../config/resources_en.properties";
	/**
	 * �ļ����ͳ���	Ӳ��
	 */
	public static String FILE_TYPE_DISK = "Disk";
	/**
	 * �ļ����ͳ���	�ļ���
	 */
	public static String FILE_TYPE_DIRE = "Dire";
	/**
	 * �ļ����ͳ���	�ļ�
	 */
	public static String FILE_TYPE_FILE = "File";
	
	/**
	 * Win�����µĸ�ϵͳ����
	 */
	public static String WINDOWS_ROOT_NAME = "�ҵĵ���";
	/**
	 * Lin�����µĸ�ϵͳ����
	 */
	public static String LINUX_ROOT_NAME = "/";
	
	/**
	 * ʹ�öϵ���������ʱ�Ļ����ļ���׺��
	 */
	public static String DOWNLOAD_TEMP_NAME = ".wfml";
	/**
	 * ʹ�öϵ���������ʱ�������ļ���׺��
	 */
	public static String DOWNLOAD_PROP_NAME = ".mlwf";
	/**
	 * �������ֽ������С
	 */
	public static int TEMP_BYTE_LENGTH = 1024;
	
	/**
	 * ��ű���վ����Ϣ��xml�ļ���·��
	 */
	public static String XMLPATH = "F:\\eclipse-lomboz-3.2\\workspace\\javaftpclient\\xml";
	/**
	 * ����վ����Ϣ��xml�ļ����ļ���
	 */
	public static String FTP_SITE_LIST_XML_FILE_NAME = "ftpsitelist.xml";
	/**
	 * ����������Ϣ��xml�ļ����ļ���
	 */
	public static String DOWNLOAD_XML_FILE_NAME = "downloadlist.xml";
	/**
	 * ����վ����Ϣ��xml�ļ���element��
	 */
	public static String ELEMENT_FTP_SITE = "ftpsite";
	/**
	 * ����������Ϣ��xml�ļ���element��
	 */
	public static String ELEMENT_DOWNLOAD = "download";
	/**
	 * ���̶߳ϵ�������ʽ�����ļ�ʱ�����߳�����������������
	 */
	public static String RESUME_THREAD_COUNT_PROPNAME = "thread_count";
	/**
	 * ���̶߳ϵ�������ʽ�����ļ�ʱ�����̵߳�Ĭ������
	 */
	public static String RESUME_THREAD_COUNT_DEFAULT = "4";
	/**
	 * �߳�״̬	��δ����
	 */
	public static int THREAD_STATUS_NEW = 0;
	/**
	 * �߳�״̬	����ִ��
	 */
	public static int THREAD_STATUS_RUNNABLE = 1;
	/**
	 * �߳�״̬	���ڵȴ�
	 */
	public static int THREAD_STATUS_WAITING = 2;
	/**
	 * �߳�״̬	�Ѿ��˳�
	 */
	public static int THREAD_STATUS_STOP = 3;
	/**
	 * ���ط�ʽ	��ͨ
	 */
	public static int DOWNLOAD_TYPE_COMMON = 0;
	/**
	 * ���ط�ʽ	�ϵ�����
	 */
	public static int DOWNLOAD_TYPE_RBT = 1;
	/**
	 * ���ط�ʽ	���̶߳ϵ�����
	 */
	public static int DOWNLOAD_TYPE_RBT_THREAD = 2;
	
	
	
}
