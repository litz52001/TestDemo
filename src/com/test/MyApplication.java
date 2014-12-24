package com.test;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class MyApplication extends Application {
	
	public Context mContext;
	public Notification notification;
	public NotificationManager notificationManager;
	private static MyApplication myApplication;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		myApplication=this;
	}
	
	public static MyApplication getInstance(){
		return myApplication;

	}
}
