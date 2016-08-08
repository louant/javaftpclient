package com.jfc.ftp.model.table;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class OptionTableModel extends DefaultTableModel {

	public Object[] rowData = {new ImageIcon(), new ImageIcon(), new ImageIcon(), new ImageIcon(), new ImageIcon()};
	public OptionTableModel() {
		super();
		dataVector.clear();
		rowData[0] = new ImageIcon("images/file/folder_home.png");
		rowData[1] = new ImageIcon("images/file/folder_goup.png");
		rowData[2] = new ImageIcon("images/file/folder_refr.png");
		rowData[3] = new ImageIcon("images/file/folder_addd.png");
		rowData[3] = new ImageIcon("images/file/folder_dele.png");
		addRow(rowData);
		fireTableDataChanged();
	}
}
