package com.jfc.ftp.model.table;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import com.jfc.ftp.model.ftpsite.FTPSiteModel;
import com.jfc.ftp.tools.SystemTools;
import com.jfc.ftp.util.PropertyUtil;
import com.jfc.ftp.util.ResourcesConstant;

public class FTPSiteTableModel extends DefaultTableModel {
	public Object[] rowData = {".."};
	public static ArrayList ftpSiteList;
	public FTPSiteTableModel() {
		super();
		addColumn(PropertyUtil.getResources(ResourcesConstant.FTPSITE_TITLE_NAME));
	}
	/**
	 * 设置不可编辑
	 * @param row
	 * @param col
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	/**
	 * 获取储存的FTP服务器信息
	 *
	 */
	public void showSiteList(){
		ftpSiteList = SystemTools.getFTPSiteList();
		for(Object obj : ftpSiteList) {
			FTPSiteModel ftpSite = (FTPSiteModel)obj;
			rowData[0] = ftpSite.getName();
			addRow(rowData);
		}
	}
}
