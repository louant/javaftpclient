package com.jfc.ftp.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.jfc.ftp.model.download.DownloadModel;
import com.jfc.ftp.tools.FTPThread;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.Constant;
/**
 * 接收主窗口事件的适配器类<br>
 * 只对关闭事件做处理
 * @author SavageGarden
 *
 */
public class FTPMainFrameListener extends WindowAdapter {

	/**
	 * 响应窗口关闭事件
	 * 停止下载线程<br>
	 * 将下载信息写入到xml文件<br>
	 * 退出
	 */
	public void windowClosing(WindowEvent e) {
		int i = JOptionPane.showConfirmDialog(null, "确认关闭吗？");
		if(i == JOptionPane.OK_OPTION) {
			ArrayList downloadList = new ArrayList();
			for(Object object : SystemTools.downloadThreadList) {
				FTPThread ftpThread = (FTPThread)object;
				if(ftpThread != null) {
					ftpThread.setStatus(Constant.THREAD_STATUS_STOP);
					DownloadModel downloadModel = ftpThread.getDownloadModel();
					downloadModel.setHadRead(ftpThread.hadRead);
					if(ftpThread.hadRead == ftpThread.getDownloadModel().getFilesize()) {
						downloadModel.setEclipsedTime(ftpThread.getEclipsedTime());
					} else {
						downloadModel.setEclipsedTime(ftpThread.getEclipsedTime() + (System.currentTimeMillis() - ftpThread.getStartTime()));
					}
					//只保存普通下载方式之外的下载记录
					if(ftpThread.downloadType != Constant.DOWNLOAD_TYPE_COMMON) {
						downloadList.add(downloadModel);
					}
				}
				
			}
			SystemTools.addDownloadMessage(downloadList);
			System.exit(0);
		} else {
			return;
		}
	}
}
