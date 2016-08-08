package com.jfc.ftp.tools.observer.progressbar;

import java.util.Observable;
import java.util.Observer;

import com.jfc.ftp.tools.SystemTools;

/**
 * 实现观察者模式的工具类
 * <p>
 * 用于实现观察者	观察进度条的进度变化
 * @author SavageGarden
 *
 */
public class ProgressBarObserver implements Observer {

	public void update(Observable o, Object arg) {
		SystemTools.updateProgressBar((String)arg);
	}
}
