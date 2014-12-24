package com.test.util;

public class CommonUtils 
{

	private static long lastClickTime;

	/**
	 * 是否重复点击
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();	
		long timeD = time - lastClickTime;	
		if ( 0 < timeD && timeD < 1000) {	
			return true;	
		}	
		lastClickTime = time;	
		return false;
	}

}
