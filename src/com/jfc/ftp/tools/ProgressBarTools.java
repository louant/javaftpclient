package com.jfc.ftp.tools;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * 进度条工具类
 * 主要用于更新进度条的显示
 * @author SavageGarden
 *
 */
public class ProgressBarTools {

	public static Runnable runner = null;
	public static JProgressBar progressBar;
	/**
	 * 启动一个新线程,更新进度条的显示
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
