package com.jfc.ftp.model.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import com.jfc.ftp.model.file.FileModel;
import com.jfc.ftp.util.FileUtil;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

/**
 * 服务端文件浏览器的表格模型
 * @author SavageGarden
 *
 */
public class ServerTableModel extends DefaultTableModel{

	public Object[] rowData = {new ImageIcon(), "..", "", "", ""};
	
	public ServerTableModel() {
		super();
		addColumn("Server");
		addColumn(PropertyUtil.getResources(ResourcesConstant.SERVER_TITLE_NAME));
		addColumn(PropertyUtil.getResources(ResourcesConstant.SERVER_TITLE_SIZE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.SERVER_TITLE_TYPE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.SERVER_TITLE_DATE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.SERVER_TITLE_DETAILS));
	}
	/**
	 * 设置只有第二列可以编辑(即可以修改文件名)
	 * @param row
	 * @param col
	 */
	public boolean isCellEditable(int row, int col) {
		if(col == 1) {
			return true;
		}
		return false;
	}
	/**
	 * 将服务端返回数据组装成FileModel列表
	 * @param dataStr
	 */
	public void showFileList(String dataStr){
		dataVector.clear();
		ArrayList fileList = FileUtil.getServerFileList(dataStr);
		for(Object object : fileList){
			FileModel fileModel = (FileModel)object;
			rowData[0] = new ImageIcon(fileModel.getFileImage());
			rowData[1] = fileModel.getFileName();
			rowData[2] = fileModel.getFileSize();
			rowData[3] = fileModel.getFileType();
			rowData[4] = fileModel.getFileDate();
			addRow(rowData);
		}
		fireTableDataChanged();
	}
	/**
	 * 清空内容
	 *
	 */
	public void clear() {
		dataVector.clear();
		fireTableDataChanged();
	}
}
