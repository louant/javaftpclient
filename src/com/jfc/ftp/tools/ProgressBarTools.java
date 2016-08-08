package com.jfc.ftp.tools;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * ������������
 * ��Ҫ���ڸ��½���������ʾ
 * @author SavageGarden
 *
 */
public class ProgressBarTools {

	public static Runnable runner = null;
	public static JProgressBar progressBar;
	/**
	 * ����һ�����߳�,���½���������ʾ
	 * @param progressBar
	 * @param showValue
	 */
	public static void update(final JProgressBar progressBar, final int showValue) {
		System.out.println("in:" + showValue);
		if(runner == null) {
			runner = new Runnable() {
				public void run() {
					progressBar.setValue(showValue);
				}
			};
		}
		SwingUtilities.invokeLater(runner);
	}
	
}
