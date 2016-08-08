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
 * �����������¼�����������<br>
 * ֻ�Թر��¼�������
 * @author SavageGarden
 *
 */
public class FTPMainFrameListener extends WindowAdapter {

	/**
	 * ��Ӧ���ڹر��¼�
	 * ֹͣ�����߳�<br>
	 * ��������Ϣд�뵽xml�ļ�<br>
	 * �˳�
	 */
	public void windowClosing(WindowEvent e) {
		int i = JOptionPane.showConfirmDialog(null, "ȷ�Ϲر���");
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
					//ֻ������ͨ���ط�ʽ֮������ؼ�¼
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
