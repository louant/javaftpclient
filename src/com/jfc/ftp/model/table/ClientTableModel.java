package com.jfc.ftp.model.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import com.jfc.ftp.model.file.FileModel;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.FileUtil;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;
/**
 * �ͻ����ļ�������ı��ģ��
 * @author SavageGarden
 *
 */
public class ClientTableModel extends DefaultTableModel {

	public Object[] rowData = {new ImageIcon(), "..", "", "", ""};
	
	public ClientTableModel() {
		super();
		addColumn("Client");
		addColumn(PropertyUtil.getResources(ResourcesConstant.CLIENT_TITLE_NAME));
		addColumn(PropertyUtil.getResources(ResourcesConstant.CLIENT_TITLE_SIZE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.CLIENT_TITLE_TYPE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.CLIENT_TITLE_DATE));
		addColumn(PropertyUtil.getResources(ResourcesConstant.CLIENT_TITLE_DETAILS));
	}
	/**
	 * ����ֻ�еڶ��п��Ա༭(�������޸��ļ���)
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
	 * ��ʾָ��Ŀ¼�µ��ļ��б�
	 * @param filepath
	 */
	public void showFileList(String filepath){
		dataVector.clear();
		ArrayList fileList;
		if(filepath.equals(SystemTools.getRootName())) {
			fileList = FileUtil.getClientRootFileList();
		} else {
			fileList = FileUtil.getClientFileList(filepath);
		}
		
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
}
