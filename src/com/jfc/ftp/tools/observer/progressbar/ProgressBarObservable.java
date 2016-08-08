package com.jfc.ftp.tools.observer.progressbar;

import java.util.Observable;

/**
 * 实现观察者模式的工具类
 * <p>
 * 用于实现观察者观察的对象 进度条的显示值
 * @author SavageGarden
 *
 */
public class ProgressBarObservable extends Observable {

	private String showValue;
    public String getShowValue() {
        return showValue;
    }
    public void setShowValue(String showValue) {
        this.showValue = showValue;
        setChanged();
        notifyObservers(showValue);
    }
}
