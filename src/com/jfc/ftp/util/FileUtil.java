package com.jfc.ftp.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.jfc.ftp.model.file.FileModel;

/**
 * ���ڴ����ļ����
 * @author SavageGarden
 *
 */
public class FileUtil {

	/**
	 * �õ���ǰ�û�����Ŀ¼
	 * @return
	 */
	public static String getUserHome() {
		Properties prop = System.getProperties();
		return prop.getProperty("user.home");
	}
	/**
	 * �õ����õĸ�ϵͳ�ļ�
	 * @return
	 */
	public static File[] getListRoots() {
		return File.listRoots();
	}
	/**
	 * ����ϵͳ�ļ�תΪFileModel�б�
	 * @return
	 */
	public static ArrayList getClientRootFileList() {
		ArrayList fileList = new ArrayList();
		for(File file : getListRoots()){
			FileModel fileModel = new FileModel();
			fileModel.setFileName(file.getPath().substring(0, file.getPath().lastIndexOf("\\")));
			fileModel.setFileSize(file.length() + "");
			fileModel.setFileImage("images/disk.png");
			fileModel.setFileType(Constant.FILE_TYPE_DISK);
			fileList.add(fileModel);
		}
		return fileList;
	}
	/**
	 * �õ��ͻ���ָ��·���µ��ļ��б�
	 * @param filepath
	 * @return
	 */
	public static ArrayList getClientFileList(String filepath){
		File parent = new File(filepath);
		File [] files = parent.listFiles();
		ArrayList fileList = new ArrayList();
		
		for(File file : files){
			FileModel fileModel = new FileModel();
			fileModel.setFileName(file.getName());
			fileModel.setFileSize(file.length() + "");
			if (file.isDirectory()){
				fileModel.setFileImage("images/folder.png");
				fileModel.setFileType(Constant.FILE_TYPE_DIRE);
			}else{
				fileModel.setFileImage("images/file.png");
				fileModel.setFileType(Constant.FILE_TYPE_FILE);
			}
			long millis = file.lastModified();
			if (millis != 0) {
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
				fileModel.setFileDate(f.format(new Date(millis)));
			} 
			fileList.add(fileModel);
		}
		return fileList;
	}
	/**
	 * �õ������ָ��·���µ��ļ��б�
	 * <br>����dataStrӦ�������¸�ʽ���ַ���
	 * <p>
	 * <br>drw-rw-rw-   1 user     group           0 Oct 30 11:40 ..
	 * <br>-rw-rw-rw-   1 user     group        7303 Aug 10 11:42 cmpylist.xml
	 * <br>d			��ʾĿ¼	
	 * <br>-			��ʾ�ļ�
	 * <br>rw-rw-rw-	��ʾȨ������
	 * @param dataStr
	 * @return
	 */
	public static ArrayList getServerFileList(String dataStr) {
		int i = 0, j;
		ArrayList fileList = new ArrayList();
		while(i < dataStr.length()) {
			if((j = i + dataStr.substring(i).indexOf("\n")) == -1) {
				break;
			}
			String fileMessage = dataStr.substring(i, j);
			if(fileMessage.length() == 0){
				break;
			}
			//���տո�fileMessage��Ϊ������ȡ�����Ϣ
			if(!fileMessage.split("\\s{1,}")[8].equals(".") && !fileMessage.split("\\s{1,}")[8].equals("..")){
				FileModel fileModel = new FileModel();
				fileModel.setFileSize(fileMessage.split("\\s{1,}")[4]);
				fileModel.setFileName(fileMessage.split("\\s{1,}")[8]);
				if(fileMessage.split("\\s{1,}")[0].charAt(0) == 'd') {
					fileModel.setFileImage("images/folder.png");
					fileModel.setFileType(Constant.FILE_TYPE_DIRE);
				}else{
					fileModel.setFileImage("images/file.png");
					fileModel.setFileType(Constant.FILE_TYPE_FILE);
				}
				String date = 
					fileMessage.split("\\s{1,}")[5] +
					fileMessage.split("\\s{1,}")[6] +
					fileMessage.split("\\s{1,}")[7];
				fileModel.setFileDate(date);
				fileList.add(fileModel);
			}
			i = j + 1;
		}
		return fileList;
	}
	/**
	 * �жϵ�ǰ·���Ƿ����̷�
	 * @param filepath
	 * @return
	 */
	public static boolean isDisk(String filepath) {
		for(File file : getListRoots()) {
			if(file.getPath().equals(filepath + File.separator)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * ����Ŀ¼
	 * @param filepath
	 */
	public static void createFolder(String filepath) {
		File file = new File(filepath);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	/**
	 * �ļ�ɾ��
	 * <br>������ļ���ֱ��ɾ��
	 * <br>������ļ�����ݹ�ɾ��
	 * @param filepath
	 */
	public static void deleteDir(String filepath) {
		File rootfile = new File(filepath);
		if(rootfile.isDirectory()) {
			File[] files = rootfile.listFiles();
			for(File file : files) {
				if(file.isDirectory()) {
					deleteDir(file.getPath());
				} else {
					file.delete();
				}
			}
		}
		rootfile.delete();
	}
	/**
	 * �޸��ļ���
	 * @param oldfile
	 * @param newfile
	 */
	public static void rename(String oldfile, String newfile) {
		File file = new File(oldfile);
		file.renameTo(new File(newfile));
	}
	/**
	 * ��ָ��·���²�������ָ���ļ�����<br>
	 * �����򷵻ش��ļ�
	 * @param filepath
	 * @param filename
	 * @return
	 */
	public static File searchFile(String filepath, String filename) {
		File dirFile = new File(filepath);
		File[] files = dirFile.listFiles();
		for(File file : files) {
			if(file.getName().equals(filename)) {
				return file;
			}
		}
		return null;
	}
}
