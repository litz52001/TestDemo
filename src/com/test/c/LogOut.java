package com.test.c;

import android.util.Log;

public class LogOut {
	
	public static void out(PorterDuffView porterDuffView ,String string)
	{
		Log.e(""+porterDuffView.getClass(), string);
	}
	
	public static void out(DownloadImgTask downloadImgTask ,String string)
	{
		Log.e(""+downloadImgTask.getClass(), string);
	}
}
