package com.jfc.ftp.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.jfc.ftp.model.file.FileModel;

/**
 * 用于处理文件相关
 * @author SavageGarden
 *
 */
public class FileUtil {

	/**
	 * 得到当前用户的主目录
	 * @return
	 */
	public static String getUserHome() {
		Properties prop = System.getProperties();
		return prop.getProperty("user.home");
	}
	/**
	 * 得到可用的根系统文件
	 * @return
	 */
	public static File[] getListRoots() {
		return File.listRoots();
	}
	/**
	 * 将根系统文件转为FileModel列表
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
	 * 得到客户端指定路径下的文件列表
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
	 * 得到服务端指定路径下的文件列表
	 * <br>其中dataStr应满足如下格式的字符串
	 * <p>
	 * <br>drw-rw-rw-   1 user     group           0 Oct 30 11:40 ..
	 * <br>-rw-rw-rw-   1 user     group        7303 Aug 10 11:42 cmpylist.xml
	 * <br>d			表示目录	
	 * <br>-			表示文件
	 * <br>rw-rw-rw-	表示权限设置
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
			//按照空格将fileMessage截为数组后获取相关信息
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
	 * 判断当前路径是否是盘符
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
	 * 创建目录
	 * @param filepath
	 */
	public static void createFolder(String filepath) {
		File file = new File(filepath);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	/**
	 * 文件删除
	 * <br>如果是文件则直接删除
	 * <br>如果是文件夹则递归删除
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
	 * 修改文件名
	 * @param oldfile
	 * @param newfile
	 */
	public static void rename(String oldfile, String newfile) {
		File file = new File(oldfile);
		file.renameTo(new File(newfile));
	}
	/**
	 * 在指定路径下查找有无指定文件存在<br>
	 * 存在则返回此文件
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
