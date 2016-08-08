package com.jfc.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;

public class PidedIO {
	public static void main(String args[]) {
		//test("G:\\ftptest\\BIGFILE.rar", 4);
		String filepath = "C:\\Documents and Settings\\User\\桌面\\弯弯的月亮.mp3";
		filepath = "G:\\ftptest\\BIGFILE.rar";
		mergerTempFile(filepath, 4);
	}
	/**
	 * 在制定文件路径下查找临时文件合并为一个文件
	 * @param filepath
	 * @param threadCount
	 */
	public static void mergerTempFile(String filepath, int threadCount) {
		try {
			BufferedOutputStream data_output = new BufferedOutputStream(new FileOutputStream(filepath));
			byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
			for(int i = 0; i < threadCount; i ++) {
				File temp_file = new File(filepath + Constant.DOWNLOAD_TEMP_NAME + i);
				BufferedInputStream data_input = new BufferedInputStream(new FileInputStream(temp_file));
				int read = 0;
				int hadRead = 0;
				while((read = data_input.read(temp, 0, temp.length)) != -1) {
					data_output.write(temp, 0, read);
					//data_output.flush();
					hadRead += read;
				}
				System.out.println(hadRead);
				data_input.close();
				//temp_file.delete();
			}
			data_output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void test(String filepath, int threadCount) {
		String[] downloadSizeArray = SystemTools.getDownloadSizeArray((int)(new File(filepath)).length(), threadCount);
		byte[] temp = new byte[Constant.TEMP_BYTE_LENGTH];
		
		try {
			BufferedInputStream data_input = new BufferedInputStream(new FileInputStream(filepath));
			for(int i = 0; i < threadCount; i ++) {
				System.out.print(downloadSizeArray[i]);
				File temp_file = new File(filepath + Constant.DOWNLOAD_TEMP_NAME + i);
				RandomAccessFile raf = new RandomAccessFile(temp_file, "rw");
				int read = 0;
				int hadRead = (int)temp_file.length();
				System.out.print("\t hadRead " + hadRead);
				//if(i > 0) {
				data_input.skip(Long.parseLong(downloadSizeArray[i].split(":")[0]) + temp_file.length());
				//}
				raf.seek(temp_file.length());
				while((read = data_input.read(temp, 0, temp.length)) != -1 && hadRead < Integer.parseInt(downloadSizeArray[i].split(":")[1])) {
					int temp_hadRead = hadRead;
					if((temp_hadRead += read) > Integer.parseInt(downloadSizeArray[i].split(":")[1])) {
						read = Integer.parseInt(downloadSizeArray[i].split(":")[1]) - hadRead;
					}
					raf.write(temp, 0, read);
					hadRead += read;
				}
				raf.close();
				System.out.print("\t hadRead " + hadRead);
				System.out.println();
			}
		} catch(Exception e) {
			
		}
		
		
		
	}
}