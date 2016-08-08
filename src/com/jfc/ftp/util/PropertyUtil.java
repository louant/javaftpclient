package com.jfc.ftp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * ���ڻ�ȡ��Դ��Ϣ��������Ϣ
 * @author SavageGarden
 *
 */
public class PropertyUtil {
	public static InputStream propertyStream;
	public static Properties propertyProp;
	
	public static InputStream resourceStream;
	public static Properties resourceProp;
	/**
	 * �������ļ��в����������������쳣���򷵻�Ĭ��ֵ
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static String getProperty(String key, String defaultvalue){
		try{
			if(propertyStream == null) {
				propertyStream = PropertyUtil.class.getResourceAsStream(Constant.PROPFILENAME);
			}
			if(propertyProp == null) {
				propertyProp = new Properties();
				propertyProp.load(propertyStream);
			}
			return propertyProp.getProperty(key).length() > 0?propertyProp.getProperty(key):defaultvalue;
		}catch(Exception e){
			e.printStackTrace();
			return defaultvalue;
		}
	}
	/**
	 * ����ϵͳ����������Ӧ����Դ�ļ�
	 * @return
	 */
	public static String getResourcesFileName() {
		String language = getProperty("system.language","");
		if(language.length() == 0) {
			Properties prop = System.getProperties();
			language = prop.getProperty("user.language");
		}
		if(language.indexOf("zh") >= 0) {
			return Constant.RESOURCES_FILENAME_ZH;
		}else {
			return Constant.RESOURCES_FILENAME_EN;
		}
	}
	/**
	 * ����Դ�ļ��ж�ȡ������緢���쳣���򷵻�Ĭ��ֵ
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static String getResources(String key, String defaultvalue) {
		try{
			if(resourceStream == null) {
				resourceStream = PropertyUtil.class.getResourceAsStream(getResourcesFileName());
			}
			if(resourceProp == null) {
				resourceProp = new Properties();
				resourceProp.load(resourceStream);
			}
			return resourceProp.getProperty(key).length() > 0?resourceProp.getProperty(key):defaultvalue;
		}catch(Exception e){
			e.printStackTrace();
			return defaultvalue;
		}
	}
	/**
	 * ����Դ�ļ��ж�ȡ������緢���쳣���򷵻ؿ��ַ���
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public static String getResources(String key) {
		return getResources(key, "");
	}
}
