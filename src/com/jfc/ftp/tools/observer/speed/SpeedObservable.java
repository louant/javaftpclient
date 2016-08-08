package com.jfc.ftp.tools.observer.speed;

import java.util.Observable;
/**
 * 实现观察者模式的工具类
 * <p>
 * 用于实现观察者观察的对象	下载进程的即时速度
 * @author SavageGarden
 *
 */
public class SpeedObservable extends Observable {

	private String speedStr;
	public String getSpeedStr() {
    	return speedStr;
    }
	public void setSpeedStr(String speedStr) {
    	this.speedStr = speedStr;
    	setChanged();
    	notifyObservers(speedStr);
    }
}
