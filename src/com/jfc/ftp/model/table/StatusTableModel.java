package com.jfc.ftp.model.table;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import com.jfc.ftp.model.download.DownloadModel;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

/**
 * ״̬�����ؼ�¼�ı��ģ��
 * @author SavageGarden
 *
 */
public class StatusTableModel extends DefaultTableModel {

	//public Object[] rowData = {"..", new JProgressBar(), "", "", ""};
	public Object[] rowData = {"..", 0, "", "", ""};
	public StatusTableModel() {
		super();
		addColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_NAME));
		addColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_STATUS));
		addColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_SIZE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_SPEED));
		addColumn(PropertyUtil.getResources(ResourcesConstant.STATUS_TITLE_DETAILS));
	}
	/**
	 * ����Ϊ���ɱ༭
	 */
	public boolean isCellEditable(int row, int column) {
        return false;
    }
	/**
	 * ��״̬�����һ��������
	 *
	 */
	public void addProgressBar(int filesize) {
		rowData[1] = filesize;
		addRow(rowData);
	}
	public void showDownloadList(String host) {
		ArrayList downloadList = SystemTools.getDownloadList(host);
		for(Object object : downloadList){
			DownloadModel download = (DownloadModel)object;
			String filename = download.getFilepath().substring(download.getFilepath().lastIndexOf("/") + 1, download.getFilepath().length());
			rowData[0] = filename;
			rowData[1] = (int)(download.getHadRead()*100/download.getFilesize());
			rowData[2] = download.getFilesize();
			addRow(rowData);
		}
		fireTableDataChanged();
	}
}
