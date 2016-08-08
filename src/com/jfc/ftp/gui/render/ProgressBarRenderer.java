package com.jfc.ftp.gui.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;;

/**
 * ����������Ⱦ��
 * @author SavageGarden
 *
 */
public class ProgressBarRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;
    private final JProgressBar b;
    public ProgressBarRenderer(){
    	super();
        setOpaque(true);
        b = new JProgressBar();
		//�Ƿ���ʾ�����ַ���
		b.setStringPainted(true);
		b.setMinimum(0);
		b.setMaximum(100);
		//�Ƿ���Ʊ߿�
		b.setBorderPainted(true);
        b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = (Integer) value;
        b.setValue(i);
        return b;
    }
}
