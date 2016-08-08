package com.jfc.test;

import java.util.ArrayList;

public class FTPTest {

	public static void main(String args[]) {
		Test test = new Test();
		ArrayList list = new ArrayList();
		list.add(test);
		test.setStart(100);
		Test t = (Test)list.get(0);
		System.out.println(t.getStart());
	}		
}
class Test {
	private int start = 0;

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
	
}


